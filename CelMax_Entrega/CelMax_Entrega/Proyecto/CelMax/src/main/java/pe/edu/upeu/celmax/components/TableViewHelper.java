package pe.edu.upeu.celmax.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TableViewHelper<T> {

    public void addColumnsInOrderWithSize(TableView<T> tableView, LinkedHashMap<String, ColumnInfo> columns, Consumer<T> updateAction, Consumer<T> deleteAction) {
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            TableColumn<T, Object> column = new TableColumn<>(entry.getKey());
            String field = entry.getValue().getField();

            // Detectar si el campo es un objeto complejo o una propiedad básica
            if (field.contains(".")) {

                column.setCellValueFactory(cellData -> {

                    T item = cellData.getValue();
                    String[] fieldPath = field.split("\\.");

                    try {
                        Object value = item.getClass().getMethod("get" + capitalize(fieldPath[0])).invoke(item);
                        if (value != null) {
                            Object nestedValue = value.getClass().getMethod("get" + capitalize(fieldPath[1])).invoke(value);
                            return new SimpleObjectProperty<>(nestedValue != null ? nestedValue : "N/A");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return new SimpleObjectProperty("N/A");
                });
            } else {
                column.setCellValueFactory(new PropertyValueFactory<>(field));
            }

            // CelMax: mostrar los Double con 2 decimales y las fechas como dd/MM/yyyy
            column.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else if (item instanceof Double d) {
                        setText(String.format("%,.2f", d));
                    } else if (item instanceof java.time.LocalDate f) {
                        setText(f.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        setText(item.toString());
                    }
                }
            });

            if (entry.getValue().getWidth() != null) {
                column.setPrefWidth(entry.getValue().getWidth());
            }

            tableView.getColumns().add(column);
        }

        // CelMax: si no se pasan acciones (null) no se agrega la columna "Acciones"
        if (updateAction != null || deleteAction != null) {
            addActionColumn(tableView, updateAction, deleteAction);
        }
        // Las columnas se reparten todo el ancho de la tabla
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /** Solo columnas de datos, sin botones (para tablas de consulta). */
    public void addColumnsInOrderWithSize(TableView<T> tableView, LinkedHashMap<String, ColumnInfo> columns) {
        addColumnsInOrderWithSize(tableView, columns, null, null);
    }

    private void addActionColumn(TableView<T> tableView, Consumer<T> updateAction, Consumer<T> deleteAction) {
        TableColumn<T, Void> actionColumn = new TableColumn<>("Acciones");

        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                final TableCell<T, Void> cell = new TableCell<>() {

                    // Crear las imágenes
                    Image updateImage = new Image(getClass().getResource("/img/document-edit-icon.png").toExternalForm());
                    Image deleteImage = new Image(getClass().getResource("/img/del-icon.png").toExternalForm());

                    // Crear ImageView para los botones
                    ImageView updateImageView = new ImageView(updateImage);
                    ImageView deleteImageView = new ImageView(deleteImage);

                    // Crear botones con los íconos en lugar de texto
                    private final Button btnUpdate = new Button("", updateImageView);
                    private final Button btnDelete = new Button("", deleteImageView);


                    {
                        btnUpdate.getStyleClass().add("btn-icon");
                        btnDelete.getStyleClass().add("btn-icon");
                        for (ImageView iv : new ImageView[]{updateImageView, deleteImageView}) {
                            iv.setFitWidth(18);
                            iv.setFitHeight(18);
                            iv.setPreserveRatio(true);
                        }
                        btnUpdate.setTooltip(new javafx.scene.control.Tooltip("Editar"));
                        btnDelete.setTooltip(new javafx.scene.control.Tooltip("Eliminar"));
                        btnUpdate.setOnAction(event -> {
                            T data = getTableView().getItems().get(getIndex());
                            updateAction.accept(data);  // Llama a la función pasada como parámetro para actualizar
                        });

                        btnDelete.setOnAction(event -> {
                            T data = getTableView().getItems().get(getIndex());
                            deleteAction.accept(data);  // Llama a la función pasada como parámetro para eliminar
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox();
                            if (updateAction != null) buttons.getChildren().add(btnUpdate);
                            if (deleteAction != null) buttons.getChildren().add(btnDelete);
                            buttons.setSpacing(10);
                            setGraphic(buttons);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
        actionColumn.setPrefWidth(90);
        actionColumn.setMinWidth(90);
        tableView.getColumns().add(actionColumn);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}

