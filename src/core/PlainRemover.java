package core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.internal.commons.io.ByteOrderMark;
import org.apache.maven.internal.commons.io.input.BOMInputStream;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by SilenceSu on 2017/5/19.
 * Email:silence.sx@gmail.com
 */
public class PlainRemover extends AbstractRemover {

    private List<Long> times = new ArrayList<>();
    private Long totalTime = new Long(0);

    public PlainRemover(Parameters parameters) {
        super(parameters);
    }

    @Override
    public int checkBOM(File file) {
        int result = 0;
        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                BOMInputStream bomIn = new BOMInputStream(fileInputStream,
                        ByteOrderMark.UTF_8,
                        ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE,
                        ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_16BE)
        ) {
            if (bomIn.hasBOM()) {
                result = bomIn.getBOM().length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Collection work() {
        String src = parameters.getSrc();
        String[] masks = parameters.getMasks();
        File directory = new File(src);
        Collection filesCollection=new  LinkedList();
        if (!directory.isDirectory()) {
            filesCollection.add(directory);
        }else{
            IOFileFilter subfolders = parameters.isRecursively() ? DirectoryFileFilter.DIRECTORY : null;
            filesCollection = FileUtils.listFiles(directory, new WildcardFileFilter(masks), subfolders);
        }
        int foundFiles = filesCollection.size();
        System.out.printf("Found %d files\n", foundFiles);
        if (foundFiles == 0) {
            System.out.println("Nothing to process");
            return filesCollection;
         } else {
            System.out.println("Begin processing");
        }
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Object obj : filesCollection) {
            PROCESSED++;
            final File file = (File) obj;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    process(file);
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.HOURS);
            long max = times.size() > 0 ? Collections.max(times) : 0;
            long min = times.size() > 0 ? Collections.min(times) : 0;
            if (max > 0) {
                String totalSeconds = new BigDecimal(totalTime).divide(new BigDecimal(1000000)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString();
                System.out.printf("Processed %d files in %ss\n", UPDATED, totalSeconds);
                String minSeconds = new BigDecimal(min).divide(new BigDecimal(1000000)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString();
                String maxSeconds = new BigDecimal(max).divide(new BigDecimal(1000000)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString();
                System.out.printf("Min processing time is %ss and max processing time is %ss \n", minSeconds, maxSeconds);
            } else {
                System.out.printf("Corrected %d out of %d files\n", UPDATED, PROCESSED);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return filesCollection;
    }

    private void process(File file) {
        int bomLength = checkBOM(file);
        if (bomLength > 0) {
            long before = System.nanoTime();
            long truncatedSize = file.length() - bomLength;
            byte[] cache = new byte[(int) (truncatedSize)];
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                inputStream.skip(bomLength);
                int totalBytes = 0;
                while (totalBytes < truncatedSize) {
                    int bytesRemaining = (int) truncatedSize - totalBytes;
                    int bytesRead = inputStream.read(cache, totalBytes, bytesRemaining);
                    if (bytesRead > 0) {
                        totalBytes = totalBytes + bytesRead;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                org.apache.commons.io.IOUtils.write(cache, outputStream);
                System.out.println(file.getName());
                UPDATED++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            long after = System.nanoTime();
            long diff = after - before;
            times.add(diff);
            totalTime += diff;
        }
    }

}
