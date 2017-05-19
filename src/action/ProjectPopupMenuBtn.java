package action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import core.Parameters;
import core.PlainRemover;
import core.Remover;

import java.util.Collection;

/**
 * Created by SilenceSu on 2017/5/19.
 * Email:silence.sx@gmail.com
 */
public class ProjectPopupMenuBtn extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        Remover r = new PlainRemover(new Parameters(file.getCanonicalPath()));
        Collection collection = r.work();

        Notifications.Bus.notify(
                new Notification("clearBom", "clear utf8 bom ok !", "clear " + collection.size() + "files", NotificationType.INFORMATION)
        );
    }
}
