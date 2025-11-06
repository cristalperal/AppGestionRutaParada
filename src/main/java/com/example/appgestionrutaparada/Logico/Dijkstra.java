package com.example.appgestionrutaparada.Logico;



import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;

import java.util.*;

//Algoritmo para encontrar la ruta más corta de un origen y destino especificados en términos de distancia
public class Dijkstra {

    public List<Ruta> calcularRutaCorta(Grafo grafo, String idOrigen, String idDestino,String criterio) {
        List<Parada> paradas = grafo.getParada();
        List<List<Ruta>> rutas = grafo.getRuta();

        //Mapas para guardar la distancia y el anterior
        Map<String, Integer> distancia = new HashMap<>();
        Map<String, String> anterior = new HashMap<>();
        //Cola de prioridad, para poder ordenar la lista de menor a mayor distancia
        PriorityQueue<String> cola = new PriorityQueue<>(Comparator.comparingInt(distancia::get));

        //Inicializar las paradas con el mapa
        for(Parada parada : paradas) {
            distancia.put(parada.getIdParada(), Integer.MAX_VALUE);
        }

        // El nodo inicial
        distancia.put(idOrigen, 0);
        cola.add(idOrigen);

        // Algoritmo principal

        while(!cola.isEmpty()) {
            String actual = cola.poll();
            int indexActual = buscarIndexParada(paradas,actual);
            
            if(indexActual == -1) {
                continue;
            }
            
            // Recorrer las rutas por el index actual 
            for(Ruta ruta : rutas.get(indexActual)) {
                String vecino = ruta.getDestinoRuta();
                int peso = obtenerPeso(ruta, criterio);

                if (distancia.get(actual) + peso < distancia.get(vecino)) {
                    distancia.put(vecino, distancia.get(actual) + peso);
                    anterior.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }
        return reconstruirCamino(grafo, anterior,idOrigen, idDestino);
    }

    private List<Ruta> reconstruirCamino(Grafo grafo, Map<String, String> anterior, String idOrigen, String idDestino) {

        List<Ruta> camino = new LinkedList<>();
        if(!anterior.containsKey(idDestino)) {
            return camino;
        }
        String actual = idDestino;
        while(!actual.equals(idOrigen)) {
            String pasado = anterior.get(actual);
            if(pasado == null) {
                break;
            }
            Ruta ruta = buscarRuta(grafo, pasado, actual);
            if(ruta != null) {
                camino.add(0, ruta);
                actual = pasado;
            }
        }
        return  camino;
    }

    private Ruta buscarRuta(Grafo grafo, String origen, String destino) {
        int index = buscarIndexParada(grafo.getParada(), origen);
        if (index != -1) {
            for (Ruta r : grafo.getRuta().get(index)) {
                if (r.getDestinoRuta().equals(destino)) return r;
            }
        }
        return null;
    }

    private int obtenerPeso(Ruta ruta, String criterio) {
        return switch (criterio.toLowerCase()){
            case "distancia" -> ruta.getDistanciaRuta();
            case "tiempo" -> ruta.getTiempoViaje();
            case "transbordo" -> ruta.getCantidadTransbordo();
            case "costo" -> (int) ruta.getCostoRuta();
            default ->  ruta.getDistanciaRuta();
        };
    }

    private int buscarIndexParada(List<Parada> paradas, String idParada) {
        for(int i = 0; i < paradas.size(); i++) {
            if(paradas.get(i).getIdParada().equals(idParada)) {
                return i;
            }
        }
        return -1;
    }
}
