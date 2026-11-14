package factory;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();

    private DriverManager() {
        // Prevent object creation
    }

    public static void setDriver(WebDriver webDriver) {

        if (webDriver == null) {
            throw new IllegalArgumentException(
                    "WebDriver instance cannot be null");
        }

        driver.set(webDriver);
    }

    public static WebDriver getDriver() {

        WebDriver currentDriver = driver.get();

        if (currentDriver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialized for thread: "
                    + Thread.currentThread().getName());
        }

        return currentDriver;
    }

    public static void quitDriver() {

        WebDriver currentDriver = driver.get();

        try {

            if (currentDriver != null) {
                currentDriver.quit();
            }

        } finally {

            driver.remove();
        }
    }
}