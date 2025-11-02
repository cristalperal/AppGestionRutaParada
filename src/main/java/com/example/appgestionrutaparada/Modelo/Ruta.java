package com.example.appgestionrutaparada.Modelo;

import javafx.beans.property.*;

public class Ruta {
    private StringProperty idRuta;
    private StringProperty nombreRuta;
    private IntegerProperty distanciaRuta;
    private FloatProperty costoRuta; // FloatProperty es apropiado aquí
    private IntegerProperty cantidadTransbordo;
    private IntegerProperty tiempoViaje;
    private StringProperty origenRuta;
    private StringProperty destinoRuta;

    public Ruta(String idRuta, String nombreRuta, int distanciaRuta, float costoRuta, int cantidadTransbordo, int tiempoViaje, String origenRuta, String destinoRuta) {
        this.idRuta = new SimpleStringProperty(idRuta);
        this.nombreRuta = new SimpleStringProperty(nombreRuta);
        this.distanciaRuta = new SimpleIntegerProperty(distanciaRuta);
        this.costoRuta = new SimpleFloatProperty(costoRuta);
        this.cantidadTransbordo = new SimpleIntegerProperty(cantidadTransbordo);
        this.tiempoViaje = new SimpleIntegerProperty(tiempoViaje);
        this.origenRuta = new SimpleStringProperty(origenRuta);
        this.destinoRuta = new SimpleStringProperty(destinoRuta);
    }

    public StringProperty idRutaProperty() { return idRuta; }
    public StringProperty nombreRutaProperty() { return nombreRuta; }
    public IntegerProperty distanciaRutaProperty() { return distanciaRuta; }
    public FloatProperty costoRutaProperty() { return costoRuta; }
    public IntegerProperty cantidadTransbordoProperty() { return cantidadTransbordo; }
    public IntegerProperty tiempoViajeProperty() { return tiempoViaje; }
    public StringProperty origenRutaProperty() { return origenRuta; }
    public StringProperty destinoRutaProperty() { return destinoRuta; }


    public String getIdRuta() { return idRuta.get(); }
    public void setIdRuta(String idRuta) { this.idRuta.set(idRuta); }

    public String getNombreRuta() { return nombreRuta.get(); }
    public void setNombreRuta(String nombreRuta) { this.nombreRuta.set(nombreRuta); }

    public int getDistanciaRuta() { return distanciaRuta.get(); }
    public void setDistanciaRuta(int distanciaRuta) { this.distanciaRuta.set(distanciaRuta); }

    public float getCostoRuta() { return costoRuta.get(); }
    public void setCostoRuta(float costoRuta) { this.costoRuta.set(costoRuta); }

    public int getCantidadTransbordo() { return cantidadTransbordo.get(); }
    public void setCantidadTransbordo(int cantidadTransbordo) { this.cantidadTransbordo.set(cantidadTransbordo); }

    public int getTiempoViaje() { return tiempoViaje.get(); }
    public void setTiempoViaje(int tiempoViaje) { this.tiempoViaje.set(tiempoViaje); }

    public String getOrigenRuta() { return origenRuta.get(); }
    public void setOrigenRuta(String origenRuta) { this.origenRuta.set(origenRuta); }

    public String getDestinoRuta() { return destinoRuta.get(); }
    public void setDestinoRuta(String destinoRuta) { this.destinoRuta.set(destinoRuta); }
}