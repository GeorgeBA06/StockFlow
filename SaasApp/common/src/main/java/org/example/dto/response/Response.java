package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response implements Serializable {
    private String status;
    private String message;
    private Object data;


    public boolean isSuccess(){
        return "OK".equals(status);
    }

    public static Response success(String message, Object data){
        return new Response("OK", message, data);
    }

    public static Response success(Object data){
        return new Response("OK", "Operation successful", data);
    }

    public static Response error(String message){
       return new Response("ERROR", message, null);
    }
}
