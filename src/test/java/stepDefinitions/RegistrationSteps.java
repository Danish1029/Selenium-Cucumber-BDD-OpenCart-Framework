package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import factory.DriverManager;

import pageObjects.AccountRegistrationPage;
import pageObjects.HomePage;
import testBase.BaseClass;

public class RegistrationSteps extends BaseClass {

    private static final Logger log =
            LogManager.getLogger(RegistrationSteps.class);

    private HomePage homePage;
    private AccountRegistrationPage registrationPage;

    private String password;


    @Given("the user launches the OpenCart application for registration")
    public void user_launches_application_for_registration() {

        Assert.assertNotNull(
                getDriver(),
                "WebDriver was not initialized by Cucumber Hooks");

        log.info("OpenCart application launched for registration");
    }


    @When("the user navigates to the Registration page")
    public void user_navigates_to_registration_page() {

    	homePage =
    	        new HomePage(
    	                DriverManager.getDriver());

        homePage.clickMyAccount();

        log.info("Clicked My Account");

        homePage.clickRegister();

        log.info("Clicked Register");

        registrationPage =
                new AccountRegistrationPage(
                        DriverManager.getDriver());
    }


    @When("the user enters valid registration details")
    public void user_enters_valid_registration_details() {

        String firstName =
                randomString().toUpperCase();

        String lastName =
                randomString().toUpperCase();

        String email =
                randomString() + "@gmail.com";

        String telephone =
                randomNumber();

        password =
                randomAlphaNumeric();

        log.info("Entering registration details");

        registrationPage.setFirstName(firstName);
        registrationPage.setLastName(lastName);
        registrationPage.setEmail(email);
        registrationPage.setTelephone(telephone);
        registrationPage.setPassword(password);
        registrationPage.setConfirmPassword(password);

        log.info(
                "Registration details entered successfully");
    }


    @When("the user accepts the Privacy Policy")
    public void user_accepts_privacy_policy() {

        registrationPage.acceptPrivacyPolicy();

        log.info("Privacy Policy accepted");
    }


    @When("the user clicks the Continue button")
    public void user_clicks_continue_button() {

        registrationPage.clickContinue();

        log.info("Clicked Continue button");
    }


    @Then("the account should be created successfully")
    public void account_should_be_created_successfully() {

        String actualMessage =
                registrationPage.getConfirmationMessage();

        String expectedMessage =
                "Your Account Has Been Created!";

        log.info(
                "Registration confirmation message: {}",
                actualMessage);

        Assert.assertEquals(
                actualMessage,
                expectedMessage,
                "Account registration confirmation message mismatch");

        log.info(
                "Account registration successful");
    }
}