package application;

import java.io.BufferedWriter;

import Controller.Mycontroller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	public static Parent root;
	@Override
	public void start(Stage primaryStage) {
		try {
			 root = FXMLLoader.load(getClass().getResource("Front.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			final ProgressBar progressBar = new ProgressBar(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		launch(args);

	}

}
