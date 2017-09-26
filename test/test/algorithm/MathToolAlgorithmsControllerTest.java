package test.algorithm;

import mathtool.component.controller.MathToolAlgorithmsController;
import org.junit.Assert;
import org.junit.Test;

public class MathToolAlgorithmsControllerTest {

    @Test
    public void formatTest() {
        String inputSourceCode = "main() {expression x = 5; return x;}";
        String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(inputSourceCode);
        Assert.assertEquals(formattedCode, "main(){\n"
                + "\texpression x=5;\n"
                + "\treturn x;\n"
                + "}\n");
    }

    @Test
    public void formatTest2() {
        String inputSourceCode = "main() {expression returnresult = 5; return returnresult;}";
        String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(inputSourceCode);
        Assert.assertEquals(formattedCode, "main(){\n"
                + "\texpression returnresult=5;\n"
                + "\treturn returnresult;\n"
                + "}\n");
    }

    @Test
    public void formatTest3() {
        String inputSourceCode = "matrixexpression main(){matrixexpression a=[1,1;2,-5]*[3;4];matrixexpression b=[7;15];if(a==b){return a;}else{return b;}}";
        String formattedCode = MathToolAlgorithmsController.formatSourceCodeFromEditor(inputSourceCode);
//        Assert.assertEquals(formattedCode, "matrixexpression main(){\n"
//                + "\tmatrixexpression a=[1,1;\n"
//                + "\t2,-5]*[3;\n"
//                + "\t4];\n"
//                + "\tmatrixexpression b=[7;\n"
//                + "\t15];\n"
//                + "\tif(a==b){\n"
//                + "\t\treturn a;\n"
//                + "\t}\n"
//                + "\telse{\n"
//                + "\treturn b;\n"
//                + "\t}\n"
//                + "}\n");
    }

}
