package com.example.server.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {
    public String hash(String rawPassword){
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public boolean matches(String rawPassword, String hashedPw){
        if(rawPassword == null || hashedPw == null){
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPw);
    }
}
