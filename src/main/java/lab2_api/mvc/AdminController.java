
package lab2_api.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lab2_api.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    // Enable or disable a user.
    @PostMapping("/admin/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {

        try {
            userService.toggleUserEnabled(id);
        } catch (IllegalStateException e) {
            return "redirect:/admin/users?lastAdmin";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users?error";
        }

        return "redirect:/admin/users";
    }

    // Change a user's role.
    @PostMapping("/admin/users/{id}/role")
    public String changeRole(
            @PathVariable Long id,
            @RequestParam String role) {

        try {
            userService.changeUserRole(id, role);
        } catch (IllegalStateException e) {
            return "redirect:/admin/users?lastAdmin";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users?invalidRole";
        }

        return "redirect:/admin/users";
    }

    // Reset a user's password.
    @PostMapping("/admin/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id) {

        userService.resetPassword(id);

        return "redirect:/admin/users";
    }
}
