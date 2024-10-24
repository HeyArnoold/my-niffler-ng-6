package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@SuppressWarnings("UnusedReturnValue")
public class FriendsPage {
    private final ElementsCollection friendList = $$("tbody#friends tr");
    private final ElementsCollection friendReqList = $$("#requests tr");
    private final ElementsCollection allPeopleList = $$("tbody#all tr");

    private final SelenideElement allPeopleButton = $("[aria-label='People tabs'] [href='/people/all']");
    private final SelenideElement emptyFriendListText = $x("//p[text()='There are no users yet']");
    private final SelenideElement myFriendsHeader = $x("//h2[text()='My friends']");
    private final SelenideElement friendRequestsHeader = $x("//h2[text()='Friend requests']");
    private final SelenideElement declineConfirmButton = $x("//div[@role='dialog'] //button[text()='Decline']");

    private final SearchField searchField = new SearchField();

    public FriendsPage clickAllPeople() {
        allPeopleButton.click();
        return this;
    }

    public FriendsPage searchFriend(String username) {
        searchField.search(username);
        return this;
    }

    public FriendsPage acceptFriend(String name) {
        friendReqList.findBy(text(name))
                .$(byText("Accept")).click();
        return this;
    }

    public FriendsPage declineFriend(String name) {
        friendReqList.findBy(text(name))
                .$(byText("Decline")).click();
        declineConfirmButton.click();
        return this;
    }

    public FriendsPage checkNameInFriendList(String name) {
        friendList.findBy(text(name)).shouldBe(visible);
        return this;
    }

    public FriendsPage checkNamesInFriendList(List<String> expectedUsernames) {
        for (String expectedUsername : expectedUsernames) {
            searchFriend(expectedUsername);
            friendList.find(text(expectedUsername)).should(visible);
        }
        return this;
    }

    public FriendsPage checkNameInRequestList(String name) {
        friendReqList.findBy(text(name)).shouldBe(visible);
        return this;
    }

    public FriendsPage checkNameInAllPeopleList(String name) {
        allPeopleList
                .findBy(text(name)).shouldBe(visible);
        return this;
    }

    public FriendsPage checkOutcomeInvitationInAllPeopleList(String name) {
        allPeopleList.findBy(text(name))
                .shouldHave(text("Waiting..."));
        return this;
    }

    public FriendsPage friendListShouldBeEmpty() {
        emptyFriendListText.shouldBe(visible);
        friendList.shouldBe(CollectionCondition.empty);
        return this;
    }

    public FriendsPage myFriendsHeaderShouldBeVisible() {
        myFriendsHeader.shouldBe(visible);
        return this;
    }

    public FriendsPage myFriendsHeaderShouldNotBeVisible() {
        myFriendsHeader.shouldNotBe(visible);
        return this;
    }

    public FriendsPage friendRequestsHeaderShouldBeVisible() {
        friendRequestsHeader.shouldBe(visible);
        return this;
    }

    public FriendsPage friendRequestsHeaderShouldNotBeVisible() {
        friendRequestsHeader.shouldNotBe(visible);
        return this;
    }

    public FriendsPage checkNameNotDisplayedOnPage(String name) {
        $x(String.format("//*[text()='%s']", name))
                .shouldNotBe(visible);
        return this;
    }
}
