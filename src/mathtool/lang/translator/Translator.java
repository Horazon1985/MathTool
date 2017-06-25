package mathtool.lang.translator;

import abstractexpressions.expression.classes.Expression;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mathtool.component.components.ErrorDialogGUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Translator {

    private static final String PREFIX_MATHTOOL_GUI_MESSAGES = "GUI";
    private static final String PREFIX_MATH_COMMAND_COMPILER_MESSAGES = "MCC";
    private static final String PREFIX_ALGORITHM_COMPILLATION_MESSAGES = "AC";
    private static final String PREFIX_ALGORITHM_EXECUTION_MESSAGES = "AE";

    private static final String PATH_LANG_GUI_MESSAGES = "mathtool/lang/messages/LangGUI.xml";
    private static final String PATH_MATH_COMMAND_COMPILER_MESSAGES = "mathtool/lang/messages/LangMathCommandCompiler.xml";
    private static final String PATH_ALGORITHM_COMPILLATION_MESSAGES = "algorithmexecuter/messages/LangAlgorithmCompiler.xml";
    private static final String PATH_ALGORITHM_EXECUTION_MESSAGES = "algorithmexecuter/messages/LangAlgorithmExecuter.xml";

    public static final String PATH_UNKNOWN_ERROR_MESSAGES = "mathtool/lang/messages/LangUndefinedError.xml";

    private static final String ELEMENT_NAME_OBJECT = "object";
    private static final String ELEMENT_ATTRIBUTE_ID = "id";

    private static final String ELEMENT_NAME_DE = "German";
    private static final String ELEMENT_NAME_EN = "English";
    private static final String ELEMENT_NAME_RU = "Russian";
    private static final String ELEMENT_NAME_UA = "Ukrainian";

    private static final String ERROR_TEXT = "Message not found.";

    private static final Map<String, String> RESOURCES = new HashMap<>();

    static {
        RESOURCES.put(PREFIX_MATHTOOL_GUI_MESSAGES, PATH_LANG_GUI_MESSAGES);
        RESOURCES.put(PREFIX_MATH_COMMAND_COMPILER_MESSAGES, PATH_MATH_COMMAND_COMPILER_MESSAGES);
        RESOURCES.put(PREFIX_ALGORITHM_COMPILLATION_MESSAGES, PATH_ALGORITHM_COMPILLATION_MESSAGES);
        RESOURCES.put(PREFIX_ALGORITHM_EXECUTION_MESSAGES, PATH_ALGORITHM_EXECUTION_MESSAGES);
    }

    public static Collection<String> getResources() {
        Collection<String> resources = new HashSet<>();
        /*
        Manipulationen an RESOURCES.values() können Änderungen an der Map 
        RESOURCES nach sich ziehen können. Deshalb wird hier eine Kopie 
        zurückgegeben.
         */
        resources.addAll(RESOURCES.values());
        return resources;
    }

    /**
     * Gibt eine Meldung entsprechend der exceptionId und der eingestellten
     * Sprache zurück.
     */
    private static String translateMessage(String exceptionId) {

        // Die entsprechende XML-Datei öffnen.
        URL langFile = null;
        try {
            for (String key : RESOURCES.keySet()) {
                if (exceptionId.startsWith(key)) {
                    langFile = ClassLoader.getSystemResource(RESOURCES.get(key));
                    break;
                }
            }
            if (langFile == null) {
                // Fall: Unbekannten Fehler aufgetreten (Präfix nicht identifizierbar).
                langFile = ClassLoader.getSystemResource(PATH_UNKNOWN_ERROR_MESSAGES);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(langFile.openStream());

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(ELEMENT_NAME_OBJECT);

            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    if (eElement.getAttribute(ELEMENT_ATTRIBUTE_ID).equals(exceptionId)) {
                        switch (Expression.getLanguage()) {
                            case DE:
                                return eElement.getElementsByTagName(ELEMENT_NAME_DE).item(0).getTextContent();
                            case EN:
                                return eElement.getElementsByTagName(ELEMENT_NAME_EN).item(0).getTextContent();
                            case RU:
                                return eElement.getElementsByTagName(ELEMENT_NAME_RU).item(0).getTextContent();
                            case UA:
                                return eElement.getElementsByTagName(ELEMENT_NAME_UA).item(0).getTextContent();
                            default:
                                break;
                        }
                    }

                }
            }
        } catch (Exception e) {
        }

        // Sollte bei korrekten Fehler-IDs und vorhandenen Fehlerdateien nie eintreten.
        return ERROR_TEXT;

    }

    /**
     * Gibt eine Meldung entsprechend der exceptionId und der eingestellten
     * Sprache zurück, wobei Tokens der Form [0], [1], [2], ... nacheinander
     * durch die Parameter params ersetzt werden.
     */
    public static String translateOutputMessage(String messageId, Object... params) {
        String message = translateMessage(messageId);
        String token;
        for (int i = 0; i < params.length; i++) {
            token = "[" + i + "]";
            while (message.contains(token)) {
                message = message.substring(0, message.indexOf(token)) + params[i] + message.substring(message.indexOf(token) + token.length());
            }
        }
        return message;
    }

}
