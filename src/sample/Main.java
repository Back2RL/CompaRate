package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Time;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
//		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//		primaryStage.setTitle("Hello World");
//		Scene scene = new Scene(root, 300, 275);
//		primaryStage.setScene(scene);
//		primaryStage.show();

		Image image;
		ImageView imageView = null;
		String URL = "/home/leonard/Bilder/images4.jpeg";
		try {
			image = new Image(URL, true);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			try (InputStream inputStream = new FileInputStream(URL);
				 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
				image = new Image(bufferedInputStream);
			}
		}
		imageView = new ImageView(image);
		imageView.setFitWidth(100);
		imageView.setPreserveRatio(true);

		Group root = new Group();
		Scene scene = new Scene(root);
		scene.setFill(Color.BLACK);
		HBox box = new HBox();
		box.getChildren().add(imageView);

		primaryStage.setTitle("ImageView");
		primaryStage.setWidth(415);
		primaryStage.setHeight(200);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();


		logic.Controller.testController();
	}


	public static void main(String[] args) {
		System.out.println(Time.get_UTC_now());
		Scanner console = new Scanner(System.in);
		do {
			System.out.println("Start Analysis? (true/false):");
		} while (!console.hasNextBoolean());
		if (!console.nextBoolean()) {
			console.close();
			System.exit(0);
		}
		console.close();
		launch(args);
	}
}
