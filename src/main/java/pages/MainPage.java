package pages;

import org.openqa.selenium.WebDriver;

import static net.otus.edu.utils.Constants.BASE_URL;

public class MainPage extends AbstractPage {
    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage open() {
        driver.get(BASE_URL);
        return this;
    }

    public PlatformHeader goToPlatformHeader() {
        return new PlatformHeader(driver);
    }
}