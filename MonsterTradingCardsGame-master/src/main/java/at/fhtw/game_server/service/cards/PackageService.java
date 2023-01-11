package at.fhtw.game_server.service.cards;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

public class PackageService implements Service{
    private final PackageController packageController;

    public PackageService(){
        this.packageController = new PackageController();
    }

    @Override
    public Response handleRequest(Request request){
        if(request.getMethod() == Method.POST) {
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.packageController.addPackage(request.getBody(), token);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
