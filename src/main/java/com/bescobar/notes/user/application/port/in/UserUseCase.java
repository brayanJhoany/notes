package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.domain.model.User;

import java.util.List;

public interface UserUseCase {
    // Authentication methods
    AuthResponseDto registerUser(User user);
    AuthResponseDto login(LoginCommand loginCommand);
    AuthResponseDto refreshToken(String refreshToken);
    void logout(String refreshToken);

    // Profile management methods
    User getProfile(Long id);
    User getProfileByEmail(String email);
    User updateProfile(Long id, UpdateProfileCommand updateCommand);

    // Admin methods
    List<User> getAllUsers();
    void deleteUser(Long id);
}
