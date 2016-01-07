package mathtool;

import components.MathToolOptionComponentTemplate;
import expressionbuilder.TypeSimplify;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JCheckBox;
import translator.Translator;

public class OutputOptionsDialogGUI extends MathToolOptionComponentTemplate {

    private HashSet<TypeSimplify> mandatorySimplifyTypes = new HashSet<>();
    private HashSet<TypeSimplify> simplifyTypes;

    public OutputOptionsDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, "GUI_OutputOptionsDialogGUI_OUTPUT_OPTIONS_TITLE",
                "icons/OutputOptionsLogo.png", numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        loadOptions();
        initMandatorySimplifyTypes();
    }

    private void initMandatorySimplifyTypes() {
        mandatorySimplifyTypes.add(TypeSimplify.order_difference_and_division);
        mandatorySimplifyTypes.add(TypeSimplify.order_sums_and_products);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_trivial);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_collect_products);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_expand_rational_factors);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
    }

    @Override
    public void loadOptions() {
        this.simplifyTypes = MathToolGUI.getSimplifyTypes();
    }

    @Override
    public void saveOptions() {
        setSimplifyTypes();
        MathToolGUI.setSimplifyTypes(simplifyTypes);
    }

    private void setSimplifyTypes() {
        simplifyTypes.clear();
        simplifyTypes.addAll(mandatorySimplifyTypes);
        for (JCheckBox opt : getOptionLabels()) {
            if (opt.getText().equals(Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS"))
                    && opt.isSelected()) {
                simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
            } else if (opt.getText().equals(Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS"))
                    && opt.isSelected()) {
                simplifyTypes.add(TypeSimplify.simplify_functional_relations);
            } else if (opt.getText().equals(Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER"))
                    && opt.isSelected()) {
                simplifyTypes.add(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
            }
        }
    }

}
