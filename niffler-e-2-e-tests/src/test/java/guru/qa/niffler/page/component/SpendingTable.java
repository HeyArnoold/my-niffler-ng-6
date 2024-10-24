package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.enums.PeriodWeb;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@SuppressWarnings("UnusedReturnValue")
public class SpendingTable {

    private final SelenideElement spendsTable = $("#spendings tbody");
    private final SelenideElement periodButton = $(".MuiBox-root #period");
    private final SelenideElement deleteButton = $(".MuiBox-root #delete");
    private final SelenideElement deleteConfirmButton = $x("//div[@role='dialog'] //button[text()='Delete']");

    private static final ElementsCollection timePeriods = $$("[role='option']");

    private static final String spendingRow = "tr";

    private final SearchField searchField = new SearchField();

    public SpendingTable selectPeriod(PeriodWeb period) {
        periodButton.click();
        timePeriods.find(text(period.getTitle())).click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        spendsTable.$$(spendingRow).find(text(description)).$("[aria-label='Edit spending']").click();
        return new EditSpendingPage();
    }

    public SpendingTable deleteSpending(String description) {
        spendsTable.$$(spendingRow).find(text(description)).$("[type='checkbox']").click();
        deleteButton.shouldBe(visible).click();
        $(deleteConfirmButton).shouldBe(visible).click();
        return this;
    }

    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    public SpendingTable checkTableContainsSpendDescriptions(String... expectedSpendDescriptions) {
        spendsTable.$$("td:nth-child(4)").shouldHave(textsInAnyOrder(expectedSpendDescriptions));
        return this;
    }

    public SpendingTable checkTableSize(int expectedSize) {
        spendsTable.$$(spendingRow).shouldHave(size(expectedSize));
        return this;
    }
}
