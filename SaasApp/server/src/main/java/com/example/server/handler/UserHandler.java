package com.example.server.handler;

import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.dto.user.UserCreateDto;
import org.example.dto.user.UserDto;
import org.example.enums.Role;

import java.util.Map;

@Slf4j
public class UserHandler implements ActionHandler{

    private final UserService userService = new UserService();

    @Override
    public Response handle(Request request){
        String operation = request.getOperation();
        try {
            switch (operation){
                case "CREATE":
                    return createUser(request);
                case "GET":
                    return getUser(request);
                case "LIST":
                    return getAllUsers(request);
                case "UPDATE":
                    return updateUser(request);
                case "DELETE":
                    return deleteUser(request);
                default:
                    return Response.error("Unknown operation: " + operation);
            }
        }catch (IllegalArgumentException ex){
            log.warn("User operation error: {}", ex.getMessage());
            return Response.success(ex.getMessage());
        }catch (Exception ex){
            log.warn("Unexpected error in user handler", ex);
            return Response.error("Internal server error");
        }
    }

    private Response deleteUser(Request request) {
        Map<String, Object> data =(Map<String, Object>) request.getData();
        Long id =((Number) data.get("id")).longValue();
        userService.deleteUser(id);
        return Response.success("User deleted successfully");

    }


    private Response getAllUsers(Request request) {
        return Response.success(userService.getAllUsers());
    }

    private Response updateUser(Request request) {
        Map<String,Object> data = (Map<String, Object>) request.getData();
        Long id = ((Number) data.get("id")).longValue();
        UserCreateDto dto = mapToUserCreateDto(data);
        UserDto updated = userService.updateUser(id,dto);
        return Response.success(updated);

    }

    private Response createUser(Request request) {
        UserCreateDto dto = mapToUserCreateDto(request.getData());
        UserDto user = userService.createUser(dto);
        return Response.success(user);
        //todo разобраться с Response success принимает в себя мапу, а не объект
    }

    private Response getUser(Request request) {
        Map<String, Object> data = (Map<String, Object>) request.getData();
        Long id = ((Number) data.get("id")).longValue();
        UserDto user = userService.getUser(id);
        return Response.success(user);
    }



        private UserCreateDto mapToUserCreateDto(Object data){

        if(data instanceof UserCreateDto) return (UserCreateDto) data;

        Map<String, Object> map = (Map<String,Object>) data;
        UserCreateDto dto = new UserCreateDto();

        dto.setName((String) map.get("name"));
        dto.setEmail((String) map.get("email"));
        dto.setPassword((String) map.get("password"));
        dto.setRole(Role.valueOf((String) map.get("role")));
        return dto;
    }
}
