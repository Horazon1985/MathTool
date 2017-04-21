package test.algorithm;

import abstractexpressions.expression.classes.Constant;
import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Operator;
import abstractexpressions.expression.classes.TypeOperator;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import exceptions.ExpressionException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AlgorithmExecutionTests {

    @Test
    public void executeEmptyMainAlgorithmTest() {
        List<AlgorithmCommand> commands = new ArrayList<>();
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null, commands);
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(mainAlg);
        try {
            Identifier result = AlgorithmExecutor.executeAlgorithm(algorithms);
            assertTrue(result == null);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void executeSimpleMainAlgorithmTest() {
        List<AlgorithmCommand> commands = new ArrayList<>();
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null, commands);

        try {
            Identifier id = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);
            AlgorithmCommand command = new AssignValueCommand(mainAlg, id, Expression.build("2+5"));
            commands.add(command);
            command = new ReturnCommand(mainAlg, id);
            commands.add(command);
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
        List<AlgorithmCommand> commands = new ArrayList<>();
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null, commands);

        try {
            Identifier idX = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);
            AlgorithmCommand command = new AssignValueCommand(mainAlg, idX, Expression.build("2+5"));
            commands.add(command);
            Identifier idY = Identifier.createIdentifier(mainAlg, "y", IdentifierTypes.MATRIX_EXPRESSION);
            command = new AssignValueCommand(mainAlg, idY, MatrixExpression.build("[0,1;3,x]"));
            commands.add(command);
            command = new ReturnCommand(mainAlg, idY);
            commands.add(command);
        } catch (ExpressionException | AlgorithmCompileException ex) {
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
        List<AlgorithmCommand> commandsMain = new ArrayList<>();
        List<AlgorithmCommand> commandsComputeGgt = new ArrayList<>();
        Algorithm mainAlg = new Algorithm(Keywords.MAIN.getValue(), new Identifier[]{}, null, commandsMain);

        Identifier idA = Identifier.createIdentifier(mainAlg, "a", IdentifierTypes.EXPRESSION);
        Identifier idB = Identifier.createIdentifier(mainAlg, "b", IdentifierTypes.EXPRESSION);
        Identifier idGgt = Identifier.createIdentifier(mainAlg, "ggt", IdentifierTypes.EXPRESSION);
        Algorithm calledAlg = new Algorithm("computeggt", new Identifier[]{idA, idB}, idGgt, commandsComputeGgt);

        Identifier idResult = Identifier.createIdentifier(mainAlg, "x", IdentifierTypes.EXPRESSION);

        try {
            AlgorithmCommand command = new AssignValueCommand(mainAlg, idA, Expression.build("15"));
            commandsMain.add(command);
            command = new AssignValueCommand(mainAlg, idB, Expression.build("25"));
            commandsMain.add(command);
            command = new AssignValueCommand(mainAlg, idGgt, calledAlg);
            commandsMain.add(command);
            command = new ReturnCommand(mainAlg, idGgt);
            commandsMain.add(command);

            command = new AssignValueCommand(calledAlg, idResult, Expression.build("gcd(a,b)"));
            commandsComputeGgt.add(command);
            command = new ReturnCommand(calledAlg, idResult);
            commandsComputeGgt.add(command);

        } catch (ExpressionException | AlgorithmCompileException ex) {
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

}
