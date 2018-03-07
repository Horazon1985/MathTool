package mathtool.component.components;

import mathtool.component.controller.MathToolAlgorithmsController;
import algorithmexecuter.AlgorithmBuilder;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.output.AlgorithmOutputPrinter;
import exceptions.EvaluationException;
import java.awt.Color;
import java.awt.event.ActionEvent;
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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mathtool.lang.translator.Translator;

public class MathToolAlgorithmsGUI extends JDialog {

    private static final String ICON_PATH = "/mathtool/icons/MathToolIcon.png";
    private static final String PATH_LOGO_MATHTOOL_ALGORITHMS = "icons/MathToolAlgorithmsLogo.png";
    private static final String PATH_RUN_LOGO = "icons/RunLogo.png";
    private static final String PATH_STOP_LOGO = "icons/StopLogo.png";

    private JMenuBar algorithmsMenuBar;
    private JMenu algorithmsMenuFile;
    private JMenu algorithmsMenuCode;
    private JMenu algorithmsMenuCodeGenerate;
    private JMenu algorithmsMenuCodeGenerateControlStructures;
    private JMenu algorithmsMenuCodeGenerateCommand;
    private JMenuItem algorithmsMenuCodeGenerateMainAlgorithm;
    private JMenuItem algorithmsMenuItemCodeGenerateSubroutine;
    private JMenuItem algorithmsMenuItemOpen;
    private JMenuItem algorithmsMenuItemSave;
    private JMenuItem algorithmsMenuItemQuit;
    private JMenuItem algorithmsMenuItemCodeGenerateIf;
    private JMenuItem algorithmsMenuItemCodeGenerateIfElse;
    private JMenuItem algorithmsMenuItemCodeGenerateWhile;
    private JMenuItem algorithmsMenuItemCodeGenerateDoWhile;
    private JMenuItem algorithmsMenuItemCodeGenerateFor;
    private JMenuItem algorithmsMenuItemCodeGenerateCommandDefine;
    private JMenuItem algorithmsMenuItemCodeGenerateCommandReturn;

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
    private LineNumberHeader lineNumberHeader;
    private JScrollPane outputAreaPane;
    private JTextPane outputArea;

    private JLabel lineNumberCaptionLabel;
    private JLabel lineNumberCounterLabel;

    private static AlgorithmOutputPrinter printer;

    private ImageIcon runIcon;
    private ImageIcon stopIcon;

    private SwingWorker<Void, Void> computingSwingWorker;

    private boolean codeVisible;
    private String code, compiledCode;

    private boolean computing;

    private static final String GUI_MENU_ALGORITHMS = "GUI_MENU_ALGORITHMS";

    private static final String GUI_MathToolAlgorithmsGUI_FILE = "GUI_MathToolAlgorithmsGUI_FILE";
    private static final String GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM = "GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM = "GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_QUIT = "GUI_MathToolAlgorithmsGUI_QUIT";

    private static final String GUI_MathToolAlgorithmsGUI_CODE = "GUI_MathToolAlgorithmsGUI_CODE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_CONTROL_STRUCTURES = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_CONTROL_STRUCTURES";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF_ELSE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF_ELSE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_WHILE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_WHILE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_DO_WHILE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_DO_WHILE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_FOR = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_FOR";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN";

    private static final String GUI_MathToolAlgorithmsGUI_LINE = "GUI_MathToolAlgorithmsGUI_LINE";
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

    public static MathToolAlgorithmsGUI getInstance(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiWidth, int mathtoolGuiHeight) {
        if (instance == null) {
            instance = new MathToolAlgorithmsGUI(mathtoolGuiX, mathtoolGuiY, mathtoolGuiWidth, mathtoolGuiHeight);
        }
        instance.updateGui();
        return instance;
    }

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Editor zum Schreiben von Algorithmencodes.<br>
     * (3) Ausgabefenster.<br>
     * (4) Buttons.<br>
     */
    private MathToolAlgorithmsGUI(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiWidth, int mathtoolGuiHeight) {

        this.computing = false;
        this.codeVisible = false;

        setTitle(Translator.translateOutputMessage(GUI_MENU_ALGORITHMS));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.white);

        int currentComponentLevel;

        // Icon setzen.
        setIconImage(new ImageIcon(getClass().getResource(ICON_PATH)).getImage());
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

            // Hauptmenü erstellen.
            createMenu();

            // Algorithmeneditor mit Header für Zeilennummern definieren.
            this.algorithmEditor = new JTextArea();
            add(this.algorithmEditor);
            this.algorithmEditor.setVisible(true);
            this.algorithmEditor.setBorder(new LineBorder(Color.black, 1));

            this.lineNumberHeader = new LineNumberHeader(algorithmEditor);
            this.lineNumberHeader.updateLineNumbers();
            
