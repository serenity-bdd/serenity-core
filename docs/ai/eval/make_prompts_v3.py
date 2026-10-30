#!/usr/bin/env python3
"""Write prompts-v3.json: suite prompts (several scenarios across several features) for the living-documentation checks.

The prompts do not mention living documentation, report structure or naming: whether the output reads as living
documentation and is grouped by function has to come from the skill or AGENTS.md, not from the prompt.
Same bookshop and notes as prompts-v2.json.
"""
import json, os, importlib.util

HERE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("v2", os.path.join(HERE, "make_prompts.py"))
v2 = importlib.util.module_from_spec(spec); spec.loader.exec_module(v2)

WEB_TASK = """write acceptance tests for the bookshop covering these three areas:
1. Signing in: a valid account reaches the Catalog page; a wrong password is refused with the message 'Invalid email or password'.
2. Searching: searching for 'python' lists exactly Fluent Python and Python Crash Course; searching for 'zzz' lists no books.
3. The basket: adding Clean Code and Refactoring makes the basket count 2; removing a book (confirming in the dialog) takes it out of the basket."""
REST_TASK = """write acceptance tests for the bookshop API covering these three areas:
1. Books: listing returns the 5 books; getting book 3 returns 'The Pragmatic Programmer'; getting book 99 returns 404 'Book not found'.
2. Signing in: valid credentials return a token; a wrong password returns 401 'Invalid email or password'.
3. Orders: a signed-in reader who orders 2 copies of book 1 gets 201 and an order id that can be fetched afterwards; an order without a name returns 422 'Validation failed'."""

P = []
def web(id_, harness, style, fault, intro): P.append(dict(id=id_, harness=harness, style=style, fault=fault, kind="web", prompt=intro + " " + WEB_TASK + "\n\n" + v2.WEB_NOTES))
def rest(id_, harness, style, fault, intro): P.append(dict(id=id_, harness=harness, style=style, fault=fault, kind="rest", prompt=intro + " " + REST_TASK + "\n\n" + v2.REST_NOTES))

web("L01", "h-sp-selenium", "Screenplay + Selenium", "search-ignored", "Using Serenity BDD Screenplay with Selenium and JUnit 5,")
web("L02", "h-po-selenium", "Page Objects + Selenium", "remove-does-nothing", "Using Serenity BDD with Page Objects and JUnit 5 (Selenium),")
web("L03", "h-cuke-sp", "Cucumber + Screenplay", "login-error-text", "Using Serenity BDD with Cucumber and Screenplay (Selenium) on the JUnit Platform,")
web("L04", "h-po-playwright", "Page Objects + Playwright", "search-ignored", "Using Serenity BDD with Playwright and Page Objects and JUnit 5,")
web("L05", "h-sp-playwright", "Screenplay + Playwright", "remove-does-nothing", "Using Serenity BDD Screenplay with Playwright and JUnit 5,")
rest("L06", "h-sp-rest", "Screenplay REST", "validation-status-400", "Using Serenity BDD Screenplay and JUnit 5,")

if __name__ == "__main__":
    json.dump(P, open(os.path.join(HERE, "prompts-v3.json"), "w"), indent=1)
    print(len(P), "prompts written")
