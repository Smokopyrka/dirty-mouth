import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindCurseWordsAction extends AnAction {
    private final static String NOT_FOUND = "No curse words were found in this document";
    private final static String NOT_FOUND_TITLE = "No curse words found";
    private final static String FOUND_TITLE = "Found curse words!!!";
    private final static List<String> CRUSE_WORDS = Arrays.asList(
            "dupa",
            "kupa",
            "chuj",
            "penis",
            "gówno"
    );


    private final String patternString;
    private final Pattern pattern;

    public String createPatternString(List<String> tokens) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("\\b(");
        var tokenIter = tokens.iterator();
        if (tokenIter.hasNext()) stringBuilder.append(tokenIter.next());
        while (tokenIter.hasNext()) {
            stringBuilder.append("|" + tokenIter.next());
        }
        stringBuilder.append(")\\b");
        return stringBuilder.toString();
    }

    public FindCurseWordsAction() {
        this.patternString = createPatternString(CRUSE_WORDS);
        this.pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    }

    private class CurseMatch {
        private int line;
        private int colBeg;
        private int colEnd;
        private int offset;
        private int len;

        public CurseMatch(int line, int colBeg, int colEnd, int offset, int len) {
            this.line = line;
            this.colBeg = colBeg;
            this.colEnd = colEnd;
            this.offset = offset;
            this.len = len;
        }

        public int getLine() {
            return line;
        }

        public int getColBeg() {
            return colBeg;
        }

        public int getColEnd() {
            return colEnd;
        }

        public int getBegOffset() {
            return this.offset;
        }

        public int getEndOffset() {
            return this.offset + this.len;
        }

        public int getLen() {
            return len;
        }
    }

    private List<CurseMatch> getCurseWordOccurrences(Document document, Pattern pattern) {
        List<CurseMatch> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(document.getText());
        while (matcher.find()) {
            int length = matcher.group().length();
            int offset = matcher.end();
            int lineNumber = document.getLineNumber(offset);
            int endColNumber = offset - document.getLineStartOffset(lineNumber);
            int begColNumber = endColNumber - length;
            matches.add(new CurseMatch(
                    lineNumber,
                    begColNumber,
                    endColNumber,
                    offset - length,
                    length
            ));
        }
        return matches;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var editor = event.getData(CommonDataKeys.EDITOR);
        var document = editor.getDocument();
        String docText = document.getText().toLowerCase(Locale.ROOT);
        var textStyle = new TextAttributes();
        textStyle.setEffectType(EffectType.BOLD_LINE_UNDERSCORE);
        textStyle.setEffectColor(Color.RED);
        List<CurseMatch> matches = getCurseWordOccurrences(document, pattern);
        if (matches.size() == 0) {
            Messages.showDialog(
                    NOT_FOUND,
                    NOT_FOUND_TITLE,
                    new String[]{"Ok"},
                    1,
                    Messages.getInformationIcon()
            );
        } else {
            Messages.showDialog(
                    String.format("Found %d curse words!!!", matches.size()),
                    FOUND_TITLE,
                    new String[]{"Ok"},
                    1,
                    Messages.getInformationIcon()
            );
            for (CurseMatch match : matches) {
                editor.getMarkupModel().addRangeHighlighter(
                        match.getBegOffset(),
                        match.getEndOffset(),
                        HighlighterLayer.WARNING,
                        textStyle,
                        HighlighterTargetArea.EXACT_RANGE);
            }
        }
    }

}

