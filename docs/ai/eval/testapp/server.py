#!/usr/bin/env python3
"""A small bookshop used as the application under test by the evaluation (web pages and a REST API).

usage: server.py [--port 8765] [--fault NAME]

Everything is in memory and deterministic, so test runs are repeatable and need no third-party site.
`--fault NAME` plants one defect; the evaluation uses it to check that a generated test really notices
a broken application (a test that still passes with the defect checks nothing). Faults:
  login-heading, login-error-text, search-ignored, basket-total, confirmation-no-number, reviews-never-load,
  sort-reversed, remove-does-nothing, books-missing-one, order-status-200, validation-status-400,
  order-not-stored, login-accepts-any-password
"""
import argparse, json, re, threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

BOOKS = [
    {"id": 1, "title": "Fluent Python", "author": "Luciano Ramalho", "price": 45.00},
    {"id": 2, "title": "Clean Code", "author": "Robert Martin", "price": 33.50},
    {"id": 3, "title": "The Pragmatic Programmer", "author": "Hunt and Thomas", "price": 39.99},
    {"id": 4, "title": "Python Crash Course", "author": "Eric Matthes", "price": 29.95},
    {"id": 5, "title": "Refactoring", "author": "Martin Fowler", "price": 47.25},
]
USER = {"email": "reader@example.com", "password": "correct-horse"}
FAULT = ""
STATE = {"tokens": set(), "orders": {}, "next_order": 1001, "next_token": 1}
LOCK = threading.Lock()

STYLE = """<style>
body{font-family:sans-serif;margin:2rem;max-width:56rem} table{border-collapse:collapse;width:100%} td,th{border:1px solid #ccc;padding:.4rem;text-align:left}
th[data-sort]{cursor:pointer} .alert-error{color:#a00;margin:.5rem 0} #confirm-dialog{border:1px solid #333;padding:1rem;background:#fff;position:fixed;top:30%;left:30%}
nav a{margin-right:1rem} label{display:block;margin:.6rem 0 .2rem} .hidden{display:none}
</style>"""

NAV = '<nav><a href="/catalog">Catalog</a><a href="/basket" id="basket-link">Basket (<span id="basket-count">0</span>)</a><a href="/logout" id="logout">Sign out</a></nav>'

COMMON_JS = """<script>
function basket(){try{return JSON.parse(sessionStorage.getItem('basket')||'[]')}catch(e){return []}}
function saveBasket(b){sessionStorage.setItem('basket',JSON.stringify(b)); showCount()}
function showCount(){var el=document.getElementById('basket-count'); if(el) el.textContent=basket().reduce(function(n,l){return n+l.qty},0)}
function money(n){return '$'+n.toFixed(2)}
showCount();
</script>"""


def page(title, body, script=""):
    return ("<!doctype html><html lang='en'><head><meta charset='utf-8'><title>%s</title>%s</head><body>%s%s%s</body></html>" % (title, STYLE, body, COMMON_JS, script)).encode()


def login_page():
    return page("Sign in - Bookshop", """
<h1>Sign in</h1>
<form id="login-form">
  <label for="email">Email</label><input id="email" name="email" type="email">
  <label for="password">Password</label><input id="password" name="password" type="password">
  <p><button type="submit">Sign in</button></p>
</form>
<div id="login-error" class="alert-error hidden" role="alert"></div>""", """<script>
document.getElementById('login-form').addEventListener('submit', function(ev){
  ev.preventDefault();
  fetch('/api/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email:email.value,password:password.value})})
   .then(function(r){return r.json().then(function(j){return {ok:r.ok,body:j}})})
   .then(function(res){
     var err=document.getElementById('login-error');
     if(res.ok){sessionStorage.setItem('token',res.body.token); location.href='/catalog';}
     else {err.textContent=res.body.error; err.classList.remove('hidden');}
   });
});
</script>""")


def catalog_page():
    heading = "Browse" if FAULT == "login-heading" else "Catalog"
    return page("Catalog - Bookshop", NAV + """
<h1>%s</h1>
<label for="search">Search books</label><input id="search" placeholder="Search books">
<table id="book-table"><thead><tr><th data-sort="title">Title</th><th>Author</th><th data-sort="price">Price</th><th></th></tr></thead><tbody id="books"></tbody></table>
<h2>Reviews</h2><div id="reviews">Loading reviews...</div>""" % heading, """<script>
var FAULT=%s; var books=[]; var sortKey=null, sortDir=1;
function render(){
  var q=document.getElementById('search').value.toLowerCase();
  var rows=books.filter(function(b){return FAULT==='search-ignored' || b.title.toLowerCase().indexOf(q)>=0});
  if(sortKey){rows=rows.slice().sort(function(a,b){var x=a[sortKey],y=b[sortKey]; return (x>y?1:x<y?-1:0)*sortDir*(FAULT==='sort-reversed'?-1:1)})}
  document.getElementById('books').innerHTML=rows.map(function(b){
    return '<tr class="book" data-id="'+b.id+'"><td class="title">'+b.title+'</td><td class="author">'+b.author+'</td><td class="price">'+money(b.price)+'</td><td><button class="add" data-testid="add-'+b.id+'">Add to basket</button></td></tr>'}).join('');
}
fetch('/api/books').then(function(r){return r.json()}).then(function(j){books=j; render();});
document.getElementById('search').addEventListener('input', render);
document.querySelectorAll('th[data-sort]').forEach(function(th){th.addEventListener('click',function(){
  var k=th.getAttribute('data-sort'); if(sortKey===k){sortDir=-sortDir}else{sortKey=k;sortDir=1} render();})});
document.getElementById('books').addEventListener('click',function(ev){
  if(!ev.target.classList.contains('add'))return;
  var id=+ev.target.closest('tr').getAttribute('data-id'); var b=basket(); var l=b.find(function(x){return x.id===id});
  if(l){l.qty++}else{b.push({id:id,qty:1})} saveBasket(b);});
setTimeout(function(){ if(FAULT==='reviews-never-load') return;
  document.getElementById('reviews').innerHTML='<ul>'+['Great for beginners','Dense but rewarding','Worth re-reading'].map(function(t){return '<li class="review">'+t+'</li>'}).join('')+'</ul>';},700);
</script>""" % json.dumps(FAULT))


def basket_page():
    return page("Basket - Bookshop", NAV + """
<h1>Your basket</h1>
<table id="basket"><thead><tr><th>Title</th><th>Qty</th><th>Line total</th><th></th></tr></thead><tbody></tbody></table>
<p id="total">Total: $0.00</p>
<p><a href="/checkout" id="checkout-link">Checkout</a></p>
<div id="confirm-dialog" role="dialog" aria-label="Confirm removal" class="hidden"><p>Remove this book?</p><button id="confirm-remove">Remove</button> <button id="cancel-remove">Cancel</button></div>""", """<script>
var FAULT=%s; var books=[]; var pending=null;
function draw(){
  var b=basket(), total=0;
  document.querySelector('#basket tbody').innerHTML=b.map(function(l){var bk=books.find(function(x){return x.id===l.id}); if(!bk) return '';
    var lt=bk.price*l.qty; total+=lt;
    return '<tr class="line" data-id="'+l.id+'"><td class="title">'+bk.title+'</td><td><input class="qty" type="number" min="1" value="'+l.qty+'"></td><td class="line-total">'+money(lt)+'</td><td><button class="remove">Remove</button></td></tr>'}).join('');
  if(FAULT==='basket-total' && b.length) total+=0.01;
  document.getElementById('total').textContent='Total: '+money(total);
}
fetch('/api/books').then(function(r){return r.json()}).then(function(j){books=j; draw();});
document.querySelector('#basket tbody').addEventListener('click',function(ev){
  if(ev.target.classList.contains('remove')){pending=+ev.target.closest('tr').getAttribute('data-id'); document.getElementById('confirm-dialog').classList.remove('hidden');}});
document.querySelector('#basket tbody').addEventListener('change',function(ev){
  if(ev.target.classList.contains('qty')){var id=+ev.target.closest('tr').getAttribute('data-id'); var b=basket(); b.find(function(x){return x.id===id}).qty=Math.max(1,+ev.target.value); saveBasket(b); draw();}});
document.getElementById('cancel-remove').addEventListener('click',function(){pending=null; document.getElementById('confirm-dialog').classList.add('hidden');});
document.getElementById('confirm-remove').addEventListener('click',function(){
  if(FAULT!=='remove-does-nothing'){saveBasket(basket().filter(function(l){return l.id!==pending}));}
  pending=null; document.getElementById('confirm-dialog').classList.add('hidden'); draw();});
</script>""" % json.dumps(FAULT))


def checkout_page():
    return page("Checkout - Bookshop", NAV + """
<h1>Checkout</h1>
<form id="checkout-form">
  <label for="name">Full name</label><input id="name" name="name">
  <label for="address">Address</label><input id="address" name="address">
  <label for="country">Country</label><select id="country" name="country"><option value="">Choose...</option><option>Canada</option><option>Germany</option><option>Japan</option></select>
  <label><input id="terms" type="checkbox"> I accept the terms</label>
  <p><button type="submit">Place order</button></p>
</form>
<div id="checkout-error" class="alert-error hidden" role="alert"></div>
<div id="confirmation" class="hidden"></div>""", """<script>
document.getElementById('checkout-form').addEventListener('submit',function(ev){
  ev.preventDefault(); var err=document.getElementById('checkout-error'); err.classList.add('hidden');
  if(!document.getElementById('terms').checked){err.textContent='You must accept the terms'; err.classList.remove('hidden'); return;}
  fetch('/api/orders',{method:'POST',headers:{'Content-Type':'application/json','Authorization':'Bearer '+(sessionStorage.getItem('token')||'')},
    body:JSON.stringify({items:basket().map(function(l){return {bookId:l.id,qty:l.qty}}),name:document.getElementById('name').value,address:document.getElementById('address').value,country:document.getElementById('country').value})})
   .then(function(r){return r.json().then(function(j){return {ok:r.ok,body:j}})})
   .then(function(res){
     if(res.ok){var c=document.getElementById('confirmation'); c.textContent='Thank you! Order '+res.body.id; c.classList.remove('hidden'); sessionStorage.removeItem('basket'); showCount();}
     else{err.textContent=res.body.error; err.classList.remove('hidden');}});
});
</script>""")


def json_response(h, status, body):
    data = json.dumps(body).encode()
    h.send_response(status)
    h.send_header("Content-Type", "application/json")
    h.send_header("Content-Length", str(len(data)))
    h.end_headers()
    h.wfile.write(data)


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *a):  # quiet
        pass

    def html(self, body, status=200):
        self.send_response(status)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def read_json(self):
        n = int(self.headers.get("Content-Length") or 0)
        try:
            return json.loads(self.rfile.read(n) or b"{}")
        except ValueError:
            return None

    def token_ok(self):
        auth = self.headers.get("Authorization", "")
        return auth.startswith("Bearer ") and auth[7:] in STATE["tokens"]

    def do_GET(self):
        path = self.path.split("?")[0]
        if path in ("/", "/login"):
            return self.html(login_page())
        if path == "/catalog":
            return self.html(catalog_page())
        if path == "/basket":
            return self.html(basket_page())
        if path == "/checkout":
            return self.html(checkout_page())
        if path == "/logout":
            self.send_response(302); self.send_header("Location", "/login"); self.end_headers(); return
        if path == "/api/books":
            books = BOOKS[:-1] if FAULT == "books-missing-one" else BOOKS
            return json_response(self, 200, books)
        m = re.fullmatch(r"/api/books/(\d+)", path)
        if m:
            b = next((b for b in BOOKS if b["id"] == int(m.group(1))), None)
            return json_response(self, 200, b) if b else json_response(self, 404, {"error": "Book not found"})
        m = re.fullmatch(r"/api/orders/(ORD-\d+)", path)
        if m:
            if not self.token_ok():
                return json_response(self, 401, {"error": "Authentication required"})
            o = STATE["orders"].get(m.group(1))
            return json_response(self, 200, o) if o else json_response(self, 404, {"error": "Order not found"})
        json_response(self, 404, {"error": "Not found"})

    def do_POST(self):
        path = self.path.split("?")[0]
        if path == "/api/reset":
            with LOCK:
                STATE.update(tokens=set(), orders={}, next_order=1001, next_token=1)
            self.send_response(204); self.end_headers(); return
        body = self.read_json()
        if path == "/api/login":
            if not body or not body.get("email") or not body.get("password"):
                return json_response(self, 422, {"error": "email and password are required"})
            ok = body["email"] == USER["email"] and (body["password"] == USER["password"] or FAULT == "login-accepts-any-password")
            if not ok:
                return json_response(self, 401, {"error": "Wrong credentials" if FAULT == "login-error-text" else "Invalid email or password"})
            with LOCK:
                tok = "tok-%d" % STATE["next_token"]; STATE["next_token"] += 1; STATE["tokens"].add(tok)
            return json_response(self, 200, {"token": tok})
        if path == "/api/orders":
            if not self.token_ok():
                return json_response(self, 401, {"error": "Authentication required"})
            if not body or not body.get("name") or not body.get("items"):
                return json_response(self, 400 if FAULT == "validation-status-400" else 422,
                                     {"error": "Validation failed", "fields": {"name": "required"} if not (body or {}).get("name") else {"items": "required"}})
            total = 0.0
            for it in body["items"]:
                b = next((b for b in BOOKS if b["id"] == it.get("bookId")), None)
                if b: total += b["price"] * int(it.get("qty", 1))
            with LOCK:
                oid = "ORD-%d" % STATE["next_order"]; STATE["next_order"] += 1
                order = {"id": oid, "name": body["name"], "total": round(total, 2), "status": "PLACED", "items": body["items"]}
                if FAULT != "order-not-stored": STATE["orders"][oid] = order
            return json_response(self, 200 if FAULT == "order-status-200" else 201,
                                 dict(order, id=("" if FAULT == "confirmation-no-number" else oid)))
        json_response(self, 404, {"error": "Not found"})


def main():
    global FAULT
    ap = argparse.ArgumentParser()
    ap.add_argument("--port", type=int, default=8765)
    ap.add_argument("--fault", default="")
    a = ap.parse_args()
    FAULT = a.fault
    srv = ThreadingHTTPServer(("127.0.0.1", a.port), Handler)
    print("bookshop on http://localhost:%d%s" % (a.port, " (fault: %s)" % FAULT if FAULT else ""), flush=True)
    srv.serve_forever()


if __name__ == "__main__":
    main()
