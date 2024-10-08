package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {
    private final ElementsCollection categoryList = $$(".MuiChip-root");
    private final SelenideElement archiveButtonSubmit = $x("//button[text()='Archive']");
    private final SelenideElement unarchiveButtonSubmit = $x("//button[text()='Unarchive']");
    private final SelenideElement successArchiveMessage = $(".MuiAlert-message");
    private final SelenideElement showArchivedCategoriesCheckbox = $("input[type='checkbox']");

    public ProfilePage clickArchiveCategoryByName(String categoryName) {
        categoryList
                .findBy(text(categoryName))
                .parent()
                .$(".MuiIconButton-sizeMedium[aria-label='Archive category']")
                .click();
        return this;
    }

    public ProfilePage clickUnarchiveCategoryByName(String categoryName) {
        categoryList
                .findBy(text(categoryName))
                .parent()
                .$("[data-testid='UnarchiveOutlinedIcon']")
                .click();
        return this;
    }

    public ProfilePage clickShowArchiveCategoryButton() {
        showArchivedCategoriesCheckbox.click();
        return this;
    }

    public ProfilePage clickArchiveButtonSubmit() {
        archiveButtonSubmit.click();
        return this;
    }

    public ProfilePage clickUnarchiveButtonSubmit() {
        unarchiveButtonSubmit.click();
        return this;
    }

    public ProfilePage shouldBeVisibleArchiveSuccessMessage(String categoryName) {
        successArchiveMessage.shouldHave(text("Category " + categoryName + " is archived")).shouldBe(visible);
        return this;
    }

    public ProfilePage shouldBeVisibleUnarchiveSuccessMessage(String categoryName) {
        successArchiveMessage.shouldHave(text("Category " + categoryName + " is unarchived")).shouldBe(visible);
        return this;
    }

    public ProfilePage shouldBeVisibleActiveCategory(String categoryName) {
        categoryList.findBy(text(categoryName)).shouldBe(visible);
        return this;
    }

    public ProfilePage shouldNotBeVisibleArchiveCategory(String categoryName) {
        categoryList.findBy(text(categoryName)).shouldNotBe(visible);
        return this;
    }
}
