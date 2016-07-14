package mathtool.utilities;

import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.interfaces.EditableAbstractExpression;
import java.util.ArrayList;

/**
 * Utility-Klasse.
 */
public abstract class MathToolUtilities {

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * Array zurück, in dem das übergebene Objekt das erste Element und ein
     * 'true' das zweite Element ist. Das 'true' markiert, dass das übergebene
     * Objekt bearbeitbar / kopierbar sein soll.
     */
    public static EditableAbstractExpression convertToEditableAbstractExpression(AbstractExpression out) {
        return new EditableAbstractExpression(out, true);
    }

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * Array zurück, in dem das übergebene Objekt das erste Element und ein
     * 'true' das zweite Element ist. Das 'true' markiert, dass das übergebene
     * Objekt bearbeitbar / kopierbar sein soll.
     */
    public static EditableAbstractExpression[] convertToEditableAbstractExpression(AbstractExpression... out) {
        EditableAbstractExpression[] editableAbstractExpressions = new EditableAbstractExpression[out.length];
        for (int i = 0; i < out.length; i++) {
            editableAbstractExpressions[i] = new EditableAbstractExpression(out[i], true);
        }
        return editableAbstractExpressions;
    }

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * ArrayList von Arrays zurück, in dem jeder Ausdruck zu einem bearbeitbaren
     * / kopierbaren Ausdruck konvertiert wurde.
     */
    public static ArrayList<EditableAbstractExpression> convertToEditableAbstractExpression(ArrayList<AbstractExpression> outList) {
        ArrayList<EditableAbstractExpression> convertedOutputs = new ArrayList<>();
        for (AbstractExpression abstrExpr : outList) {
            convertedOutputs.add(MathToolUtilities.convertToEditableAbstractExpression(abstrExpr));
        }
        return convertedOutputs;
    }

}
