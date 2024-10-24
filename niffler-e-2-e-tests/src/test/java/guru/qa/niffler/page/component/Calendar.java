package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class Calendar {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final SelenideElement calendar = $("input[name='date']");

    public Calendar selectDateInCalendar(Date date) {
        String formattedDate = dateFormat.format(date);

        calendar.clear();
        calendar.setValue(formattedDate);
        return this;
    }
}
