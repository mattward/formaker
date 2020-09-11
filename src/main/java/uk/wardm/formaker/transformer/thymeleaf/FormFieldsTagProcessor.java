package uk.wardm.formaker.transformer.thymeleaf;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.*;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import uk.wardm.formaker.model.Component;
import uk.wardm.formaker.model.Form;
import uk.wardm.formaker.generator.pojo.FormModelImpl;

public class FormFieldsTagProcessor extends AbstractAttributeModelProcessor {
    private static final String FORM_OBJECT_ATTR_NAME = "form";
    private static final int PRECEDENCE = 10000;
    private ComponentRenderer renderer;

    public FormFieldsTagProcessor(final String dialectPrefix,
                                  final ComponentRenderer renderer) {
        super(
                TemplateMode.HTML,     // This processor will apply only to HTML mode
                dialectPrefix,         // Prefix to be applied to name for matching
                "form-fields",         // Tag name
                true,                  // Prefix to be applied to tag name
                FORM_OBJECT_ATTR_NAME, // Name of the attribute that will be matched
                true,                  // Apply dialect prefix to attribute name
                PRECEDENCE,            // Precedence (inside dialect's precedence)
                true);                 // Remove the matched attribute afterwards

        this.renderer = renderer;
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
                             String attributeValue, IElementModelStructureHandler structureHandler) {

        /*
         * In order to evaluate the attribute value as a Thymeleaf Standard Expression,
         * we first obtain the parser, then use it for parsing the attribute value into
         * an expression object, and finally execute this expression object.
         */
        final IEngineConfiguration configuration = context.getConfiguration();
        final IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);
        final IStandardExpression expression = parser.parseExpression(context, attributeValue);
        final Object formPojo = expression.execute(context);

        // Form descriptor
        Form form = new FormModelImpl(formPojo);
        IModelFactory modelFactory = context.getModelFactory();

        IModel fields = modelFactory.createModel();
        for (Component component : form.getFields()) {
            fields.addModel(renderer.render(context, component));
        }

        model.remove(0);
        model.addModel(fields);
    }
}
