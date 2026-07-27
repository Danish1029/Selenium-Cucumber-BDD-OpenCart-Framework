package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import factory.DriverManager;

public class BaseClass {

    // ============================================================
    // Framework Variables
    // ============================================================

    public ResourceBundle rb;

    protected WebDriverWait wait;

    protected Logger logger;

    protected Properties p;

    protected String browserName;


    // ============================================================
    // Driver Getter
    // ============================================================

    public WebDriver getDriver() {

        return DriverManager.getDriver();
    }


    // ============================================================
    // Browser Name Getter
    // ============================================================

    public String getBrowserName() {

        return browserName;
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
        // Browser Resolution
        //
        // Priority:
        // Maven/System property > TestNG XML > default
        // --------------------------------------------------------

        String browser =
                System.getProperty("browser");

        if (browser == null || browser.isBlank()) {

            browser = br;
        }

        browser = browser.toLowerCase().trim();


        // --------------------------------------------------------
        // Headless Resolution
        // --------------------------------------------------------

        boolean headless =
                Boolean.parseBoolean(
                        System.getProperty(
                                "headless",
                                "false"));


        System.out.println(
                "Thread  = "
                        + Thread.currentThread().getName());

        System.out.println(
                "OS      = " + os);

        System.out.println(
                "Browser = " + browser);

        System.out.println(
                "Env     = " + env);

        System.out.println(
                "Headless = " + headless);


        logger.info(
                "Execution Thread: {}",
                Thread.currentThread().getName());

        logger.info(
                "Launching browser: {}",
                browser);


        // --------------------------------------------------------
        // Execution Environment
        // --------------------------------------------------------

        String executionEnv =
                p.getProperty(
                        "execution_env",
                        "local");


        // ========================================================
        // REMOTE EXECUTION
        // Selenium Grid / Docker Grid
        // ========================================================

        if (executionEnv.equalsIgnoreCase("remote")) {

            logger.info(
                    "Starting Remote Execution");

            MutableCapabilities capabilities;


            switch (browser) {

                // ------------------------------------------------
                // Chrome Remote
                // ------------------------------------------------

                case "chrome":

                    ChromeOptions chromeOptions =
                            new ChromeOptions();

                    chromeOptions.addArguments(
                            "--no-sandbox");

                    chromeOptions.addArguments(
                            "--disable-dev-shm-usage");

                    chromeOptions.addArguments(
                            "--window-size=1920,1080");

                    if (headless) {

                        chromeOptions.addArguments(
                                "--headless=new");
                    }

                    capabilities = chromeOptions;

                    browserName = "Chrome";

                    break;


                // ------------------------------------------------
                // Edge Remote
                // ------------------------------------------------

                case "edge":

                    EdgeOptions edgeOptions =
                            new EdgeOptions();

                    edgeOptions.addArguments(
                            "--no-sandbox");

                    edgeOptions.addArguments(
                            "--disable-dev-shm-usage");

                    edgeOptions.addArguments(
                            "--window-size=1920,1080");

                    if (headless) {

                        edgeOptions.addArguments(
                                "--headless=new");
                    }

                    capabilities = edgeOptions;

                    browserName = "Edge";

                    break;


                // ------------------------------------------------
                // Firefox Remote
                // ------------------------------------------------

                case "firefox":

                    FirefoxOptions firefoxOptions =
                            new FirefoxOptions();

                    firefoxOptions.addArguments(
                            "--window-size=1920,1080");

                    if (headless) {

                        firefoxOptions.addArguments(
                                "-headless");
                    }

                    capabilities = firefoxOptions;

                    browserName = "Firefox";

                    break;


                default:

                    throw new IllegalArgumentException(
                            "Unsupported browser: "
                                    + browser);
            }


            // ----------------------------------------------------
            // Platform
            // ----------------------------------------------------

            if (os.equalsIgnoreCase("windows")) {

                capabilities.setCapability(
                        "platformName",
                        "Windows");

            } else if (os.equalsIgnoreCase("mac")) {

                capabilities.setCapability(
                        "platformName",
                        "macOS");

            } else if (os.equalsIgnoreCase("linux")) {

                capabilities.setCapability(
                        "platformName",
                        "Linux");

            } else {

                throw new IllegalArgumentException(
                        "Unsupported OS: " + os);
            }


            // ----------------------------------------------------
            // Grid URL
            // ----------------------------------------------------

            String gridURL =
                    System.getProperty(
                            "gridURL",
                            "http://localhost:4444");


            logger.info(
                    "Grid URL: {}",
                    gridURL);

            logger.info(
                    "Browser: {}",
                    browser);

            logger.info(
                    "Operating System: {}",
                    os);


            // ----------------------------------------------------
            // Create Remote Driver
            // ----------------------------------------------------

            WebDriver remoteDriver =
                    new RemoteWebDriver(
                            new URL(gridURL),
                            capabilities);


            // ----------------------------------------------------
            // Store driver inside current ThreadLocal
            // ----------------------------------------------------

            DriverManager.setDriver(
                    remoteDriver);


            logger.info(
                    "Connected to Selenium Grid");
        }


        // ========================================================
        // LOCAL EXECUTION
        // ========================================================

        else if (executionEnv.equalsIgnoreCase("local")) {

            logger.info(
                    "Starting Local Execution");


            WebDriver localDriver;


            switch (browser) {

                // ------------------------------------------------
                // Chrome Local
                // ------------------------------------------------

                case "chrome":

                    ChromeOptions chromeOptions =
                            new ChromeOptions();

                    if (headless) {

                        chromeOptions.addArguments(
                                "--headless=new");

                        chromeOptions.addArguments(
                                "--window-size=1920,1080");
                    }

                    localDriver =
                            new ChromeDriver(
                                    chromeOptions);

                    browserName = "Chrome";

                    break;


                // ------------------------------------------------
                // Edge Local
                // ------------------------------------------------

                case "edge":

                    EdgeOptions edgeOptions =
                            new EdgeOptions();

                    if (headless) {

                        edgeOptions.addArguments(
                                "--headless=new");

                        edgeOptions.addArguments(
                                "--window-size=1920,1080");
                    }

                    localDriver =
                            new EdgeDriver(
                                    edgeOptions);

                    browserName = "Edge";

                    break;


                // ------------------------------------------------
                // Firefox Local
                // ------------------------------------------------

                case "firefox":

                    FirefoxOptions firefoxOptions =
                            new FirefoxOptions();

                    if (headless) {

                        firefoxOptions.addArguments(
                                "-headless");
                    }

                    localDriver =
                            new FirefoxDriver(
                                    firefoxOptions);

                    browserName = "Firefox";

                    break;


                default:

                    throw new IllegalArgumentException(
                            "Invalid browser name: "
                                    + browser);
            }


            // ----------------------------------------------------
            // Store driver inside current ThreadLocal
            // ----------------------------------------------------

            DriverManager.setDriver(
                    localDriver);


            logger.info(
                    "Connected to Selenium local environment");

            logger.info(
                    "Browser: {}",
                    browser);
        }


        // ========================================================
        // Invalid Environment
        // ========================================================

        else {

            throw new IllegalArgumentException(
                    "Invalid execution_env value: "
                            + executionEnv);
        }


        // ========================================================
        // Obtain Current Thread Driver
        // ========================================================

        WebDriver driver =
                DriverManager.getDriver();


        logger.info(
                "Driver created for thread: {}",
                Thread.currentThread().getName());


        // ========================================================
        // Common WebDriver Configuration
        // ========================================================

        driver.manage()
                .deleteAllCookies();


        int implicitWait =
                Integer.parseInt(
                        p.getProperty(
                                "implicitWait",
                                "10"));


        int explicitWait =
                Integer.parseInt(
                        p.getProperty(
                                "explicitWait",
                                "10"));


        driver.manage()
                .timeouts()
                .implicitlyWait(
                        Duration.ofSeconds(
                                implicitWait));


        driver.manage()
                .timeouts()
                .pageLoadTimeout(
                        Duration.ofSeconds(30));


        // --------------------------------------------------------
        // Explicit Wait
        // --------------------------------------------------------

        wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(
                                explicitWait));


