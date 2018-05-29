Feature: Endpoints
  Endpoints should work properly

  Scenario Outline: one makes call to GET /findAll
    Given user with name <name>, city <city> and birth date <birthdateYear>-<birthdateMonth>-<birthdateDay> in database
    When one calls /findAll
    Then one should receives in response body that user(s)

    Examples:
      | name  | city  | birthdateYear | birthdateMonth | birthdateDay |
      | Name  | City  | 1999          | 9              | 13           |
      | Name2 | City2 | 1999          | 9              | 13           |

  Scenario: one makes call to POST /register
    Given users
      | name  | city  | birthdate |
      | Name  | City  | 1999-9-13 |
      | Name2 | City2 | 1999-9-13 |
    When one registers that user(s)
    When one calls /findAll
    Then one should receives in response body that user(s)

  Scenario: one makes call to GET /findByCity
    Given user with name Name, city City and birth date 1999-9-13 in database
    When one calls /findByCity with header city equal to City
    Then one should receives in response body that user(s)