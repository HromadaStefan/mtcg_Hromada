package at.fhtw.game_server.service.transactions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import org.mockito.internal.matchers.Null;

public class TransactionService implements Service {
    private final TransactionController transactionController;

    public TransactionService(){
        this.transactionController = new TransactionController();
    }

    public Response handleRequest(Request request){
        if(request.getMethod() == Method.POST && request.getPathParts().get(1).equals("packages")){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.transactionController.aquire_package(token);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
