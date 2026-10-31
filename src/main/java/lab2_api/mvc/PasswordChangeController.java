
package lab2_api.mvc;

import jakarta.servlet.http.HttpSession;
import lab2_api.service.UserService;
import lab2_api.viewmodel.PasswordChangeViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordChangeController {

    private final UserService userService;

    public PasswordChangeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String showForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("passwordChange", new PasswordChangeViewModel());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute("passwordChange") PasswordChangeViewModel form,
            Model model,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        if (form.getNewPassword() == null ||
                form.getNewPassword().isBlank() ||
                !form.getNewPassword().equals(form.getConfirmPassword())) {

            model.addAttribute("error",
                    "New passwords must match and cannot be empty.");
            return "change-password";
        }

        boolean changed = userService.changePassword(
                userId,
                form.getCurrentPassword(),
                form.getNewPassword()
        );

        if (!changed) {
            model.addAttribute("error",
                    "Password change failed. Check your current password.");
            return "change-password";
        }

        session.invalidate();
        return "redirect:/login?passwordChanged";
    }
}

