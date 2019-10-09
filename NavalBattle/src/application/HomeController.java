package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class HomeController implements Initializable{

	@FXML
	private GridPane gridPane;
	
	private List<Tupple> listCasesBoatsSelected;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(gridPane!=null) {
			gridPane.setGridLinesVisible(true);
			for(int i = 0; i<10 ; i++) {
				for(int j = 0 ; j<10 ; j++) {
					Pane p = new Pane();
					gridPane.add(p, i, j);
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
        gridPane.getChildren().forEach(item -> {
            item.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                	getNodeByRowColumnIndex( 0, 9, gridPane).setStyle("-fx-background-color : red;");
                	item.setStyle("-fx-background-color : red;");
                    if (event.getClickCount() == 1) {
                        System.out.println("doubleClick");
                    }
                    if (event.isPrimaryButtonDown()) {
                        System.out.println("PrimaryKey event");
                    }
                }
            });
            
            item.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                	item.setStyle("-fx-background-color : blue;");
                    if (event.getClickCount() == 2) {
                        System.out.println("doubleClick");
                    }
                    if (event.isPrimaryButtonDown()) {
                        System.out.println("PrimaryKey event");
                    }

                }
            });

        });
    }
    
    public void newGame() {
    	for(Node node : gridPane.getChildren()) {
    		Integer columnIndex = GridPane.getColumnIndex(node);
    		if(columnIndex!=null) {
        		Pane p = (Pane) node;
        		p.setStyle(null);
    		}
    	}
    	initBoats();
    }
    
    public void initBoats() {
    	listCasesBoatsSelected = new ArrayList();
    	initSixCasesBoat();
    }
    
    public void initSixCasesBoat() {
    	//First Boat 6 cases
    	Random r = new Random();
    	int orientation = r.nextInt(2); //0 = horizontal, 1 = vertical
		int rowStart = r.nextInt(10);
		int colStart = r.nextInt(10);
		if(orientation == 0) {
			if(colStart>5) {
				for(int i = colStart - 6; i<colStart ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!listCasesBoatsSelected.contains(tupple)) {
						listCasesBoatsSelected.add(tupple);
						getNodeByRowColumnIndex(rowStart, i, gridPane).setStyle("-fx-background-color : yellow;");
						}
				}
			}else if(colStart<5){
				for(int i = colStart; i<colStart + 6 ; i++) {
					Tupple tupple = new Tupple(rowStart, i);
					if(!listCasesBoatsSelected.contains(tupple)) {
						listCasesBoatsSelected.add(tupple); 
						getNodeByRowColumnIndex(rowStart, i, gridPane).setStyle("-fx-background-color : yellow;");
						}
				}
			}else {
				
			}
		}else if(orientation == 1) {
			if(rowStart<5) {
				for(int i = rowStart ; i<rowStart + 6; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!listCasesBoatsSelected.contains(tupple)) {
						listCasesBoatsSelected.add(tupple);
						getNodeByRowColumnIndex(i, colStart, gridPane).setStyle("-fx-background-color : yellow;");
						}
				}
			}else if(rowStart>5) {
				for(int i = rowStart - 6 ; i<rowStart ; i++) {
					Tupple tupple = new Tupple(i, colStart);
					if(!listCasesBoatsSelected.contains(tupple)) {
						listCasesBoatsSelected.add(tupple); 
						getNodeByRowColumnIndex(i, colStart, gridPane).setStyle("-fx-background-color : yellow;");
						}
				}
			}

		}
    }
    
    private static class Tupple{
    	
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
    	
    
    }

	

}