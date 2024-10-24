package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.TopMenuComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@SuppressWarnings("UnusedReturnValue")
public class MainPage {
    private final SelenideElement statisticsHeader = $("#stat h2");
    private final SelenideElement historyOfSpendingHeader = $("#spendings h2");

    private final TopMenuComponent topMenu = new TopMenuComponent();
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

    public MainPage checkTableContainsSpendingByDescription(String spendingDescription) {
        spendingTable.checkTableContainsSpendDescriptions(spendingDescription);
        return this;
    }

    public MainPage statisticsHeaderShouldHaveText(String text) {
        statisticsHeader.shouldHave(text(text)).shouldBe(visible);
        return this;
    }

    public MainPage historyOfSpendingHeaderShouldHaveText(String value) {
        historyOfSpendingHeader.shouldHave(text(value)).shouldBe(visible);
        return this;
    }

    public MainPage search(String spend) {
        spendingTable.searchSpendingByDescription(spend);
        return this;
    }
}
