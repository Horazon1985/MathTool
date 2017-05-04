package test.algorithm;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.model.Algorithm;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AlgorithmParseTests {

    @Test
    public void preprocessMainTest() {
        String input = "main() {expression    a =     sin( 5)  ;   a = a+   5  ;   }   ";
        String outputFormatted = AlgorithmCompiler.preprocessAlgorithm(input);
        String outputFormattedExpected = "main(){expression a = sin(5);a = a+ 5;}";
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void preprocessAlgorithmTest() {
        String input = "alg(expression   a   ,  expression b) {return  a  ;   }   ";
        String outputFormatted = AlgorithmCompiler.preprocessAlgorithm(input);
        String outputFormattedExpected = "alg(expression a,expression b){return a;}";
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void parseSimpleAlgorithmWithReturnTest() {
        String input = "expression main(){expression a=5;return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            assertEquals(alg.getReturnType(), IdentifierTypes.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 2);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }
    
    @Test
    public void parseSimpleAlgorithmTest() {
        String input = "main(){expression a=5;a=a+5;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            assertEquals(alg.getReturnType(), null);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 2);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithIfElseTest() {
        String input = "expression main(){expression a=3;expression b=5;if(a==3){return a;}else{return b;};}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            assertEquals(alg.getReturnType(), IdentifierTypes.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isAssignValueCommand());
            assertTrue(alg.getCommands().get(2).isIfElseControllStructure());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }
    
    @Test
    public void parseSimpleAlgorithmWithIfElseForMatrixComparisonTest() {
        String input = "matrixexpression main(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];if(a==b){return a;}else{return b;};}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            assertEquals(alg.getReturnType(), IdentifierTypes.MATRIX_EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isAssignValueCommand());
            assertTrue(alg.getCommands().get(2).isIfElseControllStructure());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithUnreachableCodeTest() {
        String input = "expression main(){expression a=exp(1);if(a>2){return a;expression b=a+5;};}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz unerreichbarem Code compiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

    
}
