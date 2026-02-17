package com.bescobar.notes.friendship.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.friendship.application.port.in.AcceptFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.BlockFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.RejectFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.SendFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.friendship.application.port.in.GetFriendRequestsByStatusInputPort;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.friendship.infrastructure.web.dto.FriendRequestDTO;
import com.bescobar.notes.friendship.infrastructure.web.dto.FriendResponseDTO;
import com.bescobar.notes.shared.infrastructure.web.dto.PageResponse;
import com.bescobar.notes.friendship.infrastructure.web.dto.UserSummaryResponse;
import com.bescobar.notes.friendship.infrastructure.web.mapper.FriendshipWebMapper;
import com.bescobar.notes.shared.security.AuthenticatedUser;
import com.bescobar.notes.user.domain.model.User;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/friendship")
@AllArgsConstructor
public class FriendshipController {

    private final AcceptFriendRequestInputPort acceptFriendRequestUseCase;
    private final BlockFriendRequestInputPort blockFriendRequestUseCase;
    private final RejectFriendRequestInputPort rejectFriendRequestUseCase;
    private final SendFriendRequestInputPort sendFriendRequestUseCase;
    private final FriendshipWebMapper friendshipWebMapper;
    private final GetFriendRequestsByStatusInputPort getFriendRequestByStatusUseCase;

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendResponseDTO> acceptFriendRequest(
            @AuthenticatedUser User currentUser,
            @PathVariable("id") Long friendshipId) {
            FriendDecisionCommand command = FriendDecisionCommand.builder()
                    .friendshipId(friendshipId)
                    .userId(currentUser.getId())
                    .build();
        FriendshipDTO result = acceptFriendRequestUseCase.accept(command);
        FriendResponseDTO response = friendshipWebMapper.toWebResponse(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<FriendResponseDTO> blockFriendRequest(
            @AuthenticatedUser User currentUser,
            @PathVariable("id") Long friendshipId
    ) {
        FriendDecisionCommand command = FriendDecisionCommand.builder()
                .friendshipId(friendshipId)
                .userId(currentUser.getId())
                .build();
        FriendshipDTO result = blockFriendRequestUseCase.block(command);
        FriendResponseDTO response = friendshipWebMapper.toWebResponse(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<FriendResponseDTO> rejectFriendRequest(
            @AuthenticatedUser User currentUser,
            @PathVariable("id") Long friendshipId) {
            FriendDecisionCommand command = FriendDecisionCommand.builder()
                    .friendshipId(friendshipId)
                    .userId(currentUser.getId())
                    .build();
        FriendshipDTO result = rejectFriendRequestUseCase.reject(command);
        FriendResponseDTO response = friendshipWebMapper.toWebResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests")
    public ResponseEntity<FriendResponseDTO> sendFriendRequest(
            @AuthenticatedUser User currentUser,
            @Valid @RequestBody FriendRequestDTO body) {

            SendFriendRequestCommand command = SendFriendRequestCommand.builder()
                    .requesterId(currentUser.getId())
                    .addresseeId(body.getAddresseeId())
                    .build();
        FriendshipDTO result = sendFriendRequestUseCase.send(command);
        FriendResponseDTO response = friendshipWebMapper.toWebResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<UserSummaryResponse>> getFriendships(
         @AuthenticatedUser User currentUser,
         @PathVariable FriendshipStatus status,
         @PageableDefault(size = 10) Pageable pageable
    ){
        Page<UserSummaryResponse> page = getFriendRequestByStatusUseCase
                .getFriendRequests(currentUser.getId(), status, pageable)
                .map(friendshipWebMapper::toUserSummaryResponse);
        return ResponseEntity.ok(PageResponse.from(page));
    }

}
