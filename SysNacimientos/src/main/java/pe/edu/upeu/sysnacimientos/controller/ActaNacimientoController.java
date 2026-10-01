package pe.edu.upeu.sysnacimientos.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysnacimientos.components.ColumnInfo;
import pe.edu.upeu.sysnacimientos.components.TableViewHelper;
import pe.edu.upeu.sysnacimientos.components.Toast;
import pe.edu.upeu.sysnacimientos.components.ToltipCustom;
import pe.edu.upeu.sysnacimientos.model.ActaNacimiento;
import pe.edu.upeu.sysnacimientos.service.IActaNacimientoService;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ActaNacimientoController {

    private final IActaNacimientoService as;

    @FXML
    TextField txtNombreRecienNacido, txtLugarNacimiento, txtNombrePadre, txtNombreMadre;
    @FXML
    DatePicker dpFechaNacimiento;

    @FXML private TableView<ActaNacimiento> tableView;
    ObservableList<ActaNacimiento> listarActa;

    @FXML
    Label lbnMsg;
    @FXML private AnchorPane miContenedor;
    Stage stage;
    ActaNacimiento formulario;

    Long idActaCE = 0L;
    private Validator validator;
    private final ToltipCustom ttc=new ToltipCustom();


    @FXML
    public  void initialize(){

        TableViewHelper<ActaNacimiento> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("N° Acta", new ColumnInfo("idActa", 70.0));
        columns.put("Recién Nacido", new ColumnInfo("nombreRecienNacido", 200.0));
        columns.put("Fecha Nacimiento", new ColumnInfo("fechaNacimiento", 130.0));
        columns.put("Hospital / Lugar", new ColumnInfo("lugarNacimiento", 200.0));
        columns.put("Padre", new ColumnInfo("nombrePadre", 180.0));
        columns.put("Madre", new ColumnInfo("nombreMadre", 180.0));

        Consumer<ActaNacimiento> updateAction = acta ->{  editForm(acta);
        idActaCE=acta.getIdActa();
        };
        Consumer<ActaNacimiento> deleteAction = acta -> {
            as.delete(acta.getIdActa());
            if (idActaCE.equals(acta.getIdActa())) clearForm();
            stage = (Stage) miContenedor.getScene().getWindow();
            double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó correctamente!!", 2000, w, h);
            listar();
        };

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        lbnMsg.setText("");
        listar();
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listarActa = FXCollections.observableArrayList(as.findAll());
            tableView.getItems().addAll(listarActa);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void validarFormulario() {
        formulario = new ActaNacimiento();
        formulario.setNombreRecienNacido(txtNombreRecienNacido.getText());
        formulario.setFechaNacimiento(dpFechaNacimiento.getValue());
        formulario.setLugarNacimiento(txtLugarNacimiento.getText());
        formulario.setNombrePadre(txtNombrePadre.getText());
        formulario.setNombreMadre(txtNombreMadre.getText());

        Set<ConstraintViolation<ActaNacimiento>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<ActaNacimiento>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).toList();

        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
            listar();

        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    @FXML
    public void clearForm() {
        txtNombreRecienNacido.clear();
        dpFechaNacimiento.setValue(null);
        dpFechaNacimiento.getEditor().clear();
        txtLugarNacimiento.clear();
        txtNombrePadre.clear();
        txtNombreMadre.clear();
        idActaCE = 0L;
        limpiarError();
    }

    public void editForm(ActaNacimiento acta) {
        txtNombreRecienNacido.setText(acta.getNombreRecienNacido());
        dpFechaNacimiento.setValue(acta.getFechaNacimiento());
        txtLugarNacimiento.setText(acta.getLugarNacimiento());
        txtNombrePadre.setText(acta.getNombrePadre());
        txtNombreMadre.setText(acta.getNombreMadre());

        idActaCE = acta.getIdActa();
        limpiarError();
    }
    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        lbnMsg.setText("Formulario válido");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idActaCE > 0L) {
            formulario.setIdActa(idActaCE);
            as.update(idActaCE, formulario);
            Toast.showToast(stage, "Se actualizó correctamente!!", 2000, w, h);
        } else {
            as.save(formulario);
            Toast.showToast(stage, "Se guardó correctamente!!", 2000, w, h);
        }
        clearForm(); listar();
    }
    public void limpiarError() {
        List.of(txtNombreRecienNacido, dpFechaNacimiento,
                        txtLugarNacimiento, txtNombrePadre, txtNombreMadre)
                .forEach(c -> {c.getStyleClass().remove("text-field-error");
                    ttc.limpiarCampo(c);
                });
    }


    private void mostrarErroresValidacion(List<ConstraintViolation<ActaNacimiento>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreRecienNacido", txtNombreRecienNacido);
        campos.put("fechaNacimiento", dpFechaNacimiento);
        campos.put("lugarNacimiento", txtLugarNacimiento);
        campos.put("nombrePadre", txtNombrePadre);
        campos.put("nombreMadre", txtNombreMadre);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerCtrl = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst().ifPresent(v -> {

                        erroresOrdenados.put(campo, v.getMessage());

                        Control c = campos.get(campo);
                        if (c != null && !c.getStyleClass().contains("text-field-error")){
                            if (c != null) ttc.marcarError(c, v.getMessage().trim());
                        }
                        if (primerCtrl[0] == null) primerCtrl[0] = c;
                    });
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.entrySet().iterator().next().getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }
}
