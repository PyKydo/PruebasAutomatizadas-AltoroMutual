package org.example.pages;

import org.example.utils.Config;
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

    public void abrirHome() {
        navegarConReintentos(Config.config("app.baseUrl"));
    }

    public void buscarTermino(String term) {
        ingresarTexto(SEARCH_INPUT, term);
        hacerClick(SEARCH_BUTTON);
        esperarVisibilidad(MAIN_HEADING);
    }

    public void irASeccion(String section) {
        By locator = SECTION_LINKS.get(section.toLowerCase());
        if (locator == null) {
            throw new IllegalArgumentException("Sección no soportada: " + section);
        }
        hacerClick(locator);
    }

    public String obtenerTituloPrincipal() {
        return obtenerTexto(MAIN_HEADING);
    }
}
