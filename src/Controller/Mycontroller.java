/*
 * @author Prabhu Ram
 * @Date   June 2016
 */
package Controller;

import java.io.BufferedReader;

import application.BasicAuth;
import application.Main;
import application.WebExtract;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import sun.audio.*;

import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import application.Main;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Mycontroller implements Initializable {

	@FXML
	TextArea content, tree, pair;

	@FXML
	Label status;

	@FXML
	Button fetch, btnOpenDirectoryChooser;

	@FXML
	ProgressBar progressBar;

	@FXML
	ProgressIndicator progressIndicator;

	@FXML
	TextField dirLocation, url, userName, password;

	@FXML
	TextArea wordsFromWeb, wordsNotInJSON;

	@FXML
	RadioButton rd1, rd2;

	String tweet;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userName.setText("spain45@mailinator.com");
		password.setText("copart123");
		url.setText("https://g-member-qa2.copart.in/");

		ToggleGroup group = new ToggleGroup();

		rd1.setToggleGroup(group);
		rd1.setSelected(true);

		rd2.setToggleGroup(group);

		btnOpenDirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File selectedDirectory = directoryChooser.showDialog(Main.root.getScene().getWindow());

				if (selectedDirectory == null) {
					dirLocation.setText("No Directory selected");
				} else {
					dirLocation.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

		// progressBar.setProgress(.1);
		// progressIndicator.setProgress(.1);

		dirLocation.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!newValue.equals("")) {
				System.out.println("changing");
				Platform.runLater(() -> {
					// update UI here....
					progressBar.setProgress(.1);
					progressIndicator.setProgress(.1);
					status.setText("Click Fetch");

				});
			} else {
				Platform.runLater(() -> {
					// update UI here....
					progressBar.setProgress(0);
					progressIndicator.setProgress(0);
					status.setText("Update Directory");
				});
			}
		});

	}

	public void performWebServiceCall() {
		String JSON = BasicAuth.reloadTomcatWebApplication("cobalt",
				"38JW0fMi9iXVJ0sm14n04v7BC8EBkm9P4L5gfctJYnO4vybQmO",
				"http://g-services-qa1.copart.com/reference-ws/reference/makes?view=VehicleFinder&category=ALL", true);

	}

	public void createTask() {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						String language = "";
						if (rd1.isSelected()) {
							language = rd1.getText();
						}
						if (rd2.isSelected()) {
							language = rd2.getText();
						}
						// update ProgressIndicator on FX thread
						Platform.runLater(new Runnable() {

							public void run() {

								Platform.runLater(() -> {
									// update UI here....
									progressIndicator.setProgress(.3);
									progressBar.setProgress(.3);
									status.setText("Crawling launched");
								});
							}
						});

						WebExtract we = new WebExtract();

						Platform.runLater(new Runnable() {

							public void run() {

								Platform.runLater(() -> {
									// update UI here....
									progressIndicator.setProgress(.4);
									progressBar.setProgress(.4);
									status.setText("Validating OS");
								});
							}
						});
						WebExtract.osValidator();

						Platform.runLater(new Runnable() {

							public void run() {

								Platform.runLater(() -> {
									// update UI here....
									progressIndicator.setProgress(.5);
									progressBar.setProgress(.5);
									status.setText("Getting Negation List");
								});
							}
						});

						WebExtract.getDropDown(url.getText());

						Platform.runLater(() -> {
							// update UI here....
							progressIndicator.setProgress(.7);
							progressBar.setProgress(.7);
							status.setText("Parsing Smart Link");
						});
						we.smartLinkParsing(dirLocation.getText(),language);

						Platform.runLater(new Runnable() {

							public void run() {

								Platform.runLater(() -> {
									// update UI here....
									progressIndicator.setProgress(.85);
									progressBar.setProgress(.85);
									status.setText("Extracting from web");
								});
							}
						});
						wordsNotInJSON.setText(we.getWebContent(url.getText(), dirLocation.getText(),
								userName.getText(), password.getText(),language).replaceAll(",", "\n"));
						wordsFromWeb.setText(we.webWords.toString().replaceAll(",", "\n"));
						// progressBar.setProgress(1);
						// we.smartLinkParsing(dirLocation.getText());
						// we.osValidator();
						Platform.runLater(() -> {
							// update UI here....
							progressIndicator.setProgress(1);
							progressBar.setProgress(1);
							status.setText("Complete");
						});

						return null;
					}
				};
			}
		};
		service.start();
	}

	public void backGround() {
		new Thread() {

			// runnable for that thread
			public void run() {

				// update ProgressIndicator on FX thread
				Platform.runLater(new Runnable() {

					public void run() {

						Platform.runLater(() -> {
							// update UI here....
							progressIndicator.setProgress(.5);
							progressBar.setProgress(.5);
						});

						WebExtract we = new WebExtract();

						WebExtract.osValidator();
						WebExtract.getDropDown(url.getText());

						we.smartLinkParsing(dirLocation.getText(),"");

						Platform.runLater(() -> {
							// update UI here....
							progressIndicator.setProgress(.7);
							progressBar.setProgress(.7);
						});

						try {
							wordsNotInJSON.setText(we.getWebContent(url.getText(), dirLocation.getText(),
									userName.getText(), password.getText(),"").replaceAll(",", "\n"));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						wordsFromWeb.setText(we.webWords.toString().replaceAll(",", "\n"));
						// progressBar.setProgress(1);
						// we.smartLinkParsing(dirLocation.getText());
						// we.osValidator();
						Platform.runLater(() -> {
							// update UI here....
							progressIndicator.setProgress(1);
							progressBar.setProgress(1);
						});

					}
				});

			}
		}.start();
	}

	public void parse() throws InterruptedException {
		createTask();
	}

}
