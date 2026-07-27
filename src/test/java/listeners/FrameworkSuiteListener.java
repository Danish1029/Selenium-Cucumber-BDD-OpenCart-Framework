package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import utilities.ReportManager;

public class FrameworkSuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {

        System.out.println(
                "========================================");

        System.out.println(
                "Starting Automation Suite: "
                        + suite.getName());

        ReportManager.createReportDirectories();

        System.out.println(
                "========================================");
    }

    @Override
    public void onFinish(ISuite suite) {

        System.out.println(
                "========================================");

        System.out.println(
                "Automation Suite Finished: "
                        + suite.getName());

        ReportManager.archiveCucumberReport();

        System.out.println(
                "========================================");
    }
}