package com.example.appgestionrutaparada.Logico;

import com.example.appgestionrutaparada.DAO.ParadaDAO;
import com.example.appgestionrutaparada.DAO.RutaDAO;
import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;


import java.util.LinkedList;
import java.util.List;

//Implementación del CRUD de PARADA y RUTA (Agregar, modificar y eliminar)
public class Crud {
    private final ParadaDAO paradaDAO; // Nodos
    private final RutaDAO rutaDAO;// Aristas
    private static Crud instancia = null;

    public Crud() {
        paradaDAO = ParadaDAO.getInstance();
        rutaDAO = RutaDAO.getInstance();
    }

    public static Crud getInstancia() {
        if (instancia == null) {
            instancia = new Crud();
        }
        return instancia;
    }

    public List<Parada> getParada() {
        return paradaDAO.findAll();
    }

    public List<List<Ruta>> getRuta() {
        List<Parada> paradas = paradaDAO.findAll(); // Obtener todos los nodos
        List<List<Ruta>> listaDeAdyacencia = new LinkedList<>();

        for (Parada p : paradas) {
            // Obtener todas las rutas donde esta parada es el origen
            List<Ruta> rutasSalientes = rutaDAO.findByOrigen(p.getIdParada());
            listaDeAdyacencia.add(rutasSalientes);
        }
        return listaDeAdyacencia;
    }

    //Métodos de Parada
    //Objetivo: Agregar una parada a la lista
    public boolean agregarParada(Parada p) {
        if (paradaDAO.exists(p.getIdParada())) {
            return false;
        }
        paradaDAO.save(p);
        return true;
    }

    //Objetivo: Buscar si existe una parada para eliminarla y eliminar las rutas asociadas
    public boolean eliminarParada(String idParada) {
        if (paradaDAO.exists(idParada)) {
            // Eliminar Rutas que salen/llegan de esa parada
            rutaDAO.deleteRutasByParadaId(idParada);
            paradaDAO.delete(idParada);
            return true;
        }
        return false;
    }

    //Objetivo: Actualizar las paradas
    public boolean modificarParada(String idParada, Parada nuevaParada) {
        if (paradaDAO.exists(idParada)) {
            paradaDAO.update(nuevaParada);
            return true;
        }
        return false;
    }

    // Objetivo: Buscar el ID de una Parada a partir de su nombre
    public String buscarIdPorNombre(String nombreParada) {
        Parada p = paradaDAO.findByName(nombreParada);
        return (p != null) ? p.getIdParada() : null;
    }

    //Métodos de Ruta

    //Objetivo: Agregar una ruta a la lista, tambien se obtiene la parada de origen y destino
    public boolean agregarRuta(Ruta r) {
        if (!paradaDAO.exists(r.getOrigenRuta()) || !paradaDAO.exists(r.getDestinoRuta())) {
            return false; // No existen las paradas
        }
        if (rutaDAO.existsByOAndD(r.getOrigenRuta(), r.getDestinoRuta())) {
            return false;
        }
        rutaDAO.save(r);
        return true;
    }


    //Objetivo: Elimina una ruta por su index
    public boolean eliminarRuta(String idOrigenParada, String idDestinoParada) {
        // Verificar si la ruta existe usando el DAO
        if (rutaDAO.existsByOAndD(idOrigenParada, idDestinoParada)) {
            rutaDAO.delete(idOrigenParada, idDestinoParada);
            return true;
        }
        return false;
    }

    //Objetivo: Actualiza las Rutas por el index
    public boolean modificarRuta(String idOrigenParada, String idDestinoParada, Ruta nuevaRuta) {
        // Verificar si la ruta existe
        if (rutaDAO.existsByOAndD(idOrigenParada, idDestinoParada)) {
            rutaDAO.update(nuevaRuta);
            return true;
        }
        return false;
    }

    // Obtener el Grafo completo
    public Grafo obtenerGrafo() {
        Grafo grafo = new Grafo();
        grafo.setParada(this.getParada());
        grafo.setRuta(this.getRuta());
        return grafo;
    }


    // Métodos para la información dinámica del menú
    public int paradasActivas() {
        return paradaDAO.count();
    }

    public int rutasActivas() {
        return rutaDAO.count();
    }

    public int getMaxIdParada() {
        return paradaDAO.findMaxNumericId();
    }

    public int getMaxIdRuta() {
        return rutaDAO.findMaxNumericId();
    }
}