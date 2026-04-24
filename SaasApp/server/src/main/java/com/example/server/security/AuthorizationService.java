package com.example.server.security;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AuthorizationService {
    private final JwtService jwtService;
    private final Map<String, Set<String>> accessRules = new HashMap<>();

    public AuthorizationService(JwtService jwtService){
        this.jwtService = jwtService;
        initRules();
    }

    private void initRules() {

    }

    private void allow(String action, String operation, String... roles){
        accessRules.put(buildKey(action, operation), Set.of(roles));
    }

    private String buildKey(String action, String operation) {
        return (action == null ? "" : action.toUpperCase() + ":" +
                (operation == null ? "" : operation.toUpperCase()));
    }

    public Response authorize(Request request){
        String action = request.getAction();
        String operation = request.getOperation();
        String key = buildKey(action, operation);

        Set<String> allowedRoles = accessRules.get(key);

        if(allowedRoles == null || allowedRoles.isEmpty()){
            return null;
        }

        String token = request.getToken();
        String actualRole = jwtService.extractUserRole(token);

        if(actualRole == null || !allowedRoles.contains(actualRole)){
            log.warn("Access denied for action={}, operation={}, role={}", action, operation, actualRole);
            return Response.error(request.getRequestId(),
                    "Access denied",
                    ErrorCode.UNAUTHORIZED);
        }

        return null;
    }
}
