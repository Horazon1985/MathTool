package mathtool.component.components;

import mathtool.component.controller.MathToolAlgorithmsController;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.output.AlgorithmOutputPrinter;
import exceptions.EvaluationException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import mathtool.lang.translator.Translator;

public class MathToolAlgorithmsGUI extends JDialog {

    private static final String PATH_LOGO_MATHTOOL_ALGORITHMS = "icons/MathToolAlgorithmsLogo.png";
    private static final String PATH_RUN_LOGO = "icons/RunLogo.png";
    private static final String PATH_STOP_LOGO = "icons/StopLogo.png";

    private JMenuBar algorithmsMenuBar;
    private JMenu algorithmsMenuFile;
    private JMenuItem algorithmsMenuItemOpen;
    private JMenuItem algorithmsMenuItemSave;
    private JMenuItem algorithmsMenuItemQuit;

    private JPanel headerPanel;
    private JLabel headerLabel;
    private ImageIcon headerImage;

    private JButton runButton;
    private JButton formatButton;
    private JButton seeCompiledCodeButton;

    private final int STUB = 20;
    private final int PADDING = 20;

    private JScrollPane algorithmEditorPane;
    private JTextArea algorithmEditor;
    private JScrollPane outputAreaPane;
    private JTextPane outputArea;

    KeyListener keyListener;

    private ImageIcon runIcon;
    private ImageIcon stopIcon;

    private SwingWorker<Void, Void> computingSwingWorker;

    private boolean codeVisible;
    private String code, compiledCode;

    private boolean computing;

    private static final String GUI_MathToolAlgorithmsGUI_FILE = "GUI_MathToolAlgorithmsGUI_FILE";
    private static final String GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM = "GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM = "GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_QUIT = "GUI_MathToolAlgorithmsGUI_QUIT";
    private static final String GUI_MathToolAlgorithmsGUI_RUN = "GUI_MathToolAlgorithmsGUI_RUN";
    private static final String GUI_MathToolAlgorithmsGUI_STOP = "GUI_MathToolAlgorithmsGUI_STOP";
    private static final String GUI_MathToolAlgorithmsGUI_DEBUG = "GUI_MathToolAlgorithmsGUI_DEBUG";
    private static final String GUI_MathToolAlgorithmsGUI_FORMAT = "GUI_MathToolAlgorithmsGUI_FORMAT";
    private static final String GUI_MathToolAlgorithmsGUI_SEE_COMPILED_CODE = "GUI_MathToolAlgorithmsGUI_SEE_COMPILED_CODE";
    private static final String GUI_MathToolAlgorithmsGUI_SEE_EDITOR_CODE = "GUI_MathToolAlgorithmsGUI_SEE_EDITOR_CODE";

    private static MathToolAlgorithmsGUI instance = null;

    public JTextArea getAlgorithmEditor() {
        return this.algorithmEditor;
    }
    
