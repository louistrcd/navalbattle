package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
import javafx.util.Duration;
import application.Main;

public class HomeController implements Initializable {

	@FXML
	private GridPane gridPane;
	@FXML
	private GridPane gridPaneShot;

	private List<Tupple> listCasesBoatsSelected;
	private List<Tupple> listCasesBoatComputer;
	private List<Tupple> listAttemptUser;
	private List<Tupple> listAttemptComputer;
	private List<List<Tupple>> listBoatUser;
	private List<List<Tupple>> listBoatComputer;
	
	
	String successShot = "-fx-border-color : red;  -fx-border-width : 4";
	String missShot = "-fx-border-color : yellow;  -fx-border-width : 4";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (gridPane != null) {
			gridPane.setGridLinesVisible(true);
			gridPaneShot.setGridLinesVisible(true);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
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

	public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
		Node result = null;
		ObservableList<Node> childrens = gridPane.getChildren();
		for (Node node : childrens) {
			Integer columnIndex = GridPane.getColumnIndex(node);
			if (columnIndex != null && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
				result = node;
				break;
			}
		}
		return result;
	}

	
	
	private void addGridEvent() {
		gridPaneShot.getChildren().forEach(item -> {
			item.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					int row = GridPane.getRowIndex(item);
					int col = GridPane.getColumnIndex(item);
					Tupple tupple = new Tupple(row, col);
					if (!listAttemptUser.contains(tupple) && !testVictoryOrLoose()) {
						listAttemptUser.add(tupple);
						if (listCasesBoatComputer.contains(tupple)) {
							item.setStyle(successShot);
							checkBoatDestruction();
							Main.playMusic("explosion.mp3", 0.7);
						} else {
							item.setStyle(missShot);
							Main.playMusic("miss.mp3", 1.2);
						}
						if (!testVictoryOrLoose()) {
							computerAttack(1);
						}
					}
				}
			});

		});
	}

	public void computerAttack(double duration) {
		PauseTransition pause = new PauseTransition(Duration.seconds(duration));
		pause.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Random r = new Random();
				int row = r.nextInt(10);
				int col = r.nextInt(10);
				String successShot = "-fx-border-color : red; -fx-background-color : blue;  -fx-border-width : 4";
				String missShot = "-fx-border-color : yellow;  -fx-border-width : 4";
				Tupple tupple = new Tupple(row, col);
				if (!listAttemptComputer.contains(tupple)) {
					listAttemptComputer.add(tupple);
					if (listCasesBoatsSelected.contains(tupple)) {
						getNodeByRowColumnIndex(row, col, gridPane).setStyle(successShot);
						checkBoatDestruction();
						Main.playMusic("explosion.mp3", 0.7);
					} else {
						getNodeByRowColumnIndex(row, col, gridPane).setStyle(missShot);
						Main.playMusic("miss.mp3", 1.2);
					}
				} else {
					computerAttack(0);
				}
				testVictoryOrLoose();
			}
		});
		pause.play();

	}

	public boolean testVictoryOrLoose() {
		boolean b = false;
		if (listAttemptUser.containsAll(listCasesBoatComputer)) {
			System.out.println("Congratulations you won!!!");
			b = true;
		} else if (listAttemptComputer.containsAll(listCasesBoatsSelected)) {
			System.out.println("Sorry you loose..");
			b = true;
		}
		return b;
	}

	public void newGame() {
		Main.playMusic("newgame.mp3", 0.2);
		for (Node node : gridPane.getChildren()) {
			Integer columnIndex = GridPane.getColumnIndex(node);
			if (columnIndex != null) {
				Pane p = (Pane) node;
				p.setStyle(null);
			}
		}

		for (Node node : gridPaneShot.getChildren()) {
			Integer columnIndex = GridPane.getColumnIndex(node);
			if (columnIndex != null) {
				Pane p = (Pane) node;
				p.setStyle(null);
			}
		}
		initBoats();
	}

	public void initBoats() {
		listCasesBoatsSelected = new ArrayList<Tupple>();
		listCasesBoatComputer = new ArrayList<Tupple>();
		listAttemptUser = new ArrayList<Tupple>();
		listAttemptComputer = new ArrayList<Tupple>();
		listBoatUser = new ArrayList();
		listBoatComputer = new ArrayList();
		initBoat(6, listCasesBoatsSelected);
		initBoat(4, listCasesBoatsSelected);
		initBoat(2, listCasesBoatsSelected);

		initBoatComputer(6, listCasesBoatComputer);
		initBoatComputer(4, listCasesBoatComputer);
		initBoatComputer(2, listCasesBoatComputer);

	}

	public void initBoatComputer(int size, List<Tupple> list) {
		int boatSize = size;
		Random r = new Random();
		int orientation = r.nextInt(2); // 0 = horizontal, 1 = vertical
		int rowStart = r.nextInt(10);
		int colStart = r.nextInt(10);
		if (orientation == 0) {
			if (colStart > 5) {
				setCasesBoatHorizontal(colStart - boatSize, colStart, rowStart, colStart, boatSize, list);
			} else if (colStart < 5) {
				setCasesBoatHorizontal(colStart, colStart + boatSize, rowStart, colStart, boatSize, list);
			} else {
				setCasesBoatHorizontal(3, 3 + boatSize, rowStart, colStart, boatSize, list);
			}
		} else if (orientation == 1) {
			if (rowStart < 5) {
				setCasesBoatVertical(rowStart, rowStart + boatSize, rowStart, colStart, boatSize, list);
			} else if (rowStart > 5) {
				setCasesBoatVertical(rowStart - boatSize, rowStart, rowStart, colStart, boatSize, list);
			} else {
				setCasesBoatVertical(3, 3 + boatSize, rowStart, colStart, boatSize, list);
			}
		}
	}

	public void initBoat(int size, List<Tupple> list) {
		int boatSize = size;
		String boatStyle = "-fx-background-color : blue;";
		Random r = new Random();
		int orientation = r.nextInt(2); // 0 = horizontal, 1 = vertical
		int rowStart = r.nextInt(10);
		int colStart = r.nextInt(10);
		if (orientation == 0) {
			if (colStart > 5) {
				setCasesBoatHorizontal(colStart - boatSize, colStart, rowStart, colStart, boatSize, list);
			} else if (colStart < 5) {
				setCasesBoatHorizontal(colStart, colStart + boatSize, rowStart, colStart, boatSize, list);
			} else {
				setCasesBoatHorizontal(3, 3 + boatSize, rowStart, colStart, boatSize, list);
			}
		} else if (orientation == 1) {
			if (rowStart < 5) {
				setCasesBoatVertical(rowStart, rowStart + boatSize, rowStart, colStart, boatSize, list);
			} else if (rowStart > 5) {
				setCasesBoatVertical(rowStart - boatSize, rowStart, rowStart, colStart, boatSize, list);
			} else {
				setCasesBoatVertical(3, 3 + boatSize, rowStart, colStart, boatSize, list);
			}

		}
		for (Tupple tupple : list) {
			getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle(boatStyle);
		}
		
	}

	public void setCasesBoatHorizontal(int inf, int sup, int rowStart, int colStart, int boatSize, List<Tupple> list) {
		List<Tupple> listTemp = new ArrayList<Tupple>();
		for (int i = inf; i < sup; i++) {
			Tupple tupple = new Tupple(rowStart, i);
			listTemp.add(tupple);
		}
		if (Collections.disjoint(listTemp, list)) {
			list.addAll(listTemp);
			if(list != listCasesBoatComputer) {
				listBoatUser.add(listTemp);
			}else {
				listBoatComputer.add(listTemp);
			}
			
		} else {
			if(list != listCasesBoatComputer) {
				initBoat(boatSize, list);
			}else {
				initBoatComputer(boatSize, list);
			}
		}
	}
	
	public void checkBoatDestruction() {
		for(List<Tupple> boat : listBoatComputer) {
			if(listAttemptUser.containsAll(boat)) {
				for(Tupple tupple : boat) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPaneShot).setStyle("-fx-background-color : red;");
				}
			}
		}
		for(List<Tupple> boat : listBoatUser) {
			if(listAttemptComputer.containsAll(boat)) {
				for(Tupple tupple : boat) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle("-fx-background-color : red;");
				}
			}
		}
		
	}

	public void setCasesBoatVertical(int inf, int sup, int rowStart, int colStart, int boatSize, List<Tupple> list) {
		List<Tupple> listTemp = new ArrayList<Tupple>();
		for (int i = inf; i < sup; i++) {
			Tupple tupple = new Tupple(i, colStart);
			listTemp.add(tupple);
		}
		if (Collections.disjoint(listTemp, list)) {
			list.addAll(listTemp);
			if(list != listCasesBoatComputer) {
				listBoatUser.add(listTemp);
			}else {
				listBoatComputer.add(listTemp);
			}
		} else {
			if(list != listCasesBoatComputer) {
				initBoat(boatSize, list);
			}else {
				initBoatComputer(boatSize, list);
			}
		}
	}

	public static class Tupple {
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
			System.out.println("( " + row + " , " + col + " )");
		}

	}

}