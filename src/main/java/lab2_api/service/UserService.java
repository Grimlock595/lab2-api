
package lab2_api.service;

import java.util.List;
import java.util.Optional;

import lab2_api.entity.User;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    void resetPassword(Long userId);

    void register(String username, String password, String confirmPassword);

    boolean changePassword(Long userId, String currentPassword, String newPassword);

    // Administrator management operations
    void toggleUserEnabled(Long userId);

    void changeUserRole(Long userId, String role);
}
