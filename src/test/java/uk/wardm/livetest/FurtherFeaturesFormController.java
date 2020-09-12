package uk.wardm.livetest;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.wardm.formaker.annotation.Select;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class FurtherFeaturesFormController {

    public static final String FORM_VIEW_NAME = "more-form";

    @ModelAttribute("form")
    public Form formObject() {
        return new Form();
    }

    @GetMapping("/more")
    public String showForm(@ModelAttribute Form form) {
        return FORM_VIEW_NAME;
    }

    @PostMapping("/more")
    public String processFormSubmission(
            @Valid @ModelAttribute("form") Form form,
            Model model,
            BindingResult bindingResult) {
        model.addAttribute("showValues", true);
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("Season", form.getSeason());
        values.put("Truth is out there", form.getTruthIsOutThere());
        values.put("Lies are out there", form.isLiesAreOutThere());
        values.put("Season with @Select", form.getSeasonWithAnnotation());
        values.put("Boolean with @Select", form.isBooleanWithSelect());
        model.addAttribute("submittedValues", values);
        return FORM_VIEW_NAME;
    }

    @Data
    public static class Form {
        private Season season;
        private Boolean truthIsOutThere;
        private boolean liesAreOutThere;

        // TODO: I'm not sure if you should be able to do this!?
        // Perhaps it's ok if they are valid values
        @Select({"SPRING", "WINTER"})
        private Season seasonWithAnnotation;

        @Select({"yes", "no"})
        private boolean booleanWithSelect;
    }

    private static enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }
}
