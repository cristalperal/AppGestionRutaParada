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
        cargarDatosIniciales();
    }

    //Objetivo: cargar datos para probar la app
    private void cargarDatosIniciales() {

        // Creando las paradas (nodos)
        List<Parada> paradasIniciales = List.of(
                new Parada("P001", "Terminal Principal", "Av. Central, #100", "Autobus", "No Visitada"),
                new Parada("P002", "Parque Industrial", "C/ Norte, Esq. A", "Carro", " No Visitada"),
                new Parada("P003", "Centro Comercial", "Bulevar 50", "Autobus", " No Visitada"),
                new Parada("P004", "Estación Tren", "C/ Ferrocarril", "Autobus", "NO Visitada"),
                new Parada("P005", "Universidad APEC", "Zona Universitaria", "Motocicleta", "No Visitada"),
                new Parada("P006", "Playa Dorada", "Ctra. Costera", "Autobus", "No Visitada")
        );

        for (Parada p : paradasIniciales) {
            agregarParada(p); // Agregan las paradas
        }

        agregarRuta(new Ruta("R001", "Ruta A", 10, 50.0f, 1, 15, "P001", "P002"));
        agregarRuta(new Ruta("R002", "Ruta B", 5, 25.0f, 0, 8, "P002", "P003"));
        agregarRuta(new Ruta("R003", "Ruta C", 25, 120.0f, 2, 35, "P001", "P004"));
        agregarRuta(new Ruta("R004", "Ruta D", 7, 40.0f, 0, 10, "P003", "P005"));
        agregarRuta(new Ruta("R005", "Ruta E", 12, 60.0f, 3, 20, "P004", "P005"));
        agregarRuta(new Ruta("R006", "Ruta F", 30, 150.0f, 1, 45, "P005", "P006"));
        agregarRuta(new Ruta("R007", "Ruta G", 15, 80.0f, 0, 20, "P001", "P003"));

    }

    public List<Parada> getParada() {

        return paradas;
    }

    public List<List<Ruta>> getRuta() {

        return rutas;
    }

    //Patron singleton
    public static Crud getInstancia() {
        if (instancia == null) {
            instancia = new Crud();
        }
        return instancia;
    }

    //Métodos de Parada
    //Objetivo: Agregar una parada a la lista
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


    //Objetivo: Buscar si existe una parada para eliminarla y eliminar las rutas asociadas
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


    //Objetivo: Actualizar las paradas
    public boolean modificarParada(String idParada, Parada nuevaParada) {
        int index = buscarIndexParada(idParada);
        if (index != -1) {
            paradas.set(index, nuevaParada);
            return true;
        }
        return false;
    }

    // Objetivo: Buscar el ID de una Parada a partir de su nombre
    public String buscarIdPorNombre(String nombreParada) {
        for (Parada p : paradas) {
            if (p.getNombreParada().equals(nombreParada)) {
                return p.getIdParada();
            }
        }
        return null; // Si no se encuentra
    }

    //Métodos de Ruta

    //Objetivo: Agregar una ruta a la lista, tambien se obtiene la parada de origen y destino
    public boolean agregarRuta(Ruta r) {
        //Verificar antes si existen las Paradas antes de agregar/crear una Ruta
        int origen = buscarIndexParada(r.getOrigenRuta());
        int destino = buscarIndexParada(r.getDestinoRuta());

        if (origen != -1 && destino != -1) {
            List<Ruta> listaRutas = rutas.get(origen);
            if (buscarIndexRuta(listaRutas, r.getDestinoRuta()) == -1) {
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

    //Objetivo: Elimina una ruta por su index
    public boolean eliminarRuta(String idOrigenParada, String idDestinoParada) {
        int index = buscarIndexParada(idOrigenParada);
        if (index != -1) {
            return rutas.get(index).removeIf(r -> r.getDestinoRuta().equals(idDestinoParada));
        }
        return false;
    }

    //Objetivo: Actualiza las Rutas por el index
    public boolean modificarRuta(String idOrigenParada, String idDestinoParada, Ruta nuevaRuta) {
        int index = buscarIndexParada(idOrigenParada);
        if (index != -1) {
            List<Ruta> listaRutas = rutas.get(index);
            for (int i = 0; i < listaRutas.size(); i++) {
                if (listaRutas.get(i).getDestinoRuta().equals(idDestinoParada)) {
                    listaRutas.set(i, nuevaRuta);
                    return true;
                }
            }
        }
        return false;
    }

    // Obtener el Grafo completo
    public Grafo obtenerGrafo() {
        Grafo grafo = new Grafo();
        grafo.setParada(this.paradas);
        grafo.setRuta(this.rutas);
        return grafo;
    }


    // Métodos para la información dinámica del menú
    public int paradasActivas() {
        return paradas.size();
    }

    public int rutasActivas() {
        return rutas.size();
    }
}