package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    private final SelenideElement alert = $(".MuiSnackbar-root");
    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    public abstract T checkThatPageLoaded();

    @Step("Проверка появления информационного сообщения: {expectedText}")
    public @Nonnull T checkAlertMessage(String expectedText) {
        alert.should(Condition.visible).should(Condition.text(expectedText));
        return (T) this;
    }

    @Step("Проверка появления сообщения об ошибке: {expectedText}")
    public @Nonnull T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }
}
