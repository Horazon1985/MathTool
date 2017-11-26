package test.algorithm;

import abstractexpressions.expression.classes.Variable;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.model.command.AssignValueCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.constants.AlgorithmCompileExceptionIds;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.Signature;
import algorithmexecuter.model.command.ControlStructure;
import algorithmexecuter.model.command.DoWhileControlStructure;
import algorithmexecuter.model.command.ForControlStructure;
import algorithmexecuter.model.command.ReturnCommand;
import algorithmexecuter.model.command.VoidCommand;
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
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 2);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmTest() {
        String input = "main(){expression a=5;a=a+5;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), null);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 2);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithReturnTypeStringTest() {
        String input = "string main(){expression a=3;string s=\"a hat den Wert \"+a;return s;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.STRING);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertEquals(2, ((AssignValueCommand) mainAlg.getCommands().get(1)).getMalString().getStringValues().length);
            assertEquals("a hat den Wert ", ((AssignValueCommand) mainAlg.getCommands().get(1)).getMalString().getStringValues()[0]);
            assertEquals(Variable.create("a"), ((AssignValueCommand) mainAlg.getCommands().get(1)).getMalString().getStringValues()[1]);
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals("s", ((ReturnCommand) mainAlg.getCommands().get(2)).getIdentifier().getName());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithPrintCommandTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	print(\"Wert: \"+a);\n"
                + "	return a;\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 4);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isVoidCommand());
            assertEquals(((VoidCommand) mainAlg.getCommands().get(2)).getName(), "print");
            assertEquals(((VoidCommand) mainAlg.getCommands().get(2)).getIdentifiers().length, 1);
            assertEquals(((VoidCommand) mainAlg.getCommands().get(2)).getIdentifiers()[0].getType(), IdentifierType.STRING);
            assertTrue(mainAlg.getCommands().get(3).isReturnCommand());
            assertEquals("a", ((ReturnCommand) mainAlg.getCommands().get(3)).getIdentifier().getName());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseSimpleAlgorithmWithIfElseTest() {
        String input = "expression main(){expression a=3;expression b=5;if(a==3){return a;}else{return b;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isIfElseControlStructure());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithIfElseTest() {
        String input = "expression main(){expression a=3;expression b=5;if(a==3){expression c=8;return a;}else{expression c=10;return b;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isIfElseControlStructure());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().size(), 2);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().get(0).isAssignValueCommand());
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().get(1).isReturnCommand());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().size(), 2);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().get(0).isAssignValueCommand());
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().get(1).isReturnCommand());
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
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.BREAK));
        }
    }

    @Test
    public void parseSimpleAlgorithmWithIfElseForMatrixComparisonTest() {
        String input = "matrixexpression main(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];if(a==b){return a;}else{return b;}}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.MATRIX_EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isIfElseControlStructure());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().size(), 1);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsIfPart().get(0).isReturnCommand());
            assertEquals(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().size(), 1);
            assertTrue(((IfElseControlStructure) mainAlg.getCommands().get(2)).getCommandsElsePart().get(0).isReturnCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmUsageInConditionTest() {
        String input = "expression main(){expression a=4;expression b=6;if(ggt(a,b)==2){return 5;}else{return 7;}} "
                + "expression ggt(expression a, expression b){return gcd(a, b);}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
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
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isWhileControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals(((WhileControlStructure) mainAlg.getCommands().get(1)).getCommands().size(), 1);
            assertTrue(((WhileControlStructure) mainAlg.getCommands().get(1)).getCommands().get(0).isAssignValueCommand());
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
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
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isDoWhileControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
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
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isForControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
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
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isForControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());
            assertEquals(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].size(), 2);
            assertTrue(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
            assertTrue(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(1).isIfElseControlStructure());
            assertEquals(((ControlStructure) ((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) ((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(1)).getCommandBlocks()[0].get(0).isKeywordCommand());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }

    @Test
    public void parseAlgorithmWithForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0, i<7, i=i+1){a=a+i^2;}expression i=10;return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            assertEquals(mainAlg.getReturnType(), IdentifierType.EXPRESSION);
            assertEquals(mainAlg.getName(), "main");
            assertEquals(mainAlg.getInputParameters().length, 0);
            assertEquals(mainAlg.getCommands().size(), 4);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isForControlStructure());
            assertTrue(mainAlg.getCommands().get(2).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(3).isReturnCommand());
            assertEquals(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].size(), 1);
            assertTrue(((ControlStructure) mainAlg.getCommands().get(1)).getCommandBlocks()[0].get(0).isAssignValueCommand());
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            Algorithm ggtAlg;
            if (algorithmList.get(0).getName().equals("computeggt")) {
                ggtAlg = algorithmList.get(0);
            } else {
                ggtAlg = algorithmList.get(1);
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS), ggtAlg = null, myggtAlg = null;
            for (Algorithm alg : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                switch (alg.getName()) {
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS), ggtAlg;
            if (algorithmList.get(0).getName().equals("computeggt")) {
                ggtAlg = algorithmList.get(0);
            } else {
                ggtAlg = algorithmList.get(1);
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

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
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
    public void parseAlgorithmWithAlgorithmCallContainingStringArgumentTest1() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	string s=\"Aufruf\"; \n"
                + "	f(s);\n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "f(string s){\n"
                + "	print(s);\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            Algorithm printAlg;
            if (algorithmList.get(0).getName().equals("f")) {
                printAlg = algorithmList.get(0);
            } else {
                printAlg = algorithmList.get(1);
            }

            // Prüfung für den Hauptalgorithmus "main" und den Algorithmus f.
            assertEquals(mainAlg.getCommands().size(), 4);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isVoidCommand());
            assertTrue(mainAlg.getCommands().get(3).isReturnCommand());

            assertTrue(printAlg.getCommands().get(0).isVoidCommand());
            assertEquals(((VoidCommand) printAlg.getCommands().get(0)).getSignature(),
                    new Signature(null, "print", new IdentifierType[]{IdentifierType.STRING}));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        }
    }
    
    @Test
    public void parseAlgorithmWithAlgorithmCallContainingStringArgumentTest2() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	string s=f(a); \n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "string f(expression a){\n"
                + "	return \"Test!\";\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);

            Algorithm mainAlg = CompilerUtils.getMainAlgorithm(AlgorithmCompiler.ALGORITHMS);
            Algorithm stringAlg;
            if (algorithmList.get(0).getName().equals("f")) {
                stringAlg = algorithmList.get(0);
            } else {
                stringAlg = algorithmList.get(1);
            }

            // Prüfung für den Hauptalgorithmus "main" und den Algorithmus f.
            assertEquals(mainAlg.getCommands().size(), 3);
            assertTrue(mainAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(1).isAssignValueCommand());
            assertTrue(mainAlg.getCommands().get(2).isReturnCommand());

            assertTrue(stringAlg.getCommands().get(0).isAssignValueCommand());
            assertTrue(stringAlg.getCommands().get(1).isReturnCommand());
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
    public void parseAlgorithmWithoutReturnCommandTest1() {
        String input = "expression main(){expression x=2;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz fehlendem 'return' kompiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

    @Test
    public void parseAlgorithmWithoutReturnCommandTest2() {
        String input = "expression main(){expression result=1;if(result>0){result=3*result;}}";
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
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_IDENTIFIER_ALREADY_DEFINED, "a"));
        }
    }

    @Test
    public void parseAlgorithmWithWrongForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7){a=a+i^2;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz doppelt fehlerhafter For-Struktur kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_EXPECTED, ","));
        }
    }

    @Test
    public void parseAlgorithmWithWrongForLoopWithFourCommandsInHeaderTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7,i++,i=i+7){a=a+i^2;}return a;}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz doppelt fehlerhafter For-Struktur kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ")"));
        }
    }

    @Test
    public void parseAlgorithmWithWrongAssignmentTest() {
        String input = "matrixexpression main(){\n"
                + "	matrixexpression a=[3,0;-2,1];\n"
                + "	for(expression i=0,i<4,i=i+1){\n"
                + "		a=3*a+2;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz fehlerhafter Zuweisung kompiliert.");
        } catch (AlgorithmCompileException e) {
        }
    }

    @Test
    public void parseAlgorithmWithNotNecessaryDefinedVariableTest() {
        String input = "expression main(){\n"
                + "	expression a;\n"
                + "	expression b;\n"
                + "	if (true) {;\n"
                + "          a=10;\n"
                + "          b=5;\n"
                + "	}else{\n"
                + "          b=7;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz nicht initialisierter Variable kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_IDENTIFIER_MAYBE_NOT_INITIALIZED, "a"));
        }
    }

    @Test
    public void parseAlgorithmWithNotNecessaryDefinedVariableAndWhileLoopTest() {
        String input = "expression main(){\n"
                + "	expression a;\n"
                + "	expression b=1;\n"
                + "	while (b<3) {;\n"
                + "          a=1;\n"
                + "          b=b+1;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            fail("Der Algorithmus " + input + " wurde trotz nicht initialisierter Variable kompiliert.");
        } catch (AlgorithmCompileException e) {
            assertEquals(e.getMessage(), Translator.translateOutputMessage(AlgorithmCompileExceptionIds.AC_IDENTIFIER_MAYBE_NOT_INITIALIZED, "a"));
        }
    }

}
