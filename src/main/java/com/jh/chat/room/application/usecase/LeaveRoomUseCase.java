package com.jh.chat.room.application.usecase;

import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.request.RoomLeaveExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaveRoomUseCase {

    private final RoomService roomService;

    public LeaveRoomUseCase(RoomService roomService) {
        this.roomService = roomService;
    }

    public void execute(RoomLeaveExecute execute) {
        log.info("[채팅방 나가기] roomId : {}, memberId : {}", execute.roomId(), execute.memberId());
        roomService.leaveRoom(execute);
    }
}