        // --------------------------------------------------------
        // Window
        // --------------------------------------------------------

        try {

            driver.manage()
                    .window()
                    .setSize(
                            new Dimension(
                                    1920,
                                    1080));

        } catch (Exception e) {

            logger.warn(
                    "Unable to resize browser window: {}",
                    e.getMessage());
        }


        // ========================================================
        // Open Application
        // ========================================================

        String appURL =
                p.getProperty("appURL");


        if (appURL == null
                || appURL.isBlank()) {

            throw new IllegalStateException(
                    "appURL is missing from "
                            + env
                            + ".properties");
        }


        logger.info(
                "Opening URL: {}",
                appURL);


        driver.get(
                appURL);


        // ========================================================
        // Wait for document.readyState
        // ========================================================

        new WebDriverWait(
                driver,
                Duration.ofSeconds(30))
                .until(webDriver ->

                        "complete".equals(

                                ((JavascriptExecutor)
                                        webDriver)
                                        .executeScript(
                                                "return document.readyState")
                        )
                );


        // ========================================================
        // Execution Information
        // ========================================================

        System.out.println(
                "Title = "
                        + driver.getTitle());

        System.out.println(
                "URL = "
                        + driver.getCurrentUrl());


        logger.info(
                "URL = {}",
                driver.getCurrentUrl());

