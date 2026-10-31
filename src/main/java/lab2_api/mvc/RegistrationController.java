package lab2_api.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lab2_api.entity.User;
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

        String username = registration.getUsername();
        String password = registration.getPassword();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            model.addAttribute("error", "Username and password are required.");
            return "register";
        }

        if (!password.equals(registration.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        if (userService.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username is already taken.");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("USER");
        user.setEnabled(true);
        user.setPasswordChangeRequired(false);

        userService.save(user);

        return "redirect:/login?registered";
    }
}
