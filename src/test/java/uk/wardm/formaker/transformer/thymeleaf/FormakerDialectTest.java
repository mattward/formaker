package uk.wardm.formaker.transformer.thymeleaf;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class FormakerDialectTest {
    private StringTemplateResolver templateResolver;
    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new FormakerDialect());
    }

    @Test
    void canRenderFormModel() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("form", new SimpleExampleForm());
        IContext context = new Context(Locale.UK, vars);
        String html = templateEngine.process("<fm:form-fields fm:form='${form}'/>", context);

        Document doc = Jsoup.parse(html);

        System.out.println(html);

        final List<String> fieldNames = Arrays.asList("firstName", "lastName", "emailAddress");

        // Input elements
        final Elements inputFields = doc.getElementsByTag("input");
        assertIterableEquals(fieldNames,
                inputFields.stream().map(el -> el.attr("id")).collect(Collectors.toList()));

        // Form labels
        final Elements labelFields = doc.getElementsByTag("label");
        assertIterableEquals(fieldNames,
                labelFields.stream().map(el -> el.attr("for")).collect(Collectors.toList()));
    }

    private class SimpleExampleForm {
        private String firstName;
        private String lastName;
        private String emailAddress;
    }
}