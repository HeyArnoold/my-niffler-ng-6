package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
class ProfileWebTest {
    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            categories = @Category(archived = false)
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
}
