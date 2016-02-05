package mathtool.component.components;

import mathtool.MathToolGUI;
import components.MathToolOptionComponentTemplate;
import enums.TypeSimplify;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import lang.translator.Translator;

public final class OutputOptionsDialogGUI extends MathToolOptionComponentTemplate {

    private final HashSet<TypeSimplify> mandatorySimplifyTypes = new HashSet<>();
    private HashSet<TypeSimplify> simplifyTypes;

    public OutputOptionsDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, "GUI_OutputOptionsDialogGUI_OUTPUT_OPTIONS_TITLE",
                "../../icons/OutputOptionsLogo.png", numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        loadOptions();
        initMandatorySimplifyTypes();
    }

    private void initMandatorySimplifyTypes() {
        mandatorySimplifyTypes.add(TypeSimplify.order_difference_and_division);
        mandatorySimplifyTypes.add(TypeSimplify.order_sums_and_products);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_trivial);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_collect_products);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_expand_rational_factors);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
        // Für Matrizen
        mandatorySimplifyTypes.add(TypeSimplify.simplify_matrix_entries);
        mandatorySimplifyTypes.add(TypeSimplify.simplify_compute_matrix_operations);
        
    }

    @Override
    public void loadOptions() {
        this.simplifyTypes = MathToolGUI.getSimplifyTypes();
        // Checkboxen füllen.
        for (TypeSimplify type : this.simplifyTypes) {
            for (JCheckBox checkBox : getOptionCheckBoxes()) {
                if (convertSimplifyTypeToOptionName(type).equals(checkBox.getText())) {
                    checkBox.setSelected(true);
                }
            }
            // Einträge in DropDowns auswählen.
            for (JComboBox<String> comboBox : getOptionDropDowns()) {
                for (int i = 0; i < comboBox.getModel().getSize(); i++) {
                    if (comboBox.getModel().getElementAt(i).equals(convertSimplifyTypeToOptionName(type))) {
                        comboBox.setSelectedIndex(i);
                    }
                }
            }
        }
    }

    @Override
    public void saveOptions() {
        setSimplifyTypes();
        MathToolGUI.setSimplifyTypes(simplifyTypes);
    }

    private void setSimplifyTypes() {
        simplifyTypes.clear();
        simplifyTypes.addAll(mandatorySimplifyTypes);
        // Checkboxen
        for (JCheckBox opt : getOptionCheckBoxes()) {
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
        // DropDowns
        for (JComboBox<String> comboBox : getOptionDropDowns()) {
            String option = comboBox.getItemAt(comboBox.getSelectedIndex());
            for (TypeSimplify type : TypeSimplify.values()){
                if (convertSimplifyTypeToOptionName(type).equals(option)){
                    simplifyTypes.add(type);
                }
            }
        }
    }

    private String convertSimplifyTypeToOptionName(TypeSimplify type) {
        if (type.equals(TypeSimplify.simplify_algebraic_expressions)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS");
        }
        if (type.equals(TypeSimplify.simplify_functional_relations)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS");
        }
        if (type.equals(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER");
        }
        if (type.equals(TypeSimplify.simplify_factorize)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE");
        }
        if (type.equals(TypeSimplify.simplify_expand_powerful)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND");
        }
        if (type.equals(TypeSimplify.simplify_collect_logarithms)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS");
        }
        if (type.equals(TypeSimplify.simplify_expand_logarithms)) {
            return Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS");
        }
        return "";
    }

}
