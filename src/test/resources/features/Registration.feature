@Registration
Feature: OpenCart Account Registration

  As a new customer
  I want to create an OpenCart account
  So that I can access registered customer features

  @Smoke @ValidRegistration
  Scenario: Successful account registration with valid details

    Given the user launches the OpenCart application for registration
    When the user navigates to the Registration page
    And the user enters valid registration details
    And the user accepts the Privacy Policy
    And the user clicks the Continue button
    Then the account should be created successfully