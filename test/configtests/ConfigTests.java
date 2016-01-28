package configtests;

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
    public void getNumberOfCommasTest(){
        int n = mathtool.MathToolController.getNumberOfCommasForOperators("diff");
        Assert.assertTrue(n == 1);
    }
    
    
}
