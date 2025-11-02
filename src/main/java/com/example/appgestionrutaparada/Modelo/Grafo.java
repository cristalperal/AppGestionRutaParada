package com.example.appgestionrutaparada.Modelo;

import java.util.LinkedList;
import java.util.List;

//Clase útil para los algoritmos
//Modelo que organiza los datos de Crud en una forma conveniente para los algoritmos de caminos
public class Grafo {
    private List<Parada> parada; //Nodos
    private List<List<Ruta>> ruta; //Aristas

    public Grafo() {
        parada = new LinkedList<>();
        ruta = new LinkedList<>();
    }

    public List<Parada> getParada() {
        return parada;
    }

    public void setParada(List<Parada> parada) {
        this.parada = parada;
    }

    public List<List<Ruta>> getRuta() {
        return ruta;
    }

    public void setRuta(List<List<Ruta>> ruta) {
        this.ruta = ruta;
    }
}