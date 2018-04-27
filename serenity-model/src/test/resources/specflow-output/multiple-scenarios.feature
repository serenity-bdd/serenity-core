Scenario Outline: Populate business transaction and payment type drop down lists
Given the BusinessPaymentProcess drop down list is populated
And I am of <Inputter-role> role
When I select <Business payment process value> from BusinessPaymentProcess drop down list
Then the BusinessTransactionType drop down list is populated with <Transaction types>
And the PaymentType drop down list is populated with <Payment types>

Examples:
| Inputter-role          | Business payment process value      | Transaction types | Payment types                |
| Inputter               | Credit Card Repayment               | N/A               | Funds Transfer               |
| Inputter               | Fee/Interest Transfer               | N/A               | Funds Transfer               |
| Inputter               | Funds Transfer                      | N/A               | Funds Transfer,Direct Credit |
| Inputter               | Funds Transfer between Own Accounts | N/A               | Funds Transfer               |
| Inputter               | Loan Repayment                      | Loan Repayment    | Funds Transfer               |
| Inputter               | Without Passbook Withdrawal         | N/A               | Funds Transfer,Direct Credit |
| Inputter-DirectBanking | Credit Card Repayment               | N/A               | Funds Transfer               |
| Inputter-DirectBanking | Fee/Interest Transfer               | N/A               | Funds Transfer               |
| Inputter-DirectBanking | Funds Transfer                      | N/A               | Funds Transfer               |
| Inputter-DirectBanking | Funds Transfer between Own Accounts | N/A               | Funds Transfer               |
| Inputter-DirectBanking | Pay a Bill                          | N/A               | BPAY                         |