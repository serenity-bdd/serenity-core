#!/usr/bin/env python3
"""Write prompts-v2.json: the fresh prompt set, written after the guidance was frozen (git tag ai-guidance-frozen-1).

The tasks run against the local bookshop (testapp/server.py). Each prompt carries a description of the
application, as a real user would give, so that a model can write the locators without seeing the pages.
`fault` names the defect the evaluation plants to check that the generated test notices a broken application.
"""
import json, os

HERE = os.path.dirname(os.path.abspath(__file__))
BASE = "http://localhost:8765"

WEB_NOTES = """Application under test: an online bookshop at %s (it is already running; do not start it).
- /login: heading "Sign in"; text fields with ids "email" and "password"; a "Sign in" button. A failed sign-in shows its message in the element with id "login-error". The valid account is reader@example.com / correct-horse. A successful sign-in goes to /catalog.
- /catalog: heading "Catalog"; a search field (id "search"); a table (id "book-table") whose body (id "books") has one row (class "book") per book, with cells of class "title", "author" and "price" (like "$45.00") and a button "Add to basket". Clicking the Title or Price header (attribute data-sort) sorts the table; clicking again reverses it. The books are Fluent Python (45.00), Clean Code (33.50), The Pragmatic Programmer (39.99), Python Crash Course (29.95) and Refactoring (47.25). A "Reviews" section (id "reviews") first says "Loading reviews..." and about 700 ms later lists three items of class "review".
- Every page after sign-in has a link "Basket (n)" whose count n is in the element with id "basket-count".
- /basket: heading "Your basket"; a table (id "basket") with one row (class "line") per book, with cells of class "title" and "line-total", a number input of class "qty" and a "Remove" button; Remove opens a dialog (id "confirm-dialog") asking "Remove this book?" with buttons "Remove" and "Cancel". The total is in the element with id "total" and reads like "Total: $80.75". A "Checkout" link goes to /checkout.
- /checkout: text fields with ids "name" and "address", a country dropdown (id "country": Canada, Germany, Japan), a checkbox (id "terms") labelled "I accept the terms", and a "Place order" button. After a successful order the element with id "confirmation" reads "Thank you! Order ORD-1001" (the number varies). Without the terms accepted, the element with id "checkout-error" reads "You must accept the terms".""" % BASE

REST_NOTES = """Application under test: the REST API of an online bookshop at %s (it is already running; do not start it).
- GET /api/books returns 200 and a JSON array of the 5 books, each {"id","title","author","price"}. GET /api/books/{id} returns 200 and the book, or 404 {"error":"Book not found"}.
- POST /api/login with {"email","password"} returns 200 {"token"}; 401 {"error":"Invalid email or password"} for a wrong password; 422 {"error":"email and password are required"} if either is missing. The valid account is reader@example.com / correct-horse.
- POST /api/orders (header "Authorization: Bearer <token>", JSON {"items":[{"bookId":1,"qty":2}],"name":"Ada","address":"1 Main St","country":"Canada"}) returns 201 {"id":"ORD-1001","total":90.0,"status":"PLACED",...}; 401 {"error":"Authentication required"} without a valid token; 422 {"error":"Validation failed","fields":{...}} if the name or the items are missing.
- GET /api/orders/{id} (with the token) returns 200 and the order, or 404 {"error":"Order not found"}.
Book prices: 1 Fluent Python 45.00, 2 Clean Code 33.50, 3 The Pragmatic Programmer 39.99, 4 Python Crash Course 29.95, 5 Refactoring 47.25.""" % BASE

P = []
def web(id_, harness, style, fault, task): P.append(dict(id=id_, harness=harness, style=style, fault=fault, kind="web", prompt=task + "\n\n" + WEB_NOTES))
def rest(id_, harness, style, fault, task): P.append(dict(id=id_, harness=harness, style=style, fault=fault, kind="rest", prompt=task + "\n\n" + REST_NOTES))

web("A01", "h-po-selenium", "Page Objects + Selenium", "login-heading",
    "Using Serenity BDD with Page Objects and JUnit 5 (Selenium), write a test that signs in with the valid account and checks that the Catalog page is shown. Use a page object and a step library.")
