package com.jh.chat.room.application.usecase;

import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.request.GroupRoomInviteExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InviteGroupRoomUseCase {

    private final RoomService roomService;

    public InviteGroupRoomUseCase(RoomService roomService) {
        this.roomService = roomService;
    }

    public RoomResult execute(GroupRoomInviteExecute execute) {
        log.info("[단체방 초대] roomId : {}, requesterMemberId : {}",
                execute.roomId(),
                execute.requesterMemberId()
        );

        return roomService.inviteGroupRoomMembers(execute);
    }
}

