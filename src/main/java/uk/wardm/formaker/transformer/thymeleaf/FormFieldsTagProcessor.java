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
import uk.wardm.formaker.model.FormMeta;
import uk.wardm.formaker.generator.pojo.FormObjectMeta;

public class FormFieldsTagProcessor extends AbstractAttributeModelProcessor {
    private static final String FORM_OBJECT_ATTR_NAME = "form";
    private static final int PRECEDENCE = 10000;
    private static final String DEFAULT_FORM_META_VAR_NAME = "formMeta";
    private String formMetaVarName;

    public FormFieldsTagProcessor(final String dialectPrefix) {
        this(
            dialectPrefix,
            DEFAULT_FORM_META_VAR_NAME);
    }

    public FormFieldsTagProcessor(final String dialectPrefix,
                                  final String formMetaVarName) {
        super(
                TemplateMode.HTML,     // This processor will apply only to HTML mode
                dialectPrefix,         // Prefix to be applied to name for matching
                "form-fields",         // Tag name
                true,                  // Prefix to be applied to tag name
                FORM_OBJECT_ATTR_NAME, // Name of the attribute that will be matched
                true,                  // Apply dialect prefix to attribute name
                PRECEDENCE,            // Precedence (inside dialect's precedence)
                true);                 // Remove the matched attribute afterwards

        this.formMetaVarName = formMetaVarName;
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
        FormMeta formMeta = new FormObjectMeta(formPojo);
//        structureHandler.setLocalVariable(formMetaVarName, formMeta);
        IModelFactory modelFactory = context.getModelFactory();
        ComponentRenderer renderer = new DefaultComponentRenderer();

        IModel fields = modelFactory.createModel();
        for (Component component : formMeta.getFields()) {
            fields.addModel(renderer.render(context, component));
        }

        model.remove(0);
        model.addModel(fields);
    }
}
