package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

@SuppressWarnings("UnusedReturnValue")
public class SearchField {

    private final SelenideElement searchField = $("input[type='text']");

    public SearchField search(String value) {
        searchField.sendKeys(value);
        searchField.sendKeys(Keys.ENTER);
        return this;
    }

    public SearchField clearIfNotEmpty() {
        searchField.clear();
        return this;
    }
}
