@Login
Feature: OpenCart Login

  As a registered OpenCart customer
  I want to login to my account
  So that I can access my account information

  @Smoke @ValidLogin
  Scenario: Successful login using valid credentials
    Given the user launches the OpenCart application
    When the user navigates to the Login page
    And the user enters valid login credentials
    And the user clicks the Login button
    Then the user should be successfully logged in
    
    
      @Regression @NegativeLogin
  Scenario Outline: Login should fail using invalid credentials
    Given the user launches the OpenCart application
    When the user navigates to the Login page
    And the user enters email "<email>" and password "<password>"
    And the user clicks the Login button
    Then the user should see a login warning message

    Examples:
      | email                     | password        |
      | invaliduser@test.com      | invalid123      |
      | invaliduser@test.com      | Test@123        |
      | validuser@test.com        | invalid123      |
      |                           |                 |