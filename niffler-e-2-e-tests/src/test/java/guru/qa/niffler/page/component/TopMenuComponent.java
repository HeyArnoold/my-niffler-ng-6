package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.ProfilePage;

import static com.codeborne.selenide.Selenide.$;

public class TopMenuComponent {
    private final SelenideElement personIconButton = $("button[aria-label='Menu']");
    private final SelenideElement goToProfileButton = $("[href='/profile']");
    private final SelenideElement goToFriendsButton = $("[href='/people/friends']"); //
    private final SelenideElement goToAllPeopleButton = $("[href='/people/all']");

    public ProfilePage goToProfilePage() {
        personIconButton.click();
        goToProfileButton.click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsPage() {
        personIconButton.click();
        goToFriendsButton.click();
        return new FriendsPage();
    }

    public FriendsPage goToAllPeoplePage() {
        personIconButton.click();
        goToAllPeopleButton.click();
        return new FriendsPage();
    }
}
