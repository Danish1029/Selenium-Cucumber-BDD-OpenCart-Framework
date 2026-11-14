package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.ProductPage;
import pageObjects.SearchResultsPage;
import pageObjects.ShoppingCartPage;
import testBase.BaseClass;

public class TC_005_AddToCartTest extends BaseClass {

    @Test(groups = {"Sanity", "Regression", "Master"})
    public void verifyAddProductToCart() {
        logger.info("***** Starting TC_004_AddToCartTest *****");
        try {
            String productName = "iPhone";
            String quantity = "1";

            // Step 1: Search product
            HomePage homePage = new HomePage(getDriver());
            homePage.enterSearchProduct(productName);
            homePage.clickSearchButton();

            // Step 2: Select product from results
            SearchResultsPage searchResultsPage = new SearchResultsPage(getDriver());
            searchResultsPage.selectProduct(productName);

            // Step 3: Set quantity and add to cart
            ProductPage productPage = new ProductPage(getDriver());
            productPage.setQuantity(quantity);
            productPage.clickAddToCart();

            // Step 4: Verify success alert
            Assert.assertTrue(productPage.isSuccessMessageDisplayed(),
                    "Add to Cart success alert was not displayed");
            String actualAlert = productPage.getSuccessMessageText();
            logger.info("Success alert displayed: {}", actualAlert);
            Assert.assertTrue(
                    actualAlert.toLowerCase().contains(("Success: You have added " + productName + " to your shopping cart!").toLowerCase()),
                    "Unexpected alert message: " + actualAlert);

            // Step 5: Navigate to Shopping Cart and verify product presence
            productPage.clickShoppingCartLinkInAlert();
            ShoppingCartPage shoppingCartPage = new ShoppingCartPage(getDriver());
            boolean isInCart = shoppingCartPage.isProductInCart(productName);
            logger.info("Is product '{}' present in Shopping Cart? {}", productName, isInCart);

            Assert.assertTrue(isInCart, "Product was not found in Shopping Cart: " + productName);
        } catch (Exception e) {
            logger.error("Test failed due to exception: {}", e.getMessage());
            captureFailureScreenshot("FAILED_TC", "TC_004_verifyAddProductToCart");
            Assert.fail("Exception in verifyAddProductToCart: " + e.getMessage());
        }
        logger.info("***** Finished TC_004_AddToCartTest *****");
    }
}