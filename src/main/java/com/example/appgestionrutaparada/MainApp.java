package com.example.appgestionrutaparada;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/com/example/appgestionrutaparada/vistas/menuPrincipalV.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            String imagePath = "/com/example/appgestionrutaparada/vistas/Imagenes/MapaRuta.png";
            Image applicationIcon = new Image(getClass().getResourceAsStream(imagePath));
            if (applicationIcon.isError()) {
                throw new IOException("La imagen del icono no se cargó. Verifica que 'logo.png' exista en la ruta: " + imagePath);
            }
            stage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: /Imagenes/logo.png");
            e.printStackTrace();
        }

        stage.setTitle("GeoParada - Sistema de Gestión de Rutas y Paradas");
        stage.setScene(scene);
        stage.show();
    }
}
