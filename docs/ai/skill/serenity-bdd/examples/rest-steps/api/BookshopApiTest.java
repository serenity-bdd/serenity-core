package api;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Browsing the book catalogue")
class BookshopApiTest {

    static final BookshopServer server = new BookshopServer();
    static int port;

    @Steps
    BookshopApi bookshop;

    @BeforeAll
    static void startServer() throws Exception {
        port = server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void pointAtTheServer() {
        bookshop.usingTheServerAt("http://localhost:" + port);
    }

    @Test
    @DisplayName("A shopper sees the catalogue and finds Clean Code by its number")
    void shopperFindsABook() {
        bookshop.listTheBooks();
        bookshop.responseShouldHaveStatus(200);
        bookshop.responseShouldListBooks(2);

        bookshop.lookUpBook(2);
        bookshop.responseShouldShowTitle("Clean Code");
    }

    @Test
    @DisplayName("A shopper who asks for a book that does not exist is told so")
    void unknownBook() {
        bookshop.lookUpBook(99);
        bookshop.responseShouldHaveStatus(404);
        bookshop.responseShouldShowError("Book not found");
    }

    @Test
    @DisplayName("A signed-in reader can place an order, and an anonymous visitor cannot")
    void ordering() {
        bookshop.placeAnOrderFor(1);
        bookshop.responseShouldHaveStatus(201);
        bookshop.orderTotalShouldBe(90.0);

        bookshop.placeAnOrderWithoutSigningIn();
        bookshop.responseShouldHaveStatus(401);
        bookshop.responseShouldShowError("Authentication required");
    }
}
