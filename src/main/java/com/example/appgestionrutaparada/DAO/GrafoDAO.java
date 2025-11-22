package com.example.appgestionrutaparada.DAO;

import com.example.appgestionrutaparada.Modelo.Grafo;
import com.example.appgestionrutaparada.Modelo.Parada;
import com.example.appgestionrutaparada.Modelo.Ruta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrafoDAO {
    private static GrafoDAO INSTANCE;

    private GrafoDAO() {
    }

    public static GrafoDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GrafoDAO();
        }
        return INSTANCE;
    }

    //Para guardar datos (Inserta grafo, grafo_parada y grafo_ruta). Si id vacío, genera UUID.
    public void save(Grafo grafo) {
        if (grafo.getIdGrafo() == null || grafo.getIdGrafo().isEmpty()) {
            grafo.setIdGrafo(UUID.randomUUID().toString());
        }

        String idGrafo = grafo.getIdGrafo();

        final String sqlInsertGrafo = "INSERT INTO grafo (idGrafo) VALUES (?)";
        final String sqlInsertParada = "INSERT INTO grafo_parada (idGrafo, nodo_index, idParada) VALUES (?, ?, ?)";
        final String sqlInsertRuta = "INSERT INTO grafo_ruta (idGrafo, nodo_index, posicion, idRuta) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBd.getConnection();
            conn.setAutoCommit(false);

            //Si ya existe, eliminar (Evita conflicto PK)
            if (exists(idGrafo)) {
                try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM grafo WHERE idGrafo = ?")) {
                    psDel.setString(1, idGrafo);
                    psDel.executeUpdate();
                }
            }

            //Insertar grafo
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertGrafo)) {
                ps.setString(1, idGrafo);
                ps.executeUpdate();
            }

            //Insertar nodos (Paradas)
            List<Parada> nodos = grafo.getParada();
            if (nodos != null && !nodos.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertParada)) {
                    for (int i = 0; i < nodos.size(); i++) {
                        Parada p = nodos.get(i);
                        if (p == null) continue;
                        ps.setString(1, idGrafo);
                        ps.setInt(2, i);
                        ps.setString(3, p.getIdParada());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            //Insertar listas de adyacencia (rutas)
            List<List<Ruta>> listas = grafo.getRuta();
            if (listas != null && !listas.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertRuta)) {
                    for (int nodoIndex = 0; nodoIndex < listas.size(); nodoIndex++) {
                        List<Ruta> ady = listas.get(nodoIndex);
                        if (ady == null) continue;
                        for (int pos = 0; pos < ady.size(); pos++) {
                            Ruta r = ady.get(pos);
                            if (r == null) continue;
                            ps.setString(1, idGrafo);
                            ps.setInt(2, nodoIndex);
                            ps.setInt(3, pos);
                            ps.setString(4, r.getIdRuta());
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    //Elimina un grafo completo
    public void delete(String idGrafo) {
        final String sql = "DELETE FROM grafo WHERE idGrafo = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idGrafo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Comprueba si existe un grafo con ese id
    public boolean exists(String idGrafo) {
        final String sql = "SELECT 1 FROM grafo WHERE idGrafo = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idGrafo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Recupera un grafo por idGrafo
    public Grafo findById(String idGrafo) {
        Grafo grafo = new Grafo(idGrafo);

        final String sqlParadas = "SELECT nodo_index, idParada FROM grafo_parada WHERE idGrafo = ? ORDER BY nodo_index";
        final String sqlRutas = "SELECT nodo_index, posicion, idRuta FROM grafo_ruta WHERE idGrafo = ? ORDER BY nodo_index, posicion";

        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement psPar = conn.prepareStatement(sqlParadas);
             PreparedStatement psRut = conn.prepareStatement(sqlRutas)) {

            //Paradas (nodos)
            psPar.setString(1, idGrafo);
            ResultSet rsPar = psPar.executeQuery();
            List<Parada> nodos = new ArrayList<>();
            int expected = 0;
            while (rsPar.next()) {
                int idx = rsPar.getInt("nodo_index");
                String idParada = rsPar.getString("idParada");
                while (expected < idx) {
                    nodos.add(null);
                    expected++;
                }
                Parada p = ParadaDAO.getInstance().findById(idParada);
                nodos.add(p);
                expected++;
            }
            grafo.setParada(nodos);

            //Rutas (listas de adyacencia)
            //Inicializar listas vacías según tamaño de nodos (mínimo 0)
            List<List<Ruta>> listas = new ArrayList<>();
            int size = Math.max(0, nodos.size());
            for (int i = 0; i < size; i++) listas.add(new ArrayList<>());

            psRut.setString(1, idGrafo);
            ResultSet rsRut = psRut.executeQuery();
            while (rsRut.next()) {
                int nodoIndex = rsRut.getInt("nodo_index");
                String idRuta = rsRut.getString("idRuta");
                Ruta r = RutaDAO.getInstance().findById(idRuta);

                // garantizar capacidad
                while (listas.size() <= nodoIndex) listas.add(new ArrayList<>());
                listas.get(nodoIndex).add(r);
            }
            grafo.setRuta(listas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grafo;
    }
}