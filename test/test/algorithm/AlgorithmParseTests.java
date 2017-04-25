package test.algorithm;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AlgorithmParseTests {

    @Test
    public void preprocessMainTest() {
        String input = "main() {expression    a =     sin( 5)  ;   a = a+   5  ;   }   ";
        String outputFormatted = AlgorithmCompiler.preprocessAlgorithm(input);
        String outputFormattedExpected = "main(){expression a = sin(5);a = a+ 5;}";
        System.out.println(outputFormatted);
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void preprocessAlgorithmTest() {
        String input = "alg(expression   a   ,  expression b) {return  a  ;   }   ";
        String outputFormatted = AlgorithmCompiler.preprocessAlgorithm(input);
        String outputFormattedExpected = "alg(expression a,expression b){return a;}";
        System.out.println(outputFormatted);
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void parseSimpleAlgorithmTest() {
        String input = "main(){expression a = 5;a = a+ 5;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
        } catch (AlgorithmCompileException ex) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

}
