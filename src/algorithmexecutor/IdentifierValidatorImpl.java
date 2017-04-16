package algorithmexecutor;

import abstractexpressions.interfaces.IdentifierValidator;

public class IdentifierValidatorImpl implements IdentifierValidator {

    /**
     * Prüft, ob der Name identifier ein gültiger Bezeichner ist. Gültig
     * bedeutet, dass er nur aus Groß- und Kleinbuchstaben, Ziffern 0 bis 9 und
     * dem Unterstrich '_' bestehen darf.
     */
    @Override
    public boolean isValidIdentifier(String identifier) {
        int asciiValue;
        for (int i = 0; i < identifier.length(); i++) {
            asciiValue = (int) identifier.charAt(i);
            if (!(asciiValue >= 97 && asciiValue <= 122 
                    || asciiValue >= 65 && asciiValue <= 90 
                    || asciiValue >= 48 && asciiValue <= 57
                    || asciiValue == 95)) {
                return false;
            }
        }
        return true;
    }
    
}
