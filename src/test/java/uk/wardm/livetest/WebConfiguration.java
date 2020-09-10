package uk.wardm.livetest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.wardm.formaker.transformer.thymeleaf.FormakerDialect;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public FormakerDialect formObjectModelDialect() {
        return new FormakerDialect();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
    }

}
