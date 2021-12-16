Feature: Data-driven testing

Scenario: Non-web Data-driven testing from an external CSV file

Given the data in data/names-data.csv
When we enter this data
Then the values should be correct
