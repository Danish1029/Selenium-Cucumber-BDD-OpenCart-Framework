@E2E
Feature: OpenCart End-to-End Customer Purchase Journey

  As a registered OpenCart customer
  I want to log in, search for a product, add it to my cart, verify it, and log out
  So that my end-to-end shopping workflow works seamlessly

  Background:
    Given the user launches the OpenCart application
    When the user navigates to the Login page
    And the user enters valid login credentials
    And the user clicks the Login button
    Then the user should be successfully logged in

  @Regression @E2EPurchase
  Scenario: Logged-in customer searches for a product, adds it to cart, and logs out
    When the user enters product name "iphone" in the search box
    And the user clicks the Search button
    And the user selects product "iphone" from the search results
    And the user sets product quantity to "1"
    And the user clicks the Add to Cart button
    Then the user should see a success message confirming "iphone" was added to the cart
    When the user navigates to the Shopping Cart page from the alert
    Then the product "iphone" should be present in the Shopping Cart
    When the user logs out from the OpenCart account
    Then the user should be successfully logged out