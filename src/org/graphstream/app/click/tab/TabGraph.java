package org.graphstream.app.click.tab;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.graphstream.app.click.Controller;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.camera.Camera;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Management of Graph Tab in Toolbox
 * @author hicham
 */
public class TabGraph {
	
	private Controller controller ;
	
	// -- Graph Section --
	private ToggleButton newNode, newEdge, remove ;
	private CheckBox checkDirectedEdge ;
	
	// -- Sprite 
	private SpriteManager sman ;
	private Button newSprite ;
	private int idSprite = 0 ;
	
	// -- Camera
	private Button cameraResetView;
	private Button cameraBuildConfig;
	private CheckBox cameraAutoFitView ;
	private boolean autoFit ;

	private TextField viewCenterX, viewCenterY ;
	private TextField scale ;
	
	// -- File -- //
	private Button snap;
	private Button save;
	private Button load;
	
	// ========= Edge creation
	private TextField selection ;
	
	// ========= Image Icon 
	Image circle = new Image(getClass().getResourceAsStream("../res/NodeCircle.png"));
	Image plain = new Image(getClass().getResourceAsStream("../res/StrokePlain.png"));
	Image sprite = new Image(getClass().getResourceAsStream("../res/sprite.png"));
	
	// ========= Utility
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    
	public TabGraph(Controller controller) {
		this.controller = controller ;
		
		selection = new TextField("Current selection (Edge build) : -");
		selection.setMaxWidth(280);
		selection.setEditable(false);

		newNode = new ToggleButton("Node", new ImageView(circle));
		newEdge = new ToggleButton("Edge", new ImageView(plain));
		newSprite = new Button("Sprite",  new ImageView(sprite));
		remove = new ToggleButton("Remove"); 
		checkDirectedEdge = new CheckBox("Edge directed");
			
		cameraResetView = new Button("Reset View");
		cameraBuildConfig = new Button("Build Config");
		
		cameraAutoFitView = new CheckBox();
		viewCenterX = new TextField();
		viewCenterY = new TextField();
		scale = new TextField();
		
		snap = new Button("Snapshot");
		save = new Button("Save"); 
		load = new Button("Load"); 
		//------ Listeners Graph
		newNode.addEventFilter(ActionEvent.ACTION, new PressButton());
		newEdge.addEventFilter(ActionEvent.ACTION, new PressButton());
		newSprite.addEventFilter(ActionEvent.ACTION, e -> newSprite());
		remove.addEventFilter(ActionEvent.ACTION, new PressButton());
				
		//------ Listeners camera
		cameraResetView.addEventFilter(ActionEvent.ACTION, e -> {
			resetView();
			updateCameraView();
		});
		
		cameraBuildConfig.addEventFilter(ActionEvent.ACTION, e -> {
			cameraBuildConfig();
			updateCameraView();
		});
		
		cameraAutoFitView.addEventFilter(ActionEvent.ACTION, e -> {
			autoFit = cameraAutoFitView.isSelected();
			controller.getPanel().getCamera().setAutoFitView(autoFit);
			updateCameraView();
		});
		
		viewCenterX.addEventFilter(ActionEvent.ACTION, e -> {
			controller.getPanel().getCamera().setViewCenter(Double.parseDouble(viewCenterX.getText()), Double.parseDouble(viewCenterY.getText()), 0);
			updateCameraView();
		});
		
		viewCenterY.addEventFilter(ActionEvent.ACTION, e -> {
			controller.getPanel().getCamera().setViewCenter(Double.parseDouble(viewCenterX.getText()), Double.parseDouble(viewCenterY.getText()), 0);
			updateCameraView();
		});
		
		scale.addEventFilter(ActionEvent.ACTION, e -> {
			controller.getPanel().getCamera().setViewPercent(Double.parseDouble(scale.getText()));
			updateCameraView();
		});
		
		snap.addEventFilter(ActionEvent.ACTION, e -> {
			snap();
		});
		
		save.addEventFilter(ActionEvent.ACTION, e -> {
			save();
		});
		
		load.addEventFilter(ActionEvent.ACTION, e -> {
			load();
		});
		// ======================= Format
		// ----- Format TextField
		
		viewCenterX.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*+(\\.\\d*)")) {
		        	viewCenterX.setText(newValue.replaceAll("[^0-9.]", ""));
		        }
		    }
		});
		
		viewCenterY.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*+(\\.\\d*)")) {
		        	viewCenterY.setText(newValue.replaceAll("[^0-9.]", ""));
		        }
		    }
		});
		
		scale.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*+(\\.\\d*)")) {
		        	scale.setText(newValue.replaceAll("[^0-9.]", ""));
		        }
		    }
		});
	}
	
	public void snap() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
		File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
        	if(file.getName().split("\\.").length == 1) 
        		controller.getGraph().setAttribute("ui.screenshot", file.getAbsolutePath()+".png");
        	else
        		controller.getGraph().setAttribute("ui.screenshot", file.getAbsolutePath());

        	controller.getPanel().getCamera().setViewPercent(controller.getPanel().getCamera().getViewPercent()); // little hack for refresh the controller.getPanel()
        }
	}

	public void save() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(new Stage());
		
		if (file != null) {
			MultiGraph saveGraph = getCopyOfGraph(controller.getGraph());
			
			FileSinkDGS fs = new FileSinkDGS();
			try {
				if(file.getName().split("\\.").length == 1) 
					fs.writeAll(saveGraph, file.getAbsolutePath()+".dot");
				else
					fs.writeAll(saveGraph, file.getAbsolutePath());

			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }		
	}

	public void load() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(new Stage());
		
		if (file != null) {
			controller.setGraph(new MultiGraph("Graph"));

			FileSourceDGS fs = new FileSourceDGS();
			fs.addSink(controller.getGraph());
			try {
				fs.readAll(file.getAbsolutePath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			controller.getGraph().nodes().forEach(n -> {
				if ( n.getAttribute("savePos") != null ) {
					String[] save = ((String)n.getAttribute("savePos")).split(",");
					double[] xyz = new double[3];
					xyz[0] = Double.parseDouble(save[0]);
					xyz[1] = Double.parseDouble(save[1]);
					xyz[2] = Double.parseDouble(save[2]);
					n.setAttribute("xyz", xyz);
				}
					
			});
			
			cameraBuildConfig();
			resetView();
        }
	}

	public Tab getTab() {
		Tab tab = new Tab();
		tab.setText("Graph");
		tab.setOnClosed(e -> controller.closeTab(e));
		
		this.sman = new SpriteManager(controller.getGraph());
		
		TitledPane buildTitlePane = new TitledPane();
		buildTitlePane.setText("Build");
		
		VBox directed = new VBox();
		HBox build = new HBox() ;
		build.getChildren().add(newNode);
		build.getChildren().add(newEdge);
		remove.setMinSize(90, 50);
		build.getChildren().add(remove);
		directed.getChildren().add(selection);
		directed.setSpacing(10);
		directed.getChildren().add(build);
		newSprite.setMinWidth(95);
		directed.getChildren().add(newSprite);
		checkDirectedEdge.setPadding(new Insets(2, 2, 0, 2));
		directed.getChildren().add(checkDirectedEdge);
		buildTitlePane.setContent(directed);
		
		// ===========================
		
		TitledPane cameraTitlePane = new TitledPane();
		cameraTitlePane.setText("Camera");
		VBox camera = new VBox() ;
		camera.setSpacing(10);
		
		HBox buttons = new HBox();
		buttons.getChildren().add(cameraResetView);
		buttons.getChildren().add(cameraBuildConfig);
		camera.getChildren().add(buttons);
		
		HBox text1 = new HBox();
		Label autoFitLabel = new Label("AutoFit View");
		autoFitLabel.setPadding(new Insets(5, 5, 0, 5));
		text1.getChildren().add(autoFitLabel); text1.getChildren().add(cameraAutoFitView);
		camera.getChildren().add(text1);
		
		HBox text2 = new HBox();
		Label centerXLabel = new Label("center X : "); Label centerYLabel = new Label("center Y : "); 
		centerXLabel.setPadding(new Insets(5, 5, 0, 5)); centerYLabel.setPadding(new Insets(5, 5, 0, 5));
		viewCenterX.setPrefWidth(70); viewCenterY.setPrefWidth(70);
		text2.getChildren().add(centerXLabel); text2.getChildren().add(viewCenterX);
		text2.getChildren().add(centerYLabel); text2.getChildren().add(viewCenterY);
		camera.getChildren().add(text2);
		
		HBox text4 = new HBox();
		Label scaleLabel = new Label("scale");
		scaleLabel.setPadding(new Insets(5, 5, 0, 5));
		scale.setPrefWidth(50);
		text4.getChildren().add(scaleLabel); text4.getChildren().add(scale);
		camera.getChildren().add(text4);
		cameraTitlePane.setContent(camera);
		
		// ===========================
		TitledPane fileTitlePane = new TitledPane();
		fileTitlePane.setText("File");
		HBox files = new HBox() ;
		files.setSpacing(10);
		
		files.getChildren().add(snap);
		files.getChildren().add(save);
		files.getChildren().add(load);
		fileTitlePane.setContent(files);
		
		// ===========================
		VBox hbox = new VBox();
		hbox.getChildren().add(buildTitlePane);
		hbox.getChildren().add(cameraTitlePane);
		hbox.getChildren().add(fileTitlePane);

		hbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(hbox);
		

		return tab;
	}

	public void cameraBuildConfig() {
		autoFit = false ;
		controller.getPanel().getCamera().setAutoFitView(autoFit);
		controller.getPanel().getCamera().setViewCenter(400, 300, 0);
		controller.getPanel().getCamera().setViewPercent(1.0);
		controller.getPanel().getCamera().setGraphViewport(0, 0, 800, 600);
		updateCameraView();
	}
	
	public void updateCameraView() {
		Camera camera = controller.getPanel().getCamera();
		cameraAutoFitView.setSelected(autoFit);
		viewCenterX.setText(camera.getViewCenter().x+""); 
		viewCenterY.setText(camera.getViewCenter().y+"");
		scale.setText(camera.getViewPercent()+"");
	}
	
	public void resetView() {
		controller.getPanel().getCamera().resetView();
		autoFit = true ;
	}
	
	public ToggleButton getNewNode() {
		return newNode;
	}

	public ToggleButton getNewEdge() {
		return newEdge;
	}

	public ToggleButton getRemove() {
		return remove;
	}
	
	public SpriteManager getSpriteManager() {
		return sman;
	}
	
	public boolean isAutoFit() {
		return autoFit;
	}

	public void setAutoFit(boolean autoFit) {
		this.autoFit = autoFit;
	}

	public TextField getViewCenterX() {
		return viewCenterX;
	}

	public TextField getViewCenterY() {
		return viewCenterY;
	}

	public TextField getScale() {
		return scale;
	}

	public TextField getSelection() {
		return selection;
	}

	public CheckBox getCameraAutoFitView() {
		return cameraAutoFitView;
	}
	
	public CheckBox getCheckDirectedEdge() {
		return checkDirectedEdge;
	}
	
	class PressButton implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			if ( !((ToggleButton)event.getSource()).isSelected() ) {
				((ToggleButton)event.getSource()).setSelected(false);
			}
			else {
				resetToggleButton();
				((ToggleButton)event.getSource()).setSelected(true);	
			}
		}
		
		private void resetToggleButton() {
			controller.setNodeSelected(null) ;
			selection.setText("Current selection (Edge build) : -") ;
			newNode.setSelected(false);
			remove.setSelected(false);
			newEdge.setSelected(false);
		}
	}
	
	private void newSprite() {
		Stage stage = new Stage();
		stage.setTitle("New Sprite");
		stage.initStyle(StageStyle.UTILITY);
		
		VBox mainPanel = new VBox() ;
		mainPanel.setSpacing(10);
		
		// ------------- Attach Node / Edge
		VBox panelAttach = new VBox() ;
		
		ListView<String> list = new ListView<String>(); // List Node/Edge
		AtomicBoolean isNode = new AtomicBoolean(true) ;
		
		HBox chooseAttach = new HBox();
		chooseAttach.setAlignment(Pos.CENTER);
		
		Button node = new Button("Node");
		node.setMaxWidth(Double.MAX_VALUE);
		node.addEventFilter(ActionEvent.ACTION, event -> {
			List<String> items = controller.getGraph().nodes().map(n -> n.getId()).collect(Collectors.toList());
			list.setItems(FXCollections.observableArrayList(items));
			isNode.set(true) ;
		});
		
		Button edge = new Button("Edge");
		edge.setMaxWidth(Double.MAX_VALUE);
		edge.addEventFilter(ActionEvent.ACTION, event -> { 
			List<String> items = controller.getGraph().edges().map(e -> e.getId()).collect(Collectors.toList());
			list.setItems(FXCollections.observableArrayList(items));
			isNode.set(false) ;
		});
		
		HBox.setHgrow(node, Priority.ALWAYS);
		HBox.setHgrow(edge, Priority.ALWAYS);
		
		chooseAttach.getChildren().addAll(node, edge);
		panelAttach.getChildren().addAll(chooseAttach, list);
		
		// ---------- Parameters 
		TextField positionX = new TextField("0");
		positionX.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	positionX.setText(newValue.replaceAll("[^0-9]", ""));
		        }
		    }
		});
		
		TextField positionY = new TextField("0");
		positionY.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	positionX.setText(newValue.replaceAll("[^0-9]", ""));
		        }
		    }
		});
		
		HBox positions = new HBox();
		Label xLab = new Label("X = "); 
		xLab.setPadding(new Insets(5, 5, 0, 5));
		Label yLab = new Label("Y = ");
		yLab.setPadding(new Insets(5, 5, 0, 5));
		positions.getChildren().addAll(xLab, positionX, yLab, positionY);
		
		Button valid = new Button("OK");
		valid.addEventFilter(ActionEvent.ACTION, event -> { 
			if(list.getSelectionModel().getSelectedItem() != null) {		
				Sprite sprite = sman.addSprite("S"+idSprite);
				idSprite++ ;
				sprite.setPosition(StyleConstants.Units.PX, Integer.parseInt(positionX.getText()), Integer.parseInt(positionY.getText()), 0);

				if(isNode.get()) {
					sprite.attachToNode(list.getSelectionModel().getSelectedItem());
				}	
				else {
					sprite.attachToEdge(list.getSelectionModel().getSelectedItem());
				}
				
				stage.close();
			}
		});
		// ------------------------------------
		//mainPanel.getChildren().add(chooseAttach);
		//mainPanel.getChildren().add(list);
		mainPanel.getChildren().add(panelAttach);
		mainPanel.getChildren().add(positions);
		
		HBox centerOK = new HBox(valid);
		centerOK.setAlignment(Pos.CENTER);
		mainPanel.getChildren().add(centerOK);
		// ====================================
		stage.setScene(new Scene(mainPanel));
		stage.show();
	}
	
	private MultiGraph getCopyOfGraph(Graph theGraph) {
		MultiGraph aGraphCopy = new MultiGraph("Graph");
		
		theGraph.nodes().forEach(aNode -> {
			Node n = aGraphCopy.addNode(aNode.getId());
			aNode.attributeKeys().forEach(attribute -> {
				System.out.println(aNode.getAttribute(attribute).getClass());
				if ( aNode.getAttribute(attribute).getClass().isPrimitive() || isWrapperType(aNode.getAttribute(attribute).getClass()) || aNode.getAttribute(attribute).getClass().equals(String.class))
					n.setAttribute(attribute, aNode.getAttribute(attribute));
				else if ( attribute.equals("xyz") ) {
					if ( aNode.getAttribute(attribute) instanceof Object[] ) {
						Object[] coord = (Object[]) aNode.getAttribute(attribute);
						n.setAttribute("savePos", coord[0]+","+coord[1]+","+coord[2]);
					}
					else if ( aNode.getAttribute(attribute) instanceof double[] ) {
						double[] coord = (double[]) aNode.getAttribute(attribute);
						n.setAttribute("savePos", coord[0]+","+coord[1]+","+coord[2]);
					}
					else if ( aNode.getAttribute(attribute) instanceof Double[] ) {
						Double[] coord = (Double[]) aNode.getAttribute(attribute);
						n.setAttribute("savePos", coord[0]+","+coord[1]+","+coord[2]);
					}
				}
			});
		});
		
		theGraph.edges().forEach(anEdge -> {
			Edge e ;
			e = aGraphCopy.addEdge(anEdge.getId(), anEdge.getSourceNode().getId(), anEdge.getTargetNode().getId(), anEdge.isDirected());
	
			anEdge.attributeKeys().forEach(attribute -> {
				System.out.println(anEdge.getAttribute(attribute).getClass());
				if ( anEdge.getAttribute(attribute).getClass().isPrimitive() || isWrapperType(anEdge.getAttribute(attribute).getClass()) || anEdge.getAttribute(attribute).getClass().equals(String.class))
					e.setAttribute(attribute, anEdge.getAttribute(attribute));
				else if ( attribute.equals("xyz") ) {
					Object[] coord = (Object[]) anEdge.getAttribute(attribute);
					e.setAttribute("savePos", coord[0]+","+coord[1]+","+coord[2]);
				}
			});
		
		});
		
		return aGraphCopy;
	}
	
	public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        //ret.add(Void.class);
        return ret;
    }
}
