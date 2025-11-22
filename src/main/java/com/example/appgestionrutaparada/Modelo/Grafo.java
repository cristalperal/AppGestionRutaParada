package com.example.appgestionrutaparada.Modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;
import java.util.List;

//Clase para manejar los grafos
public class Grafo {
    private StringProperty idGrafo;
    private List<Parada> parada; //Nodos
    private List<List<Ruta>> ruta; //Aristas

    public Grafo() {
        this.idGrafo = new SimpleStringProperty("");
        parada = new LinkedList<>();
        ruta = new LinkedList<>();
    }

    //Constructor con id
    public Grafo(String idGrafo) {
        this.idGrafo = new SimpleStringProperty(idGrafo);
        parada = new LinkedList<>();
        ruta = new LinkedList<>();
    }

    public StringProperty idGrafoProperty() {
        return idGrafo;
    }
    public String getIdGrafo() {
        return idGrafo.get();
    }
    public void setIdGrafo(String idGrafo) {
        this.idGrafo.set(idGrafo);
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