package pageObjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResultsPage extends BasePage {

    private WebDriverWait wait;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(xpath = "//div[@id='content']//h1")
    private WebElement lblSearchHeader;

    @FindBy(xpath = "//div[contains(@class,'product-thumb')]//div[@class='caption']//h4/a")
    private List<WebElement> listProductTitles;

    @FindBy(xpath = "//div[@id='content']//p[contains(text(),'There is no product that matches the search criteria.')]")
    private WebElement msgNoProduct;

    public String getSearchHeaderText() {
        return wait.until(ExpectedConditions.visibilityOf(lblSearchHeader)).getText();
    }

    public boolean isProductDisplayed(String expectedProductName) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(listProductTitles));
            for (WebElement product : listProductTitles) {
                if (product.getText().trim().equalsIgnoreCase(expectedProductName.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean isNoProductMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(msgNoProduct)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getNoProductMessageText() {
        return wait.until(ExpectedConditions.visibilityOf(msgNoProduct)).getText().trim();
    }

    public void selectProduct(String productName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(listProductTitles));
        for (WebElement product : listProductTitles) {
            if (product.getText().trim().equalsIgnoreCase(productName.trim())) {
                String productUrl = product.getAttribute("href");
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", product);
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", product);

                try {
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(ExpectedConditions.urlContains("product/product"));
                } catch (Exception e) {
                    if (productUrl != null && !productUrl.isEmpty()) {
                        driver.get(productUrl);
                    }
                }
                wait.until(ExpectedConditions.urlContains("product/product"));
                return;
            }
        }
        throw new RuntimeException("Product not found in search results: " + productName);
    }
}