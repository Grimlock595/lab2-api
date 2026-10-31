
package lab2_api.service;

import lab2_api.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserService userService;

    public AdminBootstrap(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        boolean adminExists = userService.findAll().stream()
                .anyMatch(user -> "ADMIN".equals(user.getRole()));

        if (adminExists) {
            return;
        }

        String username = System.getenv()
                .getOrDefault("LAB4_ADMIN_USERNAME", "admin");

        String password = System.getenv("LAB4_ADMIN_PASSWORD");

        if (password == null || password.isBlank()) {
            System.err.println(
                    "No administrator exists. Set LAB4_ADMIN_PASSWORD " +
                    "to bootstrap an administrator."
            );
            return;
        }

        if (userService.findByUsername(username).isPresent()) {
            throw new IllegalStateException(
                    "Bootstrap username already exists: " + username
            );
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        admin.setPasswordChangeRequired(true);

        userService.save(admin);

        System.out.println(
                "Lab 4 administrator created: " + username +
                ". Password change required."
        );
    }
}

