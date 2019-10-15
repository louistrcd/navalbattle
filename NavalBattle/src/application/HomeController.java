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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import application.Main;

public class HomeController implements Initializable {

	@FXML
	private GridPane gridPane;
	@FXML
	private GridPane gridPaneShot;

	@FXML
	private Label labelUser;
	@FXML
	private Label labelComputer;

	private List<Tupple> listCasesBoatsSelected;
	private List<Tupple> listCasesBoatComputer;
	private List<Tupple> listAttemptUser;
	private List<Tupple> listAttemptComputer;
	private List<List<Tupple>> listBoatUser;
	private List<List<Tupple>> listBoatComputer;
	private List<List<Tupple>> listDestroyedUser;
	private List<List<Tupple>> listDestroyedComputer;

	String successShot = "-fx-border-color : red;  -fx-border-width : 4";
	String missShot = "-fx-background-color : #949494;";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (gridPane != null) {
			gridPane.setGridLinesVisible(true);
			gridPaneShot.setGridLinesVisible(true);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					Pane p1 = new Pane();
					Pane p2 = new Pane();
					glowEffectNode(p1);
					glowEffectNode(p2);
					gridPane.add(p1, i, j);
					gridPaneShot.add(p2, i, j);
				}
			}
		}
		initBoats();
		addGridEvent();
	}
	
    public static void glowEffectNode(final Node n) {
        final Node node = n;
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                if(node.getStyle()==null) {
                	node.setStyle("-fx-background-color : #343538");
                }
                node.setEffect(new Glow());

                //node.setStyle("");
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                if (node.getEffect() == null) {
                } else {
                    node.setEffect(null);
                }
            }
        });

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
							testVictoryOrLoose();
						} else {
							item.setStyle(missShot);
							Main.playMusic("miss.mp3", 1.2);
							if (!testVictoryOrLoose()) {
								computerAttack(1);
							}
						}

					}
				}
			});
			
