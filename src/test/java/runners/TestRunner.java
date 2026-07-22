package runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(

        features = "src/test/resources/features",

        glue = {
                "stepDefinitions"
        },

        tags = "@Smoke",

        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },

        monochrome = true,

        publish = false
)

public class TestRunner
        extends AbstractTestNGCucumberTests {


    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {

        return super.scenarios();
    }
}