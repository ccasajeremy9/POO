module pe.edu.upeu.repaso_grupo3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens pe.edu.upeu.repaso_grupo3 to javafx.fxml;
    exports pe.edu.upeu.repaso_grupo3;
}