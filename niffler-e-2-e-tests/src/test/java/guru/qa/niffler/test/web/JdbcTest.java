package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

public class JdbcTest {

    private static final UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-3",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx-3",
                        "duck"
                )
        );

        System.out.println(spend);
    }


    @ValueSource(strings = {
            "valentin-10"
    })
    @ParameterizedTest
    void hibernateTest(String uname) {

        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );

        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createFriends(user, 1);
    }

    @ValueSource(strings = {
            "solomon.wilkinson"
    })
    @ParameterizedTest
    void deleteTest(String uname) {

        usersDbClient.deleteUser(uname);
    }
}
