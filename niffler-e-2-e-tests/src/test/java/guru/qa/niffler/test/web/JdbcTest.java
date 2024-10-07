package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

@Disabled
public class JdbcTest {

    UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-2",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        null
                )
        );

        System.out.println(spend);
    }

    @Test
    void springJdbcTest() {
        UserJson user = usersDbClient.createUser(
                new UserJson(
                        null,
                        "valentin-5",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                ), "12345"
        );
        System.out.println(user);
    }

    @Test
    void deleteUserTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.deleteUser("");
    }

    @Test
    void addFriendsTest() {

        UserJson myself = usersDbClient.createUserByRepo("myself", "12345");

        UserJson friend = usersDbClient.createUserByRepo("friend", "12345");

        UserJson income = usersDbClient.createUserByRepo("income", "12345");

        UserJson outcome = usersDbClient.createUserByRepo("outcome", "12345");


        usersDbClient.addIncomeInvitation(myself, income);
        usersDbClient.addOutcomeInvitation(myself, outcome);
        usersDbClient.addFriends(myself, friend);
    }
}
