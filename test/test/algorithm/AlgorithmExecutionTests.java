package test.algorithm;

import abstractexpressions.expression.classes.Constant;
import abstractexpressions.expression.classes.Expression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import exceptions.ExpressionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AlgorithmExecutionTests {

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
            Identifier id = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);
            mainAlg.appendCommand(new AssignValueCommand(id, Expression.build("2+5")));
            mainAlg.appendCommand(new ReturnCommand(id));
        } catch (ExpressionException | AlgorithmCompileException ex) {
            fail();
        }

        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
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
            Identifier idX = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);
            Identifier idY = Identifier.createIdentifier(mainAlg, "y", IdentifierTypes.MATRIX_EXPRESSION);
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
            assertTrue(result.getType() == IdentifierTypes.MATRIX_EXPRESSION);
            assertTrue(result.getName().equals("y"));
            assertTrue(((MatrixExpression) result.getValue()).equals(MatrixExpression.build("[0,1;3,7]")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void executAlgorithmCallingAnotherAlgorithmTest() {
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null);

        Identifier idA = Identifier.createIdentifier(mainAlg, "a", IdentifierTypes.EXPRESSION);
        Identifier idB = Identifier.createIdentifier(mainAlg, "b", IdentifierTypes.EXPRESSION);
        Identifier idGgt = Identifier.createIdentifier(mainAlg, "ggt", IdentifierTypes.EXPRESSION);
        Algorithm calledAlg = new Algorithm("computeggt", new Identifier[]{idA, idB}, IdentifierTypes.EXPRESSION);

        Identifier idResult = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);

        try {
            mainAlg.appendCommand(new AssignValueCommand(idA, Expression.build("15")));
            mainAlg.appendCommand(new AssignValueCommand(idB, Expression.build("25")));
            mainAlg.appendCommand(new AssignValueCommand(idGgt, calledAlg));
            mainAlg.appendCommand(new ReturnCommand(idGgt));

            calledAlg.appendCommand(new AssignValueCommand(idResult, Expression.build("gcd(a,b)")));
            calledAlg.appendCommand(new ReturnCommand(idResult));
        } catch (ExpressionException | AlgorithmCompileException e) {
            fail();
        }

        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
            assertTrue(result.getName().equals("ggt"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("5")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void executeAlgorithmWithIfElseControlStructureTest() {
        String input = "expression main(){expression a=exp(1);if(a>2){return a;};a=a+1;return a;}";
        Algorithm alg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
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
            alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
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
            alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
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
            alg = AlgorithmCompiler.STORED_ALGORITHMS.get(0);
            Identifier result = AlgorithmExecutor.executeAlgorithm(Collections.singletonList(alg));
            assertTrue(result.getType() == IdentifierTypes.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("8")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + alg + " konnte nicht ausgeführt werden.");
        }
    }

}
