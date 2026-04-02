package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("UnusedReturnValue")
public class MainPage extends BasePage<MainPage> {
    private final SelenideElement statisticsHeader = $("#stat h2");
    private final SelenideElement statisticCanvas = $("canvas[role='img']");
    private final ElementsCollection statisticCells = $$("#legend-container li");

    private final Header topMenu = new Header();
    private final SpendingTable spendingTable = new SpendingTable();
    private final StatComponent statComponent = new StatComponent();


    public EditSpendingPage editSpending(String spendingDescription) {
        return spendingTable.editSpending(spendingDescription);
    }

    public ProfilePage goToProfile() {
        return topMenu.goToProfilePage();
    }

    public FriendsPage goToFriends() {
        return topMenu.goToFriendsPage();
    }

    public MainPage search(String spend) {
        spendingTable.searchSpendingByDescription(spend);
        return this;
    }

    public EditSpendingPage addSpending() {
        return topMenu.addSpendingPage();
    }

    public MainPage deleteSpending(String desc) {
        spendingTable.deleteSpending(desc);
        return this;
    }

    @Step("Проверка наличия траты по Description: {spendingDescription}")
    public MainPage checkTableContainsSpendingByDescription(String spendingDescription) {
        spendingTable.checkTableContainsSpendDescriptions(spendingDescription);
        return this;
    }

    @Step("Check that page is loaded")
    @Override
    public @Nonnull MainPage checkThatPageLoaded() {
        topMenu.getSelf().should(visible).shouldHave(text("Niffler"));
        statisticsHeader.should(visible).shouldHave(text("Statistics"));
        spendingTable.getSelf().should(visible).shouldHave(text("History of Spendings"));
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    @Step("Check that statistic image matches the expected image")
    @Nonnull
    public MainPage checkStatisticImage(BufferedImage expectedImage) throws IOException {
        BufferedImage actualImage = ImageIO.read(statisticCanvas.screenshot());
        assertFalse(new ScreenDiffResult(actualImage, expectedImage));
        return this;
    }

    @Step("Check that statistic cells contain texts {texts}")
    @Nonnull
    public MainPage checkStatisticCells(List<String> texts) {
        for (String text : texts) {
            statisticCells.findBy(text(text)).shouldBe(visible);
        }
        return this;
    }

    public MainPage checkSpendingTable(SpendJson... expectedSpends) {
        spendingTable.checkSpendingTable(expectedSpends);
        return this;
    }

    public MainPage checkBubblesContains(Bubble... bubbles) {
        statComponent.checkBubblesContains(bubbles);
        return this;
    }

    public MainPage checkBubblesInAnyOrder(Bubble... bubbles) {
        statComponent.checkBubblesInAnyOrder(bubbles);
        return this;
    }

    public MainPage checkBubbles(Bubble... bubbles) {
        statComponent.checkBubbles(bubbles);
        return this;
    }
}
