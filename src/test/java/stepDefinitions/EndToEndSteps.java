package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.MyAccountPage;
import testBase.BaseClass;

public class EndToEndSteps extends BaseClass {

    private static final Logger logger = LogManager.getLogger(EndToEndSteps.class);
    private MyAccountPage myAccountPage;

    @When("the user logs out from the OpenCart account")
    public void the_user_logs_out_from_the_opencart_account() {
        myAccountPage = new MyAccountPage(getDriver());
        logger.info("Clicking Logout link on My Account page");
        myAccountPage.clickLogout();
    }

    @Then("the user should be successfully logged out")
    public void the_user_should_be_successfully_logged_out() {
        boolean isLoggedOut = myAccountPage.isAccountLoggedOut();
        logger.info("Is user logged out? {}", isLoggedOut);
        Assert.assertTrue(isLoggedOut, "Account Logout confirmation heading was not displayed");
    }
}