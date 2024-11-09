package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.genRandomUsername;

@ExtendWith(BrowserExtension.class)
class ProfileWebTest {
    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            categories = @Category()
    )
    @Test
    void archivedCategoryShouldNotPresentInCategoriesList(CategoryJson category) {
        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .goToProfile();

        profilePage
                .clickArchiveCategoryByName(category.name())
                .clickArchiveButtonSubmit()
                .shouldBeVisibleArchiveSuccessMessage(category.name())
                .shouldNotBeVisibleArchiveCategory(category.name());
    }

    @User(
            username = "duck",
            categories = @Category(archived = true)
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .goToProfile();

        profilePage
                .clickShowArchiveCategoryButton()
                .clickUnarchiveCategoryByName(category.name())
                .clickUnarchiveButtonSubmit()
                .shouldBeVisibleUnarchiveSuccessMessage(category.name())
                .clickShowArchiveCategoryButton()
                .shouldBeVisibleActiveCategory(category.name());
    }

    @User
    @Test
    void changeName(UserJson user) {
        String name = genRandomUsername();

        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfile();

        profilePage
                .setName(name)
                .saveChanges()
                .checkName(name)
                .checkAlertMessage("Profile successfully updated");
    }

    @User
    @ScreenShotTest(value = "img/profile-expected.png")
    void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage) throws IOException {
        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToProfile();

        profilePage
                .checkThatPageLoaded()
                .uploadPhotoFromClasspath("img/cat.png")
                .saveChanges()
                .checkProfileImage(expectedProfileImage);
    }
}
