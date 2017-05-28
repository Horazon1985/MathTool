package test.algorithm;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.CompilerUtils;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.WhileControlStructure;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.model.Algorithm;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AlgorithmCompileTests {

    @Test
    public void preprocessMainTest() {
        String input = "main() {expression    a =     sin( 5)  ;   a = a+   5  ;   }   ";
        String outputFormatted = CompilerUtils.preprocessAlgorithm(input);
        String outputFormattedExpected = "main(){expression a=sin(5);a=a+ 5;}";
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void preprocessAlgorithmTest() {
        String input = "alg(expression   a   ,  expression b) {return  a  ;   }   ";
        String outputFormatted = CompilerUtils.preprocessAlgorithm(input);
        String outputFormattedExpected = "alg(expression a,expression b){return a;}";
        assertTrue(outputFormatted.equals(outputFormattedExpected));
    }

    @Test
    public void parseSimpleAlgorithmWithReturnTest() {
        String input = "expression main(){expression a=5;return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
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
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
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
        String input = "expression main(){expression a=3;expression b=5;if(a==3){return a;}else{return b;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isAssignValueCommand());
            assertTrue(alg.getCommands().get(2).isIfElseControlStructure());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithIdentifierDeclarationAndIfElseTest() {
        String input = "expression main(){expression a=2;expression b;if(a==1){b=7;}else{b=13;}return b;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 4);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isDeclareIDentifierCommand());
            assertTrue(alg.getCommands().get(2).isIfElseControlStructure());
            assertTrue(alg.getCommands().get(3).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithIfElseForMatrixComparisonTest() {
        String input = "matrixexpression main(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];if(a==b){return a;}else{return b;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.MATRIX_EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isAssignValueCommand());
            assertTrue(alg.getCommands().get(2).isIfElseControlStructure());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithWhileControlStructureTest() {
        String input = "expression main(){expression a = 1;while(a<6){a = 2*a;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isWhileControlStructure());
            assertTrue(alg.getCommands().get(2).isReturnCommand());
            assertEquals(((WhileControlStructure) alg.getCommands().get(1)).getCommands().size(), 1);
            assertTrue(((WhileControlStructure) alg.getCommands().get(1)).getCommands().get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmCallingAnotherAlgorithmTest() {
        String input = "expression main(){expression a = 15; expression b = 25; expression ggt = computeggt(a, b); return ggt;} "
                + "expression computeggt(expression a, expression b){expression result = gcd(a, b); return result;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg;
            Algorithm ggtAlg;
            if (algorithmList.get(0).getName().equals("main")) {
                mainAlg = algorithmList.get(0);
                ggtAlg = algorithmList.get(1);
            } else {
                mainAlg = algorithmList.get(1);
                ggtAlg = algorithmList.get(0);
            }

            // Prüfung für den Hauptalgorithmus "main".
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 4);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(3).isReturnCommand());

            // Prüfung für den aufgerufenen Algorithmus "computeggt".
            assertEquals(ggtAlg.getName(), "computeggt");
            assertEquals(ggtAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(ggtAlg.getInputParameters().length, 2);
            assertEquals(ggtAlg.getCommands().size(), 2);
            assertTrue(ggtAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(ggtAlg.getCommands().get(1).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmCallingAnotherAlgorithmInOneAssignmentTest() {
        String input = "expression main(){expression ggt = computeggt(15, 25)*exp(2); return ggt;} "
                + "expression computeggt(expression a, expression b){expression result = gcd(a, b); return result;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg;
            Algorithm ggtAlg;
            if (algorithmList.get(0).getName().equals("main")) {
                mainAlg = algorithmList.get(0);
                ggtAlg = algorithmList.get(1);
            } else {
                mainAlg = algorithmList.get(1);
                ggtAlg = algorithmList.get(0);
            }

            // Prüfung für den Hauptalgorithmus "main".
            assertEquals(mainAlg.getCommands().size(), 5);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(0)).getTargetExpression() != null);
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(1)).getTargetExpression() != null);
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertEquals(ggtAlg, ((AssignValueCommand) mainAlg.getCommands().get(2)).getTargetAlgorithm());
            assertTrue(mainAlg.getCommands().get(3).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(3)).getTargetExpression() != null);
            assertTrue(mainAlg.getCommands().get(4).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithCompileErrorCodeTest() {
        String input = "main(){expression a=exp(1)}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz fehlerhaftem Code kompiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

    @Test
    public void parseAlgorithmWithUnreachableCodeTest() {
        String input = "expression main(){expression a=exp(1);if(a>2){return a;expression b=a+5;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz unerreichbarem Code kompiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

    @Test
    public void parseAlgorithmWithoutReturnCommandTest() {
        String input = "expression main(){expression x=2;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz fehlendem 'return' kompiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

}
