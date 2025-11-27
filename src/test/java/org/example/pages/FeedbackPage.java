package org.example.pages;

import org.example.utils.ConfigLoader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FeedbackPage extends BasePage {

    private static final By NAME_INPUT = By.name("name");
    private static final By EMAIL_INPUT = By.name("email_addr");
    private static final By SUBJECT_INPUT = By.name("subject");
    private static final By MESSAGE_TEXTAREA = By.name("comments");
    private static final By SUBMIT_BUTTON = By.xpath("//input[@name='submit' and @type='submit']");
    private static final By RESPONSE_PARAGRAPH = By.xpath("//body/table/tbody/tr[2]/td[2]/div/p");

    public FeedbackPage(WebDriver driver) {
        super(driver);
    }

    public void openPage() {
        navigateWithRetry(ConfigLoader.get("app.baseUrl") + "/feedback.jsp");
    }

    public void fillForm(String nombre, String email, String asunto, String mensaje) {
        type(NAME_INPUT, nombre);
        type(EMAIL_INPUT, email);
        type(SUBJECT_INPUT, asunto);
        type(MESSAGE_TEXTAREA, mensaje);
    }

    public void submit() {
        click(SUBMIT_BUTTON);
    }

    public String getResponseMessage() {
        return getText(RESPONSE_PARAGRAPH);
    }
}
