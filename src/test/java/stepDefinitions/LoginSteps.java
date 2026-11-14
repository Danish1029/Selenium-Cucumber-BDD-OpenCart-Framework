package stepDefinitions;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import factory.DriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import testBase.BaseClass;

public class LoginSteps extends BaseClass {

    private static final Logger log =
            LogManager.getLogger(LoginSteps.class);

    private Properties testData;

    private HomePage homePage;
    private LoginPage loginPage;
    private MyAccountPage myAccountPage;


    public LoginSteps() {
        loadEnvironmentProperties();
    }


    private void loadEnvironmentProperties() {

        testData = new Properties();

        String env = System.getProperty("env");

        if (env == null || env.isBlank()) {
            env = "qa";
        }

        String path =
                "./src/test/resources/config/"
                + env.toLowerCase()
                + ".properties";

        try (FileInputStream file = new FileInputStream(path)) {

            testData.load(file);

            log.info("Environment properties loaded for: {}", env);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load environment properties from: " + path,
                    e);
        }
    }


    @Given("the user launches the OpenCart application")
    public void the_user_launches_the_opencart_application() {

        Assert.assertNotNull(
                getDriver(),
                "WebDriver was not initialized by Cucumber Hooks");

        log.info("OpenCart application launched successfully");
    }


    @When("the user navigates to the Login page")
    public void the_user_navigates_to_the_login_page() {

        homePage = new HomePage(DriverManager.getDriver());

        homePage.clickMyAccount();
        log.info("Clicked My Account");

        homePage.clickLogin();
        log.info("Clicked Login");

        loginPage = new LoginPage(DriverManager.getDriver());
    }


    @When("the user enters valid login credentials")
    public void the_user_enters_valid_login_credentials() {

        String email = testData.getProperty("email");
        String password = testData.getProperty("password");

        Assert.assertNotNull(email, "Email is missing from environment properties");
        Assert.assertNotNull(password, "Password is missing from environment properties");

        log.info("Entering valid login credentials");

        loginPage.setEmail(email);
        loginPage.setPassword(password);
    }


    @When("the user clicks the Login button")
    public void the_user_clicks_the_login_button() {

        loginPage.clickLogin();
        log.info("Clicked Login button");
    }


    @Then("the user should be successfully logged in")
    public void the_user_should_be_successfully_logged_in() {

        myAccountPage = new MyAccountPage(DriverManager.getDriver());

        boolean accountPageDisplayed = myAccountPage.isMyAccountPageExists();

        if (!accountPageDisplayed) {
            captureLoginFailureEvidence("ValidLogin_AssertionFailed");
        }

        Assert.assertTrue(
                accountPageDisplayed,
                "Login failed: My Account page was not displayed");

        log.info("Login validation successful");
    }


    @When("the user enters email {string} and password {string}")
    public void the_user_enters_email_and_password(String email, String password) {

        log.info("Entering credentials for negative login scenario");

        loginPage.setEmail(email);
        loginPage.setPassword(password);
    }


    @Then("the user should see a login warning message")
    public void the_user_should_see_a_login_warning_message() {

        String warningMessage = loginPage.getLoginWarningMessage();

        log.info("Login warning message: {}", warningMessage);

        // Capture screenshot whenever login fails (negative login warning displayed or assertion fails)
        captureLoginFailureEvidence("InvalidLogin_WarningDisplayed");

        Assert.assertTrue(
                warningMessage.contains("Warning: No match for E-Mail Address and/or Password."),
                "Expected invalid login warning was not displayed");
    }


    // ============================================================
    // Helper: Capture Dedicated Screenshot on Failed Login
    // ============================================================

    private void captureLoginFailureEvidence(String reason) {
        try {
            String savedPath = captureFailureScreenshot("FAILED_LOGIN", reason);
            log.warn("Captured failed login screenshot ({}): {}", reason, savedPath);

            byte[] screenshot = getScreenshotBytes();
            if (screenshot != null && screenshot.length > 0) {
                Allure.addAttachment(
                        "Failed Login - " + reason,
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png");
            }
        } catch (Exception e) {
            log.error("Unable to capture failed login screenshot: {}", e.getMessage());
        }
    }
}