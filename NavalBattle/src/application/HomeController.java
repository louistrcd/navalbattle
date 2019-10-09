package application;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import application.HomeController.Tupple;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class HomeController implements Initializable{

	@FXML
	private GridPane gridPane;
	@FXML
	private GridPane gridPaneShot;
	
	private List<Tupple> listCasesBoatsSelected;
	private List<Tupple> listCasesBoatComputer;
	private List<Tupple> listAttemptUser;
	private List<Tupple> listAttemptComputer;
	MediaPlayer mediaPlayer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(gridPane!=null) {
			gridPane.setGridLinesVisible(true);
			gridPaneShot.setGridLinesVisible(true);
			for(int i = 0; i<10 ; i++) {
				for(int j = 0 ; j<10 ; j++) {
					Pane p1 = new Pane();
					Pane p2 = new Pane();
					gridPane.add(p1, i, j);
					gridPaneShot.add(p2, i, j);
				}
			}
		}
		initBoats();
		addGridEvent();
	}
	
	public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
	    Node result = null;
	    ObservableList<Node> childrens = gridPane.getChildren();
	    for (Node node : childrens) {
	    	Integer columnIndex = GridPane.getColumnIndex(node);
	        if(columnIndex != null && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
	            result = node;
	            break;
	        }
	    }
	    return result;
	}
	
    private void addGridEvent() {
        gridPaneShot.getChildren().forEach(item -> {
//            item.setOnMousePressed(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                	item.setStyle("-fx-background-color : gray;");
////                    if (event.getClickCount() == 1) {
////                        System.out.println("doubleClick");
////                    }
//                }
//            });
            
            item.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                	int row = GridPane.getRowIndex(item);
                	int col = GridPane.getColumnIndex(item);
                	Tupple tupple = new Tupple(row, col);
                	String successShot = "-fx-border-color : red;  -fx-border-width : 4";
                	String missShot = "-fx-border-color : yellow;  -fx-border-width : 4";
                	if(!listAttemptUser.contains(tupple) && !listAttemptUser.containsAll(listCasesBoatComputer)) {
                		listAttemptUser.add(tupple);
                		if(listCasesBoatComputer.contains(tupple)) {
                    		item.setStyle(successShot);
                    		playMusic("explosion.mp3", 0.7);
                    	}else {
                    		item.setStyle(missShot);
                    		playMusic("miss.mp3", 1.2);
//                    		String musicFile = "src/application/miss.mp3";     // For example
//                    		Media sound = new Media(new File(musicFile).toURI().toString());
//                    		MediaPlayer mediaPlayer = new MediaPlayer(sound);
//                    		mediaPlayer.setStartTime(Duration.seconds(1));
//                    		System.out.println(mediaPlayer.cycleDurationProperty());
//                    		mediaPlayer.play();
                    	}
                		testVictory();
                	}
                	
                	
                }
            });

        });
    }
    
    public void computerAttack(double pauseDuration) {
    	PauseTransition pause = new PauseTransition(
                Duration.seconds(pauseDuration)
        ); 
    	pause.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
		    	Random r = new Random();
				int row = r.nextInt(10);
				int col = r.nextInt(10);
				String successShot = "-fx-border-color : red; -fx-background-color : blue;  -fx-border-width : 4";
				String missShot = "-fx-border-color : yellow;  -fx-border-width : 4";
				Tupple tupple = new Tupple(row, col);
				if(!listAttemptComputer.contains(tupple)) {
					listAttemptComputer.add(tupple);
					if(listCasesBoatsSelected.contains(tupple)) {
						getNodeByRowColumnIndex(row, col, gridPane).setStyle(successShot);
						playMusic("explosion.mp3", 0.7);
					}else {
						getNodeByRowColumnIndex(row, col, gridPane).setStyle(missShot);
						playMusic("miss.mp3", 1.2);
					}
				}else {
					computerAttack(0);
				}
				if(listAttemptComputer.containsAll(listCasesBoatsSelected)) {
					System.out.println("Sorry you loose..");
				}
			}
		});
    	pause.play();
    }
    
    public boolean testVictory() {
    	boolean b = false;
    	if(listAttemptUser.containsAll(listCasesBoatComputer)) {
    		System.out.println("Congratulations you won!!!");
    		b = true;
    	}else {
    		computerAttack(0.9);
    	}
    	return b;
    }
    
    public void newGame() {
    	playMusic("newgame.mp3", 0.2);
    	for(Node node : gridPane.getChildren()) {
    		Integer columnIndex = GridPane.getColumnIndex(node);
    		if(columnIndex!=null) {
        		Pane p = (Pane) node;
        		p.setStyle(null);
    		}
    	}
    	
    	for(Node node : gridPaneShot.getChildren()) {
    		Integer columnIndex = GridPane.getColumnIndex(node);
    		if(columnIndex!=null) {
        		Pane p = (Pane) node;
        		p.setStyle(null);
    		}
    	} 
    	initBoats();
    }
    
    public void playMusic(String music, double startTime){
        String bip = "src/application/" + music;
        Media hit = new Media(Paths.get(bip).toUri().toString());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setStartTime(Duration.seconds(startTime));
        mediaPlayer.play();
    }
    
    public void initBoats() {
    	listCasesBoatsSelected = new ArrayList<Tupple>();
    	listCasesBoatComputer = new ArrayList<Tupple>();
    	listAttemptUser = new ArrayList();
    	listAttemptComputer = new ArrayList();
    	initBoat(6, listCasesBoatsSelected);
    	initBoat(4, listCasesBoatsSelected);
    	initBoat(2, listCasesBoatsSelected);
    	
    	initBoatComputer(6, listCasesBoatComputer);
    	initBoatComputer(4, listCasesBoatComputer);
    	initBoatComputer(2, listCasesBoatComputer);

    }
    
    public void initBoatComputer(int size, List<Tupple> list) {
    	int boatSize = size;
    	String boatStyle = "-fx-background-color : blue;";
    	Random r = new Random();
    	int orientation = r.nextInt(2); //0 = horizontal, 1 = vertical
		int rowStart = r.nextInt(10);
		int colStart = r.nextInt(10);
		//System.out.println( orientation + " " + rowStart + " " + colStart + "\n");
		if(orientation == 0) {
			if(colStart>5) {
				for(int i = colStart - boatSize; i<colStart ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple);
						}
				}
			}else if(colStart<5){
				for(int i = colStart; i<colStart + boatSize ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple); 
						}
				}
			}else {
				for(int i = 3; i< 3 +boatSize ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple);
						}
				}
			}
		}else if(orientation == 1) {
			if(rowStart<5) {
				for(int i = rowStart ; i<rowStart + boatSize; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple);
						}
				}
			}else if(rowStart>5) {
				for(int i = rowStart - boatSize ; i<rowStart ; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple); 
						}
				}
			} else {
				for(int i = 3 ; i< 3 + boatSize ; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple);  
						}
				}
			}

		}
    }
    
    public void initBoat(int size, List<Tupple> list) {
    	int boatSize = size;
    	String boatStyle = "-fx-background-color : blue;";
    	Random r = new Random();
    	int orientation = r.nextInt(2); //0 = horizontal, 1 = vertical
		int rowStart = r.nextInt(10);
		int colStart = r.nextInt(10);
		//System.out.println( orientation + " " + rowStart + " " + colStart + "\n");
		if(orientation == 0) {
			if(colStart>5) {
				for(int i = colStart - boatSize; i<colStart ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple);
						getNodeByRowColumnIndex(rowStart, i, gridPane).setStyle(boatStyle);
						}
				}
			}else if(colStart<5){
				for(int i = colStart; i<colStart + boatSize ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple); 
						getNodeByRowColumnIndex(rowStart, i, gridPane).setStyle(boatStyle);
						}
				}
			}else {
				for(int i = 3; i< 3 +boatSize ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!list.contains(tupple)) {
						list.add(tupple);
						getNodeByRowColumnIndex(rowStart, i, gridPane).setStyle(boatStyle);
						}
				}
			}
		}else if(orientation == 1) {
			if(rowStart<5) {
				for(int i = rowStart ; i<rowStart + boatSize; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple);
						getNodeByRowColumnIndex(i, colStart, gridPane).setStyle(boatStyle);
						}
				}
			}else if(rowStart>5) {
				for(int i = rowStart - boatSize ; i<rowStart ; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple); 
						getNodeByRowColumnIndex(i, colStart, gridPane).setStyle(boatStyle);
						}
				}
			} else {
				for(int i = 3 ; i< 3 + boatSize ; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!list.contains(tupple)) {
						list.add(tupple);  
						getNodeByRowColumnIndex(i, colStart, gridPane).setStyle(boatStyle);
						}
				}
			}

		}
    }
    
    public static class Tupple{
    	int row;
		int col;
		
    	public Tupple(int row, int col) {
    		this.row = row;
    		this.col = col;
    	}
    	
    	public int getRow() {
			return row;
		}
		public void setRow(int row) {
			this.row = row;
		}
		public int getCol() {
			return col;
		}
		public void setCol(int col) {
			this.col = col;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + row;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tupple other = (Tupple) obj;
			if (col != other.col)
				return false;
			if (row != other.row)
				return false;
			return true;
		}
		
		public void show() {
			System.out.println("( " + row + " , " + col +" )");
		}
    	
    
    }

	

}