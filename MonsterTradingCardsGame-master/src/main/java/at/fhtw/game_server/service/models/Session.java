package at.fhtw.game_server.service.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Session {

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    private String password;

    @JsonAlias({"Authentication_token"})
    private String token;

    public Session(){};

    public Session(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Session(String token){
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
