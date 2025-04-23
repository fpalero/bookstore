package com.app.bookstore.services;
import com.app.bookstore.entities.StoreEntity;

public interface StoreService<T extends StoreEntity> {
    // This class is a placeholder for the service layer in the bookstore
    // application.
    // It will contain methods to handle business logic and interact with the
    // database.
    public StoreEntity run(T entity);
}