package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import factory.DriverManager;

public class BaseClass {

    // ============================================================
    // Framework Variables (Thread-Safe for Parallel Execution)
    // ============================================================

    public ResourceBundle rb;

    protected WebDriverWait wait;

    protected Logger logger;

    protected Properties p;

    protected String browserName;

    private static final ThreadLocal<String> tlBrowserName = new ThreadLocal<>();

    private static final ThreadLocal<WebDriverWait> tlWait = new ThreadLocal<>();


    // ============================================================
    // Driver & Wait Getters
    // ============================================================

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    public WebDriverWait getWait() {
        return tlWait.get() != null ? tlWait.get() : wait;
    }


    // ============================================================
    // Browser Name Getter
    // ============================================================

    public String getBrowserName() {
        return tlBrowserName.get() != null ? tlBrowserName.get() : browserName;
    }


    // ============================================================
    // TestNG Setup
    // ============================================================

    @BeforeClass(groups = {
            "Sanity",
            "Regression",
            "Master"
    })
    @Parameters({"os", "browser"})
    public void setup(
            @Optional("windows") String os,
            @Optional("chrome") String br)
            throws IOException, InterruptedException {

        // --------------------------------------------------------
        // Logger
        // --------------------------------------------------------

        logger = LogManager.getLogger(this.getClass());


        // --------------------------------------------------------
        // Environment
        // --------------------------------------------------------

        String env = System.getProperty("env");

        if (env == null || env.isBlank()) {
            env = "qa";
        }


        // --------------------------------------------------------
        // Load Environment Properties
        // --------------------------------------------------------

        try (FileReader file =
                     new FileReader(
                             "./src/test/resources/config/"
                                     + env
                                     + ".properties")) {

            p = new Properties();
            p.load(file);
        }


        // --------------------------------------------------------
        // TestNG XML Parameter Fallback (for Cucumber + TestNG)
        // --------------------------------------------------------

        String xmlBrowser = null;
        String xmlOs = null;
        try {
            ITestResult currentResult = Reporter.getCurrentTestResult();
            if (currentResult != null && currentResult.getTestContext() != null
                    && currentResult.getTestContext().getCurrentXmlTest() != null) {
                xmlBrowser = currentResult.getTestContext().getCurrentXmlTest().getParameter("browser");
                xmlOs = currentResult.getTestContext().getCurrentXmlTest().getParameter("os");
            }
        } catch (Exception ignored) {
        }


        // --------------------------------------------------------
        // OS Resolution (System Property > TestNG XML > Method Arg)
        // --------------------------------------------------------

        String resolvedOs = System.getProperty("os");
        if (resolvedOs == null || resolvedOs.isBlank()) {
            resolvedOs = (xmlOs != null && !xmlOs.isBlank()) ? xmlOs : os;
        }
        resolvedOs = resolvedOs.toLowerCase().trim();


        // --------------------------------------------------------
        // Browser Resolution (System Property > TestNG XML > Method Arg)
        // --------------------------------------------------------

        String browser = System.getProperty("browser");
        if (browser == null || browser.isBlank()) {
            browser = (xmlBrowser != null && !xmlBrowser.isBlank()) ? xmlBrowser : br;
        }
        browser = browser.toLowerCase().trim();


        // --------------------------------------------------------
        // Headless Resolution
        // --------------------------------------------------------

        boolean headless =
                Boolean.parseBoolean(
                        System.getProperty(
                                "headless",
                                p.getProperty("headless", "false")));


        // --------------------------------------------------------
        // Execution Environment Resolution (System Property > Properties > Default)
        // --------------------------------------------------------

        String executionEnv = System.getProperty("execution_env");
        if (executionEnv == null || executionEnv.isBlank()) {
            executionEnv = p.getProperty("execution_env", "local");
        }
        executionEnv = executionEnv.toLowerCase().trim();


        System.out.println("Thread   = " + Thread.currentThread().getName());
        System.out.println("OS       = " + resolvedOs);
        System.out.println("Browser  = " + browser);
        System.out.println("Env      = " + env);
        System.out.println("ExecEnv  = " + executionEnv);
        System.out.println("Headless = " + headless);

        logger.info("Execution Thread: {}", Thread.currentThread().getName());
        logger.info("Launching browser: {} | OS: {} | ExecutionEnv: {}", browser, resolvedOs, executionEnv);


        // ========================================================
        // REMOTE EXECUTION
        // Selenium Grid / Docker Grid
        // ========================================================

        if (executionEnv.equalsIgnoreCase("remote")) {

            logger.info("Starting Remote Execution");

            MutableCapabilities capabilities;

            switch (browser) {

                // ------------------------------------------------
                // Chrome Remote (Docker / Grid)
                // ------------------------------------------------

                case "chrome":

                    ChromeOptions chromeOptions = buildChromeOptions(headless);
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--window-size=1920,1080");

                    capabilities = chromeOptions;
                    browserName = "Chrome";
                    tlBrowserName.set("Chrome");
                    break;


                // ------------------------------------------------
                // Edge Remote (Docker / Grid)
                // ------------------------------------------------

                case "edge":

                    EdgeOptions edgeOptions = buildEdgeOptions(headless);
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    edgeOptions.addArguments("--window-size=1920,1080");

                    capabilities = edgeOptions;
                    browserName = "Edge";
                    tlBrowserName.set("Edge");
                    break;


                // ------------------------------------------------
                // Firefox Remote (Docker / Grid)
                // ------------------------------------------------

                case "firefox":

                    FirefoxOptions firefoxOptions = buildFirefoxOptions(headless);
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");

                    capabilities = firefoxOptions;
                    browserName = "Firefox";
                    tlBrowserName.set("Firefox");
                    break;


                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }


            // ----------------------------------------------------
            // Platform
            // ----------------------------------------------------

            if (resolvedOs.equalsIgnoreCase("windows")) {
                capabilities.setCapability("platformName", "Windows");
            } else if (resolvedOs.equalsIgnoreCase("mac")) {
                capabilities.setCapability("platformName", "macOS");
            } else if (resolvedOs.equalsIgnoreCase("linux")) {
                capabilities.setCapability("platformName", "Linux");
            } else {
                throw new IllegalArgumentException("Unsupported OS: " + resolvedOs);
            }


            // ----------------------------------------------------
            // Grid URL (System Property > Properties > Default)
            // ----------------------------------------------------

            String gridURL = System.getProperty("gridURL");
            if (gridURL == null || gridURL.isBlank()) {
                gridURL = p.getProperty("gridURL", "http://localhost:4444");
            }

            logger.info("Grid URL: {}", gridURL);
            logger.info("Browser: {}", browser);
            logger.info("Operating System: {}", resolvedOs);


            // ----------------------------------------------------
            // Create Remote Driver
            // ----------------------------------------------------

            WebDriver remoteDriver =
                    new RemoteWebDriver(
                            URI.create(gridURL).toURL(),
                            capabilities);

            DriverManager.setDriver(remoteDriver);

            logger.info("Connected to Selenium Grid");
        }


        // ========================================================
        // LOCAL EXECUTION
        // ========================================================

        else if (executionEnv.equalsIgnoreCase("local")) {

            logger.info("Starting Local Execution");

            WebDriver localDriver;

            switch (browser) {

                // ------------------------------------------------
                // Chrome Local
                // ------------------------------------------------

                case "chrome":

                    ChromeOptions chromeOptions = buildChromeOptions(headless);
                    localDriver = new ChromeDriver(chromeOptions);
                    browserName = "Chrome";
                    tlBrowserName.set("Chrome");
                    break;


                // ------------------------------------------------
                // Edge Local
                // ------------------------------------------------

                case "edge":

                    EdgeOptions edgeOptions = buildEdgeOptions(headless);
                    localDriver = new EdgeDriver(edgeOptions);
                    browserName = "Edge";
                    tlBrowserName.set("Edge");
                    break;


                // ------------------------------------------------
                // Firefox Local
                // ------------------------------------------------

                case "firefox":

                    FirefoxOptions firefoxOptions = buildFirefoxOptions(headless);
                    localDriver = new FirefoxDriver(firefoxOptions);
                    browserName = "Firefox";
                    tlBrowserName.set("Firefox");
                    break;


                default:
                    throw new IllegalArgumentException("Invalid browser name: " + browser);
            }

            DriverManager.setDriver(localDriver);

            logger.info("Connected to Selenium local environment");
            logger.info("Browser: {}", browser);
        }


        // ========================================================
        // Invalid Environment
        // ========================================================

        else {
            throw new IllegalArgumentException("Invalid execution_env value: " + executionEnv);
        }


        // ========================================================
        // Obtain Current Thread Driver
        // ========================================================

        WebDriver driver = DriverManager.getDriver();

        logger.info("Driver created for thread: {}", Thread.currentThread().getName());


        // ========================================================
        // Common WebDriver Configuration
        // ========================================================

        driver.manage().deleteAllCookies();

        int implicitWait =
                Integer.parseInt(
                        p.getProperty("implicitWait", "10"));

        int explicitWait =
                Integer.parseInt(
                        p.getProperty("explicitWait", "10"));

        driver.manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait));

        driver.manage()
                .timeouts()
                .pageLoadTimeout(Duration.ofSeconds(60));


        // --------------------------------------------------------
        // Explicit Wait (ThreadLocal + Instance)
        // --------------------------------------------------------

        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        tlWait.set(wait);


        // --------------------------------------------------------
        // Window
        // --------------------------------------------------------

        try {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        } catch (Exception e) {
            logger.warn("Unable to resize browser window: {}", e.getMessage());
        }


        // ========================================================
        // Open Application
        // ========================================================

        String appURL = p.getProperty("appURL");

        if (appURL == null || appURL.isBlank()) {
            throw new IllegalStateException("appURL is missing from " + env + ".properties");
        }

        logger.info("Opening URL: {}", appURL);
        driver.get(appURL);


        // ========================================================
        // Wait for document.readyState
        // ========================================================

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(webDriver ->
                        "complete".equals(
                                ((JavascriptExecutor) webDriver)
                                        .executeScript("return document.readyState")));


        // ========================================================
        // Execution Information
        // ========================================================

        System.out.println("Title = " + driver.getTitle());
        System.out.println("URL = " + driver.getCurrentUrl());

        logger.info("URL = {}", driver.getCurrentUrl());
        logger.info("Title = {}", driver.getTitle());
        logger.info("Browser = {}", getBrowserName());
        logger.info("ReadyState = {}",
                ((JavascriptExecutor) driver).executeScript("return document.readyState"));
        logger.info("My Account Present = {}",
                driver.getPageSource().contains("My Account"));
        logger.info("Window Size = {}",
                driver.manage().window().getSize());
        logger.info("Driver setup completed for thread: {}",
                Thread.currentThread().getName());
    }


    // ============================================================
    // BROWSER OPTIONS BUILDERS (Shared Local & Remote)
    // ============================================================

    private ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions chromeOptions = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        chromeOptions.setExperimentalOption("prefs", prefs);

        chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
        chromeOptions.addArguments("--disable-save-password-bubble");

        if (headless) {
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--window-size=1920,1080");
        }
        return chromeOptions;
    }

    private EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions edgeOptions = new EdgeOptions();
        if (headless) {
            edgeOptions.addArguments("--headless=new");
            edgeOptions.addArguments("--window-size=1920,1080");
        }
        return edgeOptions;
    }

    private FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (headless) {
            firefoxOptions.addArguments("-headless");
        }
        return firefoxOptions;
    }


    // ============================================================
    // CUCUMBER SETUP
    // ============================================================

    public void setupCucumber(
            String os,
            String browser)
            throws IOException, InterruptedException {

        setup(os, browser);
    }


    // ============================================================
    // CUCUMBER TEARDOWN
    // ============================================================

    public void tearDownCucumber() {
        if (logger != null) {
            logger.info("Closing Cucumber browser on thread: {}", Thread.currentThread().getName());
        }
        tlBrowserName.remove();
        tlWait.remove();
        DriverManager.quitDriver();
    }


    // ============================================================
    // TESTNG TEARDOWN
    // ============================================================

    @AfterClass(groups = {
            "Sanity",
            "Regression",
            "Master"
    })
    public void tearDown() {
        if (logger != null) {
            logger.info("Closing TestNG browser on thread: {}", Thread.currentThread().getName());
        }
        tlBrowserName.remove();
        tlWait.remove();
        DriverManager.quitDriver();
    }


    // ============================================================
    // SCREENSHOT AS BYTES
    // Used by Cucumber Scenario.attach() & Allure.addAttachment()
    // ============================================================

    public byte[] getScreenshotBytes() {
        WebDriver driver = DriverManager.getDriver();
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }


    // ============================================================
    // SCREENSHOT AS FILE (Existing Method Preserved)
    // ============================================================

    public String captureScreen(String testName) throws IOException {
        WebDriver driver = DriverManager.getDriver();

        String timeStamp =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());

        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File source = takesScreenshot.getScreenshotAs(OutputType.FILE);

        String destination =
                System.getProperty("user.dir")
                        + File.separator + "screenshots"
                        + File.separator + testName
                        + "_" + timeStamp
                        + "_" + Thread.currentThread().getId()
                        + ".png";

        File destinationFile = new File(destination);
        File parent = destinationFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        FileUtils.copyFile(source, destinationFile);

        if (logger != null) {
            logger.info("Screenshot saved: {}", destination);
        }

        return destination;
    }


    // ============================================================
    // CATEGORIZED FAILURE SCREENSHOT (Step / Login / TestCase)
    // ============================================================

    public String captureFailureScreenshot(String category, String scenarioOrTestName) {
        try {
            String sanitized = scenarioOrTestName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String browserTag = getBrowserName() != null ? getBrowserName() : "unknown";
            return captureScreen(category + "_" + browserTag + "_" + sanitized);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Failed to capture {} screenshot: {}", category, e.getMessage());
            }
            return null;
        }
    }


    // ============================================================
    // RANDOM TEST DATA
    // ============================================================

    public String randomString() {
        return RandomStringUtils.insecure().nextAlphabetic(6);
    }

    public String randomNumber() {
        return RandomStringUtils.insecure().nextNumeric(10);
    }

    public String randomAlphaNumeric() {
        return RandomStringUtils.insecure().nextAlphabetic(6)
                + "@"
                + RandomStringUtils.insecure().nextNumeric(10);
    }
}