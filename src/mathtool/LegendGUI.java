package mathtool;

import components.MathToolInfoComponentTemplate;
import java.awt.Color;
import java.util.ArrayList;

public class LegendGUI extends MathToolInfoComponentTemplate {

    public LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {
        
        super(mathtoolformX, mathtoolformY,
                mathtoolformWidth, mathtoolformHeight,
                "GUI_LegendGUI_LEGEND", "icons/LegendLogo.png",
                instructions, exprs, colors,
                new ArrayList<String>(), new ArrayList<String>());        
        
//        setTitle(Translator.translateExceptionMessage("GUI_LegendGUI_LEGEND"));
//        setLayout(null);
//        setResizable(false);
//        setModal(true);
//
//        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
//                (mathtoolformHeight - 145 - 20 * instructions.length - 20 * colors.size()) / 2 + mathtoolformY,
//                500, 145 + 20 * instructions.length + 20 * colors.size());
//        this.getContentPane().setBackground(Color.white);
//
//        JLabel instruction = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_CONTROLS") + "</u></b></html>");
//        instruction.setBounds(10, 70, 470, 25);
//        add(instruction);
//
//        JLabel[] instr = new JLabel[instructions.length];
//        for (int i = 0; i < instr.length; i++) {
//            instr[i] = new JLabel();
//            instr[i].setText(instructions[i]);
//            instr[i].setBounds(10, 90 + 20 * i, 470, 25);
//            add(instr[i]);
//        }
//
//        JLabel graphs = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_GRAPHS") + "</u></b></html>");
//        graphs.setBounds(10, 90 + 20 * instructions.length, 470, 25);
//        add(graphs);
//
//        JLabel[] colorLabels = new JLabel[colors.size()];
//        for (int i = 0; i < colorLabels.length; i++) {
//            colorLabels[i] = new JLabel();
//            colorLabels[i].setForeground(colors.get(i));
//            colorLabels[i].setText(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + exprs.get(i).writeExpression());
//            colorLabels[i].setBounds(10, 110 + 20 * instructions.length + 20 * i, 470, 25);
//            add(colorLabels[i]);
//        }
//
//        // Logo laden
//        JPanel panel = new JPanel();
//        add(panel);
//        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/LegendLogo.png"))));
//        panel.setBounds(0, -5, 500, 60);
//        panel.setVisible(true);
//        validate();
//        repaint();
                        
    }

//    /**
//     * Legende für Graphen impliziter Funktionen.
//     */
//    public LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
//            String[] instructions, Color color, Expression expr) {
//        setTitle(Translator.translateExceptionMessage("GUI_LegendGUI_LEGEND"));
//        setLayout(null);
//        setResizable(false);
//        setModal(true);
//
//        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
//                (mathtoolformHeight - 165 - 20 * instructions.length) / 2 + mathtoolformY,
//                500, 165 + 20 * instructions.length);
//        this.getContentPane().setBackground(Color.white);
//
//        JLabel instruction = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_CONTROLS") + "</u></b></html>");
//        instruction.setBounds(10, 70, 470, 25);
//        add(instruction);
//
//        JLabel[] instr = new JLabel[instructions.length];
//        for (int i = 0; i < instr.length; i++) {
//            instr[i] = new JLabel();
//            instr[i].setText(instructions[i]);
//            instr[i].setBounds(10, 90 + 20 * i, 470, 25);
//            add(instr[i]);
//        }
//
//        JLabel graphs = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_GRAPHS") + "</u></b></html>");
//        graphs.setBounds(10, 90 + 20 * instructions.length, 470, 25);
//        add(graphs);
//
//        JLabel colorLabel = new JLabel();
//        colorLabel.setForeground(color);
//        colorLabel.setText(Translator.translateExceptionMessage("GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION") + expr.writeExpression() + " = 0");
//        colorLabel.setBounds(10, 110 + 20 * instructions.length, 470, 25);
//        add(colorLabel);
//
//        /**
//         * Logo laden
//         */
//        JPanel panel = new JPanel();
//        add(panel);
//        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/LegendLogo.png"))));
//        panel.setBounds(0, -5, 500, 60);
//        panel.setVisible(true);
//        validate();
//        repaint();
//    }
//
//    public LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
//            String[] instructions, Expression expr) {
//        setTitle(Translator.translateExceptionMessage("GUI_LegendGUI_LEGEND"));
//        setLayout(null);
//        setResizable(false);
//        setModal(true);
//
//        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
//                (mathtoolformHeight - 165 - 20 * instructions.length) / 2 + mathtoolformY,
//                500, 165 + 20 * instructions.length);
//        this.getContentPane().setBackground(Color.white);
//
//        JLabel instruction = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_CONTROLS") + "</u></b></html>");
//        instruction.setBounds(10, 70, 470, 25);
//        add(instruction);
//
//        JLabel[] instr = new JLabel[instructions.length];
//        for (int i = 0; i < instr.length; i++) {
//            instr[i] = new JLabel();
//            instr[i].setText(instructions[i]);
//            instr[i].setBounds(10, 90 + 20 * i, 470, 25);
//            add(instr[i]);
//        }
//
//        JLabel graph = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + "</u></b></html>");
//        graph.setBounds(10, 90 + 20 * instructions.length, 470, 25);
//        add(graph);
//
//        JLabel exprLabel = new JLabel(expr.writeExpression());
//        exprLabel.setBounds(10, 110 + 20 * instructions.length, 470, 25);
//        add(exprLabel);
//
//        /**
//         * Logo laden
//         */
//        JPanel panel = new JPanel();
//        add(panel);
//        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/LegendLogo.png"))));
//        panel.setBounds(0, -5, 500, 60);
//        panel.setVisible(true);
//        validate();
//        repaint();
//    }
//
//    public LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
//            String[] instructions, Expression[] expr) {
//        setTitle(Translator.translateExceptionMessage("GUI_LegendGUI_LEGEND"));
//        setLayout(null);
//        setResizable(false);
//        setModal(true);
//
//        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
//                (mathtoolformHeight - 165 - 20 * instructions.length - 20 * expr.length) / 2 + mathtoolformY,
//                500, 165 + 20 * instructions.length + 20 * expr.length);
//        this.getContentPane().setBackground(Color.white);
//
//        JLabel instruction = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_CONTROLS") + "</u></b></html>");
//        instruction.setBounds(10, 70, 470, 25);
//        add(instruction);
//
//        JLabel[] instr = new JLabel[instructions.length];
//        for (int i = 0; i < instr.length; i++) {
//            instr[i] = new JLabel();
//            instr[i].setText(instructions[i]);
//            instr[i].setBounds(10, 90 + 20 * i, 470, 25);
//            add(instr[i]);
//        }
//
//        JLabel graph = new JLabel("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_PARAMETERIZED_CURVE") + "</u></b></html>");
//        graph.setBounds(10, 90 + 20 * instructions.length, 470, 25);
//        add(graph);
//
//        JLabel[] exprLabels = new JLabel[expr.length];
//        for (int i = 0; i < expr.length; i++) {
//            exprLabels[i] = new JLabel((char) (120 + i) + " = " + expr[i].writeExpression());
//            exprLabels[i].setBounds(10, 110 + 20 * instructions.length + 20 * i, 470, 25);
//            add(exprLabels[i]);
//        }
//
//        /**
//         * Logo laden
//         */
//        JPanel panel = new JPanel();
//        add(panel);
//        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/LegendLogo.png"))));
//        panel.setBounds(0, -5, 500, 60);
//        panel.setVisible(true);
//        validate();
//        repaint();
//    }

}
