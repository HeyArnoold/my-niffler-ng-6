package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@SuppressWarnings("UnusedReturnValue")
public class MainPage extends BasePage<MainPage> {
    private final SelenideElement statisticsHeader = $("#stat h2");

    private final Header topMenu = new Header();
    private final SpendingTable spendingTable = new SpendingTable();


    public EditSpendingPage editSpending(String spendingDescription) {
        return spendingTable.editSpending(spendingDescription);
    }

    public ProfilePage goToProfile() {
        return topMenu.goToProfilePage();
    }

    public FriendsPage goToFriends() {
        return topMenu.goToFriendsPage();
    }

    public MainPage search(String spend) {
        spendingTable.searchSpendingByDescription(spend);
        return this;
    }

    public EditSpendingPage addSpending() {
        return topMenu.addSpendingPage();
    }

    @Step("Проверка наличия траты по Description: {spendingDescription}")
    public MainPage checkTableContainsSpendingByDescription(String spendingDescription) {
        spendingTable.checkTableContainsSpendDescriptions(spendingDescription);
        return this;
    }

    @Step("Check that page is loaded")
    @Override
    public @Nonnull MainPage checkThatPageLoaded() {
        topMenu.getSelf().should(visible).shouldHave(text("Niffler"));
        statisticsHeader.should(visible).shouldHave(text("Statistics"));
        spendingTable.getSelf().should(visible).shouldHave(text("History of Spendings"));
        return this;
    }
}
