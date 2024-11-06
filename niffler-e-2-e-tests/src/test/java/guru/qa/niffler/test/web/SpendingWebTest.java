package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@WebTest
class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

//    @User(
//            username = "duck",
//            spendings = @Spending(
//                    category = "Обучение",
//                    description = "Обучение Advanced 2.0",
//                    amount = 79990
//            )
//    )
//    @Test
//    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
//        final String newDescription = "Обучение Niffler Next Generation";
//
//        Selenide.open(CFG.frontUrl(), LoginPage.class)
//                .login("duck", "12345")
//                .editSpending(spend.description())
//                .setNewSpendingDescription(newDescription)
//                .save()
//                .search(newDescription)
//                .checkTableContainsSpendingByDescription(newDescription);
//    }
//
//    @User
//    @Test
//    void addSpendTest(UserJson user) {
//        String category = genRandomCategory();
//        String description = genRandomSentence(2);
//
//        EditSpendingPage spendingPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
//                .login(user.username(), user.testData().password())
//                .addSpending();
//
//        MainPage mainPage = spendingPage
//                .setSpendingCategory(category)
//                .setNewSpendingDescription(description)
//                .setSpendingAmount("10")
//                .setDate(new Date())
//                .save()
//                .checkAlertMessage("New spending is successfully created");
//
//        mainPage
//                .checkTableContainsSpendingByDescription(description);
//    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expectedStatisticImage) throws IOException, InterruptedException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();

        Thread.sleep(3000);
        mainPage
                .checkStatisticImage(expectedStatisticImage);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/clear-stat.png")
    void checkStatComponentAfterDeleteSpendTest(UserJson user, BufferedImage expectedStatisticImage) throws IOException, InterruptedException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();

        mainPage
                .deleteSpending("Обучение Advanced 2.0");
        Thread.sleep(1000);
        mainPage
                .checkStatisticImage(expectedStatisticImage);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/edit-stat.png")
    void checkStatComponentAfterEditSpendTest(UserJson user, BufferedImage expectedStatisticImage) throws IOException, InterruptedException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();

        mainPage
                .editSpending("Обучение Advanced 2.0")
                .setSpendingAmount("80000")
                .save()
                .checkStatisticCells(List.of("Обучение 80000 ₽"))
                .checkThatPageLoaded();
        Thread.sleep(1000);
        mainPage
                .checkStatisticImage(expectedStatisticImage);
    }

    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Развлечения", archived = true),
                    @Category(name = "Продукты", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 1000
                    ),
                    @Spending(
                            category = "Развлечения",
                            description = "Поход в кино",
                            amount = 100
                    ),
                    @Spending(
                            category = "Продукты",
                            description = "Покупка продуктов",
                            amount = 3000
                    )
            }
    )
    @ScreenShotTest(value = "img/archived-stat.png")
    void checkStatComponentAfterArchivedCategoryTest(UserJson user, BufferedImage expectedStatisticImage) throws IOException, InterruptedException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();


        mainPage
                .checkStatisticCells(List.of("Обучение 1000 ₽", "Archived 3100 ₽"))
                .checkThatPageLoaded();
        Thread.sleep(1000);
        mainPage
                .checkStatisticImage(expectedStatisticImage);
    }
}

