# OpenCart Hybrid & BDD Test Automation Framework

![Java](https://img.shields.io/badge/Java-11-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.44.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-7.12.0-FF6C37?style=for-the-badge)
![Cucumber](https://img.shields.io/badge/Cucumber_BDD-7.29.0-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker_Grid-4.27.0-2496ED?style=for-the-badge&logo=docker&logoColor=white)

An enterprise-grade, dual-architecture UI test automation framework built for the **OpenCart** e-commerce platform (`https://tutorialsninja.com/demo/`). Designed around the **Page Object Model (POM)**, it supports seamless runtime switching between **Cucumber BDD** feature execution and standalone **TestNG** functional suites across local machines, containerized **Docker Selenium Grid** nodes, and **GitHub Actions CI/CD** pipelines.

---

## Key Architectural Features

* **Dual-Suite Execution (BDD + TestNG POM):** Run Gherkin scenarios via `testng.xml` or standalone TestNG `@Test` classes via `testng-hybrid.xml` from a single Maven command using the dynamic `-DsuiteXmlFile` property—sharing the exact same Page Objects, `BaseClass`, and `DriverManager`.
* **Thread-Safe Parallel Execution:** Built with `ThreadLocal<WebDriver>` and `ThreadLocal<ExtentTest>` isolation to support concurrent multi-thread and cross-browser runs without session collisions.
* **Cross-Browser & Environment Abstraction:** Externalized configuration (`qa.properties`, `config.properties`) with CLI overrides for browser (`chrome`, `edge`, `firefox`), OS (`windows`, `linux`), headless mode, and execution environment (`local` vs. `remote`).
* **Containerized Docker Selenium Grid:** Pre-configured `docker-compose.yml` orchestrating a Selenium Hub alongside Chrome, Edge, and Firefox browser nodes for scalable Linux container execution.
* **Rich Reporting & Failure Diagnostics:**
  * **ExtentReports 5 (Spark UI):** Timestamped dark-theme HTML reports with OS/browser metadata, TestNG group categorization, and automatic failure screenshot attachment.
  * **Allure Reporting:** Step-by-step execution timelines and attachment support via AspectJ weaving.
  * **Log4j2:** Thread-aware rolling file and console logging (`logs/`).
* **CI/CD Ready:** Includes a GitHub Actions workflow (`.github/workflows/maven-tests.yml`) that spins up Docker Grid containers, validates hub readiness, executes tests, and archives HTML reports and failure screenshots as build artifacts.

---

## Tech Stack & Dependencies

| Category | Technology / Library | Version |
| :--- | :--- | :--- |
| **Language & Build** | Java (JDK) / Apache Maven | `11` / `3.8+` |
| **Browser Automation** | Selenium WebDriver | `4.44.0` |
| **Test Runners** | TestNG / Cucumber JVM (`cucumber-testng`) | `7.12.0` / `7.29.0` |
| **Data-Driven Testing** | Apache POI (`poi-ooxml`) & Cucumber Scenario Outlines | `5.5.1` |
| **Reporting** | ExtentReports Spark / Grasshopper Adapter / Allure | `5.1.2` / `1.14.0` / `2.29.1` |
| **Logging & Utilities** | Log4j2 / Apache Commons IO & Lang3 | `2.26.0` / `2.22.0` / `3.20.0` |
| **Containerization & CI** | Docker Compose (Selenium Grid) / GitHub Actions | `4.27.0` |

---

## Project Folder Structure

```text
Selenium-Cucumber-BDD-OpenCart-Framework/
├── .github/
│   └── workflows/
│       └── maven-tests.yml             # GitHub Actions CI/CD workflow
├── src/
│   └── test/
│       ├── java/
│       │   ├── factory/                # ThreadLocal DriverManager
│       │   ├── pageObjects/            # Page Object Model classes (HomePage, LoginPage, ProductPage, etc.)
│       │   ├── runners/                # Cucumber TestNG TestRunner
│       │   ├── stepDefinitions/        # Gherkin step definitions & Cucumber Hooks
│       │   ├── testBase/               # BaseClass (WebDriver lifecycle, Grid setup, screenshot utilities)
│       │   ├── testCases/              # Standalone TestNG test classes (TC_001 - TC_006)
│       │   └── utilities/              # ExtentReportManager (ITestListener), ExcelUtility, DataProviders
│       └── resources/
│           ├── features/               # Gherkin .feature files (Login, Registration, Search, Cart, E2E)
│           ├── config.properties       # Global framework configuration
│           ├── qa.properties           # QA environment URLs and test credentials
│           ├── extent.properties       # Extent Cucumber adapter settings
│           └── log4j2.xml              # Log4j2 appenders and formatting
├── docker-compose.yml                  # Selenium Grid Hub + Chrome, Firefox & Edge nodes
├── pom.xml                             # Maven dependencies, Surefire & Allure plugin configuration
├── testng.xml                          # Cucumber BDD cross-browser suite XML
└── testng-hybrid.xml                   # Standalone TestNG POM functional suite XML
PrerequisitesEnsure the following tools are installed on your system before running the framework:Java Development Kit (JDK) 11 or higher (JAVA_HOME configured)Apache Maven 3.8+ (mvn -version)Browsers: Google Chrome, Microsoft Edge, or Mozilla Firefox (for local execution)Docker Desktop (for containerized Selenium Grid execution)How to Run Tests1. Clone the RepositoryBashgit clone [https://github.com/danishakhtaransari/Selenium-Cucumber-BDD-OpenCart-Framework.git](https://github.com/danishakhtaransari/Selenium-Cucumber-BDD-OpenCart-Framework.git)
cd Selenium-Cucumber-BDD-OpenCart-Framework
2. Local Execution (Windows / macOS / Linux)Run Standalone TestNG POM Suite (testng-hybrid.xml):Bashmvn test -Denv=qa -Dbrowser=chrome -DsuiteXmlFile=testng-hybrid
Run Cucumber BDD Suite (testng.xml — Cross-Browser):Bashmvn test -Denv=qa -Ddataproviderthreadcount=1
Run in Headless Mode:Bashmvn test -Denv=qa -Dbrowser=chrome -Dheadless=true -DsuiteXmlFile=testng-hybrid
3. Remote Execution on Docker Selenium GridStart the Selenium Hub and Chrome Node:Bashdocker compose up -d selenium-hub chrome
(Optional: Start all browser nodes using docker compose up -d and monitor live sessions at http://localhost:4444/ui)Execute the Suite Against the Grid:Bashmvn test -Denv=qa -Dexecution_env=remote -Dos=linux -Dbrowser=chrome -DsuiteXmlFile=testng-hybrid -DautoOpenReport=false
Tear Down Docker Containers:Bashdocker compose down
Test Coverage SummaryModuleTestNG Test ClassCucumber FeatureScenarios / Validations CoveredAccount RegistrationTC_001_AccountRegistrationTestRegistration.featureDynamic random user registration & account creation confirmationAuthenticationTC_002_LoginTestLogin.featureValid login & negative login matrix (invalid email, invalid password, empty fields)Data-Driven LoginTC_003_LoginDDTLogin.featureExcel-driven positive and negative credential validation via Apache POIProduct SearchTC_004_SearchTestSearch.featureExisting product lookup (iPhone, MacBook) & non-existent product warning validationShopping CartTC_005_AddToCartTestAddToCart.featureProduct selection, quantity update, success alert assertion & cart verificationEnd-to-End JourneyTC_006_EndToEndTestEndToEndFlow.featureFull customer flow: Login → Search → Add to Cart → Verify Cart → LogoutReports, Screenshots & LogsExtent Spark HTML Reports: Generated in reports/AutomationReport_<timestamp>.html (auto-opens in your browser after local runs unless -DautoOpenReport=false is passed).Allure Interactive Report: Generate and serve locally after a test run:Bashmvn allure:serve
Failure Screenshots: Automatically captured on test/step failure and saved in screenshots/.Execution Logs: Detailed thread-level logs saved in logs/.AuthorDanish Akhtar Ansari — Senior Test Engineer / SDET


# OpenCart Automation Framework

## Project Overview

This project is a Hybrid Selenium Automation Framework developed using Java for automating the OpenCart e-commerce application.

The framework is designed using industry-standard automation practices such as Page Object Model (POM), TestNG, Maven, Docker Selenium Grid, Extent Reports, and Allure Reports. It supports both local and remote execution with parallel cross-browser testing.

---

## Technology Stack

* Java
* Selenium WebDriver 4
* TestNG
* Maven
* Page Object Model (POM)
* Docker Selenium Grid
* Extent Reports
* Allure Reports
* Log4j2
* Apache Commons
* Git
* GitHub
* Jenkins

---

## Framework Features

* Hybrid Automation Framework
* Page Object Model Design Pattern
* Local and Remote (Docker Grid) Execution
* Cross Browser Testing

  * Chrome
  * Firefox
* Parallel Test Execution
* Retry Failed Test Cases
* Automatic Screenshot Capture on Failure
* Extent HTML Reporting
* Allure Reporting
* Log4j2 Logging
* Maven Build Management
* External Configuration using config.properties
* TestNG Groups (Smoke, Sanity, Regression)
* Data Driven Testing
* Reusable Utility Classes
* Explicit Wait Implementation
* JavaScript Executor Utilities

---

## Project Structure

```
src
 ├── main
 │     └── pageObjects
 │
 ├── test
 │     ├── testCases
 │     ├── testBase
 │     ├── utilities
 │     └── resources
 │
reports
screenshots
logs
test-output
pom.xml
```

---

## Execution

### Local Execution

```
mvn clean test
```

---

### Docker Selenium Grid Execution

1. Start Docker Desktop

2. Start Selenium Hub

```
docker start selenium-hub
```

3. Start Chrome Node

```
docker start chrome-node
```

4. Start Firefox Node

```
docker start firefox-node
```

5. Execute Tests

```
mvn clean test
```

---

## Reporting

After execution:

* Extent Report is generated automatically.
* Allure Report is generated automatically.
* Screenshots are captured for failed test cases.

---

## Current Automation Coverage

* Account Registration
* Login Validation

Additional test cases will be added continuously.

---

## Future Enhancements

* Jenkins CI/CD Integration
* GitHub Actions
* Email Report Integration
* API Automation
* Database Validation
* Performance Testing
* Cloud Execution (BrowserStack / LambdaTest)

---

## Author

Danish

Automation Test Engineer

---

## Note

