package test.algorithm;

import abstractexpressions.expression.classes.Expression;
import algorithmexecuter.AlgorithmCompiler;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.booleanexpression.BooleanConstant;
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
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
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
        String input = "expression main(){\n"
                + "	expression a=15;\n"
                + "	expression b=25;\n"
                + "	expression ggt=computeggt(a,b);\n"
                + "	return ggt;\n"
                + "}\n"
                + "\n"
                + "expression computeggt(expression a,expression b){\n"
                + "	expression result=gcd(a,b);\n"
                + "	return result;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            List<Algorithm> algorithmList = AlgorithmCompiler.ALGORITHMS.getAlgorithmStorage();
            assertEquals(algorithmList.size(), 2);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
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
    public void executeAlgorithmsWithStringReturnTypeTest() {
        String input = "string main(){expression a=3;string s=\"a hat den Wert \"+a;return s;}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.STRING);
            assertTrue(result.getName().equals("s"));
            assertEquals(1, result.getStringValue().length);
            assertEquals("a hat den Wert 3", result.getStringValue()[0]);
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmsWithIfElseControlStructureTest() {
        String input = "expression main(){\n"
                + "	expression a=exp(1);\n"
                + "	if(a>2){\n"
                + "		return a;\n"
                + "	}\n"
                + "	a=a+1;\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){\n"
                + "	expression a=exp(1);\n"
                + "	expression b=7;\n"
                + "	if(a>b){\n"
                + "		return a;\n"
                + "	}\n"
                + "	a=a+1;\n"
                + "	return a;\n"
                + "}";
        mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){\n"
                + "	expression a=exp(1);\n"
                + "	if(a>2){\n"
                + "		a=a+1;\n"
                + "		expression b=2;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("1+exp(1)")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }

        input = "expression main(){\n"
                + "	booleanexpression a=false;\n"
                + "	if(a==true){\n"
                + "		return 5;\n"
                + "	}\n"
                + "	return 7;\n"
                + "}";
        mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().startsWith("#"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("7")));
        } catch (AlgorithmCompileException e) {
            fail("Der Algorithmus " + input + " konnte nicht kompiliert werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithBooleanExpressionTest() {
        String input = "booleanexpression main(){\n"
                + "	expression a=1;\n"
                + "	booleanexpression b=a>=1/2;\n"
                + "	if(b){\n"
                + "		return true;\n"
                + "	}\n"
                + "	return false;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.BOOLEAN_EXPRESSION);
            assertTrue(result.getName().equals("#1"));
            assertTrue(((BooleanConstant) result.getValue()).getValue());
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithIdentifierDeclarationAndIfElseTest() {
        String input = "expression main(){\n"
                + "	expression a=2;\n"
                + "	expression b;\n"
                + "	if(a==1){\n"
                + "		b=7;\n"
                + "	}else{\n"
                + "		b=13;\n"
                + "	}\n"
                + "	return b;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("b"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("13")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithWhileControlStructureTest() {
        String input = "expression main(){\n"
                + "	expression a=1;\n"
                + "	while(a<6){\n"
                + "		a=2*a;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("8")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmCallInWhileConditionTest() {
        String input = "expression main(){\n"
                + "	expression a=1;\n"
                + "	while(f(a)*g(a)<=24){\n"
                + "		a=a+1;\n"
                + "	}\n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "expression f(expression a){\n"
                + "	return a-1;\n"
                + "}\n"
                + "\n"
                + "expression g(expression a){\n"
                + "	return a+1;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("6")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithDoWhileControlStructureTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	do{\n"
                + "		a=a+1;\n"
                + "	}\n"
                + "	while(a<10);\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("10")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void parseAlgorithmWithAlgorithmCallInDoWhileConditionTest() {
        String input = "expression main(){\n"
                + "	expression a=1;\n"
                + "	do{\n"
                + "		a=a+1;\n"
                + "	}\n"
                + "	while(f(a)*g(a)<6);\n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "expression f(expression a){\n"
                + "	return a-1;\n"
                + "}\n"
                + "\n"
                + "expression g(expression a){\n"
                + "	return a+1;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("3")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeSimpleAlgorithmWithForLoopTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	for(expression i=0,i<7,i=i+1){\n"
                + "		a=a+i^2;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("96")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithAlgorithmCallsInForLoopTest() {
        String input = "expression main(){\n"
                + "	expression a=1;\n"
                + "	for(expression i=0,f(i)<=10,i=g(i)){\n"
                + "		a=3*a+2;\n"
                + "	}\n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "expression f(expression i){\n"
                + "	return 2*i;\n"
                + "}\n"
                + "\n"
                + "expression g(expression i){\n"
                + "	return i^2+1;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("161")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithForLoopAndBreakTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	for(expression i=0,i<7,i=i+1){\n"
                + "		a=a+i^2;\n"
                + "		if(i==4){\n"
                + "			break;\n"
                + "		}\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("35")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithForLoopAndContinueTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	for(expression i=0,i<7,i=i+1){\n"
                + "		if(i<4){\n"
                + "			continue;\n"
                + "		}\n"
                + "		a=a+i^2;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("82")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeAlgorithmWithWhileLoopAndNewAssignmentInsideLoopTest() {
        String input = "expression main(){\n"
                + "	expression a=5;\n"
                + "	while(a<8){\n"
                + "		expression b=1;\n"
                + "		a=a+b;\n"
                + "	}\n"
                + "	return a;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("8")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

    @Test
    public void executeEuclideanAlgorithmTest() {
        String input = "expression main(){\n"
                + "	expression a=ggt(68,51);\n"
                + "	return a;\n"
                + "}\n"
                + "\n"
                + "expression ggt(expression a,expression b){\n"
                + "	if(a<b){\n"
                + "		return ggt(b,a);\n"
                + "	}\n"
                + "	expression r=mod(a,b);\n"
                + "	while(r!=0){\n"
                + "		expression c=a;\n"
                + "		a=b;\n"
                + "		b=mod(c,b);\n"
                + "             r=mod(a,b);"
                + "	}\n"
                + "	return b;\n"
                + "}";
        Algorithm mainAlg = null;
        try {
            AlgorithmCompiler.parseAlgorithmFile(input);
            mainAlg = AlgorithmCompiler.ALGORITHMS.getMainAlgorithm();
            Identifier result = AlgorithmExecuter.executeAlgorithm(Collections.singletonList(mainAlg));
            assertTrue(result.getType() == IdentifierType.EXPRESSION);
            assertTrue(result.getName().equals("a"));
            assertTrue(((Expression) result.getValue()).equals(Expression.build("17")));
        } catch (AlgorithmCompileException e) {
            fail(input + " konnte nicht geparst werden.");
        } catch (Exception e) {
            fail("Der Algorithmus " + mainAlg + " konnte nicht ausgeführt werden.");
        }
    }

}
