package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import io.qameta.allure.Description;
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
        usersDbClient.deleteUser("createByXA");
    }

    @Test
    @Description("откатывает транзакции в обе базы")
    void chainedSpringTransactionTest() {
        UserJson user = usersDbClient.createUserSpringChainedXaTransaction(
                new UserJson(
                        null,
                        "createByXA",
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
    @Description("Не откатывает транзакцию в базе auth")
    void chainedJdbcTransactionTest() {
        UserJson user = usersDbClient.createUserJdbcChainedXaTransaction(
                new UserJson(
                        null,
                        "createByXA",
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
    @Description("Пишет в auth, не пишет в userdata")
    void createUserSpringWithoutTxTest() {
        UserJson user = usersDbClient.createUserSpringWithoutTx(
                new UserJson(
                        null,
                        "createByXA",
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
    @Description("Пишет в auth, не пишет в userdata")
    void createUserJdbcWithoutTxTest() {
        UserJson user = usersDbClient.createUserJdbcWithoutTx(
                new UserJson(
                        null,
                        "createByXA",
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
}
