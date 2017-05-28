package test.algorithm;

import abstractexpressions.expression.classes.Constant;
import abstractexpressions.expression.classes.Expression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.output.AlgorithmOutputPrinter;
import exceptions.ExpressionException;
import java.util.ArrayList;
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
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null);
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result == null);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void executeSimpleMainAlgorithmTest() {
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null);

        try {
            Identifier id = Identifier.createIdentifier(mainAlg, "x", IdentifierType.EXPRESSION);
            mainAlg.appendCommand(new AssignValueCommand(id, Expression.build("2+5")));
            mainAlg.appendCommand(new ReturnCommand(id));
        } catch (ExpressionException | AlgorithmCompileException ex) {
            fail();
        }

        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("x"));
            assertTrue(((Expression) result.getValue()).equals(new Constant(7)));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void executeAnotherSimpleMainAlgorithmTest() {
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null);

        try {
            Identifier idX = Identifier.createIdentifier(mainAlg, "x", IdentifierType.EXPRESSION);
            Identifier idY = Identifier.createIdentifier(mainAlg, "y", IdentifierType.MATRIX_EXPRESSION);
            mainAlg.appendCommand(new AssignValueCommand(idX, Expression.build("2+5")));
            mainAlg.appendCommand(new AssignValueCommand(idY, MatrixExpression.build("[0,1;3,x]")));
            mainAlg.appendCommand(new ReturnCommand(idY));
        } catch (ExpressionException | AlgorithmCompileException e) {
            fail();
        }

        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result.getType() == IdentifierType.MATRIX_EXPRESSION);
            assertTrue(result.getName().equals("y"));
            assertTrue(((MatrixExpression) result.getValue()).equals(MatrixExpression.build("[0,1;3,7]")));
        } catch (Exception e) {
            fail();
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
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(mainAlg));
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
    public void executeAlgorithmWithIfElseControlStructureTest() {
        String input = "expression main(){expression a=exp(1);if(a>2){return a;};a=a+1;return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){expression a=exp(1);expression b=7;if(a>b){return a;};a=a+1;return a;}";
        alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){expression a=exp(1);if(a>2){a=a+1;expression b=2;};return a;}";
        alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage().get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
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
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
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
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
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
