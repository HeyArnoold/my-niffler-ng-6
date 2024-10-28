package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

public abstract class BaseComponent<T extends BaseComponent<?>> {

    protected final SelenideElement self;

    public BaseComponent(SelenideElement self) {
        this.self = self;
    }

    public @Nonnull SelenideElement getSelf() {
        return self;
    }
}
