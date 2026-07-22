package stepDefinitions;

import java.io.IOException;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import testBase.BaseClass;

public class Hooks extends BaseClass {


    @Before
    public void setUpScenario(Scenario scenario)
            throws IOException, InterruptedException {

        String browser =
                System.getProperty(
                        "browser",
                        "chrome");

        String os =
                System.getProperty(
                        "os",
                        "windows");

        logger = org.apache.logging.log4j.LogManager
                .getLogger(Hooks.class);

        logger.info(
                "==============================================");

        logger.info(
                "Starting Cucumber Scenario: {}",
                scenario.getName());

        logger.info(
                "Browser: {}",
                browser);

        logger.info(
                "OS: {}",
                os);

        setupCucumber(
                os,
                browser);
    }


    @After
    public void tearDownScenario(
            Scenario scenario) {

        try {

            if (scenario.isFailed()
                    && driver != null) {

                byte[] screenshot =
                        getScreenshotBytes();

                scenario.attach(
                        screenshot,
                        "image/png",
                        "Failure Screenshot");

                logger.error(
                        "Scenario FAILED: {}",
                        scenario.getName());
            }
            else {

                logger.info(
                        "Scenario PASSED: {}",
                        scenario.getName());
            }

        }
        catch (Exception e) {

            if (logger != null) {

                logger.error(
                        "Error during Cucumber teardown",
                        e);
            }
        }
        finally {

            tearDownCucumber();
        }
    }
}