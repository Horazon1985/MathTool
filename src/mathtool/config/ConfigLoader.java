package mathtool.config;

import enums.TypeLanguage;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mathtool.enums.TypeMode;

public class ConfigLoader {

    /**
     * WIRD NOCH NICHT VERWENDET!
     */
//    public static void configToXML() {
//
//        MathToolConfig config = new MathToolConfig();
//        
//        GeneralSettings generalSettings = new GeneralSettings();
//        generalSettings.setFontSizeGraphic(18);
//        generalSettings.setFontSizeGraphic(12);
//        generalSettings.setLanguage(TypeLanguage.EN);
//        generalSettings.setMode(TypeMode.TEXT);
//
//        ScreenSettings screenSettings = new ScreenSettings();
//        screenSettings.setMinWidth(1200);
//        screenSettings.setMinHeight(670);
//        
//        OptionSettings optionSettings = new OptionSettings();
//        optionSettings.setAlgebraicRelations(true);
//        optionSettings.setFunctionalRelations(true);
//        optionSettings.setExpandAndCollectIfShorter(true);
//        optionSettings.setFactorizeDropDownOption(OptionSettings.FactorizeDropDownOption.factorize);
//        optionSettings.setLogarithmsDropDownOption(OptionSettings.LogarithmsDropDownOption.collect);
//
//        config.setGeneralSettings(generalSettings);
//        config.setScreenSettings(screenSettings);
//        config.setOptionSettings(optionSettings);
//        
//        try {
//
//            File file = new File("C:\\file.xml");
//            JAXBContext jaxbContext = JAXBContext.newInstance(MathToolConfig.class);
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//            // output pretty printed
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            jaxbMarshaller.marshal(config, file);
//            jaxbMarshaller.marshal(config, System.out);
//
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static MathToolConfig loadConfig() throws JAXBException {
        File file = new File("src/mathtool/config/MathToolConfig.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(MathToolConfig.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (MathToolConfig) jaxbUnmarshaller.unmarshal(file);
    }

}
