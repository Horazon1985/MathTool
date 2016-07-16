package mathtool.utilities;

import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.output.EditableAbstractExpression;
import abstractexpressions.output.EditableString;
import java.util.ArrayList;

/**
 * Utility-Klasse.
 */
public abstract class MathToolUtilities {

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * Array zurück, in dem der übergebene Ausdruck das erste Attribut und ein
     * 'true' das zweite Attribut ist. Das 'true' markiert, dass der übergebene
     * Ausdruck bearbeitbar / kopierbar sein soll.
     */
    public static EditableAbstractExpression convertToEditableAbstractExpression(AbstractExpression out) {
        return new EditableAbstractExpression(out);
    }

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * Array zurück, in dem der übergebene Ausdruck das erste Attribut und ein
     * 'true' das zweite Attribut ist. Das 'true' markiert, dass der übergebene
     * Ausdruck bearbeitbar / kopierbar sein soll.
     */
    public static EditableAbstractExpression[] convertToEditableAbstractExpression(AbstractExpression... out) {
        EditableAbstractExpression[] editableAbstractExpressions = new EditableAbstractExpression[out.length];
        for (int i = 0; i < out.length; i++) {
            editableAbstractExpressions[i] = new EditableAbstractExpression(out[i]);
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

    /**
     * Hilfsmethode für die Darstellung einer grafischen Ausgabe. Liefert ein
     * EditableString zurück, in dem das übergebene Objekt als String das erste
     * Attribut und ein 'true' das zweite Attribut ist. Das 'true' markiert,
     * dass der übergebene Text bearbeitbar / kopierbar sein soll.
     */
    public static EditableString convertToEditableString(Object out) {
        return new EditableString(out.toString());
    }

}
