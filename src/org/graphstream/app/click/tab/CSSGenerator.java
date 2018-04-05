package org.graphstream.app.click.tab;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Files;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Management of Style Tab in Toolbox, include CSS Help and the web viewer 
 * @author hicham
 */
public class CSSGenerator {
	private final int HEIGHT = 800;
	private final int WIDTH = 400;
	private MenuBarGraph menu ;
	private Stage stageCSS ;
	
	// ========= StyleSheet
	// First collection : class (graph, edge, node#A etc...)
	// Second collection : attribute (color, shape.. ) with value
	private HashMap<String, HashMap<String, String>> styleSheet = new HashMap<>();
	private TextArea cssArea ;
	
	// -- Graph Section --
	private ToggleButton[] typeBackGraph ;
	private ColorPicker[] colorGraphBack ;
	private TextField paddingGraph ;
	
	// -- Node Section --
	private ToggleButton[] shapeNode ;
	private ToggleButton[] strokeNode ;
	private ColorPicker strokeNodeColor ;
	
	private ToggleButton[] typeBackNode ;
	private ColorPicker[] colorNodeBack ;
	
	private ToggleButton[] typeShadowNode ;
	private ColorPicker[] colorShadowBack ;
	
	private ColorPicker[] colorClickNode ;
	private ColorPicker[] colorSelectNode ;
	
	private TextField sizeNode, strokeWidthNode, shadowWidth, shadowOffset1, shadowOffset2 ;
	
	private ToggleButton shapeNodeImage ;
	private File fileNode = null ;
	
	// -- Edge Section --
	private ToggleButton[] shapeEdge ;
	
	private ToggleButton[] typeBackEdge ;
	private ColorPicker[] colorBackEdge ;
	
	private ToggleButton[] strokeEdge ;
	private ColorPicker strokeEdgeColor ;
	
	private ToggleButton[] shapeArrow ;
	
	private TextField sizeEdge, strokeWidthEdge, sizeArrow1, sizeArrow2 ;
	
	// -- Sprite Section --
	
	private ToggleButton[] shapeSprite ;
	private ToggleButton[] strokeSprite ;
	private ColorPicker strokeSpriteColor ;
	
	private ToggleButton[] typeBackSprite ;
	private ColorPicker[] colorSpriteBack ;
	
	private TextField sizeSprite, strokeWidthSprite ;
	
	private ToggleButton shapeSpriteImage ;
	private File fileSprite = null ;
	
	// == Image Icon ==
	Image plain = new Image(getClass().getResourceAsStream("../res/plain.png"));
	Image gradd1 = new Image(getClass().getResourceAsStream("../res/gradd1.png"));
	Image gradd2 = new Image(getClass().getResourceAsStream("../res/gradd2.png"));
	Image gradh = new Image(getClass().getResourceAsStream("../res/gradh.png"));
	Image gradr = new Image(getClass().getResourceAsStream("../res/gradr.png"));
	Image gradv = new Image(getClass().getResourceAsStream("../res/gradv.png"));

	Image box = new Image(getClass().getResourceAsStream("../res/NodeBox.png"));
	Image circle = new Image(getClass().getResourceAsStream("../res/NodeCircle.png"));
	Image cross = new Image(getClass().getResourceAsStream("../res/NodeCross.png"));
	Image diamond = new Image(getClass().getResourceAsStream("../res/NodeDiamond.png"));
	//Image pie = new Image(getClass().getResourceAsStream("../res/NodePie.png"));
	Image image = new Image(getClass().getResourceAsStream("../res/image.png"));
	Image rounded = new Image(getClass().getResourceAsStream("../res/NodeRoundedBox.png"));

	Image angle = new Image(getClass().getResourceAsStream("../res/EdgeAngle.png"));
	Image blob = new Image(getClass().getResourceAsStream("../res/EdgeBlob.png"));
	Image cubic = new Image(getClass().getResourceAsStream("../res/EdgeCubic.png"));
	Image free = new Image(getClass().getResourceAsStream("../res/EdgeFree.png"));
	Image edgeL = new Image(getClass().getResourceAsStream("../res/EdgeL.png"));
	Image arrow = new Image(getClass().getResourceAsStream("../res/arrow.png"));
	
	Image dashes = new Image(getClass().getResourceAsStream("../res/StrokeDashes.png"));
	Image dots = new Image(getClass().getResourceAsStream("../res/StrokeDots.png"));
	Image StrokeDouble = new Image(getClass().getResourceAsStream("../res/StrokeDouble.png"));
	Image StrokePlain = new Image(getClass().getResourceAsStream("../res/StrokePlain.png"));

	public CSSGenerator(TextArea css, MenuBarGraph menu) {
		this.cssArea = css ;
		this.menu = menu ;
		this.stageCSS = null ;
		init();
	}
	
	public void createCSSHelp(Stage stage, double x, double y) {
		if (stageCSS == null) {
			stageCSS = new Stage();
			stageCSS.setTitle("Help CSS");
			stageCSS.initStyle(StageStyle.UTILITY);
			stageCSS.setX(x-WIDTH);// + stage.getWidth());
			stageCSS.setY(y);
		}
		
		TabPane tabPane = new TabPane();
		tabPane.getTabs().add(tabGraph());
		tabPane.getTabs().add(tabNode());
		tabPane.getTabs().add(tabEdge());
		tabPane.getTabs().add(tabSprite());
		
		tabPane.setMinSize(WIDTH, HEIGHT);

		// layout the utility pane.
		VBox utilityLayout = new VBox(10);
		utilityLayout.setStyle(
				"-fx-padding:10; -fx-background-color: linear-gradient(to bottom, lightblue, derive(lightblue, 20%));");
	
		Hyperlink message = new Hyperlink();
		message.setText("See more features here !");
		message.setStyle("-fx-background-color: #E6D8AD;");
		message.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent e) {
		    	newStageWeb(stage);
		    }
		});
		
		utilityLayout.getChildren().add(message);
		utilityLayout.getChildren().add(tabPane);
		utilityLayout.setPrefHeight(530);
		stageCSS.setScene(new Scene(utilityLayout));
		stageCSS.show();

		stageCSS.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				stageCSS.close();
				menu.closeCSS();
			}
		});
	}
	
	public void newStageWeb(Stage stage) {
		Stage stageWeb = new Stage();
    	stageWeb.setTitle("Web");
    	stageWeb.initStyle(StageStyle.UTILITY);
    	stageWeb.setX(stage.getX() + stage.getWidth());
    	stageWeb.setY(stage.getY());
		
    	String url = "http://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/";
    	
    	final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.load(url);
        
        stageWeb.setScene(new Scene(browser));
        stageWeb.show();
	}
	
	private void init() {
		// -- Section Graph
		paddingGraph = new TextField();
		paddingGraph.setPrefWidth(50);
	
		typeBackGraph = new ToggleButton[6];
		typeBackGraph[0] = new ToggleButton("", new ImageView(plain));
		typeBackGraph[0].setSelected(true);
		typeBackGraph[1] = new ToggleButton("", new ImageView(gradv));
		typeBackGraph[2] = new ToggleButton("", new ImageView(gradh));
		typeBackGraph[3] = new ToggleButton("", new ImageView(gradr));
		typeBackGraph[4] = new ToggleButton("", new ImageView(gradd1));
		typeBackGraph[5] = new ToggleButton("", new ImageView(gradd2));

		colorGraphBack = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorGraphBack[i] = new ColorPicker();
			colorGraphBack[i].setPrefSize(100, 30);
		}
		
		//  ================ Section Node
		
		shapeNode = new ToggleButton[6];
		shapeNode[0] = new ToggleButton("", new ImageView(circle));
		shapeNode[0].setSelected(true);
		shapeNode[0].setTooltip(new Tooltip("Circle"));
		shapeNode[1] = new ToggleButton("", new ImageView(box));
		shapeNode[1].setTooltip(new Tooltip("Box"));
		shapeNode[2] = new ToggleButton("", new ImageView(rounded));
		shapeNode[2].setTooltip(new Tooltip("Rounded-box"));
		shapeNode[3] = new ToggleButton("", new ImageView(diamond));
		shapeNode[3].setTooltip(new Tooltip("Diamond"));
		shapeNode[4] = new ToggleButton("", new ImageView(cross));
		shapeNode[4].setTooltip(new Tooltip("Cross"));
		shapeNode[5] = new ToggleButton("", new ImageView(free));
		shapeNode[5].setTooltip(new Tooltip("Freeplane"));
		shapeNodeImage = new ToggleButton("", new ImageView(image));
		shapeNodeImage.setTooltip(new Tooltip("Image"));
		
		strokeNode = new ToggleButton[4];
		strokeNode[0] = new ToggleButton("", new ImageView(StrokePlain));
		strokeNode[0].setSelected(true);
		strokeNode[1] = new ToggleButton("", new ImageView(dots));
		strokeNode[1].setTooltip(new Tooltip("Dots"));
		strokeNode[2] = new ToggleButton("", new ImageView(dashes));
		strokeNode[2].setTooltip(new Tooltip("Dashes"));
		strokeNode[3] = new ToggleButton("", new ImageView(StrokeDouble));
		
		strokeNodeColor = new ColorPicker();
		strokeNodeColor.setValue(Color.BLACK);
		strokeNodeColor.setPrefSize(100, 50);
		colorNodeBack = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorNodeBack[i] = new ColorPicker();
			colorNodeBack[i].setPrefSize(100, 30);
		}
		colorNodeBack[0].setValue(Color.BLACK);
	
		typeBackNode = new ToggleButton[6];
		typeBackNode[0] = new ToggleButton("", new ImageView(plain));
		typeBackNode[0].setSelected(true);
		typeBackNode[1] = new ToggleButton("", new ImageView(gradv));
		typeBackNode[2] = new ToggleButton("", new ImageView(gradh));
		typeBackNode[3] = new ToggleButton("", new ImageView(gradr));
		typeBackNode[4] = new ToggleButton("", new ImageView(gradd1));
		typeBackNode[5] = new ToggleButton("", new ImageView(gradd2));
		
		typeShadowNode = new ToggleButton[5];
		typeShadowNode[0] = new ToggleButton("", new ImageView(plain));
		typeShadowNode[1] = new ToggleButton("", new ImageView(gradv));
		typeShadowNode[2] = new ToggleButton("", new ImageView(gradh));
		typeShadowNode[3] = new ToggleButton("", new ImageView(gradr));
		typeShadowNode[4] = new ToggleButton("None");
		typeShadowNode[4].setMinSize(50, 50);
		typeShadowNode[4].setSelected(true);
		
		colorShadowBack = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorShadowBack[i] = new ColorPicker();
			colorShadowBack[i].setPrefSize(100, 30);
		}
		colorShadowBack[0].setValue(Color.BLACK);
		
		colorClickNode = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorClickNode[i] = new ColorPicker();
			colorClickNode[i].setPrefSize(100, 30);
		}
		colorClickNode[0].setValue(Color.BLACK);
		
		colorSelectNode = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorSelectNode[i] = new ColorPicker();
			colorSelectNode[i].setPrefSize(100, 30);
		}
		colorSelectNode[0].setValue(Color.BLACK);
		
		sizeNode = new TextField(); 
		sizeNode.setPrefWidth(50);
		strokeWidthNode = new TextField();
		strokeWidthNode.setPrefWidth(50);
		shadowWidth = new TextField();
		shadowWidth.setPrefWidth(50);
		shadowOffset1 = new TextField();
		shadowOffset1.setPrefWidth(50);
		shadowOffset2 = new TextField();
		shadowOffset2.setPrefWidth(50);
		
		// ================ Section Edge
		
		shapeEdge = new ToggleButton[6];
		shapeEdge[0] = new ToggleButton("", new ImageView(StrokePlain)); 
		shapeEdge[0].setTooltip(new Tooltip("line"));
		shapeEdge[0].setSelected(true);
		shapeEdge[1] = new ToggleButton("", new ImageView(angle)); 
		shapeEdge[1].setTooltip(new Tooltip("angle"));
		shapeEdge[2] = new ToggleButton("", new ImageView(cubic));
		shapeEdge[2].setTooltip(new Tooltip("cubic-curve"));
		shapeEdge[3] = new ToggleButton("", new ImageView(blob));
		shapeEdge[3].setTooltip(new Tooltip("blob"));
		shapeEdge[4] = new ToggleButton("", new ImageView(free));
		shapeEdge[4].setTooltip(new Tooltip("freeplane"));
		shapeEdge[5] = new ToggleButton("", new ImageView(edgeL));
		shapeEdge[5].setTooltip(new Tooltip("L-square-line"));
		
		colorBackEdge = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorBackEdge[i] = new ColorPicker();
			colorBackEdge[i].setPrefSize(100, 30);
		}
		colorBackEdge[0].setValue(Color.BLACK);
	
		typeBackEdge = new ToggleButton[6];
		typeBackEdge[0] = new ToggleButton("", new ImageView(plain));
		typeBackEdge[0].setSelected(true);
		typeBackEdge[1] = new ToggleButton("", new ImageView(gradv));
		typeBackEdge[2] = new ToggleButton("", new ImageView(gradh));
		typeBackEdge[3] = new ToggleButton("", new ImageView(gradr));
		typeBackEdge[4] = new ToggleButton("", new ImageView(gradd1));
		typeBackEdge[5] = new ToggleButton("", new ImageView(gradd2));
		
		shapeArrow = new ToggleButton[4];
		shapeArrow[0] = new ToggleButton("", new ImageView(arrow));
		shapeArrow[0].setSelected(true);
		shapeArrow[1] = new ToggleButton("none");
		shapeArrow[1].setMinSize(50, 50);
		shapeArrow[2] = new ToggleButton("", new ImageView(circle));
		shapeArrow[3] = new ToggleButton("", new ImageView(diamond));
		
		strokeEdge = new ToggleButton[5];
		strokeEdge[0] = new ToggleButton("", new ImageView(StrokePlain));
		strokeEdge[1] = new ToggleButton("", new ImageView(dots));
		strokeEdge[1].setTooltip(new Tooltip("Dots"));
		strokeEdge[2] = new ToggleButton("", new ImageView(dashes));
		strokeEdge[2].setTooltip(new Tooltip("Dashes"));
		strokeEdge[3] = new ToggleButton("", new ImageView(StrokeDouble));
		strokeEdge[4] = new ToggleButton("None");
		strokeEdge[4].setMinSize(50, 50);
		strokeEdge[4].setSelected(true);

		strokeEdgeColor = new ColorPicker();
		strokeEdgeColor.setValue(Color.BLACK);
		strokeEdgeColor.setPrefSize(100, 30);
		sizeEdge = new TextField();
		sizeEdge.setPrefWidth(50);
		strokeWidthEdge = new TextField();
		strokeWidthEdge.setPrefWidth(50);
		sizeArrow1 = new TextField();
		sizeArrow1.setPrefWidth(50);
		sizeArrow2 = new TextField();
		sizeArrow2.setPrefWidth(50);
		
		// ================ Section Sprite
		
		shapeSprite = new ToggleButton[6];
		shapeSprite[0] = new ToggleButton("", new ImageView(circle));
		shapeSprite[0].setSelected(true);
		shapeSprite[0].setTooltip(new Tooltip("Circle"));
		shapeSprite[1] = new ToggleButton("", new ImageView(box));
		shapeSprite[1].setTooltip(new Tooltip("Box"));
		shapeSprite[2] = new ToggleButton("", new ImageView(rounded));
		shapeSprite[2].setTooltip(new Tooltip("Rounded-box"));
		shapeSprite[3] = new ToggleButton("", new ImageView(diamond));
		shapeSprite[3].setTooltip(new Tooltip("Diamond"));
		shapeSprite[4] = new ToggleButton("", new ImageView(cross));
		shapeSprite[4].setTooltip(new Tooltip("Cross"));
		shapeSprite[5] = new ToggleButton("", new ImageView(free));
		shapeSprite[5].setTooltip(new Tooltip("Freeplane"));
		
		shapeSpriteImage = new ToggleButton("", new ImageView(image));
		shapeSpriteImage.setTooltip(new Tooltip("Image"));
		
		strokeSprite = new ToggleButton[4];
		strokeSprite[0] = new ToggleButton("", new ImageView(StrokePlain));
		strokeSprite[0].setSelected(true);
		strokeSprite[1] = new ToggleButton("", new ImageView(dots));
		strokeSprite[1].setTooltip(new Tooltip("Dots"));
		strokeSprite[2] = new ToggleButton("", new ImageView(dashes));
		strokeSprite[2].setTooltip(new Tooltip("Dashes"));
		strokeSprite[3] = new ToggleButton("", new ImageView(StrokeDouble));
		
		strokeSpriteColor = new ColorPicker();
		strokeSpriteColor.setValue(Color.BLACK);
		strokeSpriteColor.setPrefSize(100, 50);
		colorSpriteBack = new ColorPicker[3];
		for(int i = 0 ; i < 3 ; i++) {
			colorSpriteBack[i] = new ColorPicker();
			colorSpriteBack[i].setPrefSize(100, 30);
		}
		colorSpriteBack[0].setValue(Color.BLACK);
	
		typeBackSprite = new ToggleButton[6];
		typeBackSprite[0] = new ToggleButton("", new ImageView(plain));
		typeBackSprite[0].setSelected(true);
		typeBackSprite[1] = new ToggleButton("", new ImageView(gradv));
		typeBackSprite[2] = new ToggleButton("", new ImageView(gradh));
		typeBackSprite[3] = new ToggleButton("", new ImageView(gradr));
		typeBackSprite[4] = new ToggleButton("", new ImageView(gradd1));
		typeBackSprite[5] = new ToggleButton("", new ImageView(gradd2));
		
		sizeSprite = new TextField(); 
		sizeSprite.setPrefWidth(50);
		strokeWidthSprite = new TextField();
		strokeWidthSprite.setPrefWidth(50);
		
		//===================== Listeners
		//------ Listeners Graph
		for(ColorPicker c : colorGraphBack)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ToggleButton b : typeBackGraph)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		paddingGraph.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		//------ Listeners Node
		for(ToggleButton b : shapeNode)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		shapeNodeImage.addEventFilter(ActionEvent.ACTION, e -> {
			if ( shapeNodeImage.isSelected()) {
				FileChooser fileChooser = new FileChooser();
				fileNode = fileChooser.showOpenDialog(new Stage());
				
				if (fileNode != null && !fileNode.getAbsolutePath().matches("^[a-zA-Z0-9_\\.\\-/]*$")) {
					/* Pattern p = Pattern.compile("[^a-zA-Z0-9_\\.\\-/]");
					Matcher m = p.matcher(fileNode.getAbsolutePath());
					
					while(m.find())
						System.out.println("BAD "+m.group()); */
					Alert alert = new Alert(AlertType.ERROR, "Bad character, check the name and path of the file please.", ButtonType.CLOSE);
					alert.showAndWait();
					fileNode = null ;
				}	
				
				if (fileNode == null)
					shapeNodeImage.setSelected(false);	
			}
			else {
				fileNode = null ;
			}
			updateStyle();
			
		});
		
		shapeSpriteImage.addEventFilter(ActionEvent.ACTION, e -> {
			if ( shapeSpriteImage.isSelected()) {
				FileChooser fileChooser = new FileChooser();
				fileSprite = fileChooser.showOpenDialog(new Stage());
				
				if ( fileSprite != null && !fileSprite.getAbsolutePath().matches("^[a-zA-Z0-9_\\.\\-/]*$")) {
					Alert alert = new Alert(AlertType.ERROR, "Bad character, check the name and path of the file please.", ButtonType.CLOSE);
					alert.showAndWait();
					fileSprite = null ;
				}	
				
				if (fileSprite == null)
					shapeSpriteImage.setSelected(false);	
			}
			else {
				fileSprite = null ;
			}
			updateStyle();
			
		});
		
		for(ToggleButton b : strokeNode)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ColorPicker c : colorNodeBack)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ToggleButton b : typeBackNode)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ColorPicker c : colorShadowBack)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ToggleButton b : typeShadowNode)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ColorPicker c : colorClickNode)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ColorPicker c : colorSelectNode)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		strokeNodeColor.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		sizeNode.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		strokeWidthNode.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		shadowWidth.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		shadowOffset1.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		shadowOffset2.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		//------ Listeners Edge
		
		for(ToggleButton b : shapeEdge)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ToggleButton b : typeBackEdge)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ColorPicker c : colorBackEdge)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ToggleButton b : shapeArrow)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ToggleButton b : strokeEdge)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		strokeEdgeColor.addEventFilter(ActionEvent.ACTION, new UpdateStyle());

		sizeEdge.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		strokeWidthEdge.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		sizeArrow1.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		sizeArrow2.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		//------ Listeners Sprite
		for(ToggleButton b : shapeSprite)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ToggleButton b : strokeSprite)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		for(ColorPicker c : colorSpriteBack)
			c.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		for(ToggleButton b : typeBackSprite)
			b.addEventFilter(ActionEvent.ACTION, new ActionResetGroup());
		
		strokeSpriteColor.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		sizeSprite.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		strokeWidthSprite.addEventFilter(ActionEvent.ACTION, new UpdateStyle());
		
		// ======================= Format
		// ----- Format TextField
		
		paddingGraph.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	paddingGraph.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		shadowWidth.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	shadowWidth.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		shadowOffset1.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	shadowOffset1.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		shadowOffset2.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	shadowOffset2.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		strokeWidthNode.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	strokeWidthNode.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		sizeNode.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	sizeNode.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		sizeEdge.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	sizeEdge.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		strokeWidthEdge.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	strokeWidthEdge.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		sizeArrow1.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	sizeArrow1.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		sizeArrow2.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	sizeArrow2.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		strokeWidthSprite.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	strokeWidthSprite.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		sizeSprite.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	sizeSprite.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
	}
	
	private Tab tabGraph() {
		Tab tab = new Tab();
		tab.setText("Graph");
		
		TitledPane backgroundTitlePane = new TitledPane();
		backgroundTitlePane.setText("Background");
		HBox typeBack = new HBox() ;
		for(ToggleButton b : typeBackGraph)
			typeBack.getChildren().add(b);
		
		HBox colors = new HBox();
		for(ColorPicker c : colorGraphBack)
			colors.getChildren().add(c);
		
		VBox style = new VBox();
		style.getChildren().add(typeBack);
		style.getChildren().add(colors);
		backgroundTitlePane.setContent(style);
		
		// ---------------

		TitledPane styleTitlePane = new TitledPane();
		styleTitlePane.setText("Style");
		
		HBox paddingLayout = new HBox();
		Label label = new Label("Padding :"); 
		label.setPadding(new Insets(5, 5, 0, 5));
		paddingLayout.getChildren().add(label);
		
		paddingLayout.getChildren().add(paddingGraph);
		styleTitlePane.setContent(paddingLayout);
		
		// ===========================
		VBox hbox = new VBox();
		hbox.getChildren().add(backgroundTitlePane);
		hbox.getChildren().add(styleTitlePane);

		hbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(hbox);
		

		return tab;
	}

	private Tab tabNode() {
		Tab tab = new Tab();
		tab.setText("Node");
		
		TitledPane shapeTitlePane = new TitledPane();
		shapeTitlePane.setText("Shape");
		HBox shapeNodeBox = new HBox() ;
		for(ToggleButton b : shapeNode)
			shapeNodeBox.getChildren().add(b);
		shapeNodeBox.getChildren().add(shapeNodeImage);
		shapeTitlePane.setContent(shapeNodeBox);
		
		//-------------------------------------
		TitledPane strokeTitlePane = new TitledPane();
		strokeTitlePane.setText("Stroke");
		HBox strokeNodeBox = new HBox() ;
		for(ToggleButton b : strokeNode)
			strokeNodeBox.getChildren().add(b);
		strokeNodeBox.getChildren().add(strokeNodeColor);
		strokeTitlePane.setContent(strokeNodeBox);
		
		//----------------------------------------
		TitledPane backTitlePane = new TitledPane();
		backTitlePane.setText("Background");
		
		HBox typeBack = new HBox() ;
		for(ToggleButton b : typeBackNode)
			typeBack.getChildren().add(b);
		HBox backNodeBox = new HBox() ;
		for(ColorPicker p : colorNodeBack)
			backNodeBox.getChildren().add(p);
		
		VBox background = new VBox();
		background.getChildren().add(typeBack);
		background.getChildren().add(backNodeBox);
		backTitlePane.setContent(background);
		
		//-------------------------------------------
		TitledPane shadowTitlePane = new TitledPane();
		shadowTitlePane.setText("Shadow");
		
		HBox typeShadow = new HBox() ;
		for(ToggleButton b : typeShadowNode)
			typeShadow.getChildren().add(b);
		HBox backShadowBox = new HBox() ;
		for(ColorPicker p : colorShadowBack)
			backShadowBox.getChildren().add(p);
		
		VBox shadow = new VBox();
		shadow.getChildren().add(typeShadow);
		shadow.getChildren().add(backShadowBox);
		shadowTitlePane.setContent(shadow);
		
		//--------------------------------
		TitledPane clickTitlePane = new TitledPane();
		clickTitlePane.setText("Clicked");
		
		HBox clickBox = new HBox() ;
		for(ColorPicker p : colorClickNode)
			clickBox.getChildren().add(p);
		
		clickTitlePane.setContent(clickBox);
		
		//--------------------------------
		TitledPane selectTitlePane = new TitledPane();
		selectTitlePane.setText("Selected");
				
		HBox selectBox = new HBox() ;
		for(ColorPicker p : colorSelectNode)
			selectBox.getChildren().add(p);
				
		selectTitlePane.setContent(selectBox);
		
		//--------------------------------
		TitledPane styleTitlePane = new TitledPane();
		styleTitlePane.setText("Style");
				
		VBox styleBox = new VBox() ;
		styleBox.setSpacing(10);
		
		HBox stylebox1 = new HBox();
		Label label = new Label("Size :"); 
		label.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label); stylebox1.getChildren().add(sizeNode);
		Label label2 = new Label("Stroke-width : "); 
		label2.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label2); stylebox1.getChildren().add(strokeWidthNode);
		styleBox.getChildren().add(stylebox1);
		
		HBox stylebox2 = new HBox();
		Label label3 = new Label("Shadow-width :"); 
		label3.setPadding(new Insets(5, 5, 0, 5));
		stylebox2.getChildren().add(label3); stylebox2.getChildren().add(shadowWidth);
		styleBox.getChildren().add(stylebox2);
		
		HBox stylebox3 = new HBox();
		Label label4 = new Label("Shadow-offset :");  
		label4.setPadding(new Insets(5, 5, 0, 5));
		stylebox3.getChildren().add(label4); stylebox3.getChildren().add(shadowOffset1); stylebox3.getChildren().add(shadowOffset2);
		styleBox.getChildren().add(stylebox3);

		
		styleTitlePane.setContent(styleBox);
		//----------------------------------
		VBox vbox = new VBox();
		vbox.getChildren().add(shapeTitlePane);
		vbox.getChildren().add(strokeTitlePane);
		vbox.getChildren().add(backTitlePane);
		vbox.getChildren().add(shadowTitlePane);
		vbox.getChildren().add(clickTitlePane);
		vbox.getChildren().add(selectTitlePane);
		vbox.getChildren().add(styleTitlePane);
		
		vbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(vbox);

		return tab;
	}
	
	private Tab tabSprite() {
		Tab tab = new Tab();
		tab.setText("Sprite");
		
		TitledPane shapeTitlePane = new TitledPane();
		shapeTitlePane.setText("Shape");
		HBox shapeSpriteBox = new HBox() ;
		for(ToggleButton b : shapeSprite)
			shapeSpriteBox.getChildren().add(b);
		shapeSpriteBox.getChildren().add(shapeSpriteImage);
		shapeTitlePane.setContent(shapeSpriteBox);
		
		//-------------------------------------
		TitledPane strokeTitlePane = new TitledPane();
		strokeTitlePane.setText("Stroke");
		HBox strokeSpriteBox = new HBox() ;
		for(ToggleButton b : strokeSprite)
			strokeSpriteBox.getChildren().add(b);
		strokeSpriteBox.getChildren().add(strokeSpriteColor);
		strokeTitlePane.setContent(strokeSpriteBox);
		
		//----------------------------------------
		TitledPane backTitlePane = new TitledPane();
		backTitlePane.setText("Background");
		
		HBox typeBack = new HBox() ;
		for(ToggleButton b : typeBackSprite)
			typeBack.getChildren().add(b);
		HBox backSpriteBox = new HBox() ;
		for(ColorPicker p : colorSpriteBack)
			backSpriteBox.getChildren().add(p);
		
		VBox background = new VBox();
		background.getChildren().add(typeBack);
		background.getChildren().add(backSpriteBox);
		backTitlePane.setContent(background);
		
		//--------------------------------
		TitledPane styleTitlePane = new TitledPane();
		styleTitlePane.setText("Style");
				
		VBox styleBox = new VBox() ;
		styleBox.setSpacing(10);
		
		HBox stylebox1 = new HBox();
		Label label = new Label("Size :"); 
		label.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label); stylebox1.getChildren().add(sizeSprite);
		Label label2 = new Label("Stroke-width : "); 
		label2.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label2); stylebox1.getChildren().add(strokeWidthSprite);
		styleBox.getChildren().add(stylebox1);

		styleTitlePane.setContent(styleBox);
		//----------------------------------
		VBox vbox = new VBox();
		vbox.getChildren().add(shapeTitlePane);
		vbox.getChildren().add(strokeTitlePane);
		vbox.getChildren().add(backTitlePane);
		vbox.getChildren().add(styleTitlePane);
		
		vbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(vbox);

		return tab;
	}

	private Tab tabEdge() {
		Tab tab = new Tab();
		tab.setText("Edge");
		
		TitledPane shapeTitlePane = new TitledPane();
		shapeTitlePane.setText("Shape");
		HBox shapeEdgeBox = new HBox() ;
		for(ToggleButton b : shapeEdge)
			shapeEdgeBox.getChildren().add(b);
		
		shapeTitlePane.setContent(shapeEdgeBox);
		
		//----------------------------------
		TitledPane backTitlePane = new TitledPane();
		backTitlePane.setText("Background");
		
		HBox typeBack = new HBox() ;
		for(ToggleButton b : typeBackEdge)
			typeBack.getChildren().add(b);
		HBox backEdgeBox = new HBox() ;
		for(ColorPicker p : colorBackEdge)
			backEdgeBox.getChildren().add(p);
		
		VBox background = new VBox();
		background.getChildren().add(typeBack);
		background.getChildren().add(backEdgeBox);
		backTitlePane.setContent(background);
		
		//----------------------------------
		TitledPane arrowTitlePane = new TitledPane();
		arrowTitlePane.setText("Arrow");
		HBox shapeArrowBox = new HBox() ;
		for(ToggleButton b : shapeArrow)
			shapeArrowBox.getChildren().add(b);
		
		arrowTitlePane.setContent(shapeArrowBox);
		
		//-------------------------------------
		TitledPane strokeTitlePane = new TitledPane();
		strokeTitlePane.setText("Stroke");
		HBox strokeEdgeBox = new HBox() ;
		for(ToggleButton b : strokeEdge)
			strokeEdgeBox.getChildren().add(b);
		strokeEdgeBox.getChildren().add(strokeEdgeColor);
		strokeTitlePane.setContent(strokeEdgeBox);
		
		//--------------------------------
		TitledPane styleTitlePane = new TitledPane();
		styleTitlePane.setText("Style");
				
		VBox styleBox = new VBox() ;
		styleBox.setSpacing(10);
		
		HBox stylebox1 = new HBox();
		Label label = new Label("Size :"); 
		label.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label); stylebox1.getChildren().add(sizeEdge);
		Label label2 = new Label("Stroke-width : "); 
		label2.setPadding(new Insets(5, 5, 0, 5));
		stylebox1.getChildren().add(label2); stylebox1.getChildren().add(strokeWidthEdge);
		styleBox.getChildren().add(stylebox1);
		
		HBox stylebox3 = new HBox();
		Label label4 = new Label("Arrow-size :");  
		label4.setPadding(new Insets(5, 5, 0, 5));
		stylebox3.getChildren().add(label4); stylebox3.getChildren().add(sizeArrow1); stylebox3.getChildren().add(sizeArrow2);
		styleBox.getChildren().add(stylebox3);
		
		styleTitlePane.setContent(styleBox);

		//----------------------------------
		VBox vbox = new VBox();
		vbox.getChildren().add(shapeTitlePane);
		vbox.getChildren().add(backTitlePane);
		vbox.getChildren().add(strokeTitlePane);
		vbox.getChildren().add(arrowTitlePane);
		vbox.getChildren().add(styleTitlePane);
		
		vbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(vbox);
		return tab;
	}
	
	class ActionResetGroup implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if ( !((ToggleButton)event.getSource()).isSelected() ) {
				((ToggleButton)event.getSource()).setSelected(false);
			}
			else {
				resetToggleButton(event);
				((ToggleButton)event.getSource()).setSelected(true);
				updateStyle();
			}
		}

		private void resetToggleButton(ActionEvent event) {
			if ( Arrays.asList(typeBackGraph).contains(event.getSource())) {
				for (ToggleButton b : typeBackGraph)
					b.setSelected(false);
			}
			else if ( Arrays.asList(shapeNode).contains(event.getSource())) {
				for (ToggleButton b : shapeNode)
					b.setSelected(false);
			}
			else if ( Arrays.asList(strokeNode).contains(event.getSource())) {
				for (ToggleButton b : strokeNode)
					b.setSelected(false);
			}
			else if ( Arrays.asList(typeBackNode).contains(event.getSource())) {
				for (ToggleButton b : typeBackNode)
					b.setSelected(false);
			}
			else if ( Arrays.asList(typeShadowNode).contains(event.getSource())) {
				for (ToggleButton b : typeShadowNode)
					b.setSelected(false);
			}
			else if ( Arrays.asList(shapeEdge).contains(event.getSource())) {
				for (ToggleButton b : shapeEdge)
					b.setSelected(false);
			}
			else if ( Arrays.asList(typeBackEdge).contains(event.getSource())) {
				for (ToggleButton b : typeBackEdge)
					b.setSelected(false);
			}
			else if ( Arrays.asList(shapeArrow).contains(event.getSource())) {
				for (ToggleButton b : shapeArrow)
					b.setSelected(false);
			}
			else if ( Arrays.asList(strokeEdge).contains(event.getSource())) {
				for (ToggleButton b : strokeEdge)
					b.setSelected(false);
			}
			else if ( Arrays.asList(shapeSprite).contains(event.getSource())) {
				for (ToggleButton b : shapeSprite)
					b.setSelected(false);
			}
			else if ( Arrays.asList(strokeSprite).contains(event.getSource())) {
				for (ToggleButton b : strokeSprite)
					b.setSelected(false);
			}
			else if ( Arrays.asList(typeBackSprite).contains(event.getSource())) {
				for (ToggleButton b : typeBackSprite)
					b.setSelected(false);
			}
		}
	}
	
	class UpdateStyle implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			updateStyle();
		}
	}
	
	public void updateStyle() {
		// ===== GRAPH ===== //
		HashMap<String, String> styleSheetGraph = new HashMap<>();

		if( typeBackGraph[1].isSelected() )
			styleSheetGraph.put("fill-mode",  "gradient-vertical");
		else if( typeBackGraph[2].isSelected() )
			styleSheetGraph.put("fill-mode",  "gradient-horizontal");
		else if( typeBackGraph[3].isSelected() )
			styleSheetGraph.put("fill-mode",  "gradient-radial");
		else if( typeBackGraph[4].isSelected() )
			styleSheetGraph.put("fill-mode",  "gradient-diagonal1");
		else if( typeBackGraph[5].isSelected() )
			styleSheetGraph.put("fill-mode",  "gradient-diagonal2");
		else if ( typeBackGraph[0].isSelected() )
			styleSheetGraph.put("fill-mode",  "plain");
		
		if ( !paddingGraph.getText().equals("") )
			styleSheetGraph.put("padding", Integer.parseInt(paddingGraph.getText())+"px");
			
		styleSheetGraph.put("fill-color", getRGB(colorGraphBack[0].getValue())+","+getRGB(colorGraphBack[1].getValue())+","+getRGB(colorGraphBack[2].getValue()));
		
		styleSheet.put("graph", styleSheetGraph);
		
		// ===== NODE ===== //
		HashMap<String, String> styleSheetNode = new HashMap<>();

		if( shapeNode[1].isSelected() )
			styleSheetNode.put("shape",  "box");
		else if( shapeNode[2].isSelected() )
			styleSheetNode.put("shape", "rounded-box");
		else if( shapeNode[3].isSelected() )
			styleSheetNode.put("shape", "diamond");
		else if( shapeNode[4].isSelected() )
			styleSheetNode.put("shape", "cross");
		else if( shapeNode[5].isSelected() )
			styleSheetNode.put("shape", "freeplane");
		else if ( shapeNode[0].isSelected() ) 
			styleSheetNode.put("shape", "circle");
		
		if ( shapeNodeImage.isSelected() && fileNode != null) {			
			styleSheetNode.put("icon-mode", "above");
			styleSheetNode.put("icon", "url('"+fileNode.getAbsolutePath()+"')");
		}
		
		if( typeBackNode[1].isSelected() )
			styleSheetNode.put("fill-mode",  "gradient-vertical");
		else if( typeBackNode[2].isSelected() )
			styleSheetNode.put("fill-mode",  "gradient-horizontal");
		else if( typeBackNode[3].isSelected() )
			styleSheetNode.put("fill-mode",  "gradient-radial");
		else if( typeBackNode[4].isSelected() )
			styleSheetNode.put("fill-mode",  "gradient-diagonal1");
		else if( typeBackNode[5].isSelected() )
			styleSheetNode.put("fill-mode",  "gradient-diagonal2");
		else if ( typeBackNode[0].isSelected() )
			styleSheetNode.put("fill-mode",  "plain");
		
		if ( strokeNode[0].isSelected() ) 
			styleSheetNode.put("stroke-mode", "plain");
		else if( strokeNode[1].isSelected() )
			styleSheetNode.put("stroke-mode",  "dots");
		else if( strokeNode[2].isSelected() )
			styleSheetNode.put("stroke-mode", "dashes");
		else if( strokeNode[3].isSelected() )
			styleSheetNode.put("stroke-mode", "double");
		
		styleSheetNode.put("stroke-color", getRGB(strokeNodeColor.getValue()));
		styleSheetNode.put("fill-color", getRGB(colorNodeBack[0].getValue())+","+getRGB(colorNodeBack[1].getValue())+","+getRGB(colorNodeBack[2].getValue()));
		
		if ( !typeShadowNode[4].isSelected() ) {
			if( typeShadowNode[1].isSelected() )
				styleSheetNode.put("shadow-mode",  "gradient-vertical");
			else if( typeShadowNode[2].isSelected() )
				styleSheetNode.put("shadow-mode",  "gradient-horizontal");
			else if( typeShadowNode[3].isSelected() )
				styleSheetNode.put("shadow-mode",  "gradient-radial");
			else if ( typeShadowNode[0].isSelected() )
				styleSheetNode.put("shadow-mode",  "plain");
			
			styleSheetNode.put("shadow-color", getRGB(colorShadowBack[0].getValue())+","+getRGB(colorShadowBack[1].getValue())+","+getRGB(colorShadowBack[2].getValue()));
		}
		
		if ( !sizeNode.getText().equals("") )
			styleSheetNode.put("size", sizeNode.getText()+"px");
		if ( !strokeWidthNode.getText().equals("") )
			styleSheetNode.put("stroke-width", strokeWidthNode.getText()+"px");
		if ( !shadowWidth.getText().equals("") )
			styleSheetNode.put("shadow-width", shadowWidth.getText()+"px");
		if ( !shadowOffset1.getText().equals("") && !shadowOffset2.getText().equals("") )
			styleSheetNode.put("shadow-offset", shadowOffset1.getText()+"px, "+shadowOffset2.getText());
		
		styleSheet.put("node", styleSheetNode);
		
		HashMap<String, String> clickedHash = new HashMap<>();
		clickedHash.put("fill-color", getRGB(colorClickNode[0].getValue())+","+getRGB(colorClickNode[1].getValue())+","+getRGB(colorClickNode[2].getValue()));
		styleSheet.put("node:clicked", clickedHash);
		
		HashMap<String, String> selectedHash = new HashMap<>();
		selectedHash.put("fill-color", getRGB(colorSelectNode[0].getValue())+","+getRGB(colorSelectNode[1].getValue())+","+getRGB(colorSelectNode[2].getValue()));
		styleSheet.put("node:selected", selectedHash);
		
		
		// ===== EDGE ===== //
		HashMap<String, String> styleSheetEdge = new HashMap<>();		
		
		if( shapeEdge[1].isSelected() )
			styleSheetEdge.put("shape", "angle");
		else if( shapeEdge[2].isSelected() )
			styleSheetEdge.put("shape", "cubic-curve");
		else if( shapeEdge[3].isSelected() )
			styleSheetEdge.put("shape", "blob");
		else if( shapeEdge[4].isSelected() )
			styleSheetEdge.put("shape", "freeplane");
		else if( shapeEdge[5].isSelected() )
			styleSheetEdge.put("shape", "L-square-line");
		else if ( shapeEdge[0].isSelected() ) 
			styleSheetEdge.put("shape", "line");
		
		
		if( typeBackEdge[1].isSelected() )
			styleSheetEdge.put("fill-mode",  "gradient-vertical");
		else if( typeBackEdge[2].isSelected() )
			styleSheetEdge.put("fill-mode",  "gradient-horizontal");
		else if( typeBackEdge[3].isSelected() )
			styleSheetEdge.put("fill-mode",  "gradient-radial");
		else if( typeBackEdge[4].isSelected() )
			styleSheetEdge.put("fill-mode",  "gradient-diagonal1");
		else if( typeBackEdge[5].isSelected() )
			styleSheetEdge.put("fill-mode",  "gradient-diagonal2");
		else if ( typeBackEdge[0].isSelected() )
			styleSheetEdge.put("fill-mode",  "plain");

		styleSheetEdge.put("fill-color", getRGB(colorBackEdge[0].getValue())+","+getRGB(colorBackEdge[1].getValue())+","+getRGB(colorBackEdge[2].getValue()));

		if( shapeArrow[0].isSelected() )
			styleSheetEdge.put("arrow-shape", "arrow");
		else if( shapeArrow[1].isSelected() )
			styleSheetEdge.put("arrow-shape", "none");
		else if( shapeArrow[2].isSelected() )
			styleSheetEdge.put("arrow-shape", "circle");
		else if( shapeArrow[3].isSelected() )
			styleSheetEdge.put("arrow-shape", "diamond");
		
		if ( !strokeEdge[4].isSelected() ) {
			if ( strokeEdge[0].isSelected() ) 
				styleSheetEdge.put("stroke-mode", "plain");
			else if( strokeEdge[1].isSelected() )
				styleSheetEdge.put("stroke-mode",  "dots");
			else if( strokeEdge[2].isSelected() )
				styleSheetEdge.put("stroke-mode", "dashes");
			else if( strokeEdge[3].isSelected() )
				styleSheetEdge.put("stroke-mode", "double");
			
			styleSheetEdge.put("stroke-color", getRGB(strokeEdgeColor.getValue()));
		}		
		
		if ( !sizeEdge.getText().equals("") )
			styleSheetEdge.put("size", sizeEdge.getText()+"px");
		if ( !strokeWidthEdge.getText().equals("") )
			styleSheetEdge.put("stroke-width", strokeWidthEdge.getText()+"px");
		if ( !sizeArrow1.getText().equals("") && !sizeArrow2.getText().equals("") )
			styleSheetEdge.put("arrow-size", sizeArrow1.getText()+"px, "+sizeArrow2.getText()+"px");
		
		
		styleSheet.put("edge", styleSheetEdge);
		
		// ===== SPRITE ===== //
		HashMap<String, String> styleSheetSprite = new HashMap<>();

		if( shapeSprite[1].isSelected() )
			styleSheetSprite.put("shape",  "box");
		else if( shapeSprite[2].isSelected() )
			styleSheetSprite.put("shape", "rounded-box");
		else if( shapeSprite[3].isSelected() )
			styleSheetSprite.put("shape", "diamond");
		else if( shapeSprite[4].isSelected() )
			styleSheetSprite.put("shape", "cross");
		else if( shapeSprite[5].isSelected() )
			styleSheetSprite.put("shape", "freeplane");
		else if ( shapeSprite[0].isSelected() ) 
			styleSheetSprite.put("shape", "circle");
		
		if ( shapeSpriteImage.isSelected() && fileSprite != null) {			
			styleSheetSprite.put("icon-mode", "above");
			styleSheetSprite.put("icon", "url('"+fileSprite.getAbsolutePath()+"')");
		}
		
		if( typeBackSprite[1].isSelected() )
			styleSheetSprite.put("fill-mode",  "gradient-vertical");
		else if( typeBackSprite[2].isSelected() )
			styleSheetSprite.put("fill-mode",  "gradient-horizontal");
		else if( typeBackSprite[3].isSelected() )
			styleSheetSprite.put("fill-mode",  "gradient-radial");
		else if( typeBackSprite[4].isSelected() )
			styleSheetSprite.put("fill-mode",  "gradient-diagonal1");
		else if( typeBackSprite[5].isSelected() )
			styleSheetSprite.put("fill-mode",  "gradient-diagonal2");
		else if ( typeBackSprite[0].isSelected() )
			styleSheetSprite.put("fill-mode",  "plain");
		
		if ( strokeSprite[0].isSelected() ) 
			styleSheetSprite.put("stroke-mode", "plain");
		else if( strokeSprite[1].isSelected() )
			styleSheetSprite.put("stroke-mode",  "dots");
		else if( strokeSprite[2].isSelected() )
			styleSheetSprite.put("stroke-mode", "dashes");
		else if( strokeSprite[3].isSelected() )
			styleSheetSprite.put("stroke-mode", "double");
		
		styleSheetSprite.put("stroke-color", getRGB(strokeSpriteColor.getValue()));
		styleSheetSprite.put("fill-color", getRGB(colorSpriteBack[0].getValue())+","+getRGB(colorSpriteBack[1].getValue())+","+getRGB(colorSpriteBack[2].getValue()));
		
		if ( !sizeSprite.getText().equals("") )
			styleSheetSprite.put("size", sizeSprite.getText()+"px");
		if ( !strokeWidthSprite.getText().equals("") )
			styleSheetSprite.put("stroke-width", strokeWidthSprite.getText()+"px");
		
		styleSheet.put("sprite", styleSheetSprite);
		
		System.out.println(getStyle(styleSheet));
		cssArea.setText( getStyle(styleSheet) );
	}
	
	public String getRGB(Color c) {
		String hex = String.format( "#%02X%02X%02X%02X",
	            (int)( c.getRed() * 255 ),
	            (int)( c.getGreen() * 255 ),
	            (int)( c.getBlue() * 255 ),
				(int)( c.getOpacity()* 255 ) );
		return hex ;
	}
	
	private String getStyle(HashMap<String, HashMap<String, String>> classes) {
		String style = "";
		
		for (  String idClass : classes.keySet()) {
			style += idClass+"{ \n";
			
			for ( String idAttribute : classes.get(idClass).keySet()) {
				style += "\t"+idAttribute+":"+classes.get(idClass).get(idAttribute)+"; \n";
			}
			
			style += "}\n\n";
		}
		
		return style;
	}
	
	public Stage getStageCSS() {
		return stageCSS;
	}
	
	public void saveCSS() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(new Stage());
		
		if (file != null) {
			try {
				if(file.getName().split("\\.").length == 1) {
					File f = new File(file.getAbsolutePath()+".css");
					Files.write(cssArea.getText(), f, Charset.defaultCharset());
				}
				else {
					Files.write(cssArea.getText(), file, Charset.defaultCharset());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }		
	}
	
	public void loadCSS() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(new Stage());
		
		if (file != null) {
			try {
				List<String> text = Files.readLines(file, Charset.defaultCharset());
				cssArea.clear();
				for(String line : text) {
					cssArea.setText(cssArea.getText()+line+"\n");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
