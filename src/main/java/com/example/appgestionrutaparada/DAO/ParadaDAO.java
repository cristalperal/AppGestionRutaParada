package com.example.appgestionrutaparada.DAO;

import com.example.appgestionrutaparada.Modelo.Parada;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParadaDAO {
    private static ParadaDAO INSTANCE;

    private ParadaDAO() {
    }

    public static ParadaDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ParadaDAO();
        }
        return INSTANCE;
    }

    //Para guardar datos
    public void save(Parada parada) {
        final String sql = "INSERT INTO parada (idParada, nombreParada, direccionParada, tipoTransporte, estadoParada) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parada.getIdParada());
            preparedStatement.setString(2, parada.getNombreParada());
            preparedStatement.setString(3, parada.getDireccionParada());
            preparedStatement.setString(4, parada.getTipoTransporte());
            preparedStatement.setString(5, parada.getEstadoParada());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para actualizar un campo
    public void update(Parada parada) {
        final String sql = "UPDATE parada SET nombreParada = ?, direccionParada = ?, tipoTransporte = ?, estadoParada = ? WHERE idParada = ?";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parada.getNombreParada());
            preparedStatement.setString(2, parada.getDireccionParada());
            preparedStatement.setString(3, parada.getTipoTransporte());
            preparedStatement.setString(4, parada.getEstadoParada());
            preparedStatement.setString(5, parada.getIdParada());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para eliminar un campo
    public void delete(String idParada) {
        final String sql = "DELETE FROM parada WHERE idParada = ?";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, idParada);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para listar
    public List<Parada> findAll() {
        List<Parada> list = new ArrayList<>();

        final String sql = "SELECT * FROM parada";

        try (Connection connection = ConexionBd.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Parada parada = new Parada(
                        resultSet.getString("idParada"),
                        resultSet.getString("nombreParada"),
                        resultSet.getString("direccionParada"),
                        resultSet.getString("tipoTransporte"),
                        resultSet.getString("estadoParada")
                );
                list.add(parada);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Para verificar existencia
    public boolean exists(String idParada) {
        final String sql = "SELECT 1 FROM parada WHERE idParada = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idParada);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Busca la parada mediante su ID
    public Parada findById(String idParada) {
        final String sql = "SELECT * FROM parada WHERE idParada = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idParada);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Parada(
                        rs.getString("idParada"),
                        rs.getString("nombreParada"),
                        rs.getString("direccionParada"),
                        rs.getString("tipoTransporte"),
                        rs.getString("estadoParada")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retorna el nombre de la parada para poder utilizarla en calcular la ruta
    public Parada findByNameP(String nombreParada) {
        final String sql = "SELECT * FROM parada WHERE nombreParada = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreParada);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Si se encuentra, retorna el objeto Parada
                return new Parada(
                        rs.getString("idParada"),
                        rs.getString("nombreParada"),
                        rs.getString("direccionParada"),
                        rs.getString("tipoTransporte"),
                        rs.getString("estadoParada")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cuenta cuantos elementos actuales hay en la tabla de parada
    public int count() {
        final String sql = "SELECT COUNT(*) AS total FROM parada";
        try (Connection conn = ConexionBd.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Busca los últimos dígitos de los Id para asi seguir con el siguiente y mantener la estética
    public int findMaxNumericId() {
       //lo convierte a entero para encontrar el máximo.
        final String sql = "SELECT MAX(CAST(SUBSTRING(idParada, 2) AS INTEGER)) AS max_id FROM parada";
        int maxId = 0;

        try (Connection conn = ConexionBd.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                // Si hay datos, retorna el ID máximo.
                maxId = rs.getInt("max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }
}
