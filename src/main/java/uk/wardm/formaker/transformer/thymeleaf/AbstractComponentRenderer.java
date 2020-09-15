package uk.wardm.formaker.transformer.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import uk.wardm.formaker.model.*;

import java.util.Arrays;

/**
 * An abstract Spring-orientated Thymeleaf {@link ComponentRenderer}.
 *
 * Use a concrete subclass such as {@link BootstrapComponentRenderer} or create your own
 * implementation for specialised logic or markup.
 */
public abstract class AbstractComponentRenderer implements ComponentRenderer {
    private final boolean renderRendererWarnings;

    /**
     * Constructor providing configurable values as parameters.
     *
     * @param renderRendererWarnings  Whether to render warnings in the page, rather than ignore them.
     *                                For example, if it wasn't possible to render a field.
     */
    public AbstractComponentRenderer(boolean renderRendererWarnings) {
        this.renderRendererWarnings = renderRendererWarnings;
    }

    /**
     * Default constructor
     */
    public AbstractComponentRenderer() {
        this(true);
    }

    @Override
    public IModel render(ITemplateContext context, Component component) {
        IModelFactory modelFactory = context.getModelFactory();
        if (component instanceof InputField) {
            IModel model = modelFactory.createModel();

            InputField inputField = (InputField) component;

            // Prologue, or start of wrapper
            beforeComponent(modelFactory, model, component);

            // Field contents
            renderFieldLabel(context, modelFactory, model, inputField);
            renderInputControl(context, modelFactory, model, inputField);
            renderFieldErrors(modelFactory, model, inputField);

            // Epilogue, or end of wrapper
            afterComponent(modelFactory, model, component);

            return model;
        }
        else {
            IModel model = modelFactory.createModel();

            if (renderRendererWarnings) {
                model.add(modelFactory.createOpenElementTag("p"));
                model.add(modelFactory.createText("Unable to render field for " + component.getClass()));
                model.add(modelFactory.createCloseElementTag("p"));
            }
            return model;
        }
    }


    protected abstract void beforeComponent(IModelFactory modelFactory, IModel model, Component component);

    protected abstract void afterComponent(IModelFactory modelFactory, IModel model, Component component);

    protected abstract void renderFieldLabel(ITemplateContext context, IModelFactory modelFactory,
                                             IModel model, InputField inputField);

    protected void renderInputControl(ITemplateContext context, IModelFactory modelFactory, IModel model, InputField inputField) {
        String fqPlaceholderKey = inputField.getLabel() + ".placeholder";
        String placeholderText = resolveMessage(context, fqPlaceholderKey, "");

        if (inputField instanceof TextBoxField) {
            renderTextBox(((TextBoxField) inputField), placeholderText, modelFactory, model);
        }
        else if (inputField instanceof ChoiceField) {
            // placeholder not needed, but need to resolve text options
            renderChoiceField((ChoiceField) inputField, modelFactory, model, context);
        }
        else {
            renderTextField(inputField, placeholderText, modelFactory, model);
        }
    }

    protected abstract void renderTextBox(TextBoxField inputField, String placeholderText,
                                          IModelFactory modelFactory, IModel model);


    protected abstract void renderTextField(InputField inputField, String placeholderText,
                                 IModelFactory modelFactory, IModel model);

    protected abstract void renderChoiceField(ChoiceField choiceField, IModelFactory modelFactory,
                                     IModel model, ITemplateContext context);

    protected abstract void renderFieldErrors(IModelFactory modelFactory, IModel model, InputField inputField);



    // Resolve the message - may return null if no matching property.
    protected String resolveMessage(ITemplateContext context, String fqKey, String defaultValue) {
        // TODO: cache by fqKey
        final Object[] emptyParams = new Object[0];
        final String prefix = "fm.";

        String[] keyParts = fqKey.split("\\.");

        // Match on fully key, if possible
        String value = context.getMessage(this.getClass(), fqKey, emptyParams, false);
        if (value != null) {
            return value;
        }

        // Match increasing parts of message key, starting with last part,
        // e.g. for a.b.c.d match on d, then c.d, etc.
        for (int i = keyParts.length - 1; i > 0; i--) {
            String key = String.join(".", Arrays.copyOfRange(keyParts, i, keyParts.length));
            value = context.getMessage(this.getClass(), key, emptyParams, false);
            if (value != null) {
                return value;
            }
        }

        // No match, return default
        return defaultValue;
    }

    protected String htmlInputType(InputField inputField) {
        if (inputField instanceof PasswordField) {
            return "password";
        }
        else if (inputField instanceof NumberField) {
            NumberField numberField = (NumberField) inputField;
            if (numberField.isUseSlider() && numberField.getMin() != null && numberField.getMax() != null) {
                return "range";
            }
            return "number";
        }
        else if (inputField instanceof DateField) {
            return "date";
        }
        return "text"; // ???
    }
}
