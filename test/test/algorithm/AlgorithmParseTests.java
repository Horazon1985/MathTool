package test.algorithm;

import algorithmexecutor.AlgorithmCompiler;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AlgorithmParseTests {

    @Test
    public void preprocessTest() {
        String input = "main() {expression    a =      5  ;   a = a+   5  ;   }   ";
        String outputFormatted = AlgorithmCompiler.preprocessAlgorithm(input);
        String outputFormattedExpected = "main(){expression a = 5;a = a+ 5;}";
        System.out.println(outputFormatted);
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

}
