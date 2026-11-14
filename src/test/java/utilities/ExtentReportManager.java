package utilities;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import factory.DriverManager;
import testBase.BaseClass;

public class ExtentReportManager implements ITestListener {

    private static ExtentSparkReporter sparkReporter;
    private static ExtentReports extent;
    private static String reportPath;

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {

        // Initialize once per suite so parallel <test> blocks share a single unified report
        if (extent == null) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String reportName = "AutomationReport_" + timeStamp + ".html";

            String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
            new File(reportsDir).mkdirs();
            reportPath = reportsDir + File.separator + reportName;

            sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("OpenCart Automation Report");
            sparkReporter.config().setReportName("OpenCart Functional Test Execution");
            sparkReporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Dynamic Environment & Execution Metadata
            String env = System.getProperty("env", "qa").toUpperCase();
            String execEnv = System.getProperty("execution_env", "local").toUpperCase();

            extent.setSystemInfo("Application", "OpenCart");
            extent.setSystemInfo("Framework", "Selenium + TestNG");
            extent.setSystemInfo("Automation Language", "Java");
            extent.setSystemInfo("Environment", env);
            extent.setSystemInfo("Execution Mode", execEnv);
            extent.setSystemInfo("Tester", System.getProperty("user.name"));
            extent.setSystemInfo("Execution Time",
                    new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
        }

        // Resolve OS & Browser (Maven System Property > TestNG XML Parameter)
        String os = System.getProperty("os");
        if ((os == null || os.isBlank()) && context.getCurrentXmlTest() != null) {
            os = context.getCurrentXmlTest().getParameter("os");
        }
        if (os != null && !os.isBlank()) {
            extent.setSystemInfo("Operating System (" + context.getName() + ")", os);
        }

        String browser = System.getProperty("browser");
        if ((browser == null || browser.isBlank()) && context.getCurrentXmlTest() != null) {
            browser = context.getCurrentXmlTest().getParameter("browser");
        }
        if (browser != null && !browser.isBlank()) {
            extent.setSystemInfo("Browser (" + context.getName() + ")", browser);
        }

        if (context.getCurrentXmlTest() != null) {
            List<String> groups = context.getCurrentXmlTest().getIncludedGroups();
            if (groups != null && !groups.isEmpty()) {
                extent.setSystemInfo("Executed Groups", groups.toString());
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String browserTag = "";
        if (result.getInstance() instanceof BaseClass) {
            String bName = ((BaseClass) result.getInstance()).getBrowserName();
            if (bName != null && !bName.isBlank()) {
                browserTag = " [" + bName + "]";
            }
        }

        ExtentTest test = extent.createTest(
                result.getTestClass().getName()
                        + " :: "
                        + result.getMethod().getMethodName()
                        + browserTag);

        test.assignCategory(result.getMethod().getGroups());
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (extentTest.get() != null) {
            extentTest.get().log(
                    Status.PASS,
                    result.getMethod().getMethodName() + " PASSED");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test == null) {
            return;
        }

        test.log(Status.FAIL, result.getMethod().getMethodName() + " FAILED");
        if (result.getThrowable() != null) {
            test.fail(result.getThrowable());
        }

        try {
            if (DriverManager.getDriver() != null && result.getInstance() instanceof BaseClass) {
                BaseClass testClass = (BaseClass) result.getInstance();
                String absolutePath = testClass.captureScreen(result.getMethod().getMethodName());

                // Convert to relative path (../screenshots/<file>.png) so report stays portable
                String fileName = new File(absolutePath).getName();
                String relativePath = "../screenshots/" + fileName;
                test.addScreenCaptureFromPath(relativePath, "Failure Screenshot");
            }
        } catch (Exception e) {
            test.warning("Unable to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test == null) {
            return;
        }

        test.log(Status.SKIP, result.getMethod().getMethodName() + " SKIPPED");
        if (result.getThrowable() != null) {
            test.skip(result.getThrowable());
        }
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        extentTest.remove();

        // Auto-open in browser only on local desktop runs (skip in headless/CI/Docker)
        boolean autoOpen = Boolean.parseBoolean(System.getProperty("autoOpenReport", "true"));
        if (autoOpen && !GraphicsEnvironment.isHeadless() && reportPath != null) {
            try {
                File reportFile = new File(reportPath);
                if (reportFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(reportFile.toURI());
                }
            } catch (Exception e) {
                System.err.println("Could not auto-open Extent Report: " + e.getMessage());
            }
        }
    }
}
	/*  // Create the email message 
	  ImageHtmlEmail email = new ImageHtmlEmail();
	  email.setDataSourceResolver(new DataSourceUrlResolver(url));
	  email.setHostName("smtp.googlemail.com"); 
	  email.setSmtpPort(465);
	  email.setAuthenticator(new DefaultAuthenticator("danishdrive797@gmail.com","password")); 
	  email.setSSLOnConnect(true);
	  email.setFrom("danishdrive1029@gmail.com"); //Sender
	  email.setSubject("Test Results");
	  email.setMsg("Please find Attached Report....");
	  email.addTo("pavankumar.busyqa@gmail.com"); //Receiver 
	  email.attach(url, "extent report", "please check report..."); 
	  email.send(); // send the email 
	  }
	  catch(Exception e) 
	  { 
		  e.printStackTrace(); 
		  }
	 */ 
	 

