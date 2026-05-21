package com.jh.chat.room.endpoint;

import com.jh.chat.room.application.usecase.CreateDirectRoomUseCase;
import com.jh.chat.room.application.usecase.CreateGroupRoomUseCase;
import com.jh.chat.room.application.usecase.FindRoomUseCase;
import com.jh.chat.room.application.usecase.InviteGroupRoomUseCase;
import com.jh.chat.room.application.usecase.LeaveRoomUseCase;
import com.jh.chat.room.application.usecase.request.RoomLeaveExecute;
import com.jh.chat.room.endpoint.payload.RoomPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room", description = "채팅방 관리 API")
public class RoomController {

    private final CreateDirectRoomUseCase createDirectRoomUseCase;
    private final CreateGroupRoomUseCase createGroupRoomUseCase;
    private final InviteGroupRoomUseCase inviteGroupRoomUseCase;
    private final LeaveRoomUseCase leaveRoomUseCase;
    private final FindRoomUseCase findRoomUseCase;

    @Operation(summary = "참여 중인 채팅방 목록 조회", description = "회원이 현재 참여 중인 채팅방 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<RoomPayload.Response>> getJoinedRooms(@RequestParam Long memberId) {
        var response = findRoomUseCase.findJoinedRooms(memberId).stream()
                .map(RoomPayload.Response::of)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 단건 조회", description = "채팅방과 참여자 상태를 조회합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomPayload.Response> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(RoomPayload.Response.of(findRoomUseCase.findRoom(roomId)));
    }

    @Operation(summary = "1:1 채팅방 생성 또는 재사용", description = "두 회원의 1:1 채팅방을 생성합니다. 이미 있으면 기존 채팅방을 재사용합니다.")
    @PostMapping("/direct")
    public ResponseEntity<RoomPayload.Response> createDirectRoom(
            @RequestBody RoomPayload.DirectCreateRequest request
    ) {
        var result = createDirectRoomUseCase.execute(request.toExecute());
        return ResponseEntity.ok(RoomPayload.Response.of(result));
    }

    @Operation(summary = "단체방 생성 또는 재사용", description = "단체방을 생성합니다. 동일한 초기 참여자 조합의 방이 있으면 기존 채팅방을 재사용합니다.")
    @PostMapping("/groups")
    public ResponseEntity<RoomPayload.Response> createGroupRoom(
            @RequestBody RoomPayload.GroupCreateRequest request
    ) {
        var result = createGroupRoomUseCase.execute(request.toExecute());
        return ResponseEntity.ok(RoomPayload.Response.of(result));
    }

    @Operation(summary = "단체방 회원 초대", description = "단체방에 회원을 초대합니다. 나갔던 회원이면 기존 참여 정보를 재활성화합니다.")
    @PostMapping("/{roomId}/members")
    public ResponseEntity<RoomPayload.Response> inviteGroupRoomMembers(
            @PathVariable Long roomId,
            @RequestBody RoomPayload.InviteMembersRequest request
    ) {
        var result = inviteGroupRoomUseCase.execute(request.toExecute(roomId));
        return ResponseEntity.ok(RoomPayload.Response.of(result));
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방을 삭제하지 않고 회원 참여 상태만 나감으로 변경합니다.")
    @DeleteMapping("/{roomId}/members/{memberId}")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId, @PathVariable Long memberId) {
        leaveRoomUseCase.execute(new RoomLeaveExecute(roomId, memberId));
        return ResponseEntity.noContent().build();
    }
}

