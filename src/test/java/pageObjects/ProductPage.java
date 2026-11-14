package pageObjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {

    private WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @FindBy(css = "input#input-quantity, input[name='quantity']")
    private WebElement txtQuantity;

    @FindBy(id = "button-cart")
    private WebElement btnAddToCart;

    private By successAlertBy = By.cssSelector("div.alert.alert-success");
    private By shoppingCartLinkInAlertBy = By.xpath("//div[contains(@class,'alert-success')]//a[contains(text(),'shopping cart')]");

    public void setQuantity(String qty) {
        wait.until(ExpectedConditions.elementToBeClickable(txtQuantity));
        txtQuantity.clear();
        txtQuantity.sendKeys(qty);
    }

    public void clickAddToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(btnAddToCart)).click();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successAlertBy)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successAlertBy)).getText().trim();
    }

    public void clickShoppingCartLinkInAlert() {
        wait.until(ExpectedConditions.elementToBeClickable(shoppingCartLinkInAlertBy)).click();
    }
}