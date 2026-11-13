@Search
Feature: OpenCart Product Search

  As a customer
  I want to search for products in the OpenCart store
  So that I can quickly find items I want to purchase

  @Smoke @ValidSearch
  Scenario Outline: Search for an existing product
    Given the user launches the OpenCart application
    When the user enters product name "<productName>" in the search box
    And the user clicks the Search button
    Then the product "<productName>" should be displayed in the search results

    Examples:
      | productName |
      | MacBook     |
      | iPhone      |

  @Regression @InvalidSearch
  Scenario: Search for a non-existing product
    Given the user launches the OpenCart application
    When the user enters product name "NonExistentProductXYZ999" in the search box
    And the user clicks the Search button
    Then the user should see a no product found message