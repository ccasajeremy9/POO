module pe.edu.upeu.celmax {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires static lombok;
    requires jakarta.validation;

    requires java.sql;
    requires java.naming;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    requires com.h2database;
    requires java.prefs;

    opens pe.edu.upeu.celmax to javafx.fxml;
    opens pe.edu.upeu.celmax.controller to javafx.fxml;
    exports pe.edu.upeu.celmax;
    opens pe.edu.upeu.celmax.model;
    opens pe.edu.upeu.celmax.dto;
}