            this.algorithmEditorPane = new JScrollPane(this.algorithmEditor,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.algorithmEditorPane.setRowHeaderView(this.lineNumberHeader);
            this.algorithmEditorPane.setBounds(PADDING, 180, this.getWidth() - 2 * PADDING, this.getHeight() - 570);
            this.algorithmEditor.setCaretPosition(this.algorithmEditor.getDocument().getLength());
            add(this.algorithmEditorPane);
            this.algorithmEditorPane.setVisible(true);

            currentComponentLevel += this.algorithmEditorPane.getHeight() + STUB;

            this.algorithmEditor.addKeyListener(new KeyListener() {

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
            });

            this.algorithmEditor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    MathToolAlgorithmsController.unmarkLinesWithInvalidCode();
                    lineNumberHeader.updateLineNumbers();
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    MathToolAlgorithmsController.unmarkLinesWithInvalidCode();
                    lineNumberHeader.updateLineNumbers();
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    MathToolAlgorithmsController.unmarkLinesWithInvalidCode();
                    lineNumberHeader.updateLineNumbers();
                }
            });

            // Label für Zeilenangabe definieren
            this.lineNumberCaptionLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_LINE));
            add(this.lineNumberCaptionLabel);
            this.lineNumberCaptionLabel.setBounds(PADDING, currentComponentLevel, 50, 25);
            this.lineNumberCaptionLabel.setVisible(true);
            this.algorithmEditor.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent ce) {
                    int line = getCursorLineNumber();
                    lineNumberCounterLabel.setText(Integer.toString(line));
                }
            });
            
            this.lineNumberCounterLabel = new JLabel();
            add(this.lineNumberCounterLabel);
            this.lineNumberCounterLabel.setBounds(PADDING + 50, currentComponentLevel, 50, 25);
            this.lineNumberCounterLabel.setVisible(true);

            currentComponentLevel += this.lineNumberCaptionLabel.getHeight() + STUB;

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

            // Ausgabeprinter festlegen.
            printer = AlgorithmOutputPrinter.getInstance();
            printer.setOutputArea(this.outputArea);

            currentComponentLevel += this.outputAreaPane.getHeight() + STUB;

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
                    // Zunächst formatieren.
                    formatCode();
                    String algString = algorithmEditor.getText();
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
                codeVisible = !codeVisible;
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
                algorithmEditor.setEditable(!codeVisible);
            });

            MathToolAlgorithmsController.setMathToolAlgorithmsGUI(this);

            // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
            validate();
            repaint();

        } catch (IOException e) {
        }

    }

    private void createMenu() {
        this.algorithmsMenuBar = new JMenuBar();
        this.algorithmsMenuFile = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FILE));
        this.algorithmsMenuItemOpen = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM));
        this.algorithmsMenuItemSave = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM));
        this.algorithmsMenuItemQuit = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_QUIT));
        this.algorithmsMenuFile.add(this.algorithmsMenuItemOpen);
        this.algorithmsMenuFile.add(this.algorithmsMenuItemSave);
        this.algorithmsMenuFile.add(this.algorithmsMenuItemQuit);
        this.algorithmsMenuBar.add(this.algorithmsMenuFile);
        this.algorithmsMenuCode = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE));
        this.algorithmsMenuCodeGenerate = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE));
        this.algorithmsMenuCodeGenerateMainAlgorithm = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM));
        this.algorithmsMenuItemCodeGenerateSubroutine = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE));
        this.algorithmsMenuCodeGenerateControlStructures = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_CONTROL_STRUCTURES));
        this.algorithmsMenuItemCodeGenerateIf = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF));
        this.algorithmsMenuItemCodeGenerateIfElse = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF_ELSE));
        this.algorithmsMenuItemCodeGenerateWhile = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_WHILE));
        this.algorithmsMenuItemCodeGenerateDoWhile = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_DO_WHILE));
        this.algorithmsMenuItemCodeGenerateFor = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_FOR));
        this.algorithmsMenuCodeGenerateControlStructures.add(this.algorithmsMenuItemCodeGenerateIf);
        this.algorithmsMenuCodeGenerateControlStructures.add(this.algorithmsMenuItemCodeGenerateIfElse);
        this.algorithmsMenuCodeGenerateControlStructures.add(this.algorithmsMenuItemCodeGenerateWhile);
        this.algorithmsMenuCodeGenerateControlStructures.add(this.algorithmsMenuItemCodeGenerateDoWhile);
        this.algorithmsMenuCodeGenerateControlStructures.add(this.algorithmsMenuItemCodeGenerateFor);
        this.algorithmsMenuCodeGenerate.add(this.algorithmsMenuCodeGenerateMainAlgorithm);
        this.algorithmsMenuCodeGenerate.add(this.algorithmsMenuItemCodeGenerateSubroutine);
        this.algorithmsMenuCodeGenerate.add(this.algorithmsMenuCodeGenerateControlStructures);
        this.algorithmsMenuCode.add(this.algorithmsMenuCodeGenerate);
        this.algorithmsMenuBar.add(this.algorithmsMenuCode);
        this.algorithmsMenuCodeGenerateCommand = new JMenu(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND));
        this.algorithmsMenuItemCodeGenerateCommandDefine = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE));
        this.algorithmsMenuItemCodeGenerateCommandReturn = new JMenuItem(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN));
        this.algorithmsMenuCodeGenerateCommand.add(this.algorithmsMenuItemCodeGenerateCommandDefine);
        this.algorithmsMenuCodeGenerateCommand.add(this.algorithmsMenuItemCodeGenerateCommandReturn);
        this.algorithmsMenuCodeGenerate.add(this.algorithmsMenuCodeGenerateCommand);
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

        this.algorithmsMenuCodeGenerateMainAlgorithm.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            MainAlgorithmDefinitionDialogGUI.createMainAlgorithmDefinitionDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateSubroutine.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            SubroutineDefinitionDialogGUI.createSubroutineDefinitionDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateIf.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandControlStructureIfDialogGUI.createCommandControlStructureIfDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateIfElse.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandControlStructureIfElseDialogGUI.createCommandControlStructureIfElseDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateWhile.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandControlStructureWhileDialogGUI.createCommandControlStructureWhileDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateDoWhile.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandControlStructureDoWhileDialogGUI.createCommandControlStructureDoWhileDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateFor.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandControlStructureForDialogGUI.createCommandControlStructureForDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });

        this.algorithmsMenuItemCodeGenerateCommandDefine.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandIdentifierDefinitionDialogGUI.createCommandIdentifierDefinitionDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });
        this.algorithmsMenuItemCodeGenerateCommandReturn.addActionListener((ActionEvent e) -> {
            setAlwaysOnTop(false);
            CommandReturnDialogGUI.createCommandReturnDialog(getX(), getY(), getWidth(), getHeight(), algorithmEditor);
            setAlwaysOnTop(true);
        });

    }

    /**
     * Aktualisiert sämtliche Texte gemäß der aktuellen Sprache.
     */
    private void updateGui() {
        // Titel aktualisieren
        setTitle(Translator.translateOutputMessage(GUI_MENU_ALGORITHMS));

        // Menüeinträge aktualisieren
        this.algorithmsMenuFile.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FILE));
        this.algorithmsMenuItemOpen.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_LOAD_ALGORITHM));
        this.algorithmsMenuItemSave.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SAVE_ALGORITHM));
        this.algorithmsMenuItemQuit.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_QUIT));
        this.algorithmsMenuCode.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE));
        this.algorithmsMenuCodeGenerate.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE));
        this.algorithmsMenuCodeGenerateMainAlgorithm.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM));
        this.algorithmsMenuItemCodeGenerateSubroutine.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE));
        this.algorithmsMenuCodeGenerateControlStructures.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_CONTROL_STRUCTURES));
        this.algorithmsMenuItemCodeGenerateIf.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF));
        this.algorithmsMenuItemCodeGenerateIfElse.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF_ELSE));
        this.algorithmsMenuItemCodeGenerateWhile.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_WHILE));
        this.algorithmsMenuItemCodeGenerateDoWhile.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_DO_WHILE));
        this.algorithmsMenuItemCodeGenerateFor.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_FOR));
        this.algorithmsMenuCodeGenerateCommand.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND));
        this.algorithmsMenuItemCodeGenerateCommandDefine.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE));
        this.algorithmsMenuItemCodeGenerateCommandReturn.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN));

        // Buttontexte aktualisieren
        if (this.computing) {
            this.runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_STOP));
        } else {
            this.runButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
        }
        this.formatButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_FORMAT));
        if (this.codeVisible) {
            this.seeCompiledCodeButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SEE_EDITOR_CODE));
        } else {
            this.seeCompiledCodeButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_SEE_COMPILED_CODE));
        }
    }

    private void formatCode() {
        String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(this.algorithmEditor.getText());
        this.algorithmEditor.setText(formattedCode);
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
                    // Algorithmus kompilieren.
                    printer.clearOutput();
                    printer.printStartParsingAlgorithms();

                    try {
                        AlgorithmBuilder.parseAlgorithmFile(algorithm);
                    } catch (AlgorithmCompileException e) {
                        Integer[] errorLines = e.getErrorLines();
                        MathToolAlgorithmsController.markLinesWithInvalidCode(errorLines);
                        throw e;
                    }

                    saveCompiledCode();
                    printer.printEndParsingAlgorithms();
                    // Algorithmus ausführen.
                    printer.printStartExecutingAlgorithms();
                    algorithmexecuter.AlgorithmExecuter.executeAlgorithm(AlgorithmBuilder.ALGORITHMS.getAlgorithms());
                    printer.printEndExecutingAlgorithms();
                } catch (AlgorithmCompileException | AlgorithmExecutionException | EvaluationException e) {
                    printer.printException(e);
                } catch (Exception e) {
                    printer.printUnexpectedException(e);
                }

                return null;
            }

        };
        this.computingSwingWorker.execute();

    }

    private void saveCompiledCode() {
        List<Algorithm> algorithms = AlgorithmBuilder.ALGORITHMS.getAlgorithms();
        this.compiledCode = MathToolAlgorithmsController.writeCompiledCode(algorithms);
    }

    public boolean isComputing() {
        return computing;
    }

    private int getCursorLineNumber() {
        int caretpos = this.algorithmEditor.getCaretPosition();
        String t = this.algorithmEditor.getText();
        int line = 1;
        for (int i = 0; i < caretpos; i++) {
            if (t.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

}
