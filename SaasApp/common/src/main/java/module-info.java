module common {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jakarta.validation;
    requires static lombok;

    exports org.example.dto.request;
    exports org.example.dto.response;
    exports org.example.dto.user;
    exports org.example.enums;
    exports org.example.exception;
    exports org.example.util;
}