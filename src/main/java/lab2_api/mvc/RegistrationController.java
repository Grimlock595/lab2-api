package lab2_api.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lab2_api.service.UserService;
import lab2_api.viewmodel.RegistrationViewModel;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new RegistrationViewModel());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("registration") RegistrationViewModel registration,
            Model model) {

        try {
            userService.register(
                    registration.getUsername(),
                    registration.getPassword(),
                    registration.getConfirmPassword());
        } catch (IllegalArgumentException ex) {
            registration.setPassword(null);
            registration.setConfirmPassword(null);
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
