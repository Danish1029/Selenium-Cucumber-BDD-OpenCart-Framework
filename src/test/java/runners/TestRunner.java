package runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(

        features = "src/test/resources/features",

        glue = {
                "stepDefinitions"
        },

/*        tags = "@Smoke",
        		tags = "@NegativeLogin",
        		//		tags = "@Smoke or @NegativeLogin",
 */
        		plugin = {
        		        "pretty",
        		        "html:target/cucumber-reports/cucumber.html",
        		        "json:target/cucumber-reports/cucumber.json",
        		        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        		},

        monochrome = true,

        publish = false
)

public class TestRunner
        extends AbstractTestNGCucumberTests {


    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {

        return super.scenarios();
    }
}