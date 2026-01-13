package com.bescobar.notes.Friendship.application.port.out;

import java.util.Optional;

import com.bescobar.notes.Friendship.domain.model.Friendship;

public interface FriendshipOutPort {
    Friendship save(Friendship friendship);
    Optional<Friendship> findById(Long id);
    Friendship update(Friendship friendship, Long id);
}
