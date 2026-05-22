package com.jh.chat.friend.application.usecase;

import com.jh.chat.friend.application.service.FriendService;
import com.jh.chat.friend.application.service.response.FriendMemberResult;
import org.springframework.stereotype.Service;

@Service
public class AddFriendUseCase {

    private final FriendService friendService;

    public AddFriendUseCase(FriendService friendService) {
        this.friendService = friendService;
    }

    public FriendMemberResult execute(Long requesterMemberId, Long friendMemberId) {
        return friendService.add(requesterMemberId, friendMemberId);
    }
}

