package com.example.server.handler;

import com.example.server.config.JsonMapper;
import com.example.server.service.UserService;
import com.example.server.util.ValidationUtil;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.dto.user.UserCreateDto;
import org.example.dto.user.UserDto;
import org.example.dto.user.UserIdDto;
import org.example.dto.user.UserUpdateDto;
import org.example.enums.Role;

import java.util.Map;

@Slf4j
public class UserHandler implements ActionHandler{

    private final UserService userService = new UserService();



    @Override
    public Response handle(Request request){
        String operation = request.getOperation();
        return switch (operation) {
            case "CREATE" -> createUser(request);
            case "GET" -> getUser(request);
            case "LIST" -> getAllUsers(request);
            case "UPDATE" -> updateUser(request);
            case "DELETE" -> deleteUser(request);
            default -> throw new ValidationException("Unknown operation " + operation);
        };
    }

    private Response deleteUser(Request request) {
        UserIdDto userIdDto = JsonMapper.INSTANCE.convertValue(request.getData(), UserIdDto.class);
        ValidationUtil.validate(userIdDto);
        userService.deleteUser(userIdDto.getId());
        return Response.success("User deleted successfully");

    }


    private Response getAllUsers(Request request) {
        return Response.success(userService.getAllUsers());
    }

    private Response updateUser(Request request) {
        UserUpdateDto userUpdateDto = JsonMapper.INSTANCE.convertValue(request.getData(), UserUpdateDto.class);
        ValidationUtil.validate(userUpdateDto);
        UserIdDto userIdDto = JsonMapper.INSTANCE.convertValue(request.getData(),UserIdDto.class);
        ValidationUtil.validate(userIdDto);
        UserDto updated = userService.updateUser(userIdDto.getId(),userUpdateDto);
        return Response.success(updated);

    }

    private Response createUser(Request request) {
        UserCreateDto dto = JsonMapper.INSTANCE.convertValue(request.getData(), UserCreateDto.class);

        ValidationUtil.validate(dto);

        UserDto user = userService.createUser(dto);
        log.info("User created successfully with ID: {}", user.getId());

        return Response.success("User created successfully", user);
        //todo разобраться с Response success принимает в себя мапу, а не объект
    }

    private Response getUser(Request request) {
        UserIdDto userIdDto = JsonMapper.INSTANCE.convertValue(request.getData(), UserIdDto.class);
        ValidationUtil.validate(userIdDto);
        UserDto user = userService.getUser(userIdDto.getId());
        return Response.success(user);
    }

}
