package test.algorithm;

import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.model.command.AssignValueCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.constants.CompileExceptionTexts;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.command.ControlStructure;
import algorithmexecuter.model.command.DoWhileControlStructure;
import algorithmexecuter.model.command.ForControlStructure;
import java.util.List;
import mathtool.lang.translator.Translator;
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
    public void preprocessNonTrivialAlgorithmTest() {
        String input = "expression main(){expression a=5;do  {a=a+1;}   while(a<10);return a;}";
        String outputFormatted = CompilerUtils.preprocessAlgorithm(input);
        String outputFormattedExpected = "expression main(){expression a=5;do{a=a+1;}while(a<10);return a;}";
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
    public void parseAlgorithmWithIfElseTest() {
        String input = "expression main(){expression a=3;expression b=5;if(a==3){expression c=8;return a;}else{expression c=10;return b;}}";
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
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().size(), 2);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(0).isAssignValueCommand());
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsIfPart().get(1).isReturnCommand());
            assertEquals(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().size(), 2);
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(0).isAssignValueCommand());
            assertTrue(((IfElseControlStructure) alg.getCommands().get(2)).getCommandsElsePart().get(1).isReturnCommand());
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
    public void parseAlgorithmWithIfElseAndBreakTest() {
        String input = "expression main(){expression a=2;expression b;if(a==1){b=7;}else{b=13;break;}return b;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail(input + " konnte geparst werden, obwohl es Compilerfehler enthielt.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(CompileExceptionTexts.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.BREAK));
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
    public void parseAlgorithmWithAlgorithmUsageInConditionTest() {
        String input = "expression main(){expression a=4;expression b=6;if(ggT(a,b)==2){return 5;}else{return 7;}} "
                + "expression ggt(expression a, expression b){return gcd(a, b);}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg;
            if (algorithmList.get(0).getName().equals("main")) {
                mainAlg = algorithmList.get(0);
            } else {
                mainAlg = algorithmList.get(1);
            }

            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 4);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(3).isIfElseControlStructure());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithWhileLoopTest() {
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
    public void parseAlgorithmWithAlgorithmCallInWhileConditionTest() {
        String input = "expression main(){expression a = 1;while(f(a)*g(a)<6){a=a+1;}return a;} "
                + "expression f(expression a) {return a-1;} "
                + "expression g(expression a) {return a+1;} ";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 3);

            Algorithm mainAlg = null;
            for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                if (alg.getName().equals("main")) {
                    mainAlg = alg;
                    break;
                }
            }

            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getCommands().size(), 5);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(3).isWhileControlStructure());
            assertTrue(mainAlg.getCommands().get(4).isReturnCommand());
            assertEquals(((WhileControlStructure) mainAlg.getCommands().get(3)).getCommands().size(), 3);
            assertTrue(((WhileControlStructure) mainAlg.getCommands().get(3)).getCommands().get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithDoWhileLoopTest() {
        String input = "expression main(){expression a=5;do{a=a+1;}while(a<10);return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isDoWhileControlStructure());
            assertTrue(alg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmCallInDoWhileConditionTest() {
        String input = "expression main(){expression a = 1;do{a=a+1;}while(f(a)*g(a)<6);return a;} "
                + "expression f(expression a) {return a-1;} "
                + "expression g(expression a) {return a+1;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 3);

            Algorithm mainAlg = null;
            for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                if (alg.getName().equals("main")) {
                    mainAlg = alg;
                    break;
                }
            }

            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getCommands().size(), 5);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(3).isDoWhileControlStructure());
            assertTrue(mainAlg.getCommands().get(4).isReturnCommand());
            assertEquals(((DoWhileControlStructure) mainAlg.getCommands().get(3)).getCommands().size(), 3);
            assertTrue(((DoWhileControlStructure) mainAlg.getCommands().get(3)).getCommands().get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }
    
    @Test
    public void parseSimpleAlgorithmWithForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0, i<7, i=i+1){a=a+i^2;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isForControlStructure());
            assertTrue(alg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmCallInForConditionTest() {
        String input = "expression main(){expression a = 1;for(expression i=0,f(i)<10,i=i+1){a=3*a+1;}return a;} "
                + "expression f(expression a) {return a-1;} ";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg = null;
            for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                if (alg.getName().equals("main")) {
                    mainAlg = alg;
                    break;
                }
            }

            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isForControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals(((ForControlStructure) mainAlg.getCommands().get(1)).getCommands().size(), 1);
            assertTrue(((ForControlStructure) mainAlg.getCommands().get(1)).getCommands().get(0).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }
    
    @Test
    public void parseAlgorithmWithForLoopAndBreakTest() {
        String input = "expression main(){expression a=5;for(expression i=0, i<7, i=i+1){a=a+i^2; if (i==5){break;}}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 3);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isForControlStructure());
            assertTrue(alg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].size(), 2);
            assertTrue(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
            assertTrue(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(1).isIfElseControlStructure());
            assertEquals(((ControlStructure) ((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) ((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(1)).getCommandBlocks()[0].get(0).isKeywordCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0, i<7, i=i+1){a=a+i^2;}expression i=10;return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            assertEquals(alg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(alg.getName(), "main");
            assertEquals(alg.getInputParameters().length, 0);
            assertEquals(alg.getCommands().size(), 4);
            assertTrue(alg.getCommands().get(0).isAssignValueCommand());
            assertTrue(alg.getCommands().get(1).isForControlStructure());
            assertTrue(alg.getCommands().get(2).isAssignValueCommand());
            assertTrue(alg.getCommands().get(3).isReturnCommand());
            assertEquals(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) alg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
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

            Algorithm mainAlg, ggtAlg;
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
    public void parseAlgorithmCallingTheCorrectAlgorithmTest() {
        String input = "expression main(){expression res = 5*myggt(10,14)*7; return res;} "
                + "expression ggt(expression a, expression b){expression result = gcd(a, b); return result;} "
                + "expression myggt(expression a, expression b){expression result = gcd(a, b); return result;} ";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 3);

            Algorithm mainAlg = null, ggtAlg = null, myggtAlg = null;
            for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                switch (alg.getName()) {
                    case "main":
                        mainAlg = alg;
                        break;
                    case "ggt":
                        ggtAlg = alg;
                        break;
                    case "myggt":
                        myggtAlg = alg;
                        break;
                    default:
                        break;
                }
            }

            assertTrue(mainAlg != null);
            assertTrue(ggtAlg != null);
            assertTrue(myggtAlg != null);

            // Prüfung für den Hauptalgorithmus "main".
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 5);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(0)).getTargetAlgorithm() == null);
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(1)).getTargetAlgorithm() == null);
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertEquals(((AssignValueCommand) mainAlg.getCommands().get(2)).getTargetAlgorithm(), myggtAlg);
            assertTrue(mainAlg.getCommands().get(3).isAssignValueCommand());
            assertTrue(((AssignValueCommand) mainAlg.getCommands().get(3)).getTargetAlgorithm() == null);
            assertTrue(mainAlg.getCommands().get(4).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmCallingAnotherAlgorithmInOneAssignmentTest() {
        String input = "expression main(){expression ggt = computeggt(15, 25)*exp(2)*computeggt(15, 25); return ggt;} "
                + "expression computeggt(expression a, expression b){expression result = gcd(a, b); return result;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg, ggtAlg;
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
    public void parseAlgorithmWithNonTrivialReturnCommandTest() {
        String input = "expression main(){expression a = 3; return 7*a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 1);

            Algorithm mainAlg = algorithmList.get(0);

            // Prüfung für den Hauptalgorithmus "main".
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
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

    @Test
    public void parseAlgorithmWithDoubledParametersTest() {
        String input = "expression main(){expression ggt = computeggt(15, 25); return ggt;} "
                + "expression computeggt(expression a, expression a){return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz doppelt vorkommender Parameter in einem Algorithmusheader kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED, "a"));
        }
    }

    @Test
    public void parseAlgorithmWithWrongForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7){a=a+i^2;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz doppelt fehlerhafter For-Struktur kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(CompileExceptionTexts.AC_EXPECTED, ","));
        }
    }

    @Test
    public void parseAlgorithmWithWrongForLoopWithFourCommandsInHeaderTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7,i++,i=i+7){a=a+i^2;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz doppelt fehlerhafter For-Struktur kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(CompileExceptionTexts.AC_BRACKET_EXPECTED, ")"));
        }
    }

}
