package com.lazycode.lazyhotel.service;

import com.lazycode.lazyhotel.model.BookedRoom;

import java.util.List;

public interface IBookingService  {

    List<BookedRoom> getAllBookings();

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findingByBookingConfirmationCode(String confirmationCode);

    void cancelBooking(Long bookingId);




}
