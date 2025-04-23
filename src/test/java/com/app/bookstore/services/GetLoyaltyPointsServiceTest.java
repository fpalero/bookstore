package com.app.bookstore.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.exceptions.ClientException;

@SpringBootTest
public class GetLoyaltyPointsServiceTest {

    @Test
    @Description("Sanity test")
    public void sanity() {
        assertThat(new GetLoyaltyPointsService(null)).isNotNull();
    }

    @Test
    @Description("The servie ruten an exception when the client is null")
    public void testGetLoyaltyPointsServiceWithNullClient() {
        GetLoyaltyPointsService service = new GetLoyaltyPointsService(null);

        assertThatThrownBy(() -> service.run(null))
            .isInstanceOf(ClientException.class)
            .hasMessage("Client or client ID cannot be null");

        ClientEntity client = new ClientEntity();
        client.setId(null);

        assertThatThrownBy(() -> service.run(client))
            .isInstanceOf(ClientException.class)
            .hasMessage("Client or client ID cannot be null");
    }

}
