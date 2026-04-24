package com.example.server.handler;

import com.example.server.entity.User;
import com.example.server.exception.ValidationException;
import com.example.server.security.JwtService;
import com.example.server.service.UserService;
import com.example.server.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.LoginDto;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.dto.user.UserCreateDto;
import org.example.dto.user.UserDto;
import org.example.exception.ErrorCode;
import org.example.util.JsonMapper;
import org.hibernate.engine.spi.Resolution;

@Slf4j
@RequiredArgsConstructor
public class AuthHandler implements ActionHandler{

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public Response handle(Request request){
        String operation = request.getOperation();
        if(operation == null){
            return Response.error(request.getRequestId(),
                    "Operation is required for AUTH action",
                    ErrorCode.VALIDATION_ERROR);
        }

        return switch (operation.toUpperCase()){
            case "LOGIN" -> handleLogin(request);
            case "REGISTER" -> handleRegister(request);
            default -> Response.error(request.getRequestId(),
                    "Unsupported AUTH operation: " + operation,
                    ErrorCode.VALIDATION_ERROR);
        };

    }

    private Response handleRegister(Request request) {
        try{
            UserCreateDto userCreateDto = JsonMapper.INSTANCE.convertValue(request.getData(), UserCreateDto.class);

            ValidationUtil.validate(userCreateDto);

            UserDto userDto = userService.registerUser(userCreateDto);
            log.info("User created successfully with ID: {}",userDto.getId());

            return Response.success(request.getRequestId(),
                    "Registration successful",
                    userDto);
        }catch (ValidationException ex){
            return Response.error(request.getRequestId(),
                    ex.getMessage(),
                    ErrorCode.VALIDATION_ERROR);
        }catch (Exception ex){
            log.error("Error during registration", ex);
            return Response.error(request.getRequestId(),
                    "Registration failed",
                    ErrorCode.INTERNAL_ERROR);
        }
    }

    private Response handleLogin(Request request){
        try {
            LoginDto loginDto = JsonMapper.INSTANCE.convertValue(request.getData(), LoginDto.class);

            ValidationUtil.validate(loginDto);

            User user = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());

            if(user == null){
                return Response.error(request.getRequestId(),
                        "Invalid email or password",
                        ErrorCode.UNAUTHORIZED
                        );
            }

            String token = jwtService.generateToken(user);

            return Response.successWithToken(request.getRequestId(),
                    "Login successful",
                    null,
                    token
                    );
        }catch (ValidationException ex){
            return Response.error(request.getRequestId(),
                    ex.getMessage(),
                    ErrorCode.VALIDATION_ERROR);
        }
        catch (Exception ex){
            log.error("Error during login", ex);
            return Response.error(request.getRequestId(),
                    "Authentication failed",
                    ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public boolean requiresAuthentication(){
        return false;
    }
}
