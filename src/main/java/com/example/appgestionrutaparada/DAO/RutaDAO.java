package com.example.appgestionrutaparada.DAO;

import com.example.appgestionrutaparada.Modelo.Ruta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {
    private static RutaDAO INSTANCE;

    private RutaDAO() {
    }

    public static RutaDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RutaDAO();
        }
        return INSTANCE;
    }

    //Para guardar datos
    public void save(Ruta ruta) {
        final String sql = "INSERT INTO ruta " + "(idRuta, nombreRuta, distanciaRuta, costoRuta, cantidadTransbordo, tiempoViaje, origenRuta, destinoRuta) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ruta.getIdRuta());
            preparedStatement.setString(2, ruta.getNombreRuta());
            preparedStatement.setInt(3, ruta.getDistanciaRuta());
            preparedStatement.setFloat(4, ruta.getCostoRuta());
            preparedStatement.setInt(5, ruta.getCantidadTransbordo());
            preparedStatement.setInt(6, ruta.getTiempoViaje());
            preparedStatement.setString(7, ruta.getOrigenRuta());
            preparedStatement.setString(8, ruta.getDestinoRuta());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para actualizar un campo
    public void update(Ruta ruta) {
        final String sql = "UPDATE ruta SET nombreRuta = ?, distanciaRuta = ?, costoRuta = ?, " + "cantidadTransbordo = ?, tiempoViaje = ?, origenRuta = ?, destinoRuta = ? " + "WHERE idRuta = ?";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ruta.getNombreRuta());
            preparedStatement.setInt(2, ruta.getDistanciaRuta());
            preparedStatement.setFloat(3, ruta.getCostoRuta());
            preparedStatement.setInt(4, ruta.getCantidadTransbordo());
            preparedStatement.setInt(5, ruta.getTiempoViaje());
            preparedStatement.setString(6, ruta.getOrigenRuta());
            preparedStatement.setString(7, ruta.getDestinoRuta());
            preparedStatement.setString(8, ruta.getIdRuta());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para eliminar un campo
    public void delete(String idRuta) {
        final String sql = "DELETE FROM ruta WHERE idRuta = ?";

        try (Connection connection = ConexionBd.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, idRuta);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Para listar
    public List<Ruta> findAll() {
        List<Ruta> list = new ArrayList<>();

        final String sql = "SELECT * FROM ruta";

        try (Connection connection = ConexionBd.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Ruta ruta = new Ruta(
                        resultSet.getString("idRuta"),
                        resultSet.getString("nombreRuta"),
                        resultSet.getInt("distanciaRuta"),
                        resultSet.getFloat("costoRuta"),
                        resultSet.getInt("cantidadTransbordo"),
                        resultSet.getInt("tiempoViaje"),
                        resultSet.getString("origenRuta"),
                        resultSet.getString("destinoRuta")
                );
                list.add(ruta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Para verificar existencia
    public boolean exists(String idRuta) {
        final String sql = "SELECT 1 FROM ruta WHERE idRuta = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idRuta);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Ruta findById(String idRuta) {
        final String sql = "SELECT * FROM ruta WHERE idRuta = ?";
        try (Connection conn = ConexionBd.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idRuta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Ruta(
                        rs.getString("idRuta"),
                        rs.getString("nombreRuta"),
                        rs.getInt("distanciaRuta"),
                        rs.getFloat("costoRuta"),
                        rs.getInt("cantidadTransbordo"),
                        rs.getInt("tiempoViaje"),
                        rs.getString("origenRuta"),
                        rs.getString("destinoRuta")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}