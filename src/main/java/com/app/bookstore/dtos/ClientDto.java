package com.app.bookstore.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private Long loyaltyPoints;
}
