package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitPasswordInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement successRegisterMessage = $(".form__paragraph_success");
    private final SelenideElement formError = $(".form__error");

    @Step("Ввод данных пользователя: логин {login}")
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Ввод данных пользователя: пароль {password}")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Ввод данных пользователя: Submit password {password}")
    public RegisterPage setPasswordSubmit(String password) {
        submitPasswordInput.setValue(password);
        return this;
    }

    @Step("Кликнуть кнопку Submit registration")
    public RegisterPage submitRegistration() {
        submitButton.click();
        return this;
    }

    @Step("Проверка успешного сообщения: {text}")
    public void successRegisterMessageShouldHaveText(String text) {
        successRegisterMessage.shouldHave(text(text)).shouldBe(visible);
    }

    @Step("Проверка сообщения об ошибке: {text}")
    public void formErrorShouldHaveText(String text) {
        formError.shouldHave(text(text)).shouldBe(visible);
    }
}
