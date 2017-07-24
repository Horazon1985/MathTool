package mathtool.component.components;

import org.jsoup.parser.Parser;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.output.AlgorithmOutputPrinter;
import exceptions.EvaluationException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mathtool.MathToolController;
import mathtool.lang.translator.Translator;

public class MathToolAlgorithmsGUI extends JDialog {

    private static final String PATH_LOGO_MATHTOOL_ALGORITHMS = "icons/MathToolAlgorithmsLogo.png";

    private JPanel headerPanel;
    private JLabel headerLabel;
    private ImageIcon headerImage;

    private JButton addAlgorithmButton;
    private JButton runButton;
    private JButton formatButton;

    private final int STUB = 20;
    private final int PADDING = 20;

    private JEditorPane algorithmEditor;
    private JTextPane outputArea;

    private SwingWorker<Void, Void> computingSwingWorker;
    private ComputingDialogGUI computingDialogGUI;
    private Timer computingTimer;

    private static final String GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM = "GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_RUN = "GUI_MathToolAlgorithmsGUI_RUN";
    private static final String GUI_MathToolAlgorithmsGUI_STOP = "GUI_MathToolAlgorithmsGUI_STOP";
    private static final String GUI_MathToolAlgorithmsGUI_DEBUG = "GUI_MathToolAlgorithmsGUI_DEBUG";
    private static final String GUI_MathToolAlgorithmsGUI_FORMAT = "GUI_MathToolAlgorithmsGUI_FORMAT";

    private static MathToolAlgorithmsGUI instance = null;

    public static MathToolAlgorithmsGUI getInstance(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiHeight, String titleID) {
        if (instance == null) {
            instance = new MathToolAlgorithmsGUI(mathtoolGuiX, mathtoolGuiY, mathtoolGuiHeight, titleID);
        }
        return instance;
    }

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Editor zum Schreiben von Algorithmencodes.<br>
     * (3) Ausgabefenster.<br>
     * (4) Buttons.<br>
     */
    private MathToolAlgorithmsGUI(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiHeight, String titleID) {

        setTitle(titleID);
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.white);

        int currentComponentLevel;

        try {
            // Logo ganz oben laden.
            this.headerPanel = new JPanel();
            add(this.headerPanel);
            this.headerImage = new ImageIcon(getClass().getResource(PATH_LOGO_MATHTOOL_ALGORITHMS));
            if (this.headerImage != null) {
                this.headerLabel = new JLabel(this.headerImage);
                this.headerPanel.add(this.headerLabel);
                this.headerPanel.setBounds(0, -5, this.headerImage.getIconWidth(), this.headerImage.getIconHeight());
                this.headerPanel.setVisible(true);
                currentComponentLevel = this.STUB + this.headerImage.getIconHeight() - 5;
            } else {
                this.headerLabel = new JLabel();
                currentComponentLevel = this.STUB;
            }

            setBounds(mathtoolGuiX, mathtoolGuiY, this.headerImage.getIconWidth(), mathtoolGuiHeight);

            // Algorithmeneditor definieren.
            this.algorithmEditor = new JEditorPane();
            add(this.algorithmEditor);
            this.algorithmEditor.setContentType("text/html; charset=UTF-8");
            this.algorithmEditor.setBounds(PADDING, 180, this.getWidth() - 2 * PADDING, this.getHeight() - 500);
            this.algorithmEditor.setVisible(true);
            this.algorithmEditor.setBorder(new LineBorder(Color.black, 1));
            currentComponentLevel += this.algorithmEditor.getHeight() + STUB;

            this.algorithmEditor.setText("expression main(){expression x = int(t^2,t,0,1); if(x>1){x=x+2;} return x;}");
            algorithmEditor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {

                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                }

                @Override
                public void insertUpdate(DocumentEvent e) {

                }
            });

            this.outputArea = new JTextPane();
            add(this.outputArea);
            this.outputArea.setContentType("text/html; charset=UTF-8");
            this.outputArea.setBounds(PADDING, currentComponentLevel, this.getWidth() - 2 * PADDING, 200);
            this.outputArea.setVisible(true);
            this.outputArea.setEditable(false);
            this.outputArea.setBorder(new LineBorder(Color.black, 1));
            currentComponentLevel += this.outputArea.getHeight() + STUB;

            // outputArea als Ausgabe deklarieren.
            AlgorithmOutputPrinter.setOutputArea(this.outputArea);

            // Buttons definieren.
            this.addAlgorithmButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM));
            add(this.addAlgorithmButton);
            this.addAlgorithmButton.setBounds(PADDING, currentComponentLevel, 200, 30);

            this.runButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
            add(this.runButton);
            this.runButton.setBounds(2 * PADDING + 200, currentComponentLevel, 200, 30);

            this.formatButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FORMAT));
            add(this.formatButton);
            this.formatButton.setBounds(3 * PADDING + 400, currentComponentLevel, 200, 30);

            this.runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String algString = MathToolAlgorithmsController.getPlainCode(algorithmEditor.getText());
                    String algStringWithoutUTF8 = Parser.unescapeEntities(algString, false);
                    compileAndExecuteAlgorithmAlgorithmFile(algStringWithoutUTF8);
                }
            });

            this.formatButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    String unformattedCode = algorithmEditor.getText();
                    String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(unformattedCode);
                    algorithmEditor.setDocument(algorithmEditor.getEditorKit().createDefaultDocument());
                    algorithmEditor.setText(formattedCode);
                }
            });

            // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
            validate();
            repaint();

        } catch (Exception e) {
        }

    }

    private void compileAndExecuteAlgorithmAlgorithmFile(String algorithm) {
        final MathToolAlgorithmsGUI mathToolAlgorithmsGUI = this;
        computingSwingWorker = new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                computingTimer.cancel();
                computingDialogGUI.setVisible(false);
                runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
            }

            @Override
            protected Void doInBackground() throws Exception {
                runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_STOP));
                computingDialogGUI = new ComputingDialogGUI(computingSwingWorker, mathToolAlgorithmsGUI.getX(), mathToolAlgorithmsGUI.getY(), mathToolAlgorithmsGUI.getWidth(), mathToolAlgorithmsGUI.getHeight());
                MathToolController.initTimer(computingTimer, computingDialogGUI);

                try {
                    AlgorithmOutputPrinter.clearOutput();
                    AlgorithmOutputPrinter.printStartParsingAlgorithms();
                    AlgorithmCompiler.parseAlgorithmFile(algorithm);
                    AlgorithmOutputPrinter.printEndParsingAlgorithms();

                    AlgorithmOutputPrinter.printStartAlgorithmData();
                    algorithmexecuter.AlgorithmExecuter.executeAlgorithm(AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage());
                    AlgorithmOutputPrinter.printEndAlgorithmData();
                } catch (AlgorithmCompileException | AlgorithmExecutionException | EvaluationException e) {
                    AlgorithmOutputPrinter.printException(e);
                }

                return null;
            }

        };
        computingTimer = new Timer();
        computingSwingWorker.execute();

    }

}
