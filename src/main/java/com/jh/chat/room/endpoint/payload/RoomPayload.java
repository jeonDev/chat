package com.jh.chat.room.endpoint.payload;

import com.jh.chat.room.application.usecase.request.DirectRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomInviteExecute;
import com.jh.chat.room.application.usecase.response.RoomMemberResult;
import com.jh.chat.room.application.usecase.response.RoomResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record RoomPayload() {

    @Schema(name = "RoomPayload.DirectCreateRequest")
    public record DirectCreateRequest(
            @Schema(description = "대화 상대 회원 ID", example = "2")
            Long partnerMemberId
    ) {
        public DirectRoomCreateExecute toExecute(Long requesterMemberId) {
            return new DirectRoomCreateExecute(
                    requesterMemberId,
                    partnerMemberId
            );
        }
    }

    @Schema(name = "RoomPayload.GroupCreateRequest")
    public record GroupCreateRequest(
            @Schema(description = "단체방 이름", example = "프로젝트 회의방")
            String name,
            @Schema(description = "초기 참여 회원 ID 목록. 생성자는 자동 포함됩니다.", example = "[2, 3]")
            List<Long> memberIds
    ) {
        public GroupRoomCreateExecute toExecute(Long ownerMemberId) {
            return new GroupRoomCreateExecute(
                    name,
                    ownerMemberId,
                    memberIds
            );
        }
    }

    @Schema(name = "RoomPayload.InviteMembersRequest")
    public record InviteMembersRequest(
            @Schema(description = "초대할 회원 ID 목록", example = "[4, 5]")
            List<Long> memberIds
    ) {
        public GroupRoomInviteExecute toExecute(Long roomId, Long requesterMemberId) {
            return new GroupRoomInviteExecute(
                    roomId,
                    requesterMemberId,
                    memberIds
            );
        }
    }

    @Schema(name = "RoomPayload.Response")
    public record Response(
            @Schema(description = "채팅방 ID", example = "1")
            Long roomId,
            @Schema(description = "채팅방 이름. 1:1 방은 null일 수 있습니다.", example = "프로젝트 회의방")
            String name,
            @Schema(description = "채팅방 타입", example = "GROUP")
            String roomType,
            @Schema(description = "참여자 목록")
            List<MemberResponse> members,
            @Schema(description = "채팅방 생성 시각")
            LocalDateTime createdAt
    ) {
        public static Response of(RoomResult result) {
            return new Response(
                    result.roomId(),
                    result.name(),
                    result.roomType().name(),
                    result.members().stream()
                            .map(MemberResponse::of)
                            .toList(),
                    result.createdAt()
            );
        }
    }

    @Schema(name = "RoomPayload.MemberResponse")
    public record MemberResponse(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,
            @Schema(description = "회원 이름", example = "전종현")
            String name,
            @Schema(description = "채팅방 참여 상태", example = "JOINED")
            String status
    ) {
        public static MemberResponse of(RoomMemberResult result) {
            return new MemberResponse(
                    result.memberId(),
                    result.name(),
                    result.status().name()
            );
        }
    }
}