web("A02", "h-sp-selenium", "Screenplay + Selenium", "login-error-text",
    "Using Serenity BDD Screenplay with Selenium and JUnit 5, write a test where a reader signs in with a wrong password and sees the message 'Invalid email or password'. Create a reusable sign-in task.")
web("A03", "h-po-playwright", "Page Objects + Playwright", "search-ignored",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test that signs in, searches for 'python' and checks that exactly two books are listed: Fluent Python and Python Crash Course.")
web("A04", "h-sp-playwright", "Screenplay + Playwright", "basket-total",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test where a reader signs in, adds Clean Code and Refactoring to the basket, opens the basket, and checks that the basket count is 2 and the total is $80.75.")
web("A05", "h-sp-selenium", "Screenplay + Selenium", "confirmation-no-number",
    "Using Serenity BDD Screenplay with Selenium and JUnit 5, write a test where a reader signs in, adds The Pragmatic Programmer, checks out as Ada Lovelace at '1 Main St' in Germany with the terms accepted, and checks that the confirmation shows an order number of the form ORD- followed by digits.")
web("A06", "h-po-selenium", "Page Objects + Selenium", "reviews-never-load",
    "Using Serenity BDD with Page Objects and JUnit 5 (Selenium), write a test that opens the catalog and checks that the reviews section shows three reviews. The reviews appear after a short delay; do not use fixed sleeps.")
web("A07", "h-sp-playwright", "Screenplay + Playwright", "sort-reversed",
    "Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test that opens the catalog (signed in), sorts it by price by clicking the Price header once, and checks that the first book is Python Crash Course and the last is Refactoring.")
web("A08", "h-po-playwright", "Page Objects + Playwright", "remove-does-nothing",
    "Using Serenity BDD with Playwright and Page Objects and JUnit 5, write a test that signs in, adds Clean Code, opens the basket, removes the book (confirming in the dialog), and checks that the basket has no lines.")
web("A09", "h-cuke-sp", "Cucumber + Screenplay", "login-error-text",
    "Using Serenity BDD with Cucumber and Screenplay (Selenium) on the JUnit Platform, write a feature file with a Scenario Outline for signing in (a valid account shows the Catalog page; a wrong password shows 'Invalid email or password'), the step definitions, and the runner.")
web("A10", "h-cuke-sp", "Cucumber + page objects", "basket-total",
    "Using Serenity BDD with Cucumber (not Screenplay) and Selenium on the JUnit Platform, write a feature tagged @smoke with a Background in which the reader is signed in, a data table of books to add to the basket (Clean Code and Refactoring), and a step that checks the basket total is $80.75. Use page objects and @Steps classes in the step definitions, and write the runner.")
rest("A11", "h-sp-rest", "Screenplay REST", "books-missing-one",
    "Using Serenity BDD Screenplay and JUnit 5, write a test that gets all the books and checks there are 5, and that book 3 has the title 'The Pragmatic Programmer'. Put the requests in a reusable task.")
rest("A12", "h-sp-rest", "Screenplay REST", "order-status-200",
    "Using Serenity BDD Screenplay and JUnit 5, write a test that signs in through the API to get a token, places an order for 2 copies of book 1 (name Ada, address '1 Main St', country Canada), and checks the response status is 201 and the total is 90.0. Also check that placing the same order without a token returns 401.")
rest("A13", "h-sp-rest", "Screenplay REST", "validation-status-400",
    "Using Serenity BDD Screenplay and JUnit 5, write a test that places an order without a name (signed in) and checks the response is 422 with the error 'Validation failed' and a fields entry for name.")
rest("A14", "h-sp-rest", "Screenplay REST", "order-not-stored",
    "Using Serenity BDD Screenplay and JUnit 5, write a test that signs in, places an order, then gets it by the returned id and checks that the name and the total match what was ordered.")

if __name__ == "__main__":
    json.dump(P, open(os.path.join(HERE, "prompts-v2.json"), "w"), indent=1)
    print(len(P), "prompts written")
