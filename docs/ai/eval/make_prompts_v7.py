#!/usr/bin/env python3
"""Write prompts-v7.json: a third prompt set for REST with a step library and Cucumber with page objects.

Written after the v6 follow-up fixes and before any model saw these prompts, with task shapes the guidance never
mentioned (data tables, a Background, tags, an outline over API calls, prices as decimals). R09 to R12 are REST;
C09 to C12 are Cucumber with page objects.
"""
import json, os, importlib.util

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("v5", os.path.join(HERE, "make_prompts_v5.py"))
v5 = importlib.util.module_from_spec(spec); spec.loader.exec_module(v5)
rest, cuke, REST_STYLE, CUKE_STYLE = v5.rest, v5.cuke, v5.REST_STYLE, v5.CUKE_STYLE
v5.P.clear()

rest("R09", "books-missing-one", REST_STYLE + " write a test that fetches each of books 1 to 5 by id (use a parameterised JUnit 5 test) and checks the title and price of each: 1 Fluent Python 45.00, 2 Clean Code 33.50, 3 The Pragmatic Programmer 39.99, 4 Python Crash Course 29.95, 5 Refactoring 47.25. The catalogue must also list 5 books.")
rest("R10", "validation-status-400", REST_STYLE + " write a test that places an order while signed in but without a name, and checks that the API answers 422 with the error 'Validation failed' and that the 'fields' object mentions the name.")
rest("R11", "login-error-text", REST_STYLE + " write a test that signs in with a wrong password and checks the status is 401 and the error text is 'Invalid email or password', then signs in with the valid account and checks a token is returned.")
rest("R12", "order-status-200", REST_STYLE + " write a test that, signed in, places an order for 3 copies of book 2 (name Ada, address '1 Main St', country Canada) and checks that the response is 201 and the total is 100.5.")
cuke("C09", "basket-total", CUKE_STYLE + " write a feature with a data table: a signed-in reader adds these books to the basket (Fluent Python, Refactoring), opens the basket and sees a total of Total: $92.25.")
cuke("C10", "login-error-text", CUKE_STYLE + " write a feature with a Scenario Outline that tries two bad sign-ins (a wrong password for reader@example.com, and an unknown email) and checks each is refused with 'Invalid email or password'. Tag the outline @login.")
cuke("C11", "remove-does-nothing", CUKE_STYLE + " write a feature where a signed-in reader (use a Background) adds Refactoring and Clean Code, removes Clean Code (confirming the dialog), and the basket then contains only Refactoring.")
cuke("C12", "reviews-never-load", CUKE_STYLE + " write a feature where a signed-in reader opens the catalog and, once the reviews have loaded, sees three reviews and no 'Loading reviews...' text. Do not use fixed sleeps.")

if __name__ == "__main__":
    json.dump(v5.P, open(os.path.join(HERE, "prompts-v7.json"), "w"), indent=1)
    print(len(v5.P), "prompts written")
