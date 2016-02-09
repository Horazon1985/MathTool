package test.mathcommandcompiler;

import graphic.GraphicArea;
import javax.swing.JTextArea;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandExecutionTests {

    private static GraphicArea mathToolGraphicArea;
    private static JTextArea mathToolTextArea;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public static void initGraphicArea() throws Exception {
        mathToolTextArea = new JTextArea();
        mathToolGraphicArea = new GraphicArea(0, 0, 100, 100);
    }

    @Test
    public void approxPositiveTest(){
        
        
    }
    
    @Test
    public void approxNegativeTest(){
        
        
    }
    
    
    


    
}
