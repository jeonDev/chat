package com.jh.chat.room.application.usecase;

import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.request.GroupRoomCreateExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateGroupRoomUseCase {

    private final RoomService roomService;

    public CreateGroupRoomUseCase(RoomService roomService) {
        this.roomService = roomService;
    }

    public RoomResult execute(GroupRoomCreateExecute execute) {
        log.info("[단체방 생성] ownerMemberId : {}, name : {}", execute.ownerMemberId(), execute.name());
        return roomService.createGroupRoom(execute);
    }
}

