package uk.wardm.livetest;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.wardm.formaker.annotation.Select;
import uk.wardm.formaker.model.ChoiceStyle;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class FurtherFeaturesFormController {

    public static final String FORM_VIEW_NAME = "choice-fields";

    @ModelAttribute("form")
    public Form formObject() {
        return new Form();
    }

    @GetMapping("/choices")
    public String showForm(@ModelAttribute Form form) {
        return FORM_VIEW_NAME;
    }

    @PostMapping("/choices")
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
        values.put("Boolean with ChoiceStyle.RADIO", form.isBooleanAsRadio());
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
        @Select(options = {"SPRING", "WINTER"})
        private Season seasonWithAnnotation;

        @Select(options = {"yes", "no"}, style = ChoiceStyle.RADIO)
        private boolean booleanAsRadio;
    }

    private static enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }
}
