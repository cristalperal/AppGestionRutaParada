package com.example.appgestionrutaparada.Controlador;

import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RutaController implements Initializable {

    @FXML
    private TextField txtCod;
    @FXML
    private TextField txtNombreR;
    @FXML
    private Spinner<Integer> spnDistancia; // Distancia (KM)
    @FXML
    private Spinner<Double> spnCosto;      // Costo (RD)
    @FXML
    private Spinner<Integer> spnTransbordo; // Transbordos
    @FXML
    private Spinner<Integer> spnTiempo;     // Tiempo (Minutos)
    @FXML
    private ComboBox<String> cmboxOrigen;  // ID de Parada Origen
    @FXML
    private ComboBox<String> cmboxDestino;  // ID de Parada Destino
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btneliminar;
    @FXML
    private Button btnCancelarAccion;

    @FXML
    private TableView<Ruta> tblRuta;
    @FXML
    private TableColumn<Ruta, String> colID;
    @FXML
    private TableColumn<Ruta, String> colNombre;
    @FXML
    private TableColumn<Ruta, Number> colDistancia;
    @FXML
    private TableColumn<Ruta, Number> colCosto;
    @FXML
    private TableColumn<Ruta, Number> colTransbordos;
    @FXML
    private TableColumn<Ruta, Number> colTiempo;
    @FXML
    private TableColumn<Ruta, String> colParadaOrigen;
    @FXML
    private TableColumn<Ruta, String> colParadaDestino;

    private Crud crudInstancia;
    private ObservableList<Ruta> listaRutasO;
    private Ruta rutaSeleccionada = null; // Para manejo de selección

    // Contador para el id de ruta
    private int nextRouteId = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        crudInstancia = Crud.getInstancia();
        configurarSpinners();
        configurarTabla();
        cargarOpcionesParada();
        cargarDatos();
        // Para el id automático
        txtCod.setDisable(true);
        setInitialRouteId();

        btnGuardar.setOnAction(this::guardarRuta);
        btnActualizar.setOnAction(this::modificarRuta);
        btneliminar.setOnAction(this::eliminarRuta);
        btnCancelarAccion.setOnAction(this::cancelarAccion);

        tblRuta.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarDetallesRuta(newVal)
        );
    }

    // Objetivo: Cancelar las acciones de actualizar y eliminar
    private void cancelarAccion(ActionEvent actionEvent) {
        tblRuta.getSelectionModel().clearSelection();
    }

    // Objetivo: Cargar los id de las paradas en los comboBox
    private void cargarOpcionesParada() {
        // Obtener todas las paradas
        List<Parada> paradas = crudInstancia.getParada();
        // Extraer solo los id de parada
        ObservableList<String> ids = FXCollections.observableArrayList();
        for (Parada p : paradas) {
            ids.add(p.getIdParada());
        }
        // Asignar la lista a ambos ComboBoxes
        cmboxOrigen.setItems(ids);
        cmboxDestino.setItems(ids);
    }


    //Objetivo: cargar los datos de la tabla en el formulario para actualizar
    private void mostrarDetallesRuta(Ruta ruta) {
        rutaSeleccionada = ruta;
        if (ruta != null) {
            // Cargar datos a los campos
            txtCod.setText(ruta.getIdRuta());
            txtNombreR.setText(ruta.getNombreRuta());
            spnDistancia.getValueFactory().setValue(ruta.getDistanciaRuta());
            spnCosto.getValueFactory().setValue((double) ruta.getCostoRuta());
            spnTransbordo.getValueFactory().setValue(ruta.getCantidadTransbordo());
            spnTiempo.getValueFactory().setValue(ruta.getTiempoViaje());
            cmboxOrigen.setValue(ruta.getOrigenRuta());
            cmboxDestino.setValue(ruta.getDestinoRuta());

            // Deshabilitar campos para el actualizar
            txtCod.setDisable(true);
            cmboxOrigen.setDisable(true);
            cmboxDestino.setDisable(true);
            btnGuardar.setDisable(true);
            btnActualizar.setDisable(false);
            btneliminar.setDisable(false);
            btnCancelarAccion.setDisable(false);
        } else {
            limpiarCampos();
            // Restaurar a modo Guardar
            txtCod.setDisable(true);
            cmboxOrigen.setDisable(false);
            cmboxDestino.setDisable(false);
            btnGuardar.setDisable(false);
            btnActualizar.setDisable(true);
            btneliminar.setDisable(true);
            btnCancelarAccion.setDisable(true);
        }
    }

    //Objetivo: Eliminar una ruta seleccionada
    private void eliminarRuta(ActionEvent actionEvent) {
        if (rutaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una ruta de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("Eliminar Ruta: " + rutaSeleccionada.getNombreRuta());
        confirmacion.setContentText("¿Está seguro de que desea eliminar esta ruta?.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Obtener id de Origen y Destino
            String idOrigen = rutaSeleccionada.getOrigenRuta();
            String idDestino = rutaSeleccionada.getDestinoRuta();

            // Eliminará la ruta original y su inversa.
            if (crudInstancia.eliminarRuta(idOrigen, idDestino)) {
                // Refrescar la tabla y limpiar
                cargarDatos(); // Recargar todos los datos para remover ambas rutas
                mostrarAlerta("Éxito", "La ruta fue eliminada.", Alert.AlertType.INFORMATION);
                mostrarDetallesRuta(null); // Limpia y deselecciona, restablece el formulario
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la ruta. Verifique los datos.", Alert.AlertType.ERROR);
            }
        }
    }

    //Objetivo: Modificar una ruta seleccionada
    private void modificarRuta(ActionEvent actionEvent) {

        if (rutaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una ruta de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }
        // Los id de la ruta original (Origen y Destino) no cambian
        String idExistente = rutaSeleccionada.getIdRuta();
        String origenExistente = rutaSeleccionada.getOrigenRuta();
        String destinoExistente = rutaSeleccionada.getDestinoRuta();
        //nuevos datos
        String nuevoNombre = txtNombreR.getText().trim();
        int nuevaDistancia = spnDistancia.getValue();
        float nuevoCosto = spnCosto.getValue().floatValue();
        int nuevoTransbordo = spnTransbordo.getValue();
        int nuevoTiempo = spnTiempo.getValue();

        // Validación de campos
        if (nuevoNombre.isEmpty()) {
            mostrarAlerta("Error de Datos", "El nombre de la ruta no puede estar vacío.", Alert.AlertType.ERROR);
            return;
        }
        if (nuevaDistancia <= 0 || nuevoCosto <= 0 || nuevoTiempo <= 0) {
            mostrarAlerta("Error de Datos", "Distancia, Costo y Tiempo deben ser mayores a cero.", Alert.AlertType.ERROR);
            return;
        }
        // Crear el objeto con los datos actualizados
        Ruta rutaActualizada = new Ruta(idExistente, nuevoNombre, nuevaDistancia, nuevoCosto, nuevoTransbordo, nuevoTiempo, origenExistente, destinoExistente);

        // El CRUD actualizará la ruta original y su inversa.
        if (crudInstancia.modificarRuta(origenExistente, destinoExistente, rutaActualizada)) {
            // Refrescar la tabla y limpiar
            cargarDatos(); // Recargar todos los datos para incluir la ruta inversa modificada
            mostrarAlerta("Éxito", "Ruta " + idExistente + " actualizada correctamente.", Alert.AlertType.INFORMATION);
            mostrarDetallesRuta(null); // Limpia y deselecciona, restablece el formulario
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la ruta.", Alert.AlertType.ERROR);
        }
    }

    //Objetivo: Guardar las rutas ingresadas en la lista
    private void guardarRuta(ActionEvent actionEvent) {
        String codRuta = txtCod.getText().trim();
        String nombreRuta = txtNombreR.getText().trim();
        int distancia = spnDistancia.getValue();
        float costo = spnCosto.getValue().floatValue();
        int transbordos = spnTransbordo.getValue();
        int tiempo = spnTiempo.getValue();
        String origen = cmboxOrigen.getValue();
        String destino = cmboxDestino.getValue();

        if (codRuta.isEmpty() || nombreRuta.isEmpty() || origen.isEmpty() || destino.isEmpty()) {
            mostrarAlerta("Error de Datos", "Debe completar todos los campos de texto.", Alert.AlertType.ERROR);
            return;
        }
        if (distancia <= 0 || costo <= 0 || tiempo <= 0) {
            mostrarAlerta("Error de Datos", "Distancia, Costo y Tiempo deben ser mayores a cero.", Alert.AlertType.ERROR);
            return;
        }
        // Crear el objeto Ruta
        Ruta nuevaRuta = new Ruta(codRuta, nombreRuta, distancia, costo, transbordos, tiempo, origen, destino);

        // Guardar en el CRUD
        if (crudInstancia.agregarRuta(nuevaRuta)) {
            // El CRUD guarda la ruta y su inversa. Debemos recargar la tabla para mostrar ambas.
            nextRouteId++;
            cargarDatos();
            mostrarAlerta("Registro con éxito", "Ruta " + nombreRuta + " Registrada Correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } else {
            mostrarAlerta("Error de Registro", "Asegúrese que las paradas de Origen/Destino existan y la ruta no esté duplicada.", Alert.AlertType.WARNING);
        }
    }

    //Objetivo: Configurar los spinner con valores iniciales
    private void configurarSpinners() {
        spnDistancia.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 1));
        spnDistancia.setEditable(true);
        // Usamos Double para el Spinner, que se convierte a Float en el modelo.
        spnCosto.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10000.0, 0.0, 5.0));
        spnCosto.setEditable(true);
        spnTransbordo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0, 1));
        spnTransbordo.setEditable(true);
        spnTiempo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1440, 0, 5));
        spnTiempo.setEditable(true);
    }

    //Objetivo: Muestra los datos en la tabla
    private void configurarTabla() {
        colID.setCellValueFactory(param -> param.getValue().idRutaProperty());
        colNombre.setCellValueFactory(param -> param.getValue().nombreRutaProperty());
        // Las columnas de tipo numérico (int, float) devuelven un objeto Number
        colDistancia.setCellValueFactory(param -> param.getValue().distanciaRutaProperty());
        colCosto.setCellValueFactory(param -> param.getValue().costoRutaProperty());
        colTransbordos.setCellValueFactory(param -> param.getValue().cantidadTransbordoProperty());
        colTiempo.setCellValueFactory(param -> param.getValue().tiempoViajeProperty());
        colParadaOrigen.setCellValueFactory(param -> param.getValue().origenRutaProperty());
        colParadaDestino.setCellValueFactory(param -> param.getValue().destinoRutaProperty());
    }

    //Objetivo: Cargar los datos en la tabla
    private void cargarDatos() {
        List<Ruta> todasLasRutas = new ArrayList<>();
        for (List<Ruta> listaA : crudInstancia.getRuta()) {
            todasLasRutas.addAll(listaA);
        }
        // Enlazar la lista observable con la tabla
        listaRutasO = FXCollections.observableArrayList(todasLasRutas);
        tblRuta.setItems(listaRutasO);
    }

    //Objetivo: Limpiar los campos despues de una acción
    private void limpiarCampos() {
        txtCod.setText(generateNextRouteId());
        txtNombreR.clear();
        cmboxOrigen.getSelectionModel().clearSelection();
        cmboxDestino.getSelectionModel().clearSelection();
        spnDistancia.getValueFactory().setValue(0);
        spnCosto.getValueFactory().setValue(0.0);
        spnTransbordo.getValueFactory().setValue(0);
        spnTiempo.getValueFactory().setValue(0);
    }

    //Objetivo:  Método genérico para las alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos para general el id automático
    private String generateNextRouteId() {
        return String.format("R%03d", nextRouteId);
    }

    private void setInitialRouteId() {
        // Esto asegura que, al iniciar, el ID tome el valor del último elemento + 1.
        if (listaRutasO != null && !listaRutasO.isEmpty()) {
            // Usamos el tamaño de la lista como punto de partida.
            nextRouteId = listaRutasO.size() + 1;
        } else {
            nextRouteId = 1;
        }
        txtCod.setText(generateNextRouteId());
    }

}
