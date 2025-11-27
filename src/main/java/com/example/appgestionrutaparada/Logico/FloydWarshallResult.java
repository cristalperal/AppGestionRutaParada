package com.example.appgestionrutaparada.Logico;

import com.example.appgestionrutaparada.Modelo.Parada;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;

//Contiene el resultado del algoritmo de Floyd-Warshall,incluyendo las matrices de distancia y predecesores.

public class FloydWarshallResult {

    // Define un valor grande para representar la infinidad
    private static final int INF = 999999;
    public final double[][] distancia;
    public final int[][] predecesor;
    public final List<Parada> paradas;
    public final Map<String, Integer> indiceParada;

    public FloydWarshallResult(double[][] distancia, int[][] predecesor, List<Parada> paradas, Map<String, Integer> indiceParada) {
        this.distancia = distancia;
        this.predecesor = predecesor;
        this.paradas = paradas;
        this.indiceParada = indiceParada;
    }

    //Reconstruye el camino más corto entre dos paradas usando la matriz de predecesores (anteriores)
    public List<String> reconstruirCaminoParadas(String idOrigen, String idDestino) {
        Integer i = indiceParada.get(idOrigen);
        Integer j = indiceParada.get(idDestino);

        // Verifica si las paradas existen o si no hay camino
        if (i == null || j == null || distancia[i][j] >= INF) {
            return new LinkedList<>(); // No hay camino
        }

        return obtenerRuta(i, j);
    }

    // Función recursiva para reconstruir el camino
    private List<String> obtenerRuta(int i, int j) {
        List<String> camino = new java.util.LinkedList<>();

        // Caso base: No hay predecesor o el camino es directo
        if (predecesor[i][j] == -1) {
            if (i == j) {
                camino.add(paradas.get(i).getIdParada());
            }
            return camino;
        }
        // Si el predecesor es el origen el camino es directo
        if (predecesor[i][j] == i) {
            camino.add(paradas.get(i).getIdParada());
            camino.add(paradas.get(j).getIdParada());
        } else {
            // Caso recursivo: Hay un nodo intermedio
            int k = predecesor[i][j];
            List<String> part1 = obtenerRuta(i, k);
            List<String> part2 = obtenerRuta(k, j);

            // Combinar las dos partes evitando duplicar el nodo intermedio k
            camino.addAll(part1);
            if (!part2.isEmpty()) {
                // Añadir los elementos de part2 desde el segundo ya que el primero es k duplicado
                camino.addAll(part2.subList(1, part2.size()));
            }
        }
        return camino;
    }
}