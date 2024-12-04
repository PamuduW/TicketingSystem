package com.backend.ticketing_System.model;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Ticket {
    @Id
    @NonNull
    private String ticketId;
    private String customerId;
}