package at.fhtw.game_server.service;

import at.fhtw.game_server.service.battle.BattleService;
import at.fhtw.game_server.service.cards.PackageService;
import at.fhtw.game_server.service.sessions.SessionService;
import at.fhtw.game_server.service.transactions.TransactionService;
import at.fhtw.game_server.service.users.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.utils.RequestBuilder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceTest {

    @Test
    void testCreateNewUser() {
        UserService userService = new UserService();
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/users");
        request.setBody("{\"Username\": \"test\",\"Password\": \"test\"}");

        Response response = userService.handleRequest(request);

        assertEquals( "{\"message\" : \"sucessfully registered User\"}", response.getContent());
        assertEquals(201, response.getStatus());
    }

    @Test
    void testLogin() {
        SessionService sessionService = new SessionService();
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/sessions");
        request.setBody("{\"Username\": \"test\",\"Password\": \"test\"}");

        Response response = sessionService.handleRequest(request);

        assertEquals( "\"Basic test-mtcgToken\"", response.getContent());
        assertEquals(201, response.getStatus());
    }

    @Test
    void testLoginWrongData() {
        SessionService sessionService = new SessionService();
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/sessions");
        request.setBody("{\"Username\": \"test\",\"Password\": \"test123\"}");

        Response response = sessionService.handleRequest(request);

        assertEquals( "User nicht vorhanden oder falsches Passwort", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void BattleWithUncofiduredDeck(){
        BattleService battleService = new BattleService();
        Request request1 = new Request();
        request1.setHeaderMap(new HeaderMap());
        request1.setMethod(Method.POST);
        request1.setPathname("/battles");
        request1.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");

        Response response = battleService.handleRequest(request1);

        assertEquals("Deck unconfigured", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void updateUser(){
        UserService userService = new UserService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.PUT);
        request.setPathname("/users/test");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");
        request.setBody("{\"Name\": \"tester\", \"Bio\": \"me testin...\",  \"Image\": \":-(\"}");

        Response response = userService.handleRequest(request);

        assertEquals("{ message: \"Successfully updated userdata\" }", response.getContent());
        assertEquals(202, response.getStatus());
    }

    @Test
    void updateUserInvalidData(){
        UserService userService = new UserService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.PUT);
        request.setPathname("/users/test");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");
        request.setBody("{\"Names\": \"tester\", \"Bios\": \"me testin...\",  \"Image\": \":-(\"}");

        Response response = userService.handleRequest(request);

        assertEquals("{ message: \"Failed mapping body\" }", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void updateUserInvalidAuth(){
        UserService userService = new UserService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.PUT);
        request.setPathname("/users/kienboec");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");
        request.setBody("{\"Name\": \"tester\", \"Bio\": \"me testin...\",  \"Image\": \":-(\"}");

        Response response = userService.handleRequest(request);

        assertEquals("Update failed, username/token is invalid.", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void getUserData(){
        UserService userService = new UserService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.GET);
        request.setPathname("/users/test");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");

        Response response = userService.handleRequest(request);

        assertEquals("{\"username\":\"test\",\"passwort\":\"test\",\"coins\":20,\"elo\":100,\"token\":\"Basic test-mtcgToken\",\"name\":\"tester\",\"bio\":\"me testin...\",\"image\":\":-(\"}", response.getContent());
        assertEquals(200, response.getStatus());
    }

    @Test
    void getUserDataWrongAuth(){
        UserService userService = new UserService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.GET);
        request.setPathname("/users/admin");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");

        Response response = userService.handleRequest(request);

        assertEquals("Authorisierung fehlgeschlagen", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void aquirePackageNoPackage(){
        TransactionService transactionService = new TransactionService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/users/packages");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");

        Response response = transactionService.handleRequest(request);

        assertEquals("No package available at the moment", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void aquirePackageNoToken(){
        TransactionService transactionService = new TransactionService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/transactions/packages");

        Response response = transactionService.handleRequest(request);

        assertEquals("Invalid Token", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void createPackageNotAdmin(){
        PackageService packageService = new PackageService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/packages");
        request.getHeaderMap().ingest("Authorization: Basic test-mtcgToken");

        request.setBody("[\n" +
                "{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0, \"Type\": \"spell\", \"Element\": \"fire\"}," +
                "{\"Id\":\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"Name\":\"RegularSpell\", \"Damage\": 50.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"Name\":\"Knight\", \"Damage\": 20.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\", \"Name\":\"RegularSpell\", \"Damage\": 45.0, \"Type\": \"monster\", \"Element\": \"water\"}," +
                "{\"Id\":\"2508bf5c-20d7-43b4-8c77-bc677decadef\", \"Name\":\"FireElf\", \"Damage\": 25.0, \"Type\": \"spell\", \"Element\": \"water\"}" +
                "]");

        Response response = packageService.handleRequest(request);

        assertEquals("Authorization failed, only the admin can create Packages", response.getContent());
        assertEquals(400, response.getStatus());
    }

    @Test
    void createPackageAdmin(){
        PackageService packageService = new PackageService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/packages");
        request.getHeaderMap().ingest("Authorization: Basic admin-mtcgToken");

        request.setBody("[\n" +
                "{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0, \"Type\": \"spell\", \"Element\": \"fire\"}," +
                "{\"Id\":\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"Name\":\"RegularSpell\", \"Damage\": 50.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"Name\":\"Knight\", \"Damage\": 20.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\", \"Name\":\"RegularSpell\", \"Damage\": 45.0, \"Type\": \"monster\", \"Element\": \"water\"}," +
                "{\"Id\":\"2508bf5c-20d7-43b4-8c77-bc677decadef\", \"Name\":\"FireElf\", \"Damage\": 25.0, \"Type\": \"spell\", \"Element\": \"water\"}" +
                "]");

        Response response = packageService.handleRequest(request);

        assertEquals("{ \" message \" : \"Package created\"}", response.getContent());
        assertEquals(201, response.getStatus());
    }

    @Test
    void createPackageAdminWithDuplicates(){
        PackageService packageService = new PackageService();
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/packages");
        request.getHeaderMap().ingest("Authorization: Basic admin-mtcgToken");

        request.setBody("[\n" +
                "{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0, \"Type\": \"spell\", \"Element\": \"fire\"}," +
                "{\"Id\":\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"Name\":\"RegularSpell\", \"Damage\": 50.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"Name\":\"Knight\", \"Damage\": 20.0, \"Type\": \"monster\", \"Element\": \"normal\"}," +
                "{\"Id\":\"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\", \"Name\":\"RegularSpell\", \"Damage\": 45.0, \"Type\": \"monster\", \"Element\": \"water\"}," +
                "{\"Id\":\"2508bf5c-20d7-43b4-8c77-bc677decadef\", \"Name\":\"FireElf\", \"Damage\": 25.0, \"Type\": \"spell\", \"Element\": \"water\"}" +
                "]");

        Response response = packageService.handleRequest(request);

        assertEquals("Failed creating Package", response.getContent());
        assertEquals(400, response.getStatus());
    }


}
