package com.example.appgestionrutaparada.Controlador;

import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Modelo.Ruta;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class RutaController implements Initializable {

    @FXML private TextField txtCod;
    @FXML private TextField txtNombreR;
    @FXML private Spinner<Integer> spnDistancia; // Distancia (KM)
    @FXML private Spinner<Double> spnCosto;      // Costo (RD)
    @FXML private Spinner<Integer> spnTransbordo; // Transbordos
    @FXML private Spinner<Integer> spnTiempo;     // Tiempo (Minutos)
    @FXML private TextField txtParadaO;  // ID de Parada Origen
    @FXML private TextField txtParadaD;  // ID de Parada Destino
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btneliminar;

    @FXML private TableView<Ruta> tblRuta;
    @FXML private TableColumn<Ruta, String> colID;
    @FXML private TableColumn<Ruta, String> colNombre;
    @FXML private TableColumn<Ruta, Number> colDistancia;
    @FXML private TableColumn<Ruta, Number> colCosto;
    @FXML private TableColumn<Ruta, Number> colTransbordos;
    @FXML private TableColumn<Ruta, Number> colTiempo;
    @FXML private TableColumn<Ruta, String> colParadaOrigen;
    @FXML private TableColumn<Ruta, String> colParadaDestino;

    private Crud crudInstancia;
    private ObservableList<Ruta> listaRutasO;
    private Ruta rutaSeleccionada = null; // Para manejo de selección


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}
