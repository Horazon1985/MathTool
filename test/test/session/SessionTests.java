package test.session;

import abstractexpressions.expression.classes.Expression;
import static abstractexpressions.expression.classes.Expression.ONE;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.Variable;
import java.util.HashMap;
import mathtool.session.SessionLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SessionTests {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void exportSessionTest(){
        
        Variable.create("x", ONE.div(2));
        Variable.create("y", ONE.sin().add(5));
        
        HashMap<String, Expression> abstractExpressions = new HashMap<>();
        HashMap<String, String[]> arguments = new HashMap<>();
        arguments.put("f", new String[]{"x", "y"});
        arguments.put("g", new String[]{"u"});
        abstractExpressions.put("f", Variable.create("x").add(Variable.create("y").pow(2)));
        abstractExpressions.put("g", Variable.create("u").pow(5).sin());
        SelfDefinedFunction f = new SelfDefinedFunction("f", arguments.get("f"), abstractExpressions.get("f"), null);
        SelfDefinedFunction g = new SelfDefinedFunction("g", arguments.get("g"), abstractExpressions.get("g"), null);
        SelfDefinedFunction.createSelfDefinedFunction(f);
        SelfDefinedFunction.createSelfDefinedFunction(g);
        
        SessionLoader.sessionToXML("C:\\file.xml");
        
    }
    
}
