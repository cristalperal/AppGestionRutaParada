package com.example.appgestionrutaparada.Logico;

import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//para calcular la ruta más corta entre todas las paradas de forma general.
public class FloydWarshall {

    // Define un valor grande para representar la infinidad
    private static final int INF = 999999;

    //Calcula la matriz de distancias más cortas y la matriz de caminos para todos los pares de nodos en el grafo.
    public FloydWarshallResult calcularTodoParParadas(Grafo grafo, String criterio) {
        List<Parada> paradas = grafo.getParada();
        int n = paradas.size();

        // Mapear ids de Parada a Índices de Matriz de 0 a n-1
        Map<String, Integer> indiceParada = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indiceParada.put(paradas.get(i).getIdParada(), i);
        }

        // Inicializar Matrices
        // La distancia almacena la distancia más corta entre i y j
        double[][] distancia = new double[n][n];
        // El predecesor almacena el índice de la parada que precede a j en el camino más corto de i a j
        int[][] predecesor = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distancia[i][j] = 0; // Distancia de un nodo a sí mismo es 0
                } else {
                    distancia[i][j] = INF; // Inicialmente infinito
                }
                predecesor[i][j] = -1; // Inicialmente, sin predecesor
            }
        }

        // Rellenar matrices con pesos de rutas directas
        for (int i = 0; i < n; i++) {
            Parada paradaOrigen = paradas.get(i);
            // Iterar sobre las rutas que salen de la parada i usando la lista de adyacencia
            for (Ruta ruta : grafo.getRuta().get(i)) {
                String idDestino = ruta.getDestinoRuta();
                int j = indiceParada.get(idDestino);
                double peso = obtenerPeso(ruta, criterio);

                // Solo actualizar si la nueva ruta es más corta que la actual
                if (peso < distancia[i][j]) {
                    distancia[i][j] = peso;
                    predecesor[i][j] = i; // El predecesor es el nodo de origen, índice i
                }
            }
        }

        // Algoritmo de Floyd-Warshall
        // K es la parada intermedia
        for (int k = 0; k < n; k++) {
            // i es la parada de inicio
            for (int i = 0; i < n; i++) {
                // j es la parada de destino
                for (int j = 0; j < n; j++) {
                    // Si el camino de i a k a j es más corto que el camino actual de i a j
                    if (distancia[i][k] != INF && distancia[k][j] != INF &&
                            distancia[i][k] + distancia[k][j] < distancia[i][j]) {

                        distancia[i][j] = distancia[i][k] + distancia[k][j];
                        // El predecesor de j en el camino de i es el mismo que el predecesor de j en el camino de k
                        predecesor[i][j] = predecesor[k][j];
                    }
                }
            }
        }

        return new FloydWarshallResult(distancia, predecesor, paradas, indiceParada);
    }

    //Objetivo: Obtiene el peso según el criterio que se le pase, toma el valor de este
    private double obtenerPeso(Ruta ruta, String criterio) {
        return switch (criterio.toLowerCase()) {
            case "distancia" -> (double) ruta.getDistanciaRuta();
            case "tiempo" -> (double) ruta.getTiempoViaje();
            case "transbordo" -> (double) ruta.getCantidadTransbordo();
            case "costo" -> (double) ruta.getCostoRuta();
            default -> (double) ruta.getDistanciaRuta();
        };
    }

}
