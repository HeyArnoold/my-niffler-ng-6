package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
class LoginWebTest {
    private static final Config CFG = Config.getInstance();
    private static final String STATISTICS_TEXT = "Statistics";
    private static final String HISTORY_OF_SPENDING_TEXT = "History of Spendings";
    private static final String FAILED_LOGIN_MESSAGE = "Неверные учетные данные пользователя";


    @User(
            categories = {
                    @Category(name = "cat_1", archived = false),
                    @Category(name = "cat_2", archived = true),
            },
            spendings = {
                    @Spending(
                            category = "cat_3",
                            description = "test_spend",
                            amount = 100
                    )
            }
    )
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage.statisticsHeaderShouldHaveText(STATISTICS_TEXT)
                .historyOfSpendingHeaderShouldHaveText(HISTORY_OF_SPENDING_TEXT);
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername("duck")
                .setPassword("123")
                .clickSubmitButton()
                .formErrorShouldHaveText(FAILED_LOGIN_MESSAGE);
    }
}
