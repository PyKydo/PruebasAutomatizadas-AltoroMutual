package org.example.pages;

import org.example.utils.ConfigLoader;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("uid");
    private static final By PASSWORD_INPUT = By.id("passw");
    private static final By SUBMIT_BUTTON = By.name("btnSubmit");
    private static final By SIGN_OFF_LINK = By.linkText("Sign Off");
    private static final By ERROR_MESSAGE = By.id("_ctl0__ctl0_Content_Main_message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void openLoginPage() {
        String loginUrl = ConfigLoader.get("app.baseUrl") + "/login.jsp";
        navigateWithRetry(loginUrl);
        dismissPotentialAlert();
    }

    public void performLogin(String username, String password) {
        type(USERNAME_INPUT, username == null ? "" : username);
        type(PASSWORD_INPUT, password == null ? "" : password);
        click(SUBMIT_BUTTON);
        dismissPotentialAlert();
    }

    public boolean isUserLoggedIn() {
        try {
            return waitForVisibility(SIGN_OFF_LINK).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorMessageVisible() {
        try {
            return waitForVisibility(ERROR_MESSAGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void dismissPotentialAlert() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {
        }
    }
}
