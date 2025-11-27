package org.example.pages;

import org.example.utils.ConfigLoader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class BusquedaNavegacionPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("query");
    private static final By SEARCH_BUTTON = By.xpath("//input[@type='submit' and @value='Go']");
    private static final By MAIN_HEADING = By.tagName("h1");

    private static final Map<String, By> SECTION_LINKS = Map.of(
            "personal", By.id("LinkHeader2"),
            "small business", By.id("LinkHeader3")
    );

    public BusquedaNavegacionPage(WebDriver driver) {
        super(driver);
    }

    public void openHomePage() {
        navigateWithRetry(ConfigLoader.get("app.baseUrl"));
    }

    public void searchFor(String term) {
        type(SEARCH_INPUT, term);
        click(SEARCH_BUTTON);
        waitForVisibility(MAIN_HEADING);
    }

    public void navigateToSection(String section) {
        By locator = SECTION_LINKS.get(section.toLowerCase());
        if (locator == null) {
            throw new IllegalArgumentException("Sección no soportada: " + section);
        }
        click(locator);
    }

    public String getMainHeadingText() {
        return getText(MAIN_HEADING);
    }
}
