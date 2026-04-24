package org.example.client.network;

public class SessionManager {
    String role;
    String token;
    String email;

    public void setSession(String token, String email, String role){
        this.role = role;
        this.email = email;
        this.token = token;
    }

    public void closeSession(){
        this.token = null;
        this.email = null;
        this.role = null;
    }

    public boolean isAuthenticated(){
        return (token != null && !token.isBlank());
    }

    public String getRole(){
        return role;
    }

    public String getToken(){
        return token;
    }

    public String getEmail(){
        return email;
    }
}
