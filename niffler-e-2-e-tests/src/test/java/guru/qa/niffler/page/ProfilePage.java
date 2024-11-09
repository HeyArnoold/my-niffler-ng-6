package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("UnusedReturnValue")
public class ProfilePage extends BasePage<ProfilePage> {
    private final ElementsCollection categoryList = $$(".MuiChip-root");
    private final SelenideElement archiveButtonSubmit = $x("//button[text()='Archive']");
    private final SelenideElement unarchiveButtonSubmit = $x("//button[text()='Unarchive']");
    private final SelenideElement successArchiveMessage = $(".MuiAlert-message");
    private final SelenideElement showArchivedCategoriesCheckbox = $("input[type='checkbox']");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement profileImage = $(".MuiAvatar-img");

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

    @Override
    public ProfilePage checkThatPageLoaded() {
        nameInput.should(visible);
        return this;
    }

    @Step("Upload photo from classpath")
    @Nonnull
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    @Step("Check profile image matches the expected image")
    @Nonnull
    public ProfilePage checkProfileImage(BufferedImage expectedImage) throws IOException {
        BufferedImage actualImage = ImageIO.read(profileImage.screenshot());
        assertFalse(new ScreenDiffResult(actualImage, expectedImage));
        return this;
    }
}