//			item.setOnMouseClicked(new EventHandler<MouseEvent>() {
//				@Override
//				public void handle(MouseEvent event) {
//					int row = GridPane.getRowIndex(item);
//					int col = GridPane.getColumnIndex(item);
//					getNodeByRowColumnIndex(row, col, gridPaneShot).setEffect(new InnerShadow(BlurType.GAUSSIAN, Color.valueOf("#e6e6e6"), 10, 0, 0, 3));;
//					
//				}
//			});
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
				Tupple tupple = null;
				Tupple test = findAnotherCaseToAttack();
				tupple = new Tupple(row, col);
				if (test != null && !listAttemptComputer.contains(test) && test.getRow() >= 0 && test.getCol() >= 0) {
					test.show();
					tupple.setRow(test.getRow());
					tupple.setCol(test.getCol());
					tupple.show();
				}

				if (!listAttemptComputer.contains(tupple)) {
					listAttemptComputer.add(tupple);
					if (listCasesBoatsSelected.contains(tupple)) {
						getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle(successShot);
						checkBoatDestruction();
						if (!testVictoryOrLoose()) {
							computerAttack(1);
						}
						Main.playMusic("explosion.mp3", 0.7);
					} else {
						getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle(missShot);
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

	public Tupple findAnotherCaseToAttack() {
		Tupple caseToAttack = null;
		List<Tupple> allPossibleCases = new ArrayList();
		for (List<Tupple> list : listBoatUser) {
			for (Tupple tupple : list) {
				if (listAttemptComputer.contains(tupple) && !listAttemptComputer.containsAll(list)) {
					int row = tupple.getRow();
					int col = tupple.getCol();
					List<Tupple> listNearCases = getNearCaseCoordinates(row, col);
					for(Tupple t : listNearCases) {
						if(!allPossibleCases.contains(t)) {
							allPossibleCases.add(t);
						}
					}
				}
			}
		}
		if(allPossibleCases.size()>0) {
			Random r = new Random();
			int i = r.nextInt(allPossibleCases.size());
			caseToAttack = allPossibleCases.get(i);
		}
		return caseToAttack;
	}

	public List<Tupple> getNearCaseCoordinates(int row, int col) {
		Tupple caseToAttack = null;
		List<Tupple> listNearCases = new ArrayList();
		caseToAttack = new Tupple(row + 1, col);

		if (caseToAttack.getCol() >= 0 && caseToAttack.getRow() >= 0 && caseToAttack.getRow()<10 && caseToAttack.getCol()<10
				&&!listNearCases.contains(caseToAttack) 
				&& !listAttemptComputer.contains(caseToAttack)) {
			Tupple tupple = new Tupple(caseToAttack.getRow(), caseToAttack.getCol());
			listNearCases.add(tupple);
		}
		
		caseToAttack.setRow(row);
		caseToAttack.setCol(col + 1);
		
		if (caseToAttack.getCol() >= 0 && caseToAttack.getRow() >= 0 && caseToAttack.getRow()<10 && caseToAttack.getCol()<10
				&&!listNearCases.contains(caseToAttack) 
				&& !listAttemptComputer.contains(caseToAttack)) {
			Tupple tupple = new Tupple(caseToAttack.getRow(), caseToAttack.getCol());
			listNearCases.add(tupple);
		}
		
		caseToAttack.setRow(row - 1);
		caseToAttack.setCol(col);
		
		if (caseToAttack.getCol() >= 0 && caseToAttack.getRow() >= 0 && caseToAttack.getRow()<10 && caseToAttack.getCol()<10
				&&!listNearCases.contains(caseToAttack) 
				&& !listAttemptComputer.contains(caseToAttack)) {
			Tupple tupple = new Tupple(caseToAttack.getRow(), caseToAttack.getCol());
			listNearCases.add(tupple);
		}
		
		caseToAttack.setRow(row);
		caseToAttack.setCol(col - 1);
		
		if (caseToAttack.getCol() >= 0 && caseToAttack.getRow() >= 0 && caseToAttack.getRow()<10 && caseToAttack.getCol()<10
				&&!listNearCases.contains(caseToAttack) 
				&& !listAttemptComputer.contains(caseToAttack)) {
			Tupple tupple = new Tupple(caseToAttack.getRow(), caseToAttack.getCol());
			listNearCases.add(tupple);
		}

		System.out.println("Taille liste itnermediaire  " + listNearCases.size());
		return listNearCases;

	}

	public void showEverything(List<Tupple> list) {
		for (Tupple t : list) {
			t.show();
		}
	}

	public boolean testVictoryOrLoose() {
		boolean b = false;
		if (listAttemptUser.containsAll(listCasesBoatComputer)) {
			System.out.println("Congratulations you won!!!");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Tu as gagné fdp");
			alert.setHeaderText("VICTOIRE");
			alert.show();
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
		labelComputer.setText("Nombre de bâteaux détruits : 0");
		labelUser.setText("Nombre de bâteaux détruits : 0");
		listCasesBoatsSelected = new ArrayList<Tupple>();
		listCasesBoatComputer = new ArrayList<Tupple>();
		listAttemptUser = new ArrayList<Tupple>();
		listAttemptComputer = new ArrayList<Tupple>();
		listBoatUser = new ArrayList();
		listBoatComputer = new ArrayList();
		listDestroyedUser = new ArrayList();
		listDestroyedComputer = new ArrayList();
		initBoat(6, listCasesBoatsSelected);
		initBoat(4, listCasesBoatsSelected);
		initBoat(2, listCasesBoatsSelected);
		initBoat(3, listCasesBoatsSelected);

		initBoatComputer(6, listCasesBoatComputer);
		initBoatComputer(4, listCasesBoatComputer);
		initBoatComputer(2, listCasesBoatComputer);
		initBoatComputer(3, listCasesBoatComputer);

		for (List<Tupple> list : listBoatUser) {
			if (list.get(0).getRow() == list.get(1).getRow()) {
				for (Tupple tupple : list) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle(
							"-fx-border-style: solid none solid none; -fx-border-width : 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
				}
				getNodeByRowColumnIndex(list.get(0).getRow(), list.get(0).getCol(), gridPane).setStyle(
						"-fx-border-style: solid none solid solid; -fx-border-width : 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
				getNodeByRowColumnIndex(list.get(list.size() - 1).getRow(), list.get(list.size() - 1).getCol(),
						gridPane).setStyle(
								"-fx-border-style: solid solid solid none; -fx-border-width : 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
			}

			else if (list.get(0).getCol() == list.get(1).getCol()) {

				for (Tupple tupple : list) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane).setStyle(
							"-fx-border-style: none none none none; -fx-border-width : 0 5 0 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
				}
				getNodeByRowColumnIndex(list.get(0).getRow(), list.get(0).getCol(), gridPane).setStyle(
						"-fx-border-style: none none none none; -fx-border-width : 5 5 0 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
				getNodeByRowColumnIndex(list.get(list.size() - 1).getRow(), list.get(list.size() - 1).getCol(),
						gridPane).setStyle(
								"-fx-border-style: none none none none; -fx-border-width : 0 5 5 5;-fx-border-color : #00e1ff; -fx-background-color : #0228d1");
			}
		}

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
			if (list != listCasesBoatComputer) {
				listBoatUser.add(listTemp);
			} else {
				listBoatComputer.add(listTemp);
			}

		} else {
			if (list != listCasesBoatComputer) {
				initBoat(boatSize, list);
			} else {
				initBoatComputer(boatSize, list);
			}
		}
	}

	public void checkBoatDestruction() {
		for (List<Tupple> boat : listBoatComputer) {
			if (listAttemptUser.containsAll(boat)) {
				for (Tupple tupple : boat) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPaneShot)
							.setStyle("-fx-background-color : red;");
				}
				if (!listDestroyedUser.contains(boat)) {
					listDestroyedUser.add(boat);
				}
				labelUser.setText("Nombre de bâteaux détruits : " + listDestroyedUser.size());
			}
		}
		for (List<Tupple> boat : listBoatUser) {
			if (listAttemptComputer.containsAll(boat)) {
				for (Tupple tupple : boat) {
					getNodeByRowColumnIndex(tupple.getRow(), tupple.getCol(), gridPane)
							.setStyle("-fx-background-color : red;");
				}
				if (!listDestroyedComputer.contains(boat)) {
					listDestroyedComputer.add(boat);
				}
				labelComputer.setText("Nombre de bâteaux détruits : " + listDestroyedComputer.size());
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
			if (list != listCasesBoatComputer) {
				listBoatUser.add(listTemp);
			} else {
				listBoatComputer.add(listTemp);
			}
		} else {
			if (list != listCasesBoatComputer) {
				initBoat(boatSize, list);
			} else {
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