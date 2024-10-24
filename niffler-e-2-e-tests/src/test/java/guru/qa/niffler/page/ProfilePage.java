package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@SuppressWarnings("UnusedReturnValue")
public class ProfilePage {
    private final ElementsCollection categoryList = $$(".MuiChip-root");
    private final SelenideElement archiveButtonSubmit = $x("//button[text()='Archive']");
    private final SelenideElement unarchiveButtonSubmit = $x("//button[text()='Unarchive']");
    private final SelenideElement successArchiveMessage = $(".MuiAlert-message");
    private final SelenideElement showArchivedCategoriesCheckbox = $("input[type='checkbox']");

    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $x("//button[text()='Save changes']");

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

    @Step("Кликнуть чекбокс Show archived")
    public ProfilePage clickShowArchiveCategoryButton() {
        showArchivedCategoriesCheckbox.click();
        return this;
    }

    @Step("Кликнуть кнопку Archive")
    public ProfilePage clickArchiveButtonSubmit() {
        archiveButtonSubmit.click();
        return this;
    }

    @Step("Кликнуть кнопку Unarchive")
    public ProfilePage clickUnarchiveButtonSubmit() {
        unarchiveButtonSubmit.click();
        return this;
    }

    @Step("Ввод имени: {name}")
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Сохранить изменения имени")
    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Step("Проверка имени: {name}")
    public ProfilePage checkName(String name) {
        nameInput.should(value(name));
        return this;
    }

    @Step("Проверка что категория {categoryName} успешно архивирована")
    public ProfilePage shouldBeVisibleArchiveSuccessMessage(String categoryName) {
        successArchiveMessage.shouldHave(text("Category " + categoryName + " is archived")).shouldBe(visible);
        return this;
    }

    @Step("Проверка что категория {categoryName} успешно разархивирована")
    public ProfilePage shouldBeVisibleUnarchiveSuccessMessage(String categoryName) {
        successArchiveMessage.shouldHave(text("Category " + categoryName + " is unarchived")).shouldBe(visible);
        return this;
    }

    @Step("Проверка что активная категория {categoryName} отображается")
    public ProfilePage shouldBeVisibleActiveCategory(String categoryName) {
        categoryList.findBy(text(categoryName)).shouldBe(visible);
        return this;
    }

    @Step("Проверка что архивная категория {categoryName} не отображается")
    public ProfilePage shouldNotBeVisibleArchiveCategory(String categoryName) {
        categoryList.findBy(text(categoryName)).shouldNotBe(visible);
        return this;
    }
}
