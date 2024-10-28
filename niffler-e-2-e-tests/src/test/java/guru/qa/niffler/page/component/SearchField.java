package guru.qa.niffler.page.component;

import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

@SuppressWarnings("UnusedReturnValue")
public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("input[aria-label='search']"));
    }

    @Step("Поиск по значению: {value}")
    public SearchField search(String value) {
        getSelf().sendKeys(value);
        getSelf().sendKeys(Keys.ENTER);
        return this;
    }

    @Step("Очистить строку поиска")
    public SearchField clearIfNotEmpty() {
        getSelf().clear();
        return this;
    }
}
