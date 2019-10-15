package application;
	
import java.io.FileInputStream;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;




public class Main extends Application {
	static MediaPlayer mediaPlayer;
	static MediaPlayer mediaPlayerSong;
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("Home.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("BattleShip");
			try {
	            primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("logo.png")));
			}catch(Exception e) {
				e.printStackTrace();
			}
			primaryStage.show();
			playSoundtrack("soundtrack.mp3");
			//playMusic("welcome.mp3", 2.3);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void playSoundtrack(String music){
        String bip = "src/application/" + music;
        Media hit = new Media(Paths.get(bip).toUri().toString());
        mediaPlayerSong = new MediaPlayer(hit);
        
        mediaPlayerSong.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayerSong.play();
    }
	
    public static void playMusic(String music, double startTime){
        String bip = "src/application/" + music;
        Media hit = new Media(Paths.get(bip).toUri().toString());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setStartTime(Duration.seconds(startTime));
        mediaPlayer.play();
    }
	
	public static void main(String[] args) {
		
		launch(args);
	}
}