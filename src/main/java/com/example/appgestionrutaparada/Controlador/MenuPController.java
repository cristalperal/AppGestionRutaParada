package com.example.appgestionrutaparada.Controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Metodos para Abrir las ventanas de parada y ruta desde el menu principal
    @FXML
    public void AbrirGestionParada(ActionEvent actionEvent) {

        abrirNuevaVentana("GestionarParadaV.fxml", "Gestión de Paradas");
    }
    @FXML
    public void AbrirGestionRuta(ActionEvent actionEvent) {

        abrirNuevaVentana("GestionarRutaV.fxml", "Gestión de Rutas");
    }

    /**
      Método genérico para cargar y mostrar una nueva ventana de forma modal.
     */
    private void abrirNuevaVentana(String fxml, String titulo) {
        try {
            //  Cargar el FXML de la nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/appgestionrutaparada/vistas/" + fxml));
            Parent parent = fxmlLoader.load();
            // Crear un nueva ventana
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(parent));
            // bloquea la ventana principal hasta que se cierra
            stage.initModality(Modality.APPLICATION_MODAL);
            // Centrar la nueva ventana en la pantalla
            stage.centerOnScreen();
            // Mostrar la ventana
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            //  informar al usuario si falla la carga.
            System.err.println("Error al cargar la ventana FXML: " + fxml);
        }
    }


}
