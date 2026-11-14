@Cart
Feature: OpenCart Shopping Cart Management

  As a customer
  I want to add products to my shopping cart
  So that I can review my selected items before checkout

  @Smoke @AddToCart
  Scenario: Add a searched product to the shopping cart
    Given the user launches the OpenCart application
    When the user enters product name "iPhone" in the search box
    And the user clicks the Search button
    And the user selects product "iPhone" from the search results
    And the user sets product quantity to "2"
    And the user clicks the Add to Cart button
    Then the user should see a success message confirming "iPhone" was added to the cart
    When the user navigates to the Shopping Cart page from the alert
    Then the product "iPhone" should be present in the Shopping Cart