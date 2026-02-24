package com.bescobar.notes.user.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bescobar.notes.user.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    User findByEmail(String email);

    User findById(Long id);

    Page<User> findAll(Pageable pageable, String email, String fullname);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}
