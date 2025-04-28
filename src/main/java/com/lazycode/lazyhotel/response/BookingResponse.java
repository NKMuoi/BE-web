package com.lazycode.lazyhotel.response;

import com.lazycode.lazyhotel.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data

@AllArgsConstructor
@NoArgsConstructor

public class BookingResponse {
    private Long bookingId;


    private LocalDate checkInDate;


    private LocalDate checkOutDate;


    private String guestFullName;


    private String guestEmail;


    private int NumOfAdults;


    private int NumOfChildren;


    private int totalNumOfGuests;

    private String bookingConfiramtionCode;

    private RoomResponse room;

    public BookingResponse(Long bookingId, LocalDate checkInDate,
                           LocalDate checkOutDate,
                           String bookingConfiramtionCode) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfiramtionCode = bookingConfiramtionCode;
    }


}
