package test.algorithm;

import abstractexpressions.expression.classes.Expression;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.output.AlgorithmOutputPrinter;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextPane;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgorithmExecutionTests {

    @BeforeClass
    public static void init() {
        AlgorithmOutputPrinter.setOutputArea(new JTextPane());
    }

    @Before
    public void teardown() {
        AlgorithmOutputPrinter.clearOutput();
    }

    @Test
    public void executeEmptyMainAlgorithmTest() {
        String input = "main(){}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 1);
            mainAlg = algorithmList.get(0);
            assertTrue(mainAlg.getCommands().isEmpty());
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result == null);
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void parseAlgorithmCallingAnotherAlgorithmTest() {
        String input = "expression main(){expression a = 15; expression b = 25; expression ggt = computeggt(a, b); return ggt;} "
                + "expression computeggt(expression a, expression b){expression result = gcd(a, b); return result;}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);
            if (algorithmList.get(0).getName().equals("main")) {
                mainAlg = algorithmList.get(0);
            } else {
                mainAlg = algorithmList.get(1);
            }
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("ggt"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("5")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmsWithIfElseControlStructureTest() {
        String input = "expression main(){expression a=exp(1);if(a>2){return a;}a=a+1;return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){expression a=exp(1);expression b=7;if(a>b){return a;}a=a+1;return a;}";
        alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){expression a=exp(1);if(a>2){a=a+1;expression b=2;}return a;}";
        alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){booleanexpression a=false;if(a==true){return 5;}return 7;}";
        alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().startsWith("#"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("7")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithIdentifierDeclarationAndIfElseTest() {
        String input = "expression main(){expression a=2;expression b;if(a==1){b=7;}else{b=13;}return b;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("b"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("13")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithWhileControlStructureTest() {
        String input = "expression main(){expression a = 1;while(a<6){a = 2*a;};return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("8")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }
    
    @Test
    public void parseAlgorithmWithAlgorithmCallInWhileConditionTest() {
        String input = "expression main(){expression a = 1;while(f(a)*g(a)<=24){a=a+1;}return a;} "
                + "expression f(expression a) {return a-1;} "
                + "expression g(expression a) {return a+1;}";
            Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            
            for (Algorithm algorithm : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                if (algorithm.getName().equals("main")) {
                    alg = algorithm;
                    break;
                }
            }

            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("6")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }
    
    @Test
    public void executeAlgorithmWithDoWhileControlStructureTest() {
        String input = "expression main(){expression a=5;do{a=a+1;}while(a<10);return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("10")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmCallInDoWhileConditionTest() {
        String input = "expression main(){expression a = 1;do{a=a+1;}while(f(a)*g(a)<6);return a;} "
                + "expression f(expression a) {return a-1;} "
                + "expression g(expression a) {return a+1;}";
            Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            
            for (Algorithm algorithm : AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage()) {
                if (algorithm.getName().equals("main")) {
                    alg = algorithm;
                    break;
                }
            }

            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("3")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }
    
    @Test
    public void executeSimpleAlgorithmWithForLoopTest() {
        String input = "expression main(){expression a=5;for(expression i=0, i<7, i=i+1){a=a+i^2;}return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("96")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithAlgorithmCallsInForLoopTest() {
        String input = "expression main(){expression a = 1;for(expression i=0,f(i)<=10,i=g(i)){a=3*a+2;}return a;} "
                + "expression f(expression i) {return 2*i;} "
                + "expression g(expression i) {return i^2+1;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("161")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }
    
    @Test
    public void executeAlgorithmWithForLoopAndBreakTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7,i=i+1){a=a+i^2;if(i==4){break;}}return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("35")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }
    
    @Test
    public void executeAlgorithmWithForLoopAndContinueTest() {
        String input = "expression main(){expression a=5;for(expression i=0,i<7,i=i+1){if(i<4){continue;}a=a+i^2;}return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("82")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithWhileLoopAndAssignmentTest() {
        String input = "expression main(){expression a=5;while(a<8){expression b = 1; a=a+b;}return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("8")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

}
