package pageObjects;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyAccountPage extends BasePage {

    private WebDriverWait wait;

    public MyAccountPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(xpath = "//span[normalize-space()='My Account']") // Top navbar My Account dropdown
    WebElement msgHeading;

    @FindBy(xpath = "//ul[contains(@class,'dropdown-menu')]//a[text()='Logout']")
    WebElement lnkLogout;

    @FindBy(xpath = "//div[@id='content']//h1[text()='Account Logout']")
    WebElement msgAccountLogout;

    public boolean isMyAccountPageExists() {
        try {
            return msgHeading.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(msgHeading)).click();
        wait.until(ExpectedConditions.elementToBeClickable(lnkLogout)).click();
    }

    public boolean isAccountLoggedOut() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(msgAccountLogout)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}