package com.github.mcoder.exploringreactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReservationRequest {
    private final String name;
}
