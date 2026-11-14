package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ReportManager {

    private static final String TIMESTAMP =
            ExecutionContext.getExecutionTimestamp();

    public static void createReportDirectories() {

        new File("reports/cucumber").mkdirs();
        new File("reports/allure").mkdirs();
        new File("logs").mkdirs();
    }

    public static void archiveCucumberReport() {

        try {

            Path source =
                    Paths.get("target/cucumber-reports/cucumber.html");

            if (!Files.exists(source)) {

                System.out.println(
                        "Cucumber HTML report not found: " + source);

                return;
            }

            Path destination =
                    Paths.get(
                            "reports/cucumber/CucumberReport_"
                                    + TIMESTAMP
                                    + ".html");

            Files.createDirectories(destination.getParent());

            Files.copy(
                    source,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println(
                    "Cucumber report saved: "
                            + destination.toAbsolutePath());

        } catch (IOException e) {

            System.err.println(
                    "Unable to archive Cucumber report: "
                            + e.getMessage());
        }
    }
}