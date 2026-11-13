package stepDefinitions;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.ITestResult;
import org.testng.Reporter;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import io.qameta.allure.Allure;

import testBase.BaseClass;

public class Hooks extends BaseClass {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    // ================= BEFORE SCENARIO =================

    @Before
    public void beforeScenario(Scenario scenario) throws Exception {

        logger.info("==============================================");
        logger.info("Starting Cucumber Scenario: {}", scenario.getName());
        logger.info("Thread: {}", Thread.currentThread().getName());

        String browser = System.getProperty("browser");
        String os = System.getProperty("os");

        // Fallback to TestNG XML parameters if not passed via Maven CLI
        try {

            ITestResult currentResult = Reporter.getCurrentTestResult();

            if (currentResult != null
                    && currentResult.getTestContext() != null
                    && currentResult.getTestContext().getCurrentXmlTest() != null) {

                if (browser == null || browser.isBlank()) {
                    browser = currentResult.getTestContext()
                            .getCurrentXmlTest().getParameter("browser");
                }

                if (os == null || os.isBlank()) {
                    os = currentResult.getTestContext()
                            .getCurrentXmlTest().getParameter("os");
                }
            }

        } catch (Exception e) {
            logger.warn("Unable to retrieve browser/OS parameters from TestNG XML: {}",
                    e.getMessage());
        }

        // Default configuration
        if (browser == null || browser.isBlank()) {
            browser = "chrome";
        }

        if (os == null || os.isBlank()) {
            os = "windows";
        }

        logger.info("Browser: {}", browser);
        logger.info("OS: {}", os);

        setupCucumber(os, browser);
    }


    // ================= AFTER EACH STEP =================

    @AfterStep
    public void afterEachStep(Scenario scenario) {

        try {

            if (scenario.isFailed() && getDriver() != null) {

                // Save failed-step screenshot to project directory
                String savedPath = captureFailureScreenshot(
                        "FAILED_STEP", scenario.getName());

                logger.error(
                        "Step FAILED in scenario '{}' | Screenshot saved: {}",
                        scenario.getName(), savedPath);

                // Capture screenshot bytes
                byte[] screenshot = getScreenshotBytes();

                if (screenshot != null && screenshot.length > 0) {

                    // Attach screenshot to Cucumber report
                    scenario.attach(
                            screenshot,
                            "image/png",
                            "Step Failure Screenshot");

                    // Attach screenshot to Allure report
                    Allure.addAttachment(
                            "Step Failure - " + scenario.getName(),
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            ".png");
                }
            }

        } catch (IllegalStateException e) {

            logger.warn(
                    "Skipping step screenshot capture; WebDriver was not initialized: {}",
                    e.getMessage());

        } catch (Exception e) {

            logger.error("Error capturing step failure screenshot", e);
        }
    }


    // ================= AFTER SCENARIO =================

    @After
    public void afterScenario(Scenario scenario) {

        try {

            if (getDriver() != null) {

                if (scenario.isFailed()) {

                    // Save failure screenshot to project directory
                    String savedPath = captureFailureScreenshot(
                            "FAILED_TC", scenario.getName());

                    logger.error(
                            "Scenario FAILED: {} | Screenshot saved: {}",
                            scenario.getName(), savedPath);

                    // Capture screenshot bytes
                    byte[] screenshot = getScreenshotBytes();

                    if (screenshot != null && screenshot.length > 0) {

                        // Attach screenshot to Cucumber report
                        scenario.attach(
                                screenshot,
                                "image/png",
                                "Test Case Failure Screenshot");

                        // Attach screenshot to Allure report
                        Allure.addAttachment(
                                "Test Case Failure - " + scenario.getName(),
                                "image/png",
                                new ByteArrayInputStream(screenshot),
                                ".png");
                    }

                } else {

                    logger.info("Scenario PASSED: {}", scenario.getName());
                }
            }

        } catch (IllegalStateException e) {

            logger.warn(
                    "Skipping screenshot capture; WebDriver was not initialized: {}",
                    e.getMessage());

        } catch (Exception e) {

            logger.error("Error during Cucumber teardown", e);

        } finally {

            try {

                tearDownCucumber();

            } catch (Exception e) {

                logger.warn(
                        "Error while closing WebDriver: {}",
                        e.getMessage());
            }
        }
    }
}