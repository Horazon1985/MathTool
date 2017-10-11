package mathtool.component.components;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

public class AlgorithmEditor extends JTextArea {

    private final JTextArea textArea;

    public AlgorithmEditor(JTextArea textArea) {
        this.textArea = textArea;
        setEditable(false);

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                updateLineNumbers();
            }
        });
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
