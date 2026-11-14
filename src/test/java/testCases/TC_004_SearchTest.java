package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.SearchResultsPage;
import testBase.BaseClass;

public class TC_004_SearchTest extends BaseClass {

    @Test(priority = 1, groups = {"Sanity", "Regression", "Master"})
    public void verifyExistingProductSearch() {
        logger.info("***** Starting TC_003_SearchTest - verifyExistingProductSearch *****");
        try {
            HomePage homePage = new HomePage(getDriver());
            String productName = "iPhone";

            logger.info("Entering product name in search box: {}", productName);
            homePage.enterSearchProduct(productName);
            homePage.clickSearchButton();

            SearchResultsPage searchResultsPage = new SearchResultsPage(getDriver());
            boolean isDisplayed = searchResultsPage.isProductDisplayed(productName);
            logger.info("Is product '{}' displayed in search results? {}", productName, isDisplayed);

            Assert.assertTrue(isDisplayed, "Expected product was not found in search results: " + productName);
        } catch (Exception e) {
            logger.error("Test failed due to exception: {}", e.getMessage());
            captureFailureScreenshot("FAILED_TC", "TC_003_verifyExistingProductSearch");
            Assert.fail("Exception in verifyExistingProductSearch: " + e.getMessage());
        }
        logger.info("***** Finished TC_003_SearchTest - verifyExistingProductSearch *****");
    }

    @Test(priority = 2, groups = {"Regression", "Master"})
    public void verifyNonExistingProductSearch() {
        logger.info("***** Starting TC_003_SearchTest - verifyNonExistingProductSearch *****");
        try {
            HomePage homePage = new HomePage(getDriver());
            String invalidProduct = "NonExistentProductXYZ";

            logger.info("Entering non-existing product in search box: {}", invalidProduct);
            homePage.enterSearchProduct(invalidProduct);
            homePage.clickSearchButton();

            SearchResultsPage searchResultsPage = new SearchResultsPage(getDriver());
            Assert.assertTrue(searchResultsPage.isNoProductMessageDisplayed(),
                    "Expected 'no product matches' message was not displayed");

            String actualMsg = searchResultsPage.getNoProductMessageText();
            logger.info("No product message displayed: {}", actualMsg);
            Assert.assertEquals(actualMsg, "There is no product that matches the search criteria.");
        } catch (Exception e) {
            logger.error("Test failed due to exception: {}", e.getMessage());
            captureFailureScreenshot("FAILED_TC", "TC_003_verifyNonExistingProductSearch");
            Assert.fail("Exception in verifyNonExistingProductSearch: " + e.getMessage());
        }
        logger.info("***** Finished TC_003_SearchTest - verifyNonExistingProductSearch *****");
    }
}