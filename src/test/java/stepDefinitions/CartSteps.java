package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.ProductPage;
import pageObjects.SearchResultsPage;
import pageObjects.ShoppingCartPage;
import testBase.BaseClass;

public class CartSteps extends BaseClass {

    private static final Logger logger = LogManager.getLogger(CartSteps.class);
    private SearchResultsPage searchResultsPage;
    private ProductPage productPage;
    private ShoppingCartPage shoppingCartPage;

    @When("the user selects product {string} from the search results")
    public void the_user_selects_product_from_the_search_results(String productName) {
        searchResultsPage = new SearchResultsPage(getDriver());
        logger.info("Selecting product from search results: {}", productName);
        searchResultsPage.selectProduct(productName);
        productPage = new ProductPage(getDriver());
    }

    @When("the user sets product quantity to {string}")
    public void the_user_sets_product_quantity_to(String quantity) {
        logger.info("Setting product quantity to: {}", quantity);
        productPage.setQuantity(quantity);
    }

    @When("the user clicks the Add to Cart button")
    public void the_user_clicks_the_add_to_cart_button() {
        logger.info("Clicking Add to Cart button");
        productPage.clickAddToCart();
    }

    @Then("the user should see a success message confirming {string} was added to the cart")
    public void the_user_should_see_a_success_message_confirming_was_added_to_the_cart(String productName) {
        Assert.assertTrue(productPage.isSuccessMessageDisplayed(), "Add to Cart success alert was not displayed");
        String actualMessage = productPage.getSuccessMessageText();
        logger.info("Add to Cart success alert: {}", actualMessage);
        Assert.assertTrue(
                actualMessage.toLowerCase().contains(("Success: You have added " + productName + " to your shopping cart!").toLowerCase()),
                "Unexpected alert text: " + actualMessage);
    }

    @When("the user navigates to the Shopping Cart page from the alert")
    public void the_user_navigates_to_the_shopping_cart_page_from_the_alert() {
        logger.info("Navigating to Shopping Cart page from alert link");
        productPage.clickShoppingCartLinkInAlert();
        shoppingCartPage = new ShoppingCartPage(getDriver());
    }

    @Then("the product {string} should be present in the Shopping Cart")
    public void the_product_should_be_present_in_the_shopping_cart(String productName) {
        Assert.assertTrue(shoppingCartPage.isShoppingCartPageDisplayed(), "Shopping Cart page header not displayed");
        boolean inCart = shoppingCartPage.isProductInCart(productName);
        logger.info("Is product '{}' present in Shopping Cart? {}", productName, inCart);
        Assert.assertTrue(inCart, "Expected product was not found in Shopping Cart: " + productName);
    }
}