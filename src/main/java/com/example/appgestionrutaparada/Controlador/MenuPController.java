package com.example.appgestionrutaparada.Controlador;

import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Logico.Dijkstra;
import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class MenuPController implements Initializable {

    // Para Manejar las rutas y paradas activas
    @FXML
    private Label lblRutasActivas;
    @FXML
    private Label lblParadasActivas;

    // Campos de resultados
    @FXML
    private Label lblOrigen;
    @FXML
    private Label lblDestino;

    // Paneles de Resultados (Distancia)
    @FXML
    private Label lblDistanciaD;
    @FXML
    private Label lblTiempoD;
    @FXML
    private Label lblTransbD;
    @FXML
    private Label lblCostoD;

    // Paneles de Resultados (Tiempo)
    @FXML
    private Label lblTiempoTi;
    @FXML
    private Label lblDistanciaTi;
    @FXML
    private Label lblTransbTi;
    @FXML
    private Label lblCostoTi;

    // Paneles de Resultados (Transbordos)
    @FXML
    private Label lblCantTrasbordoTr;
    @FXML
    private Label lblDistanciaTra;
    @FXML
    private Label lblTiempoTras;
    @FXML
    private Label lblCostoTra;

    // Paneles de Resultados (Costo)
    @FXML
    private Label lblCostoC;
    @FXML
    private Label lblDistanciaC;
    @FXML
    private Label lblTiempoC;
    @FXML
    private Label lblTransbC;

    // Botones y Paneles
    @FXML
    private Button btnCalcularRuta;
    @FXML
    private Button btnLimpiarResultados;
    @FXML
    private Pane pnlResultados;
    @FXML
    private Pane pnlGeneralNombre;
    @FXML
    private ComboBox<String> cmboxOrigen;  // ID de Parada Origen
    @FXML
    private ComboBox<String> cmboxDestino;  // ID de Parada Destino

    private Crud crudInstancia;
    private Dijkstra dijkstra = new Dijkstra();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pnlResultados.setVisible(false);
        crudInstancia = Crud.getInstancia();
        cargarOpcionesParada();
        btnCalcularRuta.setOnAction(this::CalcularRuta);
        btnLimpiarResultados.setOnAction(this::LimpiarResultados);
        // Limpiar al inicio
        limpiarCamposResultados();
        MostrarRutasParadasActivas();

    }

    private void MostrarRutasParadasActivas() {
        // Mostrar la cantidad de paradas y rutas activas
        int totalParadas = crudInstancia.paradasActivas();
        int totalRutas = crudInstancia.rutasActivas();
        // formateando como String
        lblParadasActivas.setText(String.valueOf("00"+ totalParadas));
        lblRutasActivas.setText(String.valueOf("00"+totalRutas));
    }

    // Objetivo: Calcular las rutas por los diferentes criterios
    private void CalcularRuta(ActionEvent actionEvent) {
        String nombreOrigen = cmboxOrigen.getValue();
        String nombreDestino = cmboxDestino.getValue();

        if (nombreOrigen == null || nombreDestino == null || nombreOrigen.equals(nombreDestino)) {
            mostrarAlerta("Error", "Debe seleccionar un Origen y Destino válidos y diferentes.", Alert.AlertType.WARNING);
            return;
        }
        //Convertir el nombre al id de la parada
        String idOrigen = crudInstancia.buscarIdPorNombre(nombreOrigen);
        String idDestino = crudInstancia.buscarIdPorNombre(nombreDestino);
        // Mostrar el nombre de origen y destino en el panel de resultados
        lblOrigen.setText(nombreOrigen);
        lblDestino.setText(nombreDestino);
        //Crear grafo
        Grafo grafo = crudInstancia.obtenerGrafo();

        //Si no hay ruta que conecte las paradas
        List<Ruta> VerificarDistancia = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "distancia");
        if (VerificarDistancia.isEmpty()) {
            limpiarCamposResultados();
            pnlResultados.setVisible(false);
            mostrarAlerta("Ruta No Disponible", "No se pudo encontrar ninguna ruta que conecte \n" + nombreOrigen + " con " + nombreDestino, Alert.AlertType.INFORMATION);
            return; // Detener la ejecución de la función
        }

        //hacer visible el panel de mostrar los resultados
        pnlResultados.setVisible(true);
        pnlGeneralNombre.setVisible(false);

        // Algoritmo de dijkstra para cada criterio
        // Distancia
        List<Ruta> caminoDistancia = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "distancia");
        mostrarResultadoEnPanel(caminoDistancia, "DISTANCIA");

        // Tiempo
        List<Ruta> caminoTiempo = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "tiempo");
        mostrarResultadoEnPanel(caminoTiempo, "TIEMPO");

        //Transbordos
        List<Ruta> caminoTransbordo = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "transbordo");
        mostrarResultadoEnPanel(caminoTransbordo, "TRANSBORDO");

        // Costo
        List<Ruta> caminoCosto = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "costo");
        mostrarResultadoEnPanel(caminoCosto, "COSTO");
    }

    //Objetivo: Mostrar los resultados del algoritmo de Dijkstra en el panel usando el calculo para mostrar el mejor camino
    private void mostrarResultadoEnPanel(List<Ruta> camino, String panel) {
        MejorCamino mejorCamino = CalcularMejoresCaminos(camino);
        String distancia = String.format("%.1f", mejorCamino.distanciaTotal);
        String tiempo = String.format("%.0f", mejorCamino.tiempoTotal);
        String costo = String.format("%.2f", mejorCamino.costoTotal);
        String transbordo = String.valueOf(mejorCamino.transbordos);

        // Para mostrar segun cada criterio
        switch (panel.toUpperCase()) {
            case "DISTANCIA" -> {
                // Distancia
                lblDistanciaD.setText(distancia);
                lblTiempoD.setText(tiempo);
                lblTransbD.setText(transbordo);
                lblCostoD.setText(costo);
            }
            case "TIEMPO" -> {
                // Tiempo
                lblTiempoTi.setText(tiempo);
                lblDistanciaTi.setText(distancia);
                lblTransbTi.setText(transbordo);
                lblCostoTi.setText(costo);
            }
            case "TRANSBORDO" -> {
                // Transbordo
                lblCantTrasbordoTr.setText(transbordo);
                lblDistanciaTra.setText(distancia);
                lblTiempoTras.setText(tiempo);
                lblCostoTra.setText(costo);
            }
            case "COSTO" -> {
                // Costo
                lblCostoC.setText(costo);
                lblDistanciaC.setText(distancia);
                lblTiempoC.setText(tiempo);
                lblTransbC.setText(transbordo);
            }
        }
    }

     //Objetivo:  Suma las distancias, tiempos, costos y cuenta los tramos de la ruta.
    private MejorCamino CalcularMejoresCaminos(List<Ruta> camino) {
        MejorCamino mejor = new MejorCamino();

        if (camino == null || camino.isEmpty()) {
            return mejor; // Devuelve ceros si no hay camino
        }

        mejor.transbordos = camino.size(); // Cada ruta en la lista es un transbordo
        for (Ruta r : camino) {
            mejor.distanciaTotal += r.getDistanciaRuta();
            mejor.tiempoTotal += r.getTiempoViaje();
            mejor.costoTotal += r.getCostoRuta();
        }
        return mejor;
    }

    // Clase auxiliar para almacenar las 4 métricas (Mejor camino) de cualquier camino
    private static class MejorCamino {
        double distanciaTotal = 0;
        double tiempoTotal = 0;
        double costoTotal = 0;
        int transbordos = 0;
    }

    //Objetivo: Limpiar los resultados obtenidos de dijkstra
    private void limpiarCamposResultados() {
        String text = "";
        String principales = "0000";

        // Limpiar Panel Distancia
        lblDistanciaD.setText(principales);
        lblTiempoD.setText(text);
        lblTransbD.setText(text);
        lblCostoD.setText(text);

        // Limpiar Panel Tiempo
        lblTiempoTi.setText(principales);
        lblDistanciaTi.setText(text);
        lblTransbTi.setText(text);
        lblCostoTi.setText(text);

        // Limpiar Panel Transbordo
        lblCantTrasbordoTr.setText(principales);
        lblDistanciaTra.setText(text);
        lblTiempoTras.setText(text);
        lblCostoTra.setText(text);

        // Limpiar Panel Costo
        lblCostoC.setText(principales);
        lblDistanciaC.setText(text);
        lblTiempoC.setText(text);
        lblTransbC.setText(text);

    }

    //Objetivo: Limpiar lo seleccionado en los combos y todo en general
    private void LimpiarResultados(ActionEvent actionEvent) {
        limpiarCamposResultados();
        lblOrigen.setText("");
        lblDestino.setText("");
        cmboxOrigen.getSelectionModel().clearSelection();
        cmboxDestino.getSelectionModel().clearSelection();
        pnlGeneralNombre.setVisible(true);
        pnlResultados.setVisible(false);
    }


    // Metodos para Abrir las ventanas de parada y ruta desde el menu principal
    @FXML
    public void AbrirGestionParada(ActionEvent actionEvent) {

        abrirNuevaVentana("GestionarParadaV.fxml", "Gestión de Paradas");
    }

    @FXML
    public void AbrirGestionRuta(ActionEvent actionEvent) {

        abrirNuevaVentana("GestionarRutaV.fxml", "Gestión de Rutas");
    }


    // Objetivo: Cargar los nombres  de las paradas en los comboBox
    private void cargarOpcionesParada() {
        // Obtener todas las paradas
        List<Parada> paradas = crudInstancia.getParada();
        // Extraer solo los nombres de parada
        ObservableList<String> nom = FXCollections.observableArrayList();
        for (Parada p : paradas) {
            nom.add(p.getNombreParada());
        }
        // Asignar la lista a ambos ComboBoxes
        cmboxOrigen.setItems(nom);
        cmboxDestino.setItems(nom);
    }


    /**
     * Método genérico para cargar y mostrar una nueva ventana de forma modal.
     */
    private void abrirNuevaVentana(String fxml, String titulo) {
        try {
            //  Cargar el FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/appgestionrutaparada/vistas/" + fxml));
            Parent parent = fxmlLoader.load();
            // Crear un nueva ventana
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(parent));
            // bloquea la ventana principal hasta que se cierra
            stage.initModality(Modality.APPLICATION_MODAL);
            // Centrar la nueva ventana en la pantalla
            stage.centerOnScreen();
            // Mostrar la ventana
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            //  informar al usuario si falla la carga.
            System.err.println("Error al cargar la ventana FXML: " + fxml);
        }
    }

    //Objetivo:  Método genérico para las alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
