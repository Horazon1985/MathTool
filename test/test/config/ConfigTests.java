package test.config;

import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import mathtool.config.ConfigLoader;
import mathtool.config.MathToolConfig;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigTests {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void exportConfigTest(){
        ConfigLoader.configToXML();
    }

    @Test
    public void loadConfigTest(){
        try {
            MathToolConfig config = ConfigLoader.loadConfig();
        } catch (JAXBException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void getNumberOfCommasForOperatorTest(){
        int n = mathtool.MathToolController.getNumberOfCommasForOperators("cov");
        Assert.assertTrue(n == 0);
        n = mathtool.MathToolController.getNumberOfCommasForOperators("diff");
        Assert.assertTrue(n == 1);
        n = mathtool.MathToolController.getNumberOfCommasForOperators("int");
        Assert.assertTrue(n == 1);
        n = mathtool.MathToolController.getNumberOfCommasForOperators("mu");
        Assert.assertTrue(n == 0);
        n = mathtool.MathToolController.getNumberOfCommasForOperators("sum");
        Assert.assertTrue(n == 3);
    }
    
    @Test
    public void getNumberOfCommasForCommandsTest(){
        int n = mathtool.MathToolController.getNumberOfCommasForCommands("clear");
        Assert.assertTrue(n == 0);
        n = mathtool.MathToolController.getNumberOfCommasForCommands("eigenvalues");
        Assert.assertTrue(n == 0);
        n = mathtool.MathToolController.getNumberOfCommasForCommands("regressionline");
        Assert.assertTrue(n == 1);
        n = mathtool.MathToolController.getNumberOfCommasForCommands("solve");
        Assert.assertTrue(n == 0);
        n = mathtool.MathToolController.getNumberOfCommasForCommands("solvesystem");
        Assert.assertTrue(n == 1);
    }
    
}