    public static MathToolAlgorithmsGUI getInstance(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiWidth, int mathtoolGuiHeight, String titleID) {
        if (instance == null) {
            instance = new MathToolAlgorithmsGUI(mathtoolGuiX, mathtoolGuiY, mathtoolGuiWidth, mathtoolGuiHeight, titleID);
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
    private MathToolAlgorithmsGUI(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiWidth, int mathtoolGuiHeight, String titleID) {

        this.computing = false;
        this.codeVisible = true;

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

            setBounds(mathtoolGuiX + (mathtoolGuiWidth - this.headerImage.getIconWidth()) / 2, mathtoolGuiY, this.headerImage.getIconWidth(), mathtoolGuiHeight);

            this.algorithmsMenuBar = new JMenuBar();
            this.algorithmsMenuFile = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FILE));
            this.algorithmsMenuItemOpen = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM));
            this.algorithmsMenuItemSave = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM));
            this.algorithmsMenuItemQuit = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_QUIT));
            this.algorithmsMenuFile.add(this.algorithmsMenuItemOpen);
            this.algorithmsMenuFile.add(this.algorithmsMenuItemSave);
            this.algorithmsMenuFile.add(this.algorithmsMenuItemQuit);
            this.algorithmsMenuBar.add(this.algorithmsMenuFile);
            setJMenuBar(this.algorithmsMenuBar);

            this.algorithmsMenuItemOpen.addActionListener((ActionEvent e) -> {
                MathToolAlgorithmsController.loadAlgorithm();
            });
            this.algorithmsMenuItemSave.addActionListener((ActionEvent e) -> {
                MathToolAlgorithmsController.saveAlgorithm();
            });
            this.algorithmsMenuItemQuit.addActionListener((ActionEvent e) -> {
                instance.dispose();
                instance = null;
            });

            // Algorithmeneditor definieren.
            this.algorithmEditor = new JTextArea();
            add(this.algorithmEditor);
            this.algorithmEditor.setVisible(true);
            this.algorithmEditor.setBorder(new LineBorder(Color.black, 1));

            this.algorithmEditorPane = new JScrollPane(this.algorithmEditor,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.algorithmEditorPane.setBounds(PADDING, 180, this.getWidth() - 2 * PADDING, this.getHeight() - 520);
            this.algorithmEditor.setCaretPosition(this.algorithmEditor.getDocument().getLength());
            add(this.algorithmEditorPane);
            this.algorithmEditorPane.setVisible(true);

            currentComponentLevel += this.algorithmEditorPane.getHeight() + STUB;

            this.algorithmEditor.setText("expression main(){\n"
                    + "	expression a=1;\n"
                    + "	for(expression i=0,f(i)<=10,i=i+g(i)){\n"
                    + "		a=3*a+2;\n"
                    + "	}\n"
                    + "	return a;\n"
                    + "}\n"
                    + "\n"
                    + "expression f(expression i){\n"
                    + "	return 2*i;\n"
                    + "}\n"
                    + "\n"
                    + "expression g(expression i){\n"
                    + "	return i^2+1;\n"
                    + "}");

            keyListener = new KeyListener() {

                private boolean controlPressed = false;

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                        controlPressed = true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F && controlPressed) {
                        formatCode();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                        controlPressed = false;
                    }
                }
            };
            this.algorithmEditor.addKeyListener(this.keyListener);

            // Ausgabefeld definieren.
            this.outputArea = new JTextPane();
            add(this.outputArea);
            this.outputArea.setContentType("text/html; charset=UTF-8");
            this.outputArea.setVisible(true);
            this.outputArea.setEditable(false);
            this.outputArea.setBorder(new LineBorder(Color.black, 1));
            this.outputAreaPane = new JScrollPane(this.outputArea,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.outputAreaPane.setBounds(PADDING, currentComponentLevel, this.getWidth() - 2 * PADDING, 200);
            add(this.outputAreaPane);
            this.outputAreaPane.setVisible(true);

            currentComponentLevel += this.outputAreaPane.getHeight() + STUB;

            // outputArea als Ausgabe deklarieren.
            AlgorithmOutputPrinter.setOutputArea(this.outputArea);

            // Buttons definieren.
            this.runIcon = new ImageIcon(ImageIO.read(getClass().getResource(PATH_RUN_LOGO)));
            this.stopIcon = new ImageIcon(ImageIO.read(getClass().getResource(PATH_STOP_LOGO)));

            this.runButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
            this.runButton.setIcon(this.runIcon);
            this.runButton.setOpaque(false);
            add(this.runButton);
            this.runButton.setBounds(PADDING, currentComponentLevel, 200, 30);

            this.formatButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FORMAT));
            add(this.formatButton);
            this.formatButton.setBounds(2 * PADDING + 200, currentComponentLevel, 200, 30);

            this.seeCompiledCodeButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SEE_COMPILED_CODE));
            add(this.seeCompiledCodeButton);
            this.seeCompiledCodeButton.setBounds(3 * PADDING + 400, currentComponentLevel, 320, 30);

            // Actions definieren.
            this.runButton.addActionListener((ActionEvent ae) -> {
                if (!computing) {
                    computing = true;
                    String algString = MathToolAlgorithmsController.getPlainCode(algorithmEditor.getText());
                    compileAndExecuteAlgorithmAlgorithmFile(algString);
                } else {
                    computing = false;
                    computingSwingWorker.cancel(true);
                }
            });

            this.formatButton.addActionListener((ActionEvent e) -> {
                formatCode();
            });

            this.seeCompiledCodeButton.addActionListener((ActionEvent e) -> {
                if (codeVisible) {
                    seeCompiledCodeButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SEE_EDITOR_CODE));
                    code = algorithmEditor.getText();
                    algorithmEditor.setBackground(Color.LIGHT_GRAY);
                    algorithmEditor.setText(compiledCode);
                } else {
                    seeCompiledCodeButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SEE_COMPILED_CODE));
                    algorithmEditor.setBackground(Color.WHITE);
                    algorithmEditor.setText(code);
                }
                codeVisible = !codeVisible;
                algorithmEditor.setEditable(codeVisible);
            });

            MathToolAlgorithmsController.setMathToolAlgorithmsGUI(this);
            
            // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
            validate();
            repaint();

        } catch (IOException e) {
        }

    }
    
    private void formatCode() {
        String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(algorithmEditor.getText());
        algorithmEditor.setText(formattedCode);
    }

    private void compileAndExecuteAlgorithmAlgorithmFile(String algorithm) {
        this.computingSwingWorker = new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
                runButton.setIcon(runIcon);
                computing = false;
            }

            @Override
            protected Void doInBackground() throws Exception {
                runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_STOP));
                runButton.setIcon(stopIcon);

                try {
                    // Zunächst formatieren.
                    formatCode();
                    // Algorithmus kompilieren.
                    AlgorithmOutputPrinter.clearOutput();
                    AlgorithmOutputPrinter.printStartParsingAlgorithms();
                    AlgorithmCompiler.parseAlgorithmFile(algorithm);
                    saveCompiledCode();
                    AlgorithmOutputPrinter.printEndParsingAlgorithms();
                    // Algorithmus ausführen.
                    AlgorithmOutputPrinter.printStartAlgorithmData();
                    algorithmexecuter.AlgorithmExecuter.executeAlgorithm(AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage());
                    AlgorithmOutputPrinter.printEndAlgorithmData();
                } catch (AlgorithmCompileException | AlgorithmExecutionException | EvaluationException e) {
                    AlgorithmOutputPrinter.printException(e);
                }

                return null;
            }

        };
        this.computingSwingWorker.execute();

    }

    private void saveCompiledCode() {
        List<Algorithm> algorithms = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
        this.compiledCode = MathToolAlgorithmsController.writeCompiledCode(algorithms);
    }

}
