package lab2_api.service;

import java.util.List;
import java.util.Optional;

import lab2_api.entity.User;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

}