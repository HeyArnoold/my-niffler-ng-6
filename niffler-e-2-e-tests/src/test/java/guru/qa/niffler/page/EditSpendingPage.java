package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;

import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement categoryInput = $("#category");

    private final Calendar calendar = new Calendar();

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    public EditSpendingPage setSpendingAmount(String amount) {
        amountInput.setValue(amount);
        return this;
    }

    public EditSpendingPage setSpendingCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    public EditSpendingPage setDate(Date date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
