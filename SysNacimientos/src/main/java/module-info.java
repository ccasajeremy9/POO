module pe.edu.upeu.sysnacimientos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.sysnacimientos to javafx.fxml;
    opens pe.edu.upeu.sysnacimientos.controller to javafx.fxml;
    exports pe.edu.upeu.sysnacimientos;
    opens pe.edu.upeu.sysnacimientos.model;
}
