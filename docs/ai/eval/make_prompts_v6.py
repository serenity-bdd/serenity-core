#!/usr/bin/env python3
"""Write prompts-v6.json: held-out prompts for REST with a step library and Cucumber with page objects.

Written after the guidance for these styles was finished and before any model saw them. Different tasks and
faults from prompts-v5.json (which the guidance was tuned on). R05 to R08 are REST; C05 to C08 are Cucumber.
"""
import json, os, importlib.util

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("v5", os.path.join(HERE, "make_prompts_v5.py"))
v5 = importlib.util.module_from_spec(spec); spec.loader.exec_module(v5)
rest, cuke, REST_STYLE, CUKE_STYLE = v5.rest, v5.cuke, v5.REST_STYLE, v5.CUKE_STYLE
v5.P.clear()

rest("R05", "login-accepts-any-password", REST_STYLE + " write a test where someone signs in with the right email but a wrong password and is refused: the API answers 401 with the error 'Invalid email or password' and no token.")
rest("R06", "confirmation-no-number", REST_STYLE + " write a test that signs in, places an order for 2 copies of book 3 (name Grace Hopper, address '2 Side Rd', country Japan) and checks the confirmation has an order id that starts with 'ORD-' and the status PLACED.")
rest("R07", "books-missing-one", REST_STYLE + " write a test that fetches the catalogue and checks that it lists 5 books and that the titles include Refactoring and Fluent Python.")
rest("R08", "order-not-stored", REST_STYLE + " write a test that places an order for 2 copies of book 1, then looks the order up again by its id and checks that its total is 90.0 and its status is PLACED.")
cuke("C05", "login-accepts-any-password", CUKE_STYLE + " write a feature where someone who signs in with the right email and a wrong password is refused with 'Invalid email or password' and stays on the sign-in page.")
cuke("C06", "sort-reversed", CUKE_STYLE + " write a feature where a signed-in reader sorts the catalog by title by clicking the Title header once and sees the titles in alphabetical order: Clean Code, Fluent Python, Python Crash Course, Refactoring, The Pragmatic Programmer.")
cuke("C07", "confirmation-no-number", CUKE_STYLE + " write a feature where a signed-in reader buys Refactoring, checking out as Grace Hopper at '2 Side Rd' in Japan with the terms accepted, and is thanked with an order number like ORD-1001.")
cuke("C08", "search-ignored", CUKE_STYLE + " write a feature for the catalog search where searching for 'clean' lists one book, Clean Code, and searching for 'python' lists two books.")

if __name__ == "__main__":
    json.dump(v5.P, open(os.path.join(HERE, "prompts-v6.json"), "w"), indent=1)
    print(len(v5.P), "prompts written")
