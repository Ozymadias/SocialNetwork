Feature: Endpoints
  Endpoints should work properly

  Scenario: one makes call to GET /findAll
    Given user with name Name, city City and birth date 1999-9-13 in database
    When one calls /findAll
    Then one should receives in response body that user

  Scenario: one makes call to POST /register
    Given user with name Name, city City and birth date 1999-9-13
    When one calls /register that user
    When one calls /findAll
    Then one should receives in response body that user

  Scenario: one makes call to GET /findByCity
    Given user with name Name, city City and birth date 1999-9-13 in database
    When one calls /findByCity with header city equal to City
    Then one should receives in response body that user