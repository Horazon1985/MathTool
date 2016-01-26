package configtests;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import mathtool.config.ConfigLoader;
import mathtool.config.ConfigLoader;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
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
            ConfigLoader.loadConfig();
        } catch (JAXBException e) {
            fail(e.getMessage());
        }
    }
    
    
    
}
