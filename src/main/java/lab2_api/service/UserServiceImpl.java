package lab2_api.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lab2_api.entity.User;
import lab2_api.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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

	@Override
    public void register(String username, String password, String confirmPassword) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must be 72 bytes or fewer.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setEnabled(true);
        user.setPasswordChangeRequired(false);

        userRepository.save(user);
    
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
        return false;
    }

    User user = userOptional.get();

    if (!user.getPassword().equals(currentPassword)) {
        return false;
    }

    if (newPassword == null || newPassword.isBlank()) {
        return false;
    }

    user.setPassword(newPassword);
    user.setPasswordChangeRequired(false);
    userRepository.save(user);

    return true;
}

}
