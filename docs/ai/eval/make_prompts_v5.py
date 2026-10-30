#!/usr/bin/env python3
"""Write prompts-v5.json: REST with a step library (SerenityRest / RestAssured style) and Cucumber with page objects.

R01 to R04: Serenity BDD REST tests written with SerenityRest (REST-assured) and a @Steps step library, JUnit 5.
C01 to C04: Cucumber on the JUnit Platform with page objects and a step library (no Screenplay), Selenium.
Same bookshop and notes as prompts-v2.json; each prompt names a planted fault for the healthy-versus-faulty judge.
"""
import json, os, importlib.util

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("v2", os.path.join(HERE, "make_prompts.py"))
v2 = importlib.util.module_from_spec(spec); spec.loader.exec_module(v2)

P = []
def rest(id_, fault, task): P.append(dict(id=id_, harness="h-sp-rest", style="REST + step library", fault=fault, kind="rest", prompt=task + "\n\n" + v2.REST_NOTES))
def cuke(id_, fault, task): P.append(dict(id=id_, harness="h-cuke-sp", style="Cucumber + page objects", fault=fault, kind="web", prompt=task + "\n\n" + v2.WEB_NOTES))

REST_STYLE = "Using Serenity BDD with SerenityRest (the REST-assured integration), a @Steps step library and JUnit 5 (no Screenplay),"
CUKE_STYLE = "Using Serenity BDD with Cucumber (not Screenplay) and Selenium on the JUnit Platform, with page objects and a step library,"

rest("R01", "books-missing-one", REST_STYLE + " write a test that lists the books and checks there are 5 and that book 2 is 'Clean Code' (check only its title).")
rest("R02", "order-status-200", REST_STYLE + " write a test that signs in through the API, places an order for 1 copy of book 4 (name Grace, address '2 Side Rd', country Japan), and checks the response is 201 with a status of PLACED and an id starting with 'ORD-'.")
rest("R03", "validation-status-400", REST_STYLE + " write a test that places an order with an empty items list (signed in, name Ada) and checks the API answers 422 with the error 'Validation failed'.")
rest("R04", "order-not-stored", REST_STYLE + " write a test that places an order for book 5, then fetches it by the returned id and checks that the order is PLACED and its total is 47.25.")
cuke("C01", "login-error-text", CUKE_STYLE + " write a feature where a reader who signs in with a wrong password is refused with the message 'Invalid email or password', and a reader with the valid account reaches the Catalog page.")
cuke("C02", "search-ignored", CUKE_STYLE + " write a feature for searching the catalog: searching for 'refactoring' lists exactly one book, 'Refactoring', and searching for 'zzz' lists none. Use a Scenario Outline.")
cuke("C03", "basket-total", CUKE_STYLE + " write a feature where a signed-in reader (use a Background) adds Clean Code and The Pragmatic Programmer to the basket, opens it, and sees a total of Total: $73.49.")
cuke("C04", "remove-does-nothing", CUKE_STYLE + " write a feature where a signed-in reader adds Fluent Python, removes it from the basket (confirming the dialog) and sees an empty basket. Tag the scenario @basket.")

if __name__ == "__main__":
    json.dump(P, open(os.path.join(HERE, "prompts-v5.json"), "w"), indent=1)
    print(len(P), "prompts written")
