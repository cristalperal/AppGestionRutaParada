package com.example.appgestionrutaparada.Controlador;

import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Modelo.Parada;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ParadaController implements Initializable {

    @FXML
    private TableView<Parada> tblParadas;
    @FXML
    private TableColumn<Parada, String> colID;
    @FXML
    private TableColumn<Parada, String> colNombre;
    @FXML
    private TableColumn<Parada, String> colDireccion;
    @FXML
    private TableColumn<Parada, String> colTipoT;
    // para poder conectar los elementos con el formulario
    @FXML
    private TextField txtCod;         // fx:id="txtCod"
    @FXML
    private TextField txtNombrePa;    // fx:id="txtNombrePa"
    @FXML
    private TextField txtDireccion;   // fx:id="txtDireccion"
    @FXML
    private ComboBox<String> cmboxTipoT; // fx:id="cmboxTipoT"
    @FXML
    private Button btnGuardar;    // fx:id="btnGuardar"
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnCancelarAccion;

    @FXML
    private Button btnBuscar;
    @FXML
    private ComboBox<String> cmbBuscar;

    private Crud crudInstancia;// instancia del CRUD para poder usarlo
    private ObservableList<Parada> listaParadasO; // ObservableList se usa para poder refrezcar la tabla al cambiar algun elemento, es similar a ArrayList
    private Parada paradaSeleccionada = null;// para guardar la parada seleccionada

    // Contador para el id de parada
    private int generarId = 1;

    // para poder iniciar el crud y las listas
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // iniciar la instancia y la lista
        crudInstancia = Crud.getInstancia();
        listaParadasO = FXCollections.observableArrayList(crudInstancia.getParada());
        // Para el id automático
        txtCod.setDisable(true);
        setInitialRouteId();
        // metodos para que el crud funcione
        configurarTabla(); // para hacer que la tabla sea dinamica 
        cargarDatos(); //para cargar los datos que ya esten 
        cargarOpcionesTransporte();// para que se cargen los diferentes transporte
        cargarOpcionesParada();

        btnGuardar.setOnAction(this::guardarParada);
        btnActualizar.setOnAction(this::ModificarParada);
        btnEliminar.setOnAction(this::eliminarParada);
        btnCancelarAccion.setOnAction(this::cancelarAccion);
        btnBuscar.setOnAction(this::buscarParadaPorNombre);

        // Habilitar la funcionalidad de CLIC en la tabla
        tblParadas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesParada(newValue)
        );
    }

     // Objetivo: Buscar una parada por Nombre seleccionado en el ComboBox y mostrar solo esa
    private void buscarParadaPorNombre(ActionEvent actionEvent) {
        String nombreBuscado = cmbBuscar.getValue(); // Obtiene el valor seleccionado

        // Si el usuario no ha seleccionado nada o ha deseleccionado
        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            cargarDatos();
            tblParadas.refresh();
            mostrarAlerta("Información", "No ha seleccionado una parada", Alert.AlertType.INFORMATION);
            return;
        }

        Parada paradaEncontrada = crudInstancia.buscarParadaPorNombre(nombreBuscado);
        // una lista observable solo con la parada encontrada
        ObservableList<Parada> listaFiltrada = FXCollections.observableArrayList();

        if (paradaEncontrada != null) {
            listaFiltrada.add(paradaEncontrada);
            btnCancelarAccion.setDisable(false);
        } else {
            mostrarAlerta("Error", "No se pudo encontrar la parada seleccionada.", Alert.AlertType.WARNING);
        }
        tblParadas.setItems(listaFiltrada);
        tblParadas.refresh();
    }


    //Objetivo: Eliminar una parada teniendo en cuenta que tambien se van a eleiminar sus rutas asociadas
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
                cargarOpcionesParada();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la parada.", Alert.AlertType.ERROR);
            }
        }
    }

    //Objetivo: Actualizar una parada
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
            cargarOpcionesParada();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la parada.", Alert.AlertType.ERROR);
        }
    }

    //Objetivo: Guardar una parada ingresada en la lista para mostrarala en la tabla, se podra actualizar y eliminar
    private void guardarParada(ActionEvent actionEvent) {
        String codParada = txtCod.getText();
        String nombreParada = txtNombrePa.getText();
        String direccionParada = txtDireccion.getText();
        String tipoT = cmboxTipoT.getValue();

        if (codParada.isEmpty() || nombreParada.isEmpty() || direccionParada.isEmpty() || tipoT == null) {
            mostrarAlerta("Error de Datos", "Debe completar todos los campos para registrar la parada.", Alert.AlertType.ERROR);
            return;
        }
        // crear el objeto
        Parada nuevaparada = new Parada(codParada, nombreParada, direccionParada, tipoT, "No Visitada");

        // para guardar la informacion
        if (crudInstancia.agregarParada(nuevaparada)) {
            // si se agrego correctamente se añade a la lista
            generarId++;
            listaParadasO.add(nuevaparada);
            mostrarAlerta("Registro con éxito", "Parada " + nombreParada + " Registrada Correctamente .", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarOpcionesParada();
        } else {
            mostrarAlerta("Error de Registro", "La parada con código " + codParada + " ya existe.", Alert.AlertType.WARNING);
        }
    }

    //Objetivo: cargar los datos de la tabla en el formulario para actualizar
    private void mostrarDetallesParada(Parada parada) {
        paradaSeleccionada = parada; // Guarda la referencia del objeto seleccionado
        if (parada != null) {
            // Cargar los datos de la parada seleccionada en los campos de texto
            txtCod.setText(parada.getIdParada());
            txtNombrePa.setText(parada.getNombreParada());
            txtDireccion.setText(parada.getDireccionParada());
            cmboxTipoT.setValue(parada.getTipoTransporte());
            txtCod.setDisable(true);
            btnGuardar.setDisable(true);
            btnActualizar.setDisable(false);
            btnEliminar.setDisable(false);
            btnCancelarAccion.setDisable(false);
        } else {
            restaurarEstadoFormulario();
        }
    }

    //Objetivo: Restablecer totalmente el formulario despues de una acción
    private void restaurarEstadoFormulario() {
        paradaSeleccionada = null;
        limpiarCampos();
        cmbBuscar.getSelectionModel().clearSelection();
        cargarDatos();
        tblParadas.refresh();
        // Reestablecer botones y campos
        txtCod.setDisable(true);
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnCancelarAccion.setDisable(true);
    }

    // Objetivo: Cancelar las acciones de actualizar y eliminar
    private void cancelarAccion(ActionEvent actionEvent) {
        tblParadas.getSelectionModel().clearSelection();
        restaurarEstadoFormulario();
    }

    //Objetivo: Limpia los campos del formulario
    private void limpiarCampos() {
        txtCod.setText(generateNextRouteId());
        txtNombrePa.clear();
        txtDireccion.clear();
        cmboxTipoT.getSelectionModel().clearSelection();
    }

    //Objetivo: Carga los diferentes tipos de transporte en el comboBox
    private void cargarOpcionesTransporte() {
        ObservableList<String> tipos = FXCollections.observableArrayList("Carro", "Autobus", "Motocicleta", "Mixto");
        cmboxTipoT.setItems(tipos);
    }


    // Objetivo: Cargar los nombres de las paradas en los comboBox
    private void cargarOpcionesParada() {
        // Obtener todas las paradas
        List<Parada> paradas = crudInstancia.getParada();
        // Extraer solo los nombres de parada
        ObservableList<String> nom = FXCollections.observableArrayList();
        for (Parada p : paradas) {
            nom.add(p.getNombreParada());
        }
        cmbBuscar.setItems(nom);

    }
    //Objetivo: Carga todos los datos de la lista
    private void cargarDatos() {
        // Enlazar la lista observable con la tabla
        tblParadas.setItems(listaParadasO);
    }

    //Objetivo: Muestra los datos en la tabla
    private void configurarTabla() {
        //Los nombres deben coincidir con los de la clase original
        // La lambda toma un objeto Parada (param) y devuelve la propiedad (getParadaProperty()).
        colID.setCellValueFactory(param -> param.getValue().idParadaProperty());
        colNombre.setCellValueFactory(param -> param.getValue().nombreParadaProperty());
        colDireccion.setCellValueFactory(param -> param.getValue().direccionParadaProperty());
        colTipoT.setCellValueFactory(param -> param.getValue().tipoTransporteProperty());
    }

    //Objetivo: método genérico para las alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos para general el id automático
    private String generateNextRouteId() {
        return String.format("P%03d", generarId);
    }

    private void setInitialRouteId() {
        int maxIdInDB = crudInstancia.getMaxIdParada();
        generarId = maxIdInDB + 1;
        txtCod.setText(generateNextRouteId());
    }

}
