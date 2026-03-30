package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.exception.ErrorCode;
import org.example.exception.ErrorResponseDto;

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
    private String  errorCode;


    public boolean isSuccess(){
        return "OK".equals(status);
    }



    public static Response success(String message, Object data){
        return new Response("OK", message, data,null);
    }

    public static Response success(Object data){
        return new Response("OK", "Operation successful", data, null);
    }

    public static Response error(String message, ErrorCode errorCode){
       return new Response("ERROR", message, null, errorCode != null ? errorCode.name() : null);
    }

    public static Response error(ErrorResponseDto dto){
        return new Response("ERROR", dto.getMessage(), null, dto.getErrorCode().name());
    }
}
