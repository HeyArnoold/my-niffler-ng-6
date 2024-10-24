package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TopMenuComponent {
    private final SelenideElement personIconButton = $("header button[aria-label='Menu']");
    private final SelenideElement goToMainButton = $("header [href='/main']");
    private final SelenideElement newSpendingButton = $x("//header //*[text()='New spending']");

    private final SelenideElement goToProfileButton = $("[role='menu'] [href='/profile']");
    private final SelenideElement goToFriendsButton = $("[role='menu'] [href='/people/friends']"); //
    private final SelenideElement goToAllPeopleButton = $("[role='menu'] [href='/people/all']");
    private final SelenideElement signOutButton = $x("//*[@role='menu'] //*[text()='Sign out']");

    @Step("Перейти на страницу профиля")
    public ProfilePage goToProfilePage() {
        personIconButton.click();
        goToProfileButton.click();
        return new ProfilePage();
    }

    @Step("Перейти на 'Friends' страницу")
    public FriendsPage goToFriendsPage() {
        personIconButton.click();
        goToFriendsButton.click();
        return new FriendsPage();
    }

    @Step("Перейти на 'All People' страницу")
    public FriendsPage goToAllPeoplePage() {
        personIconButton.click();
        goToAllPeopleButton.click();
        return new FriendsPage();
    }

    @Step("Разлогинить пользователя")
    public LoginPage signOut() {
        personIconButton.click();
        signOutButton.click();
        return new LoginPage();
    }

    @Step("Добавить новую трату")
    public EditSpendingPage addSpendingPage() {
        newSpendingButton.click();
        return new EditSpendingPage();
    }

    @Step("Перейти на главную страницу")
    public MainPage goToMainPage() {
        goToMainButton.click();
        return new MainPage();
    }
}
