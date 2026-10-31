package lab2_api.mvc;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import lab2_api.entity.User;
import lab2_api.service.UserService;
import lab2_api.viewmodel.LoginViewModel;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new LoginViewModel());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("login") LoginViewModel login,
            Model model,
            HttpSession session) {

        Optional<User> userOptional =
                userService.findByUsername(login.getUsername());

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(login.getPassword())) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

        if (!user.isEnabled()) {
            model.addAttribute("error", "This account is disabled.");
            return "login";
        }

        
session.setAttribute("userId", user.getUserId());
session.setAttribute("username", user.getUsername());
session.setAttribute("role", user.getRole());

if (user.isPasswordChangeRequired()) {
    return "redirect:/change-password";
}

return "redirect:/";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
