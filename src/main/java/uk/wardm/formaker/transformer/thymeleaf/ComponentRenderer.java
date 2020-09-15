package uk.wardm.formaker.transformer.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import uk.wardm.formaker.model.Component;

/**
 * Implementations of this interface are responsible for rendering the
 * Abstract Form Tree (aka "form object model") into whatever output
 * is required.
 *
 * {@link BootstrapComponentRenderer} is a fairly vanilla implementation
 * that can have a basic theme applied by customising the CSS classes
 * used by the output markup.
 *
 * {@link BootstrapConfig} is unsurprisingly a Bootstrap CSS theme.
 */
public interface ComponentRenderer {
    IModel render(ITemplateContext context, Component component);
}
