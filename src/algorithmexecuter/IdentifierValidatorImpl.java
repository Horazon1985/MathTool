package algorithmexecuter;

import abstractexpressions.interfaces.IdentifierValidator;
import algorithmexecuter.enums.FixedAlgorithmNames;
import algorithmexecuter.enums.Keyword;
import java.math.BigInteger;

public class IdentifierValidatorImpl implements IdentifierValidator {

    /**
     * Prüft, ob der Name identifier ein gültiger Bezeichner ist. Gültig
     * bedeutet, dass er entweder<br>
     * (1) die Form #i, i >= 1 besitzt (technischer Identifier), oder <br>
     * (2) nur aus Groß- und Kleinbuchstaben, Ziffern 0 bis 9 und dem
     * Unterstrich '_' bestehen darf (aber selbst keine ganze Zahl ist). Es darf
     * aber keine ganze Zahl sein.
     */
    @Override
    public boolean isValidIdentifier(String identifierName) {
        // Prüfung, ob es kein Keyword ist.
        for (Keyword keyword : Keyword.values()) {
            if (keyword.getValue().equals(identifierName)) {
                return false;
            }
        }
        // Prüfung, ob es kein fester Algorithmenname ist.
        for (FixedAlgorithmNames name : FixedAlgorithmNames.values()) {
            if (name.getValue().equals(identifierName)) {
                return false;
            }
        }
        // Fall (1).
        if (identifierName.startsWith(CompilerUtils.GEN_VAR)) {
            try {
                Integer.valueOf(identifierName.substring(1));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        // Fall (2): Prüfung, ob es nur zulässige Zeichen enthält.
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

}
