package com.jh.chat.friend.application.usecase;

import com.jh.chat.friend.application.service.FriendService;
import com.jh.chat.friend.application.service.response.FriendMemberResult;
import com.jh.chat.friend.application.service.response.FriendSearchResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FindFriendUseCase {

    private final FriendService friendService;

    public FindFriendUseCase(FriendService friendService) {
        this.friendService = friendService;
    }

    public List<FriendSearchResult> search(Long memberId, String keyword) {
        return friendService.search(memberId, keyword);
    }

    public List<FriendMemberResult> getMyFriends(Long memberId) {
        return friendService.getMyFriends(memberId);
    }

    public List<FriendMemberResult> getMembersWhoAddedMe(Long memberId) {
        return friendService.getMembersWhoAddedMe(memberId);
    }
}

