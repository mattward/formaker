package uk.wardm.formaker.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

public class FOMDialect extends AbstractProcessorDialect {
    public FOMDialect() {
        super("FOM Dialect", "fm", 10000);
    }

    @Override
    public Set<IProcessor> getProcessors(String prefix) {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new FormFieldsTagProcessor(prefix));
        // Remove the xmlns:fm attribute we might add for IDE validation
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, prefix));
        return processors;
    }
}
