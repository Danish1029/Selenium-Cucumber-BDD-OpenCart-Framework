package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.HomePage;
import pageObjects.SearchResultsPage;
import testBase.BaseClass;

public class SearchSteps extends BaseClass {

    private static final Logger logger = LogManager.getLogger(SearchSteps.class);
    private HomePage homePage;
    private SearchResultsPage searchResultsPage;

    @When("the user enters product name {string} in the search box")
    public void the_user_enters_product_name_in_the_search_box(String productName) {
        homePage = new HomePage(getDriver());
        logger.info("Entering product name in search box: {}", productName);
        homePage.enterSearchProduct(productName);
    }

    @When("the user clicks the Search button")
    public void the_user_clicks_the_search_button() {
        logger.info("Clicking Search button");
        homePage.clickSearchButton();
        searchResultsPage = new SearchResultsPage(getDriver());
    }

    @Then("the product {string} should be displayed in the search results")
    public void the_product_should_be_displayed_in_the_search_results(String productName) {
        boolean isDisplayed = searchResultsPage.isProductDisplayed(productName);
        logger.info("Is product '{}' displayed in search results? {}", productName, isDisplayed);
        Assert.assertTrue(isDisplayed, "Expected product was not found in search results: " + productName);
    }

    @Then("the user should see a no product found message")
    public void the_user_should_see_a_no_product_found_message() {
        boolean isNoProductMsgVisible = searchResultsPage.isNoProductMessageDisplayed();
        Assert.assertTrue(isNoProductMsgVisible, "Expected 'no product matches' message was not displayed");
        String actualMsg = searchResultsPage.getNoProductMessageText();
        logger.info("No product message displayed: {}", actualMsg);
        Assert.assertEquals(actualMsg, "There is no product that matches the search criteria.");
    }
}