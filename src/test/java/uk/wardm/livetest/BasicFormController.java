package uk.wardm.livetest;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.wardm.formaker.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Controller
public class BasicFormController {
    @ModelAttribute("form")
    public BasicForm formObject() {
        return new BasicForm();
    }

    @GetMapping("/basic")
    public String basicForm(@ModelAttribute BasicForm form) {
        return "basic-form";
    }

    @PostMapping("/basic")
    public String saveBasicForm(
            @Valid @ModelAttribute("form") BasicForm form,
            BindingResult bindingResult) {
        return "basic-form";
    }

    @Data
    public static class BasicForm {
        @Length(max = 10)
        private String firstName;

        @Length(min = 1, max = 10)
        private String lastName;

        @Min(0) @Max(Byte.MAX_VALUE)
        private byte age;

        @Min(0) @Max(Integer.MAX_VALUE)
        private Integer luckyNumber;

        @Min(-100) @Max(100)
        @Range
        private Long happiness;

        private LocalDate favouriteDay;

        @Select(options = { "true", "false" })
        private Boolean weatherIsRainy;

        @TextBox
        private String description;

        @Exclude
        private String youWontSeeMe;

        @Exclude
        private int youWontSeeMeEither;

        @Password
        @Length(max = 30)
        private String password;
    }
}
