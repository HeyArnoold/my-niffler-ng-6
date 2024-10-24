package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
class FriendsWebTest {
    private static final Config CFG = Config.getInstance();

    @User(
            addedFriends = 1
    )
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends();

        friendsPage
                .checkNameInFriendList(user.testData().addedFriends().getFirst())
                .myFriendsHeaderShouldBeVisible()
                .friendRequestsHeaderShouldNotBeVisible();
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password()).goToFriends();

        friendsPage
                .friendListShouldBeEmpty()
                .myFriendsHeaderShouldNotBeVisible()
                .friendRequestsHeaderShouldNotBeVisible();
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password()).goToFriends();

        friendsPage
                .checkNameInRequestList(user.testData().income().getFirst())
                .myFriendsHeaderShouldNotBeVisible()
                .friendRequestsHeaderShouldBeVisible();
    }

    @User(
            outcomeInvitations = 1
    )
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends();

        friendsPage
                .clickAllPeople()
                .checkNameInAllPeopleList(user.testData().outcome().getFirst())
                .checkOutcomeInvitationInAllPeopleList(user.testData().outcome().getFirst());
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    void acceptInvitation(UserJson user) {
        String incomeUserName = user.testData().income().getFirst();

        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends();

        friendsPage
                .acceptFriend(incomeUserName)
                .checkNameInFriendList(incomeUserName);
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    void declineInvitation(UserJson user) {
        String incomeUserName = user.testData().income().getFirst();

        FriendsPage friendsPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends();

        friendsPage
                .declineFriend(incomeUserName)
                .checkNameNotDisplayedOnPage(incomeUserName);
    }
}
