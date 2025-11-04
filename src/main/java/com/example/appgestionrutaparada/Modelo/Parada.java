package com.example.appgestionrutaparada.Modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Parada {
    // StringProperty nos permite actualizar mas rapido las tablas
    private  StringProperty idParada;
    private StringProperty nombreParada;
    private StringProperty direccionParada;
    private StringProperty tipoTransporte;
    private StringProperty estadoParada; //Visitada o no visitada

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

}