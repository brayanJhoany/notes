package com.bescobar.notes.user.infrastructure.web;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.port.in.ListUsersInputPort;
import com.bescobar.notes.user.infrastructure.web.dto.PageResponse;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final ListUsersInputPort listUsersUseCase;

     @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullname
    ) {
        PageResponse<UserResponse> response = PageResponse.from(
                listUsersUseCase.getAllUsers(pageable, email, fullname)
                        .map(UserDtoMapper::toResponse)
        );
        return ResponseEntity.ok(response);
    }

}
