package com.lazycode.lazyhotel.controller;

import com.lazycode.lazyhotel.exception.PhotoRetrievalExcetion;
import com.lazycode.lazyhotel.exception.ResourceNotFoundException;
import com.lazycode.lazyhotel.model.BookedRoom;
import com.lazycode.lazyhotel.model.Room;
import com.lazycode.lazyhotel.response.BookingResponse;
import com.lazycode.lazyhotel.response.RoomResponse;
import com.lazycode.lazyhotel.service.BookingService;
import com.lazycode.lazyhotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:4000")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;

    private final BookingService bookingService;

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(
                savedRoom.getId(),
                savedRoom.getRoomType(),
                savedRoom.getRoomPrice()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    private RoomResponse buildRoomResponseWithPhoto(Room room) throws SQLException {
        byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
        RoomResponse roomResponse = getRoomResponse(room);
        if (photoBytes != null && photoBytes.length > 0) {
            String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
            roomResponse.setPhoto(base64Photo);
        }
        return roomResponse;
    }



    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room>  rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(Room room : rooms) {
            roomResponses.add(buildRoomResponseWithPhoto(room));
            }
        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false)  String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException {
        byte[] photoBytes =photo != null && !photo.isEmpty()? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes) : null;
        Room theRoom = roomService.updateRoom(roomId,roomType,roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : availableRooms) {
            roomResponses.add(buildRoomResponseWithPhoto(room));
            }
        if(roomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(roomResponses);
        }

    }


    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomId(@PathVariable Long roomId) {
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = getAllBookingByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings.stream().
                map(booking -> new BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getBookingConfirmationCode()
                )).toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null) {
            try{
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievalExcetion("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes, bookingInfo);
    }

    private List<BookedRoom> getAllBookingByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }
}
