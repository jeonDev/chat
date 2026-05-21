package com.jh.chat.room.application.usecase;

import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.request.DirectRoomCreateExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateDirectRoomUseCase {

    private final RoomService roomService;

    public CreateDirectRoomUseCase(RoomService roomService) {
        this.roomService = roomService;
    }

    public RoomResult execute(DirectRoomCreateExecute execute) {
        log.info("[1:1 채팅방 생성] requesterMemberId : {}, partnerMemberId : {}",
                execute.requesterMemberId(),
                execute.partnerMemberId()
        );

        return roomService.createDirectRoom(execute);
    }
}

