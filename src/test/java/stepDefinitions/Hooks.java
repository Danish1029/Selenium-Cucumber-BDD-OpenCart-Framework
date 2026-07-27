package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import testBase.BaseClass;

public class Hooks extends BaseClass {

    private static final Logger logger =
            LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario)
            throws Exception {

        logger.info(
                "==============================================");

        logger.info(
                "Starting Cucumber Scenario: {}",
                scenario.getName());

        logger.info(
                "Thread: {}",
                Thread.currentThread().getName());


        String browser =
                System.getProperty(
                        "browser",
                        "chrome");

        String os =
                System.getProperty(
                        "os",
                        "windows");


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
    public void afterScenario(
            Scenario scenario) {

        try {

            if (scenario.isFailed()) {

                byte[] screenshot =
                        getScreenshotBytes();

                scenario.attach(
                        screenshot,
                        "image/png",
                        "Failure Screenshot");

                logger.error(
                        "Scenario FAILED: {}",
                        scenario.getName());

            } else {

                logger.info(
                        "Scenario PASSED: {}",
                        scenario.getName());
            }

        } catch (Exception e) {

            logger.error(
                    "Error during Cucumber teardown",
                    e);

        } finally {

            tearDownCucumber();
        }
    }
}