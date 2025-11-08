package com.bescobar.notes.user.application.port.out;

import com.bescobar.notes.user.domain.model.User;

import java.util.List;

public interface UserRepositoryPort {
    User save(User user);
    User findByEmail(String email);
    User findById(Long id);
    List<User> findAll();
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void deleteById(Long id);
}
