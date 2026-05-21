package com.jh.chat.room.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import com.jh.chat.room.application.usecase.request.DirectRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomInviteExecute;
import com.jh.chat.room.application.usecase.request.RoomLeaveExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private JpaMemberRepository memberRepository;

    @Test
    void createDirectRoom_reusesExistingRoomAndRejoinsMembers() {
        Member requester = saveMember("direct-user-1");
        Member partner = saveMember("direct-user-2");

        RoomResult created = roomService.createDirectRoom(
                new DirectRoomCreateExecute(requester.getId(), partner.getId())
        );
        roomService.leaveRoom(new RoomLeaveExecute(created.roomId(), requester.getId()));

        RoomResult reused = roomService.createDirectRoom(
                new DirectRoomCreateExecute(partner.getId(), requester.getId())
        );

        assertThat(reused.roomId()).isEqualTo(created.roomId());
        assertThat(reused.members())
                .extracting(member -> member.status())
                .containsOnly(ChatRoomMemberStatus.JOINED);
    }

    @Test
    void inviteGroupRoomMembers_rejoinsLeftMemberWithoutDeletingRoom() {
        Member owner = saveMember("group-owner");
        Member member = saveMember("group-member");
        Member invitee = saveMember("group-invitee");

        RoomResult created = roomService.createGroupRoom(
                new GroupRoomCreateExecute(
                        "테스트 단체방",
                        owner.getId(),
                        List.of(member.getId())
                )
        );
        RoomResult invited = roomService.inviteGroupRoomMembers(
                new GroupRoomInviteExecute(
                        created.roomId(),
                        owner.getId(),
                        List.of(invitee.getId())
                )
        );
        roomService.leaveRoom(new RoomLeaveExecute(invited.roomId(), invitee.getId()));

        RoomResult afterLeave = roomService.getRoom(created.roomId());
        assertThat(statusOf(afterLeave, invitee.getId())).isEqualTo(ChatRoomMemberStatus.LEFT);

        RoomResult rejoined = roomService.inviteGroupRoomMembers(
                new GroupRoomInviteExecute(
                        created.roomId(),
                        owner.getId(),
                        List.of(invitee.getId())
                )
        );

        assertThat(rejoined.roomId()).isEqualTo(created.roomId());
        assertThat(statusOf(rejoined, invitee.getId())).isEqualTo(ChatRoomMemberStatus.JOINED);
    }

    private Member saveMember(String loginId) {
        return memberRepository.save(Member.of(loginId, "password", loginId));
    }

    private ChatRoomMemberStatus statusOf(RoomResult room, Long memberId) {
        return room.members().stream()
                .filter(member -> member.memberId().equals(memberId))
                .findFirst()
                .orElseThrow()
                .status();
    }
}

