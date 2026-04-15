module com.app {
    requires org.slf4j;
    requires static lombok;

    requires javafx.controls;
    requires javafx.fxml;
    requires common;

    opens org.example.client to javafx.fxml;
    exports org.example.client;
    opens org.example.client.controller to javafx.fxml;
    exports org.example.client.controller;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
}