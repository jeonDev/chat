package com.jh.chat.room.application.service;

import com.jh.chat.common.exception.NotFoundException;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import com.jh.chat.room.application.usecase.request.DirectRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomCreateExecute;
import com.jh.chat.room.application.usecase.request.GroupRoomInviteExecute;
import com.jh.chat.room.application.usecase.request.RoomLeaveExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import com.jh.chat.room.domain.entity.ChatRoom;
import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;
import com.jh.chat.room.domain.entity.ChatRoomType;
import com.jh.chat.room.domain.repository.JpaChatRoomMemberRepository;
import com.jh.chat.room.domain.repository.JpaChatRoomRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final JpaChatRoomRepository chatRoomRepository;
    private final JpaChatRoomMemberRepository chatRoomMemberRepository;
    private final JpaMemberRepository memberRepository;

    public RoomService(JpaChatRoomRepository chatRoomRepository,
                       JpaChatRoomMemberRepository chatRoomMemberRepository,
                       JpaMemberRepository memberRepository
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public RoomResult createDirectRoom(DirectRoomCreateExecute execute) {
        validateDirectRoomExecute(execute);

        Member requester = getMember(execute.requesterMemberId());
        Member partner = getMember(execute.partnerMemberId());
        String roomKey = ChatRoom.directRoomKey(requester.getId(), partner.getId());

        ChatRoom chatRoom = chatRoomRepository.findByRoomTypeAndRoomKey(ChatRoomType.DIRECT, roomKey)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.direct(requester.getId(), partner.getId())));

        joinOrRejoin(chatRoom, requester);
        joinOrRejoin(chatRoom, partner);

        return getRoomResult(chatRoom);
    }

    @Transactional
    public RoomResult createGroupRoom(GroupRoomCreateExecute execute) {
        validateGroupRoomExecute(execute);

        Set<Long> participantIds = new LinkedHashSet<>();
        participantIds.add(execute.ownerMemberId());
        participantIds.addAll(execute.memberIds());
        if (participantIds.size() < 2) {
            throw new IllegalArgumentException("단체방은 생성자를 포함해 2명 이상 필요합니다.");
        }

        List<Member> members = getMembers(participantIds);
        String roomKey = ChatRoom.groupRoomKey(participantIds);

        ChatRoom chatRoom = chatRoomRepository.findByRoomTypeAndRoomKey(ChatRoomType.GROUP, roomKey)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.group(execute.name(), participantIds)));
        chatRoom.rename(execute.name());

        members.forEach(member -> joinOrRejoin(chatRoom, member));

        return getRoomResult(chatRoom);
    }

    @Transactional
    public RoomResult inviteGroupRoomMembers(GroupRoomInviteExecute execute) {
        validateInviteExecute(execute);

        ChatRoom chatRoom = getChatRoom(execute.roomId());
        validateGroupRoom(chatRoom);
        validateJoinedMember(chatRoom.getId(), execute.requesterMemberId());

        List<Member> members = getMembers(new LinkedHashSet<>(execute.memberIds()));
        members.forEach(member -> joinOrRejoin(chatRoom, member));

        return getRoomResult(chatRoom);
    }

    @Transactional
    public void leaveRoom(RoomLeaveExecute execute) {
        if (execute.roomId() == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (execute.memberId() == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }

        getChatRoom(execute.roomId());
        ChatRoomMember roomMember = chatRoomMemberRepository
                .findByChatRoomIdAndMemberId(execute.roomId(), execute.memberId())
                .orElseThrow(() -> new NotFoundException("채팅방 참여 정보를 찾을 수 없습니다."));
        roomMember.leave();
    }

    @Transactional(readOnly = true)
    public List<RoomResult> getJoinedRooms(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }

        getMember(memberId);

        return chatRoomMemberRepository.findAllByMemberIdAndStatus(memberId, ChatRoomMemberStatus.JOINED)
                .stream()
                .map(ChatRoomMember::getChatRoom)
                .map(this::getRoomResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResult getRoom(Long roomId) {
        return getRoomResult(getRoomEntity(roomId));
    }

    private void validateDirectRoomExecute(DirectRoomCreateExecute execute) {
        if (execute.requesterMemberId() == null) {
            throw new IllegalArgumentException("requesterMemberId는 필수입니다.");
        }
        if (execute.partnerMemberId() == null) {
            throw new IllegalArgumentException("partnerMemberId는 필수입니다.");
        }
        if (execute.requesterMemberId().equals(execute.partnerMemberId())) {
            throw new IllegalArgumentException("1:1 채팅방은 서로 다른 회원으로 생성해야 합니다.");
        }
    }

    private void validateGroupRoomExecute(GroupRoomCreateExecute execute) {
        if (execute.name() == null || execute.name().isBlank()) {
            throw new IllegalArgumentException("단체방 이름은 필수입니다.");
        }
        if (execute.ownerMemberId() == null) {
            throw new IllegalArgumentException("ownerMemberId는 필수입니다.");
        }
        if (execute.memberIds() == null || execute.memberIds().isEmpty()) {
            throw new IllegalArgumentException("단체방 참여 회원은 1명 이상 필요합니다.");
        }
        if (execute.memberIds().stream().anyMatch(memberId -> memberId == null)) {
            throw new IllegalArgumentException("memberIds에는 null이 포함될 수 없습니다.");
        }
    }

    private void validateInviteExecute(GroupRoomInviteExecute execute) {
        if (execute.roomId() == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (execute.requesterMemberId() == null) {
            throw new IllegalArgumentException("requesterMemberId는 필수입니다.");
        }
        if (execute.memberIds() == null || execute.memberIds().isEmpty()) {
            throw new IllegalArgumentException("초대할 회원은 1명 이상 필요합니다.");
        }
        if (execute.memberIds().stream().anyMatch(memberId -> memberId == null)) {
            throw new IllegalArgumentException("memberIds에는 null이 포함될 수 없습니다.");
        }
    }

    private void validateGroupRoom(ChatRoom chatRoom) {
        if (!chatRoom.isGroup()) {
            throw new IllegalArgumentException("단체방에서만 가능한 기능입니다.");
        }
    }

    private void validateJoinedMember(Long roomId, Long memberId) {
        ChatRoomMember roomMember = chatRoomMemberRepository.findByChatRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("요청 회원은 채팅방 참여자가 아닙니다."));

        if (!roomMember.isJoined()) {
            throw new IllegalArgumentException("채팅방에 참여 중인 회원만 요청할 수 있습니다.");
        }
    }

    private void joinOrRejoin(ChatRoom chatRoom, Member member) {
        chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId())
                .ifPresentOrElse(
                        ChatRoomMember::rejoin,
                        () -> chatRoomMemberRepository.save(ChatRoomMember.join(chatRoom, member))
                );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. memberId=%d".formatted(memberId)));
    }

    private List<Member> getMembers(Set<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        if (members.size() != memberIds.size()) {
            throw new NotFoundException("존재하지 않는 회원이 포함되어 있습니다.");
        }

        Map<Long, Member> memberMap = members.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        return memberIds.stream()
                .map(memberMap::get)
                .toList();
    }

    private ChatRoom getChatRoom(Long roomId) {
        return getRoomEntity(roomId);
    }

    private ChatRoom getRoomEntity(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. roomId=%d".formatted(roomId)));
    }

    private RoomResult getRoomResult(ChatRoom chatRoom) {
        return RoomResult.of(
                chatRoom,
                chatRoomMemberRepository.findAllByChatRoomId(chatRoom.getId())
        );
    }
}
