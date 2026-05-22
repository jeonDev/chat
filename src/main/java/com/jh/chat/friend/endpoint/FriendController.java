package com.jh.chat.friend.endpoint;

import com.jh.chat.friend.application.usecase.AddFriendUseCase;
import com.jh.chat.friend.application.usecase.FindFriendUseCase;
import com.jh.chat.friend.endpoint.payload.FriendPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/friends")
@Tag(name = "Friend", description = "친구 API")
public class FriendController {

    private final FindFriendUseCase findFriendUseCase;
    private final AddFriendUseCase addFriendUseCase;

    @Operation(summary = "친구 검색", description = "이름 또는 연락처로 친구를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<FriendPayload.SearchResponse>> search(
            @RequestParam Long memberId,
            @RequestParam String keyword
    ) {
        var response = findFriendUseCase.search(memberId, keyword)
                .stream()
                .map(FriendPayload.SearchResponse::of)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 친구 목록", description = "내가 친구로 추가한 회원 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FriendPayload.MemberResponse>> getMyFriends(@RequestParam Long memberId) {
        var response = findFriendUseCase.getMyFriends(memberId)
                .stream()
                .map(FriendPayload.MemberResponse::of)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나를 친구 추가한 목록", description = "나를 친구로 추가한 회원 목록을 조회합니다.")
    @GetMapping("/added-me")
    public ResponseEntity<List<FriendPayload.MemberResponse>> getMembersWhoAddedMe(@RequestParam Long memberId) {
        var response = findFriendUseCase.getMembersWhoAddedMe(memberId)
                .stream()
                .map(FriendPayload.MemberResponse::of)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 추가", description = "상대 회원을 내 친구 목록에 추가합니다.")
    @PostMapping
    public ResponseEntity<FriendPayload.MemberResponse> addFriend(@RequestBody FriendPayload.AddRequest request) {
        return ResponseEntity.ok(
                FriendPayload.MemberResponse.of(
                        addFriendUseCase.execute(request.requesterMemberId(), request.friendMemberId())
                )
        );
    }
}

