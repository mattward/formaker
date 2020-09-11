package uk.wardm.formaker.transformer.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

public class FormakerDialect extends AbstractProcessorDialect {
    private final ComponentRenderer renderer;

    /**
     * Default constructor - uses a default bootstrap renderer.
     */
    public FormakerDialect() {
        this(new BootstrapComponentRenderer());
    }

    /**
     * Constructor allowing use of a specific ComponentRenderer implementation.
     *
     * @param renderer ComponentRenderer
     */
    public FormakerDialect(ComponentRenderer renderer) {
        super("Formaker Dialect", "fm", 10000);
        this.renderer = renderer;
    }

    @Override
    public Set<IProcessor> getProcessors(String prefix) {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new FormFieldsTagProcessor(prefix, renderer));
        // Remove the xmlns:fm attribute we might add for IDE validation
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, prefix));
        return processors;
    }
}
