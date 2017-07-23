package algorithmexecuter.model.command;

import algorithmexecuter.enums.Keyword;
import algorithmexecuter.exceptions.AlgorithmBreakException;
import algorithmexecuter.exceptions.AlgorithmContinueException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;

public class KeywordCommand extends AlgorithmCommand {

    private final Keyword keyword;

    public KeywordCommand(Keyword keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "KeyWordCommand[keyword = " + this.keyword + "]";
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        if (this.keyword.equals(Keyword.BREAK)) {
            throw new AlgorithmBreakException();
        }
        throw new AlgorithmContinueException();
    }

}
