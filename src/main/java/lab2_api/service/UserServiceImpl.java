
package lab2_api.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lab2_api.entity.User;
import lab2_api.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // Determine whether this is the last enabled administrator.
    private boolean isLastEnabledAdmin(User user) {

        if (!"ADMIN".equals(user.getRole()) || !user.isEnabled()) {
            return false;
        }

        long enabledAdmins = userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole())
                        && u.isEnabled())
                .count();

        return enabledAdmins <= 1;
    }

    // Enable or disable a user.
    @Override
    @Transactional
    public void toggleUserEnabled(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        if (isLastEnabledAdmin(user)) {
            throw new IllegalStateException(
                    "Cannot disable the last enabled administrator.");
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    // Change a user's role.
    @Override
    @Transactional
    public void changeUserRole(Long userId, String role) {

        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            throw new IllegalArgumentException("Invalid role.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        if ("USER".equals(role) && isLastEnabledAdmin(user)) {
            throw new IllegalStateException(
                    "Cannot demote the last enabled administrator.");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    // Reset a user's password.
    @Override
    public void resetPassword(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        user.setPassword(passwordEncoder.encode("password"));
        user.setPasswordChangeRequired(true);

        userRepository.save(user);
    }

    // Register a new account with BCrypt.
    @Override
    public void register(
            String username,
            String password,
            String confirmPassword) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Username and password are required.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "Password must be 72 bytes or fewer.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username is already taken.");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setEnabled(true);
        user.setPasswordChangeRequired(false);

        userRepository.save(user);
    }

    // Change a password using BCrypt verification and hashing.
    @Override
    public boolean changePassword(
            Long userId,
            String currentPassword,
            String newPassword) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        if (currentPassword == null ||
                !passwordEncoder.matches(
                        currentPassword, user.getPassword())) {

            return false;
        }

        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }

        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);

        userRepository.save(user);

        return true;
    }
}
