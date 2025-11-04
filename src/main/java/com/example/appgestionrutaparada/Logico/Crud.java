package com.example.appgestionrutaparada.Logico;
import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;


import java.util.LinkedList;
import java.util.List;

//Implementación del CRUD de PARADA y RUTA (Agregar, modificar y eliminar)
public class Crud {
    private List<Parada> paradas; //Nodos
    private List<List<Ruta>> rutas; //Aristas

    private static Crud instancia = null;

    public Crud() {
        paradas = new LinkedList<>();
        rutas = new LinkedList<>();
    }

    public List<Parada> getParada() {
        return paradas;
    }
    public List<List<Ruta>> getRuta() {
        return rutas;
    }

    public static Crud getInstancia() {
        if (instancia == null) {
            instancia = new Crud();
        }
        return instancia;
    }

    //Métodos de Parada
    //Objetivo:
    public boolean agregarParada(Parada p) {
        if (buscarIndexParada(p.getIdParada()) == -1) {
            paradas.add(p);
            rutas.add(new LinkedList<>());
            return true;
        }
        return false;
    }


    //Objetivo: Para verificar si ya existe la Parada, para eliminarla o modificarla
    public int buscarIndexParada(String idParada) {
        for (int i = 0; i < paradas.size(); i++) {
            if (paradas.get(i).getIdParada().equals(idParada)) {
                return i;
            }
        }
        return -1;
    }


    //Objetivo:
    public boolean eliminarParada(String idParada) {
        int index = buscarIndexParada(idParada);
        if (index != -1) {
            //Eliminar parada y su lista de rutas salientes (Que la tenían como origen)
            paradas.remove(index);
            rutas.remove(index);

            //Eliminar rutas que tenían a esta parada como destino
            for (List<Ruta> lista : rutas) {
                lista.removeIf(r -> r.getDestinoRuta().equals(idParada));
            }
            return true;
        }
        return false;
    }


    //Objetivo:
    public boolean modificarParada(String idParada, Parada nuevaParada) {
        int index = buscarIndexParada(idParada);
        if (index != -1) {
            paradas.set(index, nuevaParada);
            return true;
        }
        return false;
    }

    //Métodos de Ruta

    //Objetivo:
    public boolean agregarRuta(Ruta r){
        //Verificar antes si existen las Paradas antes de agregar/crear una Ruta
        int origen = buscarIndexParada(r.getOrigenRuta());
        int destino = buscarIndexParada(r.getDestinoRuta());

        if(origen != -1 && destino != -1) {
            List<Ruta> listaRutas = rutas.get(origen);
            if(buscarIndexRuta(listaRutas, r.getDestinoRuta()) == -1) {
                listaRutas.add(r);
                return true;
            }
        }
        return false; //Por si no existen las paradas
    }


    //Objetivo: Para verificar si ya existe la Ruta y para modificarla
    public int buscarIndexRuta(List<Ruta> listaRutas, String idDestinoRuta) {
        for (int i = 0; i < listaRutas.size(); i++) {
            if (listaRutas.get(i).getDestinoRuta().equals(idDestinoRuta)) {
                return i;
            }
        }
        return -1;
    }

    //Objetivo:
    public boolean eliminarRuta(String idOrigenParada, String idDestinoParada){
        int index = buscarIndexParada(idOrigenParada);
        if(index != -1){
            return rutas.get(index).removeIf(r -> r.getDestinoRuta().equals(idDestinoParada));
        }
        return false;
    }

    //Objetivo:
    public boolean modificarRuta(String idOrigenParada, String idDestinoParada, Ruta nuevaRuta){
        int index = buscarIndexParada(idOrigenParada);
        if(index != -1){
            List<Ruta> listaRutas = rutas.get(index);
            for(int i = 0; i < listaRutas.size(); i++) {
                if(listaRutas.get(i).getDestinoRuta().equals(idDestinoParada)) {
                    listaRutas.set(i, nuevaRuta);
                    return true;
                }
            }
        }
        return false;
    }
}