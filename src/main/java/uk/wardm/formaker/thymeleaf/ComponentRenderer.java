package uk.wardm.formaker.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import uk.wardm.formaker.model.Component;

public interface ComponentRenderer {
    IModel render(ITemplateContext context, Component component);
}
