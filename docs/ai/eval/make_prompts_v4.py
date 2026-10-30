#!/usr/bin/env python3
"""Write prompts-v4.json: eight fresh Playwright prompts (W01 to W08), written after the Playwright rules were added.

Held-out for the Playwright guidance: the tasks differ from A03, A04, A07 and A08, which the rules were written from.
Same bookshop and notes as prompts-v2.json; each prompt names a planted fault for the healthy-versus-faulty judge.
"""
import json, os, importlib.util

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("v2", os.path.join(HERE, "make_prompts.py"))
v2 = importlib.util.module_from_spec(spec); spec.loader.exec_module(v2)

P = []
def web(id_, harness, style, fault, task): P.append(dict(id=id_, harness=harness, style=style, fault=fault, kind="web", prompt=task + "\n\n" + v2.WEB_NOTES))

SP = ("h-sp-playwright", "Screenplay + Playwright")
PO = ("h-po-playwright", "Page Objects + Playwright")
web("W01", *SP, "login-accepts-any-password",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test where someone tries to sign in with the right email but the wrong password and is refused: the message 'Invalid email or password' is shown and they are still on the sign-in page.")
web("W02", *PO, "login-heading",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test that signs in with the valid account and checks that the Catalog heading is shown and the basket count is 0. Use a page object and a step library.")
web("W03", *SP, "search-ignored",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test where a signed-in reader searches the catalog for 'clean' and checks that exactly one book is listed, and that it is Clean Code.")
web("W04", *PO, "confirmation-no-number",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test where a reader signs in, adds Refactoring to the basket, checks out as Grace Hopper at '2 Side Rd' in Japan with the terms accepted, and checks that the confirmation thanks them and shows an order number like ORD-1001.")
web("W05", *SP, "basket-total",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test where a reader signs in, adds Clean Code, opens the basket, changes the quantity to 3, and checks that the line total for Clean Code is $100.50 and the basket total reads 'Total: $100.50'.")
web("W06", *PO, "remove-does-nothing",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test that signs in, adds The Pragmatic Programmer and Python Crash Course, opens the basket, removes The Pragmatic Programmer (confirming in the dialog), and checks that only Python Crash Course is left.")
web("W07", *SP, "reviews-never-load",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test that opens the catalog while signed in and checks that, once the reviews have loaded, there are three reviews and the 'Loading reviews...' text is gone. Do not use fixed sleeps.")
web("W08", *PO, "sort-reversed",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test that signs in and sorts the catalog by title by clicking the Title header once, then checks that the titles appear in alphabetical order: Clean Code, Fluent Python, Python Crash Course, Refactoring, The Pragmatic Programmer.")

if __name__ == "__main__":
    json.dump(P, open(os.path.join(HERE, "prompts-v4.json"), "w"), indent=1)
    print(len(P), "prompts written")
