package org.sollabs.messenger.service;

import java.util.Collection;
import java.util.UUID;

import org.sollabs.messenger.entity.Account;
import org.sollabs.messenger.entity.Message;
import org.sollabs.messenger.entity.QRoom;
import org.sollabs.messenger.entity.Room;
import org.sollabs.messenger.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;

@Service
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomRepository roomRepo;
	
	public Room getRoomId(long myId, long friendId) {
			
		QRoom room = QRoom.room;
		
		BooleanBuilder builder = new BooleanBuilder();
		
		builder.and(room.member.contains(new Account(myId)));
		builder.and(room.member.contains(new Account(friendId)));
		builder.and(room.member.size().eq(2));
		
		if (roomRepo.exists(builder)) {
			return roomRepo.findOne(builder);
		} else {
			Room newRoom = new Room(myId, friendId);
			
			// TODO	차단 여부 확인
			// TODO	생성된 방에 두 유저 모두 세션 접속
			
			return roomRepo.save(newRoom);
		}		
	}

	public Page<Room> getRooms(Pageable page, long myId) {
		QRoom room = QRoom.room;
		
		return roomRepo.findAll(new BooleanBuilder().and(new BooleanBuilder().and(room.member.contains(new Account(myId)))), page);
	}

	public Collection<Account> getMembers(UUID roomId) {
		return roomRepo.findOne(roomId).getMember();
	}
	
	public Room updateLastMessage(Message message) {
		// Set Room.LastMessage
		Room room = roomRepo.findOne(message.getRoomId());
		room.setLastMessage(message.getContent());		
		return roomRepo.save(room);
	}

}
