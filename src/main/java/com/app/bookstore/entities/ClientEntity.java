package com.app.bookstore.entities;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * Entity class representing a client in the bookstore system.
 * This class is used to map client data to the database.
 */
@Entity
@Table(name = "clients")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientEntity extends StoreEntity {
   
    private String name;
    private String email;
    private String phone;
    private String address;

    @Column(name = "loyalty_points")
    private Long loyaltyPoints;
}
