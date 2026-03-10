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
    private Map<String, Object> data;


    public boolean isSuccess(){
        return "OK".equals(status);
    }

    public static Response success(String message, Map<String,Object> data){
        return new Response("OK", message, data);
    }

    public static Response success(Map<String, Object> data){
        return new Response("OK", "Operation successfull", data);
    }

    public static Response error(String message){
       return new Response("ERROR", message, null);
    }
}
