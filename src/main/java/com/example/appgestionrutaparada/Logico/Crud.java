package com.example.appgestionrutaparada.Logico;

import com.example.appgestionrutaparada.DAO.ParadaDAO;
import com.example.appgestionrutaparada.DAO.RutaDAO;
import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;


import java.util.LinkedList;
import java.util.List;

//Implementación del CRUD de parada y ruta
public class Crud {
    private final ParadaDAO paradaDAO; // Nodos
    private final RutaDAO rutaDAO;// Aristas
    private static Crud instancia = null;

    public Crud() {
        paradaDAO = ParadaDAO.getInstance();
        rutaDAO = RutaDAO.getInstance();
    }

    //Objetivo: instancia del crud
    public static Crud getInstancia() {
        if (instancia == null) {
            instancia = new Crud();
        }
        return instancia;
    }

    //Objetivo: Obtener todas las paradas
    public List<Parada> getParada() {
        return paradaDAO.findAll();
    }

    //Objetivo: Obtener todas las rutas
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

    //------ MÉTODOS DE PARADA ---------

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
        Parada p = paradaDAO.findByNameP(nombreParada);
        return (p != null) ? p.getIdParada() : null;
    }

    // Objetivo: Buscar una parada por Nombre y devolver el objeto Parada completo
    public Parada buscarParadaPorNombre(String nombreParada) {
        return paradaDAO.findByNameP(nombreParada);
    }

    // Devolver una parada por su id
    public Parada buscarParadaPorId(String idParada) {
        return paradaDAO.findById(idParada);
    }

    // ------- MÉTODOS DE RUTA --------

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

    // Objetivo: Busca una ruta específica dado su origen y destino - Floy
    public Ruta buscarRuta(String idOrigen, String idDestino) {
        //  Obtener la lista de paradas para encontrar el índice del origen
        List<Parada> paradas = paradaDAO.findAll();
        int indexOrigen = -1;
        for (int i = 0; i < paradas.size(); i++) {
            if (paradas.get(i).getIdParada().equals(idOrigen)) {
                indexOrigen = i;
                break;
            }
        }
        if (indexOrigen != -1) {
            // Obtener la lista de rutas salientes de ese origen
            List<Ruta> rutasSalientes = rutaDAO.findByOrigen(idOrigen);
            //Buscar la ruta cuyo destino coincida
            for (Ruta r : rutasSalientes) {
                if (r.getDestinoRuta().equals(idDestino)) {
                    return r;
                }
            }
        }
        return null;
    }

    // Objetivo: Buscar una ruta por Nombre y devolver el objeto ruta completo
    public Ruta buscarRutaPorNombre(String nombreRuta) {
        return rutaDAO.findByNameR(nombreRuta);
    }

    // ------ MÉTODOS DE GRAFO ------

    // Obtener el Grafo completo
    public Grafo obtenerGrafo() {
        Grafo grafo = new Grafo();
        grafo.setParada(this.getParada());
        grafo.setRuta(this.getRuta());
        return grafo;
    }

    // ---- MÉTODOS DE UTILIDADES -----

    // Métodos para la información dinámica del menú
    public int paradasActivas() {
        return paradaDAO.count();
    }

    public int rutasActivas() {
        return rutaDAO.count();
    }

    // Métodos para encontrar el max de elementos, para generar los ids
    public int getMaxIdParada() {
        return paradaDAO.findMaxNumericId();
    }

    public int getMaxIdRuta() {
        return rutaDAO.findMaxNumericId();
    }
}