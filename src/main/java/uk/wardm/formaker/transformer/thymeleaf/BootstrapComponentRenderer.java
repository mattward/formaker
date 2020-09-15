package uk.wardm.formaker.transformer.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.unbescape.html.HtmlEscape;
import uk.wardm.formaker.model.*;

import java.util.Map;


/**
 * A Boostrap CSS orientated implementation of {@link ComponentRenderer}.
 *
 * Customise the look and feel with {@link BootstrapConfig}.
 */
public class BootstrapComponentRenderer extends AbstractComponentRenderer {
    private final BootstrapConfig config;

    public BootstrapComponentRenderer() {
        super();
        this.config = new BootstrapConfig();
    }

    public BootstrapComponentRenderer(boolean renderRendererWarnings, BootstrapConfig config) {
        super(renderRendererWarnings);
        this.config = config;
    }

    @Override
    protected void beforeComponent(IModelFactory modelFactory, IModel model, Component component) {
        if (isFieldSet(component)) {
            Map<String, String> attrs = config.getFieldSetAttrs();
            model.add(modelFactory.createOpenElementTag("fieldset", attrs, AttributeValueQuotes.DOUBLE, false));
        }
        else {
            Map<String, String> attrs = config.getFormGroupAttrs();
            model.add(modelFactory.createOpenElementTag("div", attrs, AttributeValueQuotes.DOUBLE, false));
        }
    }

    @Override
    protected void afterComponent(IModelFactory modelFactory, IModel model, Component component) {
        if (isFieldSet(component)) {
            model.add(modelFactory.createCloseElementTag("fieldset"));
        }
        else {
            model.add(modelFactory.createCloseElementTag("div")); // form-group
        }
    }


    /**
     * Does the field get rendered out as a non-atomic (compound) field?
     * If so, it will need to be in a fieldset with a legend, as labels shouldn't
     * be used to label a set of fields.
     */
    protected boolean isFieldSet(Component component) {
        if (component instanceof ChoiceField) {
            ChoiceField choice = (ChoiceField) component;
            return choice.getStyle() == ChoiceStyle.RADIO || choice.getStyle() == ChoiceStyle.CHECK_BOX;
        }
        else {
            return false;
        }
    }

    @Override
    protected void renderFieldLabel(ITemplateContext context, IModelFactory modelFactory, IModel model, InputField inputField) {
        String id = inputField.getName();
        String labelText = resolveMessage(context, inputField.getLabel(), inputField.getName());
        // label or legend?
        if (isFieldSet(inputField)) {
            model.add(modelFactory.createOpenElementTag("legend"));
            model.add(modelFactory.createText(HtmlEscape.escapeHtml5(labelText)));
            model.add(modelFactory.createCloseElementTag("legend"));
        }
        else {
            model.add(modelFactory.createOpenElementTag("label", "for", HtmlEscape.escapeHtml5(id)));
            model.add(modelFactory.createText(HtmlEscape.escapeHtml5(labelText)));
            model.add(modelFactory.createCloseElementTag("label"));
        }
    }

    @Override
    protected void renderFieldErrors(IModelFactory modelFactory, IModel model, InputField inputField) {
        Map<String, String> errorAttrs = config.getFieldErrorsAttrs();
        errorAttrs.put("th:errors", "*{" + inputField.getName() + "}");
        model.add(modelFactory.createOpenElementTag("div", errorAttrs, AttributeValueQuotes.DOUBLE, false));
        model.add(modelFactory.createCloseElementTag("div"));
    }

    @Override
    protected void renderTextBox(TextBoxField inputField, String placeholderText, IModelFactory modelFactory, IModel model) {
        Map<String, String> attrs = config.getTextBoxAttrs();
        attrs.put("placeholder", HtmlEscape.escapeHtml5(placeholderText));
        attrs.put("th:field", "*{" + inputField.getName() + "}");
        model.add(modelFactory.createOpenElementTag("textarea", attrs, AttributeValueQuotes.DOUBLE, false));
        model.add(modelFactory.createCloseElementTag("textarea"));
    }

