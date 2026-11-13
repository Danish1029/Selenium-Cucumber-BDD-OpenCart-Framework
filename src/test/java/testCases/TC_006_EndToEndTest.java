package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import pageObjects.ProductPage;
import pageObjects.SearchResultsPage;
import pageObjects.ShoppingCartPage;
import testBase.BaseClass;

public class TC_006_EndToEndTest extends BaseClass {

    @Test(groups = {"Regression", "Master"})
    public void verifyEndToEndCustomerPurchaseJourney() {
        logger.info("***** Starting TC_005_EndToEndTest *****");
        try {
            // Step 1: Navigate to Login Page
            HomePage homePage = new HomePage(getDriver());
            homePage.clickMyAccount();
            homePage.clickLogin();

            // Step 2: Login with valid credentials from properties
            LoginPage loginPage = new LoginPage(getDriver());
            loginPage.setEmail(p.getProperty("email"));
            loginPage.setPassword(p.getProperty("password"));
            loginPage.clickLogin();

            MyAccountPage myAccountPage = new MyAccountPage(getDriver());
            boolean isLoggedIn = myAccountPage.isMyAccountPageExists();
            if (!isLoggedIn) {
                captureFailureScreenshot("FAILED_LOGIN", "TC_005_EndToEndLogin");
            }
            Assert.assertTrue(isLoggedIn, "Login failed: My Account page was not displayed");
            logger.info("User logged in successfully");

            // Step 3: Search and select product
            String productName = "iPhone";
            homePage.enterSearchProduct(productName);
            homePage.clickSearchButton();

            SearchResultsPage searchResultsPage = new SearchResultsPage(getDriver());
            searchResultsPage.selectProduct(productName);

            // Step 4: Set quantity and add to cart
            ProductPage productPage = new ProductPage(getDriver());
            productPage.setQuantity("1");
            productPage.clickAddToCart();

            Assert.assertTrue(productPage.isSuccessMessageDisplayed(),
                    "Add to Cart success alert was not displayed");
            productPage.clickShoppingCartLinkInAlert();

            // Step 5: Verify product in Shopping Cart
            ShoppingCartPage shoppingCartPage = new ShoppingCartPage(getDriver());
            Assert.assertTrue(shoppingCartPage.isProductInCart(productName),
                    "Product was not found in Shopping Cart: " + productName);
            logger.info("Product '{}' verified in Shopping Cart", productName);

            // Step 6: Logout and verify Account Logout confirmation
            myAccountPage.clickLogout();
            Assert.assertTrue(myAccountPage.isAccountLoggedOut(),
                    "Account Logout confirmation was not displayed");
            logger.info("User logged out successfully");

        } catch (Exception e) {
            logger.error("Test failed due to exception: {}", e.getMessage());
            captureFailureScreenshot("FAILED_TC", "TC_005_verifyEndToEndCustomerPurchaseJourney");
            Assert.fail("Exception in verifyEndToEndCustomerPurchaseJourney: " + e.getMessage());
        }
        logger.info("***** Finished TC_005_EndToEndTest *****");
    }
}