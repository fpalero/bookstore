package com.app.bookstore.services;

import org.springframework.stereotype.Service;

import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.exceptions.BookStoreErrorCodes;
import com.app.bookstore.exceptions.ClientException;
import com.app.bookstore.repositories.ClientsRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetLoyaltyPointsService implements StoreService<ClientEntity> {
    ClientsRepository clientsRepository;

    @Override
    public ClientEntity run(ClientEntity client) {

        if (client == null || client.getId() == null) {
            throw new ClientException("Client or client ID cannot be null", BookStoreErrorCodes.CLIENT_NOT_FOUND.getErrorCode());
        }

        return clientsRepository.findById(client.getId())
                .orElseThrow(() -> new ClientException("Client with ID " + client.getId() + " not found", BookStoreErrorCodes.CLIENT_NOT_FOUND.getErrorCode()));
    }

}