        logger.info(
                "Title = {}",
                driver.getTitle());

        logger.info(
                "Browser = {}",
                browserName);

        logger.info(
                "ReadyState = {}",
                ((JavascriptExecutor) driver)
                        .executeScript(
                                "return document.readyState"));


        logger.info(
                "My Account Present = {}",
                driver.getPageSource()
                        .contains(
                                "My Account"));


        logger.info(
                "Window Size = {}",
                driver.manage()
                        .window()
                        .getSize());


        logger.info(
                "Driver setup completed for thread: {}",
                Thread.currentThread().getName());
    }


    // ============================================================
    // CUCUMBER SETUP
    // ============================================================

    public void setupCucumber(
            String os,
            String browser)
            throws IOException, InterruptedException {

        setup(
                os,
                browser);
    }


    // ============================================================
    // CUCUMBER TEARDOWN
    // ============================================================

    public void tearDownCucumber() {

        if (logger != null) {

            logger.info(
                    "Closing Cucumber browser on thread: {}",
                    Thread.currentThread().getName());
        }


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

            logger.info(
                    "Closing TestNG browser on thread: {}",
                    Thread.currentThread().getName());
        }


        DriverManager.quitDriver();
    }


    // ============================================================
    // SCREENSHOT AS BYTES
    // Used by Cucumber Scenario.attach()
    // ============================================================

    public byte[] getScreenshotBytes() {

        WebDriver driver =
                DriverManager.getDriver();


        return ((TakesScreenshot) driver)
                .getScreenshotAs(
                        OutputType.BYTES);
    }


    // ============================================================
    // SCREENSHOT AS FILE
    // ============================================================

    public String captureScreen(
            String testName)
            throws IOException {

        WebDriver driver =
                DriverManager.getDriver();


        String timeStamp =
                new SimpleDateFormat(
                        "yyyy-MM-dd_HH-mm-ss-SSS")
                        .format(
                                new Date());


        TakesScreenshot takesScreenshot =
                (TakesScreenshot) driver;


        File source =
                takesScreenshot
                        .getScreenshotAs(
                                OutputType.FILE);


        String destination =
                System.getProperty(
                        "user.dir")
                        + File.separator
                        + "screenshots"
                        + File.separator
                        + testName
                        + "_"
                        + timeStamp
                        + "_"
                        + Thread.currentThread()
                                .getId()
                        + ".png";


        File destinationFile =
                new File(
                        destination);


        File parent =
                destinationFile
                        .getParentFile();


        if (parent != null) {

            parent.mkdirs();
        }


        FileUtils.copyFile(
                source,
                destinationFile);


        logger.info(
                "Screenshot saved: {}",
                destination);


        return destination;
    }


    // ============================================================
    // RANDOM TEST DATA
    // ============================================================

    public String randomString() {

        return RandomStringUtils
                .insecure()
                .nextAlphabetic(6);
    }


    public String randomNumber() {

        return RandomStringUtils
                .insecure()
                .nextNumeric(10);
    }


    public String randomAlphaNumeric() {

        return RandomStringUtils
                .insecure()
                .nextAlphabetic(6)
                + "@"
                + RandomStringUtils
                        .insecure()
                        .nextNumeric(10);
    }
}