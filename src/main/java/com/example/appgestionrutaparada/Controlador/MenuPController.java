package com.example.appgestionrutaparada.Controlador;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.example.appgestionrutaparada.Logico.Crud;
import com.example.appgestionrutaparada.Logico.Dijkstra;
import com.example.appgestionrutaparada.Logico.FloydWarshall;
import com.example.appgestionrutaparada.Logico.FloydWarshallResult;
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
    private Pane pnlMapa;
    @FXML
    private ComboBox<String> cmboxOrigen;  // ID de Parada Origen
    @FXML
    private ComboBox<String> cmboxDestino;  // ID de Parada Destino

    //Instancias
    private Crud crudInstancia;
    private Dijkstra dijkstra = new Dijkstra();
    private FloydWarshall floydWarshall = new FloydWarshall();
    private FloydWarshallResult fwDistanciaResult;
    private SmartGraphPanel<String, String> graphView;

    // Variables de estado para la selección en el grafo
    private String paradaSeleccionadaOrigen = null;
    private String paradaSeleccionadaDestino = null;

    // DATOS DE INICIO
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pnlResultados.setVisible(false);
        crudInstancia = Crud.getInstancia();
        inicializarGrafo();
        cargarOpcionesParada();
        btnCalcularRuta.setOnAction(this::CalcularRuta);
        btnLimpiarResultados.setOnAction(this::LimpiarResultados);
        // Limpiar al inicio
        limpiarCamposResultados();
        MostrarRutasParadasActivas();
        agregarResaltadoGrafo();

    }

    // ------------- MÉTODOS DEL GRAFO ------------

    //Objetivo: Inicializar el grafo, con sus nodos y aristas
    private void inicializarGrafo() {

        Grafo grafo = crudInstancia.obtenerGrafo();
        // para el algoritmo de floyd
        if (!grafo.getParada().isEmpty()) {
            fwDistanciaResult = floydWarshall.calcularTodoParParadas(grafo, "distancia");
        } else {
            fwDistanciaResult = null;
        }

        if (!grafo.getParada().isEmpty()) {
            try {
                // Convertir el modelo de grafo
                Digraph<String, String> smartGraphModel = construirDigraph(grafo);

                // Crear el panel de visualización
                graphView = new SmartGraphPanel<>(
                        smartGraphModel,
                        new SmartCircularSortedPlacementStrategy()
                );
                // Añadir al Pane
                pnlMapa.getChildren().add(graphView);
                // Asegurar que se ajuste al tamaño del Pane
                graphView.prefWidthProperty().bind(pnlMapa.widthProperty());
                graphView.prefHeightProperty().bind(pnlMapa.heightProperty());

                javafx.application.Platform.runLater(() -> {
                    try {
                        graphView.init();
                        agregarSeleccionGrafo();
                    } catch (IllegalStateException e) {
                        System.err.println("Error al inicializar la visualización del grafo : " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                System.err.println("Error al inicializar la visualización del grafo: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    //Objetivo: Construir el diagrama del grafo con las paradas como nodos y las rutas como aristas
    private Digraph<String, String> construirDigraph(Grafo grafo) {
        Digraph<String, String> g = new DigraphEdgeList<>();

        // Agregar todos los vértices
        List<Parada> paradas = grafo.getParada();
        if (paradas != null) {
            for (Parada p : paradas) {
                if (p != null) {
                    // Usamos el Nombre de la Parada como valor del nodo
                    g.insertVertex(p.getNombreParada());
                }
            }
        }
        // Agregar todas las aristas
        List<List<Ruta>> listasRuta = grafo.getRuta();
        if (listasRuta != null) {
            for (int i = 0; i < listasRuta.size(); i++) {
                Parada origen = (i < paradas.size() && paradas.get(i) != null) ? paradas.get(i) : null;
                if (origen == null) continue;

                List<Ruta> rutasAdyacentes = listasRuta.get(i);
                if (rutasAdyacentes != null) {
                    for (Ruta ruta : rutasAdyacentes) {
                        if (ruta != null) {

                            Parada destino = crudInstancia.buscarParadaPorId(ruta.getDestinoRuta());

                            if (destino != null) {
                                String edgeKey = ruta.getIdRuta();
                                g.insertEdge(origen.getNombreParada(), destino.getNombreParada(), edgeKey);
                            }
                        }
                    }
                }
            }
        }
        return g;
    }


    // ------------- MÉTODOS PARA PODER SELECCIONAR EN EL GRAFO ------------

    //Objetivo: Permitir seleccionar un origen y un destino directamente del grafo, estos se cargaran a los combo box y se podra calcular la ruta
    private void agregarSeleccionGrafo() {
        if (graphView == null) return;

        graphView.getSmartVertices().forEach(v -> {

           // Se hara click en el label del nodo
            javafx.scene.Node nodo = (javafx.scene.Node) v.getStylableLabel();

            String nombreParada = v.getUnderlyingVertex().element();

            nodo.setOnMouseClicked(event -> {

                // Si no hay origen seleccionado, este será el origen
                if (paradaSeleccionadaOrigen == null || paradaSeleccionadaDestino != null) {

                    //Origen
                    if (paradaSeleccionadaOrigen != null) {
                        // Si ya hay una selección, limpiamos completamente lo seleccionado
                        LimpiarSeleccionGrafo();
                    }

                    paradaSeleccionadaOrigen = nombreParada;
                    paradaSeleccionadaDestino = null; // Reiniciar Destino

                    cmboxOrigen.getSelectionModel().select(nombreParada);
                    cmboxDestino.getSelectionModel().clearSelection(); // Limpiar combo de destino

                    // Resaltar
                    v.addStyleClass("origen");

                    mostrarAlerta("Origen Seleccionado", "Origen: " + nombreParada, Alert.AlertType.INFORMATION);

                } else {
                    // Destino, tiene que existir un origen
                    if (paradaSeleccionadaOrigen.equals(nombreParada)) {
                        mostrarAlerta("Error", "El destino no puede ser igual al origen.", Alert.AlertType.WARNING);
                        return;
                    }

                    paradaSeleccionadaDestino = nombreParada;
                    cmboxDestino.getSelectionModel().select(nombreParada);

                    // Resaltar el destino
                    v.addStyleClass("destino");

                    mostrarAlerta("Destino Seleccionado", "Origen: " + paradaSeleccionadaOrigen + "\nDestino: " + nombreParada, Alert.AlertType.INFORMATION);
                }
            });
        });
    }

    //Objetivo: Limpiar la seleccion que se haga en el grafo
    void LimpiarSeleccionGrafo() {
        if (graphView == null) return;

        // CORRECCIÓN: Usamos getModel().vertices()
        graphView.getModel().vertices().forEach(v -> {
            String nombreParada = v.element().toString();
            var vertexNode = graphView.getStylableVertex(nombreParada);
            if (vertexNode != null) {
                vertexNode.removeStyleClass("origen");
               vertexNode.removeStyleClass("destino");
            }
        });
    }


    // ------------- MÉTODOS DE VISUALIZACIÓN DE RUTA ------------

   //Objectivo: Establecer la interactividad del resaltado de rutas, asigna la acción de resaltar la ruta óptima en el grafo
    private void agregarResaltadoGrafo() {
        lblDistanciaD.setOnMouseClicked(e -> resaltarRutaCriterio("distancia"));
        lblTiempoTi.setOnMouseClicked(e -> resaltarRutaCriterio("tiempo"));
        lblCantTrasbordoTr.setOnMouseClicked(e -> resaltarRutaCriterio("transbordo"));
        lblCostoC.setOnMouseClicked(e -> resaltarRutaCriterio("costo"));
    }

     //Objectivo: Obtiene el camino más corto entre el origen y el destino, lo resalta en el grafo con el color correspondiente.
    private void resaltarRutaCriterio(String criterio) {
        String nombreOrigen = lblOrigen.getText();
        String nombreDestino = lblDestino.getText();

        if (nombreOrigen == null || nombreDestino == null || nombreOrigen.isEmpty() || nombreDestino.isEmpty()) {
            return;
        }

        String idOrigen = crudInstancia.buscarIdPorNombre(nombreOrigen);
        String idDestino = crudInstancia.buscarIdPorNombre(nombreDestino);

        Grafo grafo = crudInstancia.obtenerGrafo();
        List<Ruta> camino;
        String claseCSS = "";

        if (criterio.equalsIgnoreCase("distancia")) {
            List<String> caminoIdsDistancia = fwDistanciaResult.reconstruirCaminoParadas(idOrigen, idDestino);
            camino = reconstruirRutasDesdeParadas(grafo, caminoIdsDistancia);
            claseCSS = "ruta-distancia";
        } else {

            camino = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, criterio.toLowerCase());
            switch (criterio.toLowerCase()) {
                case "tiempo" -> claseCSS = "ruta-tiempo";
                case "transbordo" -> claseCSS = "ruta-transbordo";
                case "costo" -> claseCSS = "ruta-costo";
            }
        }

        // Resaltar la ruta con el color correspondiente
        ResaltarRuta(camino, claseCSS);
    }

    //Objectivo: Remueve las clases CSS de resaltado de todas las aristas para despintar la ruta anterior.
    private void LimpiarRutaResaltada(boolean cleanNodes) {
        if (graphView == null) return;

        graphView.getSmartEdges().forEach(edge -> {
            edge.removeStyleClass("ruta-distancia");
            edge.removeStyleClass("ruta-tiempo");
            edge.removeStyleClass("ruta-transbordo");
            edge.removeStyleClass("ruta-costo");
        });

        if (cleanNodes) {
            graphView.getSmartVertices().forEach(vertex -> {
                vertex.removeStyleClass("origen");
                vertex.removeStyleClass("destino");
            });
        }
    }

    // objectivo: Aplicar el estilo visual a un camino específico en el grafo, recorre la lista de rutas del camino y aplica la clase CSS a cada arista
    private void ResaltarRuta(List<Ruta> camino, String styleClass) {
        if (graphView == null || camino == null || camino.isEmpty()) return;

        LimpiarRutaResaltada(false);

        // Resaltar el nodo Origen
        Parada origen = crudInstancia.buscarParadaPorId(camino.get(0).getOrigenRuta());
        if (origen != null) {
            String nombreOrigen = origen.getNombreParada();
            if (graphView.getStylableVertex(nombreOrigen) != null) {
                // Limpiar estilos anteriores antes de poner el nuevo de origen y destino
                graphView.getStylableVertex(nombreOrigen).removeStyleClass("origen");
                graphView.getStylableVertex(nombreOrigen).addStyleClass("origen");
            }
        }

        // Resaltar el nodo Destino
        String idDestinoFinal = camino.get(camino.size() - 1).getDestinoRuta();
        Parada destino = crudInstancia.buscarParadaPorId(idDestinoFinal);

        if (destino != null) {
            String nombreDestino = destino.getNombreParada();
            if (graphView.getStylableVertex(nombreDestino) != null) {
                // Limpiar estilos anteriores antes de poner el nuevo de origen y destino
                graphView.getStylableVertex(nombreDestino).removeStyleClass("destino");
                graphView.getStylableVertex(nombreDestino).addStyleClass("destino");
            }
        }

        // Resaltar cada arista en el camino
        for (Ruta ruta : camino) {
            String idRuta = ruta.getIdRuta(); // La clave única insertada

            if (graphView.getStylableEdge(idRuta) != null) {
                graphView.getStylableEdge(idRuta).removeStyleClass("ruta-distancia");
                graphView.getStylableEdge(idRuta).removeStyleClass("ruta-tiempo");
                graphView.getStylableEdge(idRuta).removeStyleClass("ruta-transbordo");
                graphView.getStylableEdge(idRuta).removeStyleClass("ruta-costo");

                graphView.getStylableEdge(idRuta).addStyleClass(styleClass);
            }
        }
    }

    //Objectivo: Muestra las cuatro rutas óptimas (Distancia, Tiempo, Transbordo, Costo) al mismo tiempo, utilizando los colores de cada criterio.
    private void ResaltarMejorRuta(List<Ruta> caminoDistancia, List<Ruta> caminoTiempo, List<Ruta> caminoTransbordo, List<Ruta> caminoCosto) {
        // Limpiamos todos los resaltados anteriores
        LimpiarRutaResaltada(true);

        // Si no hay caminos
        if (caminoDistancia.isEmpty()) {
            return;
        }

        // Una estructura para mapear el id de ruta a sus criterios óptimos
        java.util.Map<String, java.util.Set<String>> rutasOptimas = new java.util.HashMap<>();

        // Función auxiliar para registrar los id de ruta en el mapa
        java.util.function.BiConsumer<List<Ruta>, String> registrarRuta = (camino, claseCSS) -> {
            for (Ruta ruta : camino) {
                String idRuta = ruta.getIdRuta();
                rutasOptimas.computeIfAbsent(idRuta, k -> new java.util.HashSet<>()).add(claseCSS);
            }
        };

        registrarRuta.accept(caminoDistancia, "ruta-distancia");
        registrarRuta.accept(caminoTiempo, "ruta-tiempo");
        registrarRuta.accept(caminoTransbordo, "ruta-transbordo");
        registrarRuta.accept(caminoCosto, "ruta-costo");

        for (java.util.Map.Entry<String, java.util.Set<String>> entry : rutasOptimas.entrySet()) {
            String idRuta = entry.getKey();
            java.util.Set<String> clasesCSS = entry.getValue();

            if (graphView.getStylableEdge(idRuta) != null) {
                for (String clase : clasesCSS) {
                    graphView.getStylableEdge(idRuta).addStyleClass(clase);
                }
            }
        }

        if (!caminoDistancia.isEmpty()) {
            // Resaltar el nodo Origen
            String nombreOrigen = crudInstancia.buscarParadaPorId(caminoDistancia.get(0).getOrigenRuta()).getNombreParada();
            if (graphView.getStylableVertex(nombreOrigen) != null) {
                graphView.getStylableVertex(nombreOrigen).addStyleClass("origen");
            }

            // Resaltar el nodo Destino
            String idDestinoFinal = caminoDistancia.get(caminoDistancia.size() - 1).getDestinoRuta();
            String nombreDestino = crudInstancia.buscarParadaPorId(idDestinoFinal).getNombreParada();
            if (graphView.getStylableVertex(nombreDestino) != null) {
                graphView.getStylableVertex(nombreDestino).addStyleClass("destino");
            }
        }
    }


    // ------------- MÉTODOS DE LOS ALGORITMOS  ------------

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
        // Floy Warshall, distancia
        List<String> caminoIdsDistancia = fwDistanciaResult.reconstruirCaminoParadas(idOrigen, idDestino);
        List<Ruta> caminoDistancia = reconstruirRutasDesdeParadas(grafo, caminoIdsDistancia);
        // Si no hay ruta que conecte las paradas
        if (caminoDistancia.isEmpty()) {
            limpiarCamposResultados();
            pnlResultados.setVisible(false);
            LimpiarRutaResaltada(true);
            mostrarAlerta("Ruta No Disponible", "No se pudo encontrar ninguna ruta que conecte \n" + nombreOrigen + " con " + nombreDestino, Alert.AlertType.INFORMATION);
            return;
        }
        //hacer visible el panel de mostrar los resultados
        pnlResultados.setVisible(true);
        pnlGeneralNombre.setVisible(false);

        List<Ruta> caminoTiempo = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "tiempo");
        List<Ruta> caminoTransbordo = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "transbordo");
        List<Ruta> caminoCosto = dijkstra.calcularRutaCorta(grafo, idOrigen, idDestino, "costo");

        mostrarResultadoEnPanel(caminoDistancia, "DISTANCIA");
        mostrarResultadoEnPanel(caminoTiempo, "TIEMPO");
        mostrarResultadoEnPanel(caminoTransbordo, "TRANSBORDO");
        mostrarResultadoEnPanel(caminoCosto, "COSTO");

        ResaltarMejorRuta(caminoDistancia, caminoTiempo, caminoTransbordo, caminoCosto);
    }

    // Reconstruir el camino para el algoritmo de Floyd
    private List<Ruta> reconstruirRutasDesdeParadas(Grafo grafo, List<String> caminoParadas) {
        List<Ruta> caminoRutas = new java.util.LinkedList<>();

        if (caminoParadas == null || caminoParadas.size() < 2) {
            return caminoRutas;
        }

        for (int i = 0; i < caminoParadas.size() - 1; i++) {
            String idOrigen = caminoParadas.get(i);
            String idDestino = caminoParadas.get(i + 1);

            Ruta ruta = crudInstancia.buscarRuta(idOrigen, idDestino);

            if (ruta != null) {
                caminoRutas.add(ruta);
            } else {
                return new java.util.LinkedList<>();//si no encontro un camino valido
            }
        }
        return caminoRutas;
    }

    //Objetivo: Suma las distancias, tiempos, costos y cuenta los tramos de la ruta.
    private MejorCamino CalcularMejoresCaminos(List<Ruta> camino) {
        MejorCamino mejor = new MejorCamino();

        if (camino == null || camino.isEmpty()) {
            return mejor; // Devuelve ceros si no hay camino
        }

        if (camino.size() <= 1) {
            mejor.transbordos = 0;
        } else {
            mejor.transbordos = camino.size() - 1;
        }
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

    //Objetivo: Mostrar los resultados del algoritmo de Dijkstra en el panel usando el cálculo para mostrar el mejor camino
    private void mostrarResultadoEnPanel(List<Ruta> camino, String panel) {
        MejorCamino mejorCamino = CalcularMejoresCaminos(camino);
        String distancia = String.format("%.1f", mejorCamino.distanciaTotal);
        String tiempo = String.format("%.0f", mejorCamino.tiempoTotal);
        String costo = String.format("%.2f", mejorCamino.costoTotal);
        String transbordo = String.valueOf(mejorCamino.transbordos);

        // Para mostrar según cada criterio
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

    // ------------- MÉTODOS AUXILIARES ------------

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
        LimpiarRutaResaltada(true);

    }

    //Objetivo: Limpiar lo seleccionado en los combos y la visualizacion de los paneles
    private void LimpiarResultados(ActionEvent actionEvent) {
        limpiarCamposResultados();
        lblOrigen.setText("");
        lblDestino.setText("");
        cmboxOrigen.getSelectionModel().clearSelection();
        cmboxDestino.getSelectionModel().clearSelection();
        pnlGeneralNombre.setVisible(true);
        pnlResultados.setVisible(false);
        LimpiarRutaResaltada(true);
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
        cmboxOrigen.setItems(nom);
        cmboxDestino.setItems(nom);
    }

    // Objetivo: Mostrar en los paneles las rutas y paradas activas
    private void MostrarRutasParadasActivas() {
        // Mostrar la cantidad de paradas y rutas activas
        int totalParadas = crudInstancia.paradasActivas();
        int totalRutas = crudInstancia.rutasActivas();
        // formateando como String
        lblParadasActivas.setText(String.valueOf("00" + totalParadas));
        lblRutasActivas.setText(String.valueOf("00" + totalRutas));
    }

    //Objetivo: Refrescar el menu de inicio
    private void refrescarDatosMenu() {
        inicializarGrafo();
        cargarOpcionesParada();
        cmboxOrigen.getSelectionModel().clearSelection();
        cmboxDestino.getSelectionModel().clearSelection();
        MostrarRutasParadasActivas();
        limpiarCamposResultados();
        pnlGeneralNombre.setVisible(true);
        pnlResultados.setVisible(false);
    }

    //Método genérico para cargar y mostrar una nueva ventana
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
            stage.showAndWait();
            // Una vez que la ventana de gestión se cierra, se ejecuta el refresco
            refrescarDatosMenu();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la ventana FXML: " + fxml);
        }
    }

    // Métodos para Abrir las ventanas de parada y ruta desde el menu principal
    @FXML
    public void AbrirGestionParada(ActionEvent actionEvent) {
        abrirNuevaVentana("GestionarParadaV.fxml", "Gestión de Paradas");
    }

    @FXML
    public void AbrirGestionRuta(ActionEvent actionEvent) {
        abrirNuevaVentana("GestionarRutaV.fxml", "Gestión de Rutas");
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
