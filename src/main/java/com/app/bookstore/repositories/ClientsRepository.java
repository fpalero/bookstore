package com.app.bookstore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.bookstore.entities.ClientEntity;

@Repository
public interface ClientsRepository extends JpaRepository<ClientEntity, Long> {
}
