package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.api.UserApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Order(Integer.MAX_VALUE)
public class FullUsersTest {

    @User
    @Test
    void shouldReturnNotEmptyUsersListTest(UserJson user) {
        UserApiClient usersApiClient = new UserApiClient();
        List<UserJson> response = usersApiClient.allUsers(user.username(), null);
        assertFalse(response.isEmpty(), "Список пользователей должен быть не пустым");
    }
}
