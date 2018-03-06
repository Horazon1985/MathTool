package mathtool.component.components;

import javax.swing.JTextArea;
import javax.swing.text.Element;

public class LineNumberHeader extends JTextArea {

    private final JTextArea textArea;

    public LineNumberHeader(JTextArea textArea) {
        this.textArea = textArea;
        setEditable(false);
    }

    public void updateLineNumbers() {
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);
    }

    private String getLineNumbersText() {
        int caretPosition = this.textArea.getDocument().getLength();
        Element root = this.textArea.getDocument().getDefaultRootElement();
        StringBuilder lineNumbersTextBuilder = new StringBuilder();
        lineNumbersTextBuilder.append("1").append(System.lineSeparator());

        for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++) {
            lineNumbersTextBuilder.append(elementIndex).append(System.lineSeparator());
        }

        return lineNumbersTextBuilder.toString();
    }
}
