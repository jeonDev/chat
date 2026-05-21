package com.jh.chat.room.application.usecase;

import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.response.RoomResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FindRoomUseCase {

    private final RoomService roomService;

    public FindRoomUseCase(RoomService roomService) {
        this.roomService = roomService;
    }

    public List<RoomResult> findJoinedRooms(Long memberId) {
        return roomService.getJoinedRooms(memberId);
    }

    public RoomResult findRoom(Long roomId) {
        return roomService.getRoom(roomId);
    }
}