    @Override
    protected void renderTextField(InputField inputField, String placeholderText, IModelFactory modelFactory, IModel model) {
        String typeAttr = htmlInputType(inputField);
        Map<String, String> attrs = typeAttr.equals("range") ?
                config.getRangeControlAttrs() :
                config.getTextInputAttrs();
        attrs.put("type", typeAttr);
        attrs.put("id", inputField.getName());
        if (inputField instanceof NumberField) {
            NumberField numberField = (NumberField) inputField;
            attrs.put("min", numberField.getMin() != null ? numberField.getMin().toString() : null);
            attrs.put("max", numberField.getMax() != null ? numberField.getMax().toString() : null);
        }

        attrs.put("placeholder", HtmlEscape.escapeHtml5(placeholderText));
        attrs.put("th:field", "*{" + inputField.getName() + "}");

        model.add(modelFactory.createStandaloneElementTag("input", attrs, AttributeValueQuotes.DOUBLE, false, false));
    }

    @Override
    protected void renderChoiceField(ChoiceField choiceField, IModelFactory modelFactory, IModel model, ITemplateContext context) {
        if (choiceField.getStyle() == ChoiceStyle.SELECT) {
            Map<String, String> attrs = config.getSelectAttrs();
            attrs.put("th:field", "*{" + choiceField.getName() + "}");
            model.add(modelFactory.createOpenElementTag("select", attrs, AttributeValueQuotes.DOUBLE, false));
            for (ChoiceField.Option option : choiceField.getOptions()) {
                // TODO: escaping... everywhere!?!?
                String fqOptionLabelKey = choiceField.getLabel() + ".labels." + option.getLabelKey();
                String optionValueAsText = String.valueOf(option.getValue());
                String optionText = resolveMessage(context, fqOptionLabelKey, optionValueAsText);
                model.add(modelFactory.createOpenElementTag("option", "value", optionValueAsText));
                model.add(modelFactory.createText(optionText));
                model.add(modelFactory.createCloseElementTag("option"));
            }
            model.add(modelFactory.createCloseElementTag("select"));
        }
        else if (choiceField.getStyle() == ChoiceStyle.RADIO) {
            int index = 0;
            for (ChoiceField.Option option : choiceField.getOptions()) {

                // renderFieldLabel should be a <legend> for this
                String fqOptionLabelKey = choiceField.getLabel() + ".labels." + option.getLabelKey();
                String optionValueAsText = String.valueOf(option.getValue());
                String optionId = choiceField.getName() + index;

                model.add(modelFactory.createOpenElementTag("div", config.getRadioWrapperAttrs(),
                        AttributeValueQuotes.DOUBLE, false));

                Map<String, String> attrs = config.getRadioAttrs();
                attrs.put("th:field", "*{" + choiceField.getName() + "}");
                attrs.put("id", optionId);
                attrs.put("type", "radio");
                attrs.put("value", HtmlEscape.escapeHtml5(optionValueAsText));
                model.add(modelFactory.createStandaloneElementTag("input", attrs, AttributeValueQuotes.DOUBLE, false, true));

                Map<String, String> labelAttrs = config.getRadioLabelAttrs();
                labelAttrs.put("for", optionId);
                model.add(modelFactory.createOpenElementTag("label", labelAttrs, AttributeValueQuotes.DOUBLE, false));
                String optionText = resolveMessage(context, fqOptionLabelKey, optionValueAsText);
                model.add(modelFactory.createText(optionText));
                model.add(modelFactory.createCloseElementTag("label"));

                model.add(modelFactory.createCloseElementTag("div")); // radio wrapper/custom-control
                index++;
            }
        }
        else {
            throw new UnsupportedOperationException("Not yet supported: " + choiceField.getStyle());
        }
    }

}
