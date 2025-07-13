package com.jalo.tenderstream.model;

import java.time.LocalDateTime;

public record Tender(
        String number,
        String title,
        LocalDateTime startingDate,
        LocalDateTime closingDate,
        String fee,
        String attribute
) {
    public Tender {
        if (number == null || number.isBlank())
            throw new IllegalArgumentException("Tender number cannot be blank");
    }

    @Override
    public String toString() {
        return number + " | " + title;
    }
}
