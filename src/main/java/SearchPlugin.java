import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;

public class SearchPlugin extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            Messages.showDialog(result, "Szukaj", new String[]{"Idź"}, 1, Messages.getInformationIcon());
            Desktop.getDesktop().browse(new URL("https://www.google.com/search?q=" + result + "+stackoverflow").toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project currentProject = e.getProject();
        e.getPresentation().setEnabledAndVisible(currentProject != null);
        super.update(e);
    }
}