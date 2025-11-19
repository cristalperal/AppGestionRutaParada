package com.example.appgestionrutaparada.Modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Parada {
    // StringProperty nos permite actualizar mas rapido las tablas
    private StringProperty idParada;
    private StringProperty nombreParada;
    private StringProperty direccionParada;
    private StringProperty tipoTransporte;
    private StringProperty estadoParada; //Visitada o no visitada

    //Para la base de datos
    public Parada() {
    }

    public Parada(StringProperty nombreParada, StringProperty direccionParada, StringProperty tipoTransporte, StringProperty estadoParada) {
        this.nombreParada = nombreParada;
        this.direccionParada = direccionParada;
        this.tipoTransporte = tipoTransporte;
        this.estadoParada = estadoParada;
    }

    public Parada(String idParada, String nombreParada, String direccionParada, String tipoTransporte, String estadoParada) {
        this.idParada = new SimpleStringProperty(idParada);;
        this.nombreParada = new SimpleStringProperty(nombreParada);
        this.direccionParada = new SimpleStringProperty(direccionParada);
        this.tipoTransporte = new SimpleStringProperty(tipoTransporte);
        this.estadoParada = new SimpleStringProperty(estadoParada);
    }

    public StringProperty idParadaProperty() {
        return idParada;
    }

    public StringProperty nombreParadaProperty() {
        return nombreParada;
    }

    public StringProperty direccionParadaProperty() {
        return direccionParada;
    }

    public StringProperty tipoTransporteProperty() {
        return tipoTransporte;
    }

    public StringProperty estadoParadaProperty() {
        return estadoParada;
    }

  //  metodos estandar, Manipulan el valor String

    public String getIdParada() {
        //  Usa .get() para obtener el String
        return idParada.get();
    }

    public void setIdParada(String idParada) {
        this.idParada.set(idParada);
    }

    public String getNombreParada() {
        return nombreParada.get();
    }

    public void setNombreParada(String nombreParada) {
        this.nombreParada.set(nombreParada);
    }

    public String getDireccionParada() {
        return direccionParada.get();
    }

    public void setDireccionParada(String direccionParada) {
        this.direccionParada.set(direccionParada);
    }

    public String getTipoTransporte() {
        return tipoTransporte.get();
    }

    public void setTipoTransporte(String tipoTransporte) {
        this.tipoTransporte.set(tipoTransporte);
    }

    public String getEstadoParada() {
        return estadoParada.get();
    }

    public void setEstadoParada(String estadoParada) {
        this.estadoParada.set(estadoParada);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Parada parada = (Parada) o;
//        return Objects.equals(idParada, parada.idParada);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(idParada);
//    }
}