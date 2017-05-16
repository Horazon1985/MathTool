package algorithmexecutor;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.enums.Keywords;
import java.math.BigInteger;
import java.util.Map;

public class IdentifierConditionValidatorImpl implements IdentifierConditionValidator {

    /**
     * Prüft, ob der Name identifier ein gültiger Bezeichner ist. Gültig
     * bedeutet, dass er nur aus Groß- und Kleinbuchstaben, Ziffern 0 bis 9 und
     * dem Unterstrich '_' bestehen darf. Es darf aber keine ganze Zahl sein.
     */
    @Override
    public boolean isValidIdentifier(String identifierName) {
        // Prüfung, ob es kein Keyword ist.
        for (Keywords keyword : Keywords.values()) {
            if (keyword.getValue().equals(identifierName)) {
                return false;
            }
        }
        // Prüfung, ob es nur zulässige Zeichen enthält.
        int asciiValue;
        for (int i = 0; i < identifierName.length(); i++) {
            asciiValue = (int) identifierName.charAt(i);
            if (!(asciiValue >= 97 && asciiValue <= 122
                    || asciiValue >= 65 && asciiValue <= 90
                    || asciiValue >= 48 && asciiValue <= 57
                    || asciiValue == 95)) {
                return false;
            }
        }
        // Prüfung, ob identifier keine ganze Zahl ist.
        try {
            new BigInteger(identifierName);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    @Override
    public boolean isValidIdentifier(String identifierName, Map<String, AbstractExpression> valuesMap) {
        return valuesMap.containsKey(identifierName);
    }

}
