package com.example.appgestionrutaparada.Controlador;

import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Modelo.Parada;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ParadaController implements Initializable {

    @FXML private TableView<Parada> tblParadas;
    @FXML private TableColumn<Parada, String> colID;
    @FXML private TableColumn<Parada, String> colNombre;
    @FXML private TableColumn<Parada, String> colDireccion;
    @FXML private TableColumn<Parada, String> colTipoT;



    // para poder conectar los elementos con el formulario
    @FXML private TextField txtCod;         // fx:id="txtCod"
    @FXML private TextField txtNombrePa;    // fx:id="txtNombrePa"
    @FXML private TextField txtDireccion;   // fx:id="txtDireccion"
    @FXML private ComboBox<String> cmboxTipoT; // fx:id="cmboxTipoT"
    @FXML private Button btnGuardar;    // fx:id="btnGuardar"
    @FXML private Button btnEliminar;
    @FXML private Button btnActualizar;
    @FXML private Button btnCancelarAccion;


    private Crud crudInstancia;// instancia del CRUD para poder usarlo
    private ObservableList<Parada> listaParadasO; // ObservableList se usa para poder refrezcar la tabla al cambiar algun elemento, es similar a ArrayList

    private Parada paradaSeleccionada = null;// para guardar la parada seleccionada


    // para poder iniciar el crud y las listas
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // iniciar la instancia y la lista
        crudInstancia = Crud.getInstancia();
        listaParadasO = FXCollections.observableArrayList(crudInstancia.getParada());

        // metodos para que el crud funcione

        configurarTabla(); // para hacer que la tabla sea dinamica 
        cargarDatos(); //para cargar los datos que ya esten 
        cargarOpcionesTransporte();// para que se cargen los diferentes transporte

        btnGuardar.setOnAction(this::guardarParada);
        btnActualizar.setOnAction(this::ModificarParada);
        btnEliminar.setOnAction(this::eliminarParada);
        btnCancelarAccion.setOnAction(this::cancelarAccion);

        // Habilitar la funcionalidad de CLIC en la tabla
        tblParadas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesParada(newValue)
        );
    }

    private void cancelarAccion(ActionEvent actionEvent) {
        tblParadas.getSelectionModel().clearSelection();
        restaurarEstadoFormulario();
    }

    private void mostrarDetallesParada(Parada parada) {

        paradaSeleccionada = parada; // Guarda la referencia del objeto seleccionado

        if (parada != null) {
            // Cargar los datos de la parada seleccionada en los campos de texto
            txtCod.setText(parada.getIdParada());
            txtNombrePa.setText(parada.getNombreParada());
            txtDireccion.setText(parada.getDireccionParada());
            cmboxTipoT.setValue(parada.getTipoTransporte());

            txtCod.setDisable(true); // Evita que se cambie el ID al editar
            btnGuardar.setDisable(true); // Bloquea Guardar cuando hay algo seleccionado
            btnActualizar.setDisable(false);
            btnEliminar.setDisable(false);
            btnCancelarAccion.setDisable(false);
        } else {
            restaurarEstadoFormulario();
        }
    }

    private void eliminarParada(ActionEvent actionEvent) {
        if (paradaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una parada de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Confirmar la eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("Eliminar Parada: " + paradaSeleccionada.getNombreParada());
        confirmacion.setContentText("¿Está seguro de que desea eliminar esta parada?\n Esto también eliminará todas las rutas asociadas.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Llamar  la lógica CRUD
            String idEliminar = paradaSeleccionada.getIdParada();
            if (crudInstancia.eliminarParada(idEliminar)) {
                //  Remover de la lista observable
                listaParadasO.remove(paradaSeleccionada);
                mostrarAlerta("Éxito", "La parada fue eliminada y sus rutas asociadas han sido removidas.", Alert.AlertType.INFORMATION);
                restaurarEstadoFormulario();
                limpiarCampos();
                tblParadas.getSelectionModel().clearSelection();
                tblParadas.refresh();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la parada.", Alert.AlertType.ERROR);
            }
        }
    }

    private void restaurarEstadoFormulario() {
        paradaSeleccionada = null;
        limpiarCampos();
        // Reestablecer botones y campos
        txtCod.setDisable(false);
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnCancelarAccion.setDisable(true);
    }

    // Metodo para actualizar una parada
    private void ModificarParada(ActionEvent actionEvent) {
        // si no eligen una parada valida
        if (paradaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una parada de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }

       // guardar los nuevos datos
        String idExistente = paradaSeleccionada.getIdParada(); // El ID NO cambia
        String nuevoNombre = txtNombrePa.getText();
        String nuevaDireccion = txtDireccion.getText();
        String nuevoTipoT = cmboxTipoT.getValue();

        //si estan vacios
        if (nuevoNombre.isEmpty() || nuevaDireccion.isEmpty() || nuevoTipoT == null) {
            mostrarAlerta("Error de Datos", "Debe completar todos los campos.", Alert.AlertType.ERROR);
            return;
        }
        // crear la nueva parada
        Parada paradaActualizada = new Parada(idExistente, nuevoNombre, nuevaDireccion, nuevoTipoT, paradaSeleccionada.getEstadoParada());

        // llamar la funcion del crud
        if (crudInstancia.modificarParada(idExistente, paradaActualizada)) {

            // Actualizar la lista para la tabla
            int index = listaParadasO.indexOf(paradaSeleccionada);
            if (index != -1) {
                // Remover la vieja parada de la lista y añadir la nueva o simplemente reemplazarla
                listaParadasO.set(index, paradaActualizada);
                tblParadas.refresh(); // refrezcar la tabla
            }
            mostrarAlerta("Éxito modificando los datos ", "Parada  " + idExistente + " actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos(); // Limpia y deselecciona
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la parada.", Alert.AlertType.ERROR);
        }
    }


    private void guardarParada(ActionEvent actionEvent) {
        String codParada = txtCod.getText();
        String nombreParada = txtNombrePa.getText();
        String direccionParada = txtDireccion.getText();
        String tipoT = cmboxTipoT.getValue();

        if(codParada.isEmpty() || nombreParada.isEmpty() || direccionParada.isEmpty() || tipoT == null){
            mostrarAlerta("Error de Datos", "Debe completar todos los campos para registrar la parada.", Alert.AlertType.ERROR);
            return;
        }

        // crear el objeto
        Parada nuevaparada = new Parada(codParada,nombreParada,direccionParada,tipoT, "No Visitada" );

        // para guardar la informacion
        if(crudInstancia.agregarParada(nuevaparada)){
            // si se agrego correctamente se añade a la lista
            listaParadasO.add(nuevaparada);
            mostrarAlerta("Registro con éxito", "Parada " + nombreParada + " Registrada Correctamente .", Alert.AlertType.INFORMATION);
            limpiarCampos();
        }else{
            mostrarAlerta("Error de Registro", "La parada con código " + codParada + " ya existe.", Alert.AlertType.WARNING);
        }
    }

    private void limpiarCampos() {
        txtCod.clear();
        txtNombrePa.clear();
        txtDireccion.clear();
        cmboxTipoT.getSelectionModel().clearSelection();
    }

    private void cargarOpcionesTransporte() {
        ObservableList<String> tipos = FXCollections.observableArrayList("Carro", "Autobus", "Motocicleta", "Mixto");
        cmboxTipoT.setItems(tipos);
    }

    private void cargarDatos() {
        // Enlazar la lista observable con la tabla
        tblParadas.setItems(listaParadasO);
    }

    private void configurarTabla() {
        /*
         * Los nombres deben coincidir con los de la clase original
         * */
        // Columna ID
        // La lambda toma un objeto Parada (param) y devuelve la propiedad (getParadaProperty()).
        colID.setCellValueFactory(param -> param.getValue().idParadaProperty());

        // Columna Nombre
        colNombre.setCellValueFactory(param -> param.getValue().nombreParadaProperty());

        // Columna Dirección
        colDireccion.setCellValueFactory(param -> param.getValue().direccionParadaProperty());

        // Columna Tipo Transporte
        colTipoT.setCellValueFactory(param -> param.getValue().tipoTransporteProperty());
    }


    // metodo generico para las alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
