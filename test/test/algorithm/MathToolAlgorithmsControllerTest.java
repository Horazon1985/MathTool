package test.algorithm;

import mathtool.component.components.MathToolAlgorithmsController;
import org.junit.Assert;
import org.junit.Test;

public class MathToolAlgorithmsControllerTest {

    @Test
    public void emphasizeWordsTest() {
        String inputSourceCode = "main() {expression x = 5; return x;}";
        String formattedSourceCode = MathToolAlgorithmsController.emphasizeWordsInAlgorithmSourceCode(inputSourceCode);
        Assert.assertEquals("<i>main</i>() {expression x = 5; <b><font color=\"blue\">return</font></b> x;}", formattedSourceCode);
    }

    @Test
    public void emphasizeWordsButNotFalseWordsTest() {
        String inputSourceCode = "main() {expression returnresult = 5; return returnresult;}";
        String formattedSourceCode = MathToolAlgorithmsController.emphasizeWordsInAlgorithmSourceCode(inputSourceCode);
        Assert.assertEquals("<i>main</i>() {expression returnresult = 5; <b><font color=\"blue\">return</font></b> returnresult;}", formattedSourceCode);
    }

    @Test
    public void emphasizeVariousWordsTest() {
        String inputSourceCode = "matrixexpression main(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];if(a==b){return a;}else{return b;}}";
        String formattedSourceCode = MathToolAlgorithmsController.emphasizeWordsInAlgorithmSourceCode(inputSourceCode);
        Assert.assertEquals("matrixexpression <i>main</i>(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];<b><font color=\"blue\">if</font></b>(a==b){<b><font color=\"blue\">return</font></b> a;}<b><font color=\"blue\">else</font></b>{<b><font color=\"blue\">return</font></b> b;}}", formattedSourceCode);
    }


    
}
