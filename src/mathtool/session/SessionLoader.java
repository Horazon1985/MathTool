package mathtool.session;

import mathtool.session.classes.MathToolSession;
import mathtool.session.classes.DefinedFunction;
import mathtool.session.classes.DefinedVar;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.Variable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mathtool.session.classes.Arguments;
import mathtool.session.classes.DefinedFunctions;
import mathtool.session.classes.DefinedVars;

public class SessionLoader {
    
    public static void sessionToXML(String path) {
        
        MathToolSession session = new MathToolSession();

        DefinedVars definedVars = new DefinedVars();
        DefinedFunctions definedFunctions = new DefinedFunctions();
        List<DefinedVar> definedVarsToSet = new ArrayList<>();
        List<DefinedFunction> definedfunctionsToSet = new ArrayList<>();
        DefinedVar definedVar;
        for (String var : Variable.getVariablesWithPredefinedValues()) {
            definedVar = new DefinedVar();
            definedVar.setVarname(var);
            definedVar.setValue(Variable.create(var).getPreciseExpression().toString());
            definedVarsToSet.add(definedVar);
        }
        definedVars.setDefinedVarList(definedVarsToSet);
        DefinedFunction definedfunction;
        Arguments argumentsToSet;
        List<String> arguments;
        for (String function : SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet()) {
            arguments = new ArrayList<>();
            definedfunction = new DefinedFunction();
            definedfunction.setFunctionname(function);
            arguments.addAll(Arrays.asList(SelfDefinedFunction.getArgumentsForSelfDefinedFunctions().get(function)));
            argumentsToSet = new Arguments();
            argumentsToSet.setArguments(arguments);
            definedfunction.setArguments(argumentsToSet);
            definedfunction.setFunctionterm(SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().get(function).toString());
            definedfunctionsToSet.add(definedfunction);
        }
        definedFunctions.setDefinedFunctionList(definedfunctionsToSet);
        
        session.setDefinedVars(definedVars);
        session.setDefinedFunctions(definedFunctions);
        
        try {
            
            File file = new File(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(MathToolSession.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            jaxbMarshaller.marshal(session, file);
//            jaxbMarshaller.marshal(session, System.out);
            
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        
    }
    
    public static MathToolSession loadSession(String path) throws JAXBException {
        File file = new File(path);
        JAXBContext jaxbContext = JAXBContext.newInstance(MathToolSession.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (MathToolSession) jaxbUnmarshaller.unmarshal(file);
    }
    
}
