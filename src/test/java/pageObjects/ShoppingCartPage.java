package pageObjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ShoppingCartPage extends BasePage {

    private WebDriverWait wait;

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(xpath = "//div[@id='content']//h1[contains(text(),'Shopping Cart')]")
    private WebElement lblShoppingCartHeader;

    @FindBy(xpath = "//div[@id='content']//form//table//tbody//tr/td[2]/a")
    private List<WebElement> listCartProductNames;

    public boolean isShoppingCartPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblShoppingCartHeader)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProductInCart(String expectedProductName) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(listCartProductNames));
            for (WebElement item : listCartProductNames) {
                if (item.getText().trim().equalsIgnoreCase(expectedProductName.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}