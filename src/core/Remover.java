package core;

import java.io.File;
import java.util.Collection;

/**
 * Created by SilenceSu on 2017/5/19.
 * Email:silence.sx@gmail.com
 */
public interface Remover {

	int checkBOM(File file);
	int checkBOM(String str);
	int checkBOM(StringBuilder builder);

	Collection work();

}
