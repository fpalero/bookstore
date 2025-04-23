package com.app.bookstore.entities;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a client in the bookstore system.
 * This class is used to map client data to the database.
 */
@Entity
@Table(name = "clients")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity implements StoreEntity {
   
    @Id
    protected Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    @Column(name = "loyalty_points")
    private Long loyaltyPoints;
}
