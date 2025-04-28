package com.lazycode.lazyhotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;

    @Lob
    private Blob photo;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings = new ArrayList<>();

    public static String generateNumericCode(int length) {
        return RandomGenerator.getDefault()
                .ints(length, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public void addBooking(BookedRoom booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        booking.setRoom(this);
        isBooked = true;
        booking.setBookingConfiramtionCode(generateNumericCode(10));
    }
}
