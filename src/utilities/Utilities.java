package utilities;

public abstract class Utilities {

    /**
     * Berechnet für Operatoren und Befehle die Mindestanzahl der benötigten
     * Kommata bei einer gültigen Eingabe.
     */
    public static int getNumberOfComma(String operatorOrCommandName) {

        if (operatorOrCommandName.equals("diff")) {
            return 1;
        }
        if (operatorOrCommandName.equals("gcd")) {
            return 1;
        }
        if (operatorOrCommandName.equals("int")) {
            return 1;
        }
        if (operatorOrCommandName.equals("lcm")) {
            return 1;
        }
        if (operatorOrCommandName.equals("mod")) {
            return 1;
        }
        if (operatorOrCommandName.equals("prod")) {
            return 3;
        }
        if (operatorOrCommandName.equals("sum")) {
            return 3;
        }
        if (operatorOrCommandName.equals("taylor")) {
            return 3;
        }

        if (operatorOrCommandName.equals("plot2d")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plot3d")) {
            return 4;
        }
        if (operatorOrCommandName.equals("plotcurve")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plotpolar")) {
            return 2;
        }
        if (operatorOrCommandName.equals("solvedeq")) {
            return 5;
        }
        if (operatorOrCommandName.equals("tangent")) {
            return 1;
        }
        if (operatorOrCommandName.equals("taylordeq")) {
            return 5;
        }

        // Default-Case! Alle Operatoren/Befehle, welche höchstens ein Argument benötigen.
        return 0;

    }
    
}
