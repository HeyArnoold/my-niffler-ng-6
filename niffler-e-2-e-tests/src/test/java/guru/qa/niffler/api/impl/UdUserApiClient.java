package guru.qa.niffler.api.impl;

import guru.qa.niffler.api.UdUserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UdUserApiClient extends RestClient {

    private final UdUserApi userApi;

    public UdUserApiClient() {
        super(CFG.userdataUrl());
        this.userApi = retrofit.create(UdUserApi.class);
    }

    @Step("Получение текущего пользователя по имени: {username}")
    public @Nullable UserJson getCurrentUser(@Nonnull String username) {
        final Response<UserJson> response;
        try {
            response = userApi.getCurrentUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Отправка приглашения от пользователя {username} пользователю {targetUsername}")
    public @Nullable UserJson sendInvitation(@Nonnull String username, @Nonnull String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.sendInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Принятие приглашения от пользователя {username} пользователю {targetUsername}")
    public @Nullable UserJson acceptInvitation(@Nonnull String username, @Nonnull String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.acceptInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
