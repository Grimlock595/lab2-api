
package lab2_api.mvc;

import jakarta.servlet.http.HttpSession;
import lab2_api.entity.User;
import lab2_api.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

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

            // Prevent disabling the last enabled administrator.
            if ("ADMIN".equals(user.getRole()) && user.isEnabled()) {
                long enabledAdmins = userService.findAll().stream()
                        .filter(u -> "ADMIN".equals(u.getRole()) && u.isEnabled())
                        .count();

                if (enabledAdmins <= 1) {
                    return "redirect:/admin/users?lastAdmin";
                }
            }

            user.setEnabled(!user.isEnabled());
            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String changeRole(
            @PathVariable Long id,
            @RequestParam String role,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Only the two permitted roles are accepted.
        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            return "redirect:/admin/users?invalidRole";
        }

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Prevent demoting the last enabled administrator.
            if ("ADMIN".equals(user.getRole())
                    && user.isEnabled()
                    && "USER".equals(role)) {

                long enabledAdmins = userService.findAll().stream()
                        .filter(u -> "ADMIN".equals(u.getRole()) && u.isEnabled())
                        .count();

                if (enabledAdmins <= 1) {
                    return "redirect:/admin/users?lastAdmin";
                }
            }

            user.setRole(role);
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

            // Temporary Lab 4 password; replaced by BCrypt in Lab 5.
            user.setPassword("password");
            user.setPasswordChangeRequired(true);
            userService.save(user);
        }

        return "redirect:/admin/users";
    }
}

