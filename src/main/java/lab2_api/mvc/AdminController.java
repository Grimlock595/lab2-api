package lab2_api.mvc;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import lab2_api.entity.User;
import lab2_api.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model, HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/toggle")
    public String toggleUser(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(!user.isEnabled());
            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/reset-password")
    public String resetPassword(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setPassword("password");
            user.setPasswordChangeRequired(true);

            userService.save(user);
        }

        return "redirect:/admin/users";
    }
}