package org.graphstream.app.click;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.graphstream.app.click.tab.CSSGenerator;
import org.graphstream.app.click.tab.MenuBarGraph;
import org.graphstream.app.click.tab.TabAlgorithm;
import org.graphstream.app.click.tab.TabGenerator;
import org.graphstream.app.click.tab.TabGraph;
import org.graphstream.graph.Element;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.util.FxMouseManager;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Main class
 * Creates the different parts of the application (graph viewer and toolbox)
 * @author hicham
 */
public class Controller extends Application implements ViewerListener {
	
	// ========= Controller ============
	private Stage stageGraph;
	private Stage stageToolbox;
	private boolean clicInGraph = false ;
	
	// -- Tab
	private TabPane tabPane ;
	
	private TabGraph tabGraph;
	private TabGenerator tabGenerator;
	private TabAlgorithm tabAlgorithm;
	private MenuBarGraph menu ;
	
	private ArrayList<Tab> closedTab ;
	
	// -- CSS Section --
	private TextArea css ;
	private Button applyCss ;
	private Button cssGenerator;
	private CSSGenerator help;
	
	//  ========= Node creation
	public static int autoId = 1 ;
	
	// ========= Edge creation
	private String nodeSelected = null ;
	
	// ========= Graph
	private MultiGraph graph ;
	private FxViewPanel panel;
	private FxViewer viewer;
	private boolean loop = true ;
	
	// ========= Info Pane
	private TabPane infoNodePane ;
	private TabPane infoEdgePane ;
	private final int LIMIT_ATTRIBUTE = 10;
	private AtomicInteger limit ;
	//private HashMap<String, TextField> infoNode ;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initUI() {
		// -- Section Graph
		tabGraph = new TabGraph(this);
		tabGenerator = new TabGenerator(this);
		tabAlgorithm = new TabAlgorithm(this);
		closedTab = new ArrayList<>() ;
		
		// -- Section CSS
		css = new TextArea("graph{}\n\nnode{}\n\nnode:clicked{\n\tfill-color: red;\n}\n\nnode:selected{\n\tfill-color: blue;\n}\n\nedge{}\n") ;
		css.setPrefWidth(270);		
		applyCss = new Button("Apply");
		cssGenerator = new Button("CSS Generator");
		cssGenerator.setTooltip(new Tooltip("Warning : Overload style"));

		menu = new MenuBarGraph(this, tabGraph);
		help = new CSSGenerator(css, menu);
		menu.setHelpCSS(help);
		// ============ Listeners
		
		//------ Listeners CSS
		applyCss.addEventFilter(ActionEvent.ACTION, e -> {
			applyCss();
		});
		
		cssGenerator.addEventFilter(ActionEvent.ACTION, e -> {
			help.createCSSHelp(stageToolbox, stageGraph.getX(), stageGraph.getY());
			menu.openCSS();
		});
	}

	public void initGraph(MultiGraph graph) {
		this.graph = graph;
		
		viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		ViewerPipe pipeIn = viewer.newViewerPipe();
		
		panel = (FxViewPanel)viewer.addDefaultView(false, new FxGraphRenderer());
		
		panel.setMouseManager(new FxMouseManager(EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE)));
	
		panel.addEventFilter(MouseEvent.MOUSE_PRESSED, new MousePressGraph());
		panel.addEventFilter(MouseEvent.MOUSE_RELEASED, new MouseReleaseGraph());
		panel.addEventFilter(MouseEvent.ANY, e -> tabGraph.updateCameraView());
		panel.addEventFilter(KeyEvent.ANY, e -> tabGraph.updateCameraView() );
		
		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();
		
		new Thread( () -> {
			while( loop ) {
				pipeIn.pump();
				sleep(100);
			}	
		}).start();
		
		infoNodePane = new TabPane();
		infoEdgePane = new TabPane();
		SplitPane infoPane = new SplitPane(infoNodePane, infoEdgePane);
		infoPane.setOrientation(Orientation.VERTICAL);
		SplitPane mainPane = new SplitPane(panel, infoPane);
		mainPane.setDividerPositions(0.8, 0.2);
		
		BorderPane mainPaneWithMenu = new BorderPane(mainPane);
		mainPaneWithMenu.setTop(new VBox(menu.getMenuBar()));
		Scene scene = new Scene(mainPaneWithMenu, 800, 600);
		stageGraph.setScene(scene);
	}
	
	@Override
	public void start(Stage stage) {
		this.stageGraph = stage ;	
		initUI();
		initGraph(new MultiGraph("Graph"));
		
		//viewer.enableAutoLayout();
		tabGraph.cameraBuildConfig();
		//Node center = graph.addNode("0");
		//center.setAttribute("xyz",new double[] { 400, 300, 0 });
	
		stage.show();
		
		createToolBox(stage);
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				stageToolbox.close();
				loop = false ;
			}
		});
	}
	
	private void createToolBox(Stage stage) {
		stageToolbox = new Stage();
		stageToolbox.setTitle("ToolBox");
		stageToolbox.initStyle(StageStyle.UTILITY);
		stageToolbox.setX(stage.getX() + stage.getWidth());
		stageToolbox.setY(stage.getY());

		tabPane = new TabPane();

		tabPane.getTabs().add(tabGraph.getTab());
		tabPane.getTabs().add(tabStyle());
		tabPane.getTabs().add(tabGenerator.getTab());
		tabPane.getTabs().add(tabAlgorithm.getTab());
		
		// layout the utility pane.
		VBox utilityLayout = new VBox(10);
		utilityLayout.setStyle(
				"-fx-padding:10; -fx-background-color: linear-gradient(to bottom, lightblue, derive(lightblue, 20%));");
	
		utilityLayout.getChildren().add(tabPane);
		utilityLayout.setPrefHeight(600);
		stageToolbox.setScene(new Scene(utilityLayout));
		stageToolbox.show();
		
		stageToolbox.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent windowEvent) {
				menu.closeToolbox();
			}
		});
	}

	public void closeTab(Event e) {
		Tab tab = (Tab)e.getSource();
		closedTab.add(tab);
	}
	
	public void restoreTab() {
		for (Tab t : closedTab) {
			tabPane.getTabs().add(t);
		}
		
		closedTab.clear(); 
	}

	private Tab tabStyle() {
		Tab tab = new Tab();
		tab.setText("Style");
		tab.setOnClosed(e -> closeTab(e));
		
		BorderPane vbox = new BorderPane();
		vbox.setCenter(css);
		
		HBox alignCenter = new HBox() ;
		alignCenter.getChildren().add(applyCss);
		alignCenter.getChildren().add(cssGenerator);
		alignCenter.setAlignment(Pos.CENTER);
		vbox.setBottom(alignCenter);
		//vbox.setAlignment(Pos.CENTER);
		tab.setContent(vbox);
		
		return tab;
	}
	
	@Override
	public void viewClosed(String viewName) {
		// TODO Auto-generated method stub
		loop = false ;
	}

	@Override
	public void buttonPushed(String id) {
		clicInGraph = true ;
		System.out.println("ID= "+id);
		if (tabGraph.getRemove().isSelected() ) {
			graph.removeNode(id);
		}
		else if (tabGraph.getNewEdge().isSelected()) {
			if ( nodeSelected == null ) {
				nodeSelected = id ;
				tabGraph.getSelection().setText("Current selection (Edge build) : "+id) ;
			}
			else {
				boolean edgeAdded = false ;
				String idEdge = id+" - "+nodeSelected ;
				while(!edgeAdded) {
					try {
						graph.addEdge(idEdge, nodeSelected, id, tabGraph.getCheckDirectedEdge().isSelected());
						edgeAdded = true ;
					} catch (IdAlreadyInUseException e) {
						idEdge += "'";
						edgeAdded = false ;
					}			
				}
				nodeSelected = null ;
				tabGraph.getSelection().setText("Current selection (Edge build) : -") ;
			}
		}
		/*
		graph.nodes().forEach(System.out::println);
		System.out.println("edge -----------------------");
		graph.edges().forEach(System.out::println);
		System.out.println("------------------------");
		*/
	}
	
	@Override
	public void buttonReleased(String id) {
		System.out.println("buttonReleased = "+clicInGraph+" ");
		
		graph.edges().forEach(n -> System.out.println(n.getAttribute("ui.selected")));
		
		clicInGraph = false ;
	}
	
	@Override
	public void mouseOver(String id) {}

	@Override
	public void mouseLeft(String id) {}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	class MousePressGraph implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent e) {
			sleep(100);
			if ( tabGraph.getNewNode().isSelected() && !clicInGraph ) {
				Node n = graph.addNode("M-"+autoId);
				Point3 p = panel.getCamera().transformPxToGu(e.getX(), e.getY()) ;
				
				n.setAttribute("xyz",new double[] { p.x, p.y, 0 });	 
				
				autoId++;
			}
		}
	}
	
	class MouseReleaseGraph implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent e) {
			sleep(100);
			updateInfoPane();
			
			if ( tabGraph.getRemove().isSelected() && !clicInGraph ) {
				graph.nodes()
				.filter(n -> (n.getAttribute("ui.selected") != null))
				.filter(n -> ((boolean)n.getAttribute("ui.selected")))
				.collect(Collectors.toList())
				.forEach(graph::removeNode);
				
				graph.edges()
				.filter(n -> (n.getAttribute("ui.selected") != null))
				.filter(n -> ((boolean)n.getAttribute("ui.selected")))
				.collect(Collectors.toList())
				.forEach(graph::removeEdge);
				
				StreamSupport.stream(tabGraph.getSpriteManager().spliterator(), false)
				.filter(s -> (s.getAttribute("ui.selected") != null))
				.filter(s -> ((boolean)s.getAttribute("ui.selected")))
				.collect(Collectors.toList())
				.forEach(s -> tabGraph.getSpriteManager().removeSprite(s.getId()));
				
				/*
				for ( Sprite s : tabGraph.getSpriteManager().sprites()) {
					if ( s.getAttribute("ui.selected") != null && ((boolean)s.getAttribute("ui.selected")) ) {
						tabGraph.getSpriteManager().removeSprite(s.getId());
					}
				}*/
			}
		}
	}
	
	public void applyCss() {
		graph.removeAttribute("ui.stylesheet");

		System.out.println(css.getText());
		graph.setAttribute( "ui.stylesheet", css.getText() );
	}
	
	public FxViewPanel getPanel() {
		return panel;
	}
	
	public MultiGraph getGraph() {
		return graph;
	}
	
	public void setGraph(MultiGraph graph) {
		initGraph(graph);
	}
	
	public void setNodeSelected(String id) {
		this.nodeSelected = id ;
	}
	
	public TabGraph getTabGraph() {
		return tabGraph;
	}
	
	public Stage getStageGraph() {
		return stageGraph;
	}
	
	public Stage getStageToolbox() {
		return stageToolbox;
	}
	
	private boolean isNumeric(String s) {
		try	{
			Double.parseDouble(s.trim());
			return true ;
		}
		catch(NumberFormatException e) {
			return false ;
		}
	}
	
	/**
	 * Print node selected attributes in Pane
	 */
	private void updateInfoPane() {
		infoNodePane.getTabs().clear();
		infoEdgePane.getTabs().clear();
		limit = new AtomicInteger(0);
		
		graph.nodes().forEach(n -> {
			updateInfoElement(n, infoNodePane, true);
		});	
		
		limit = new AtomicInteger(0);
		
		graph.edges().forEach(n -> {
			updateInfoElement(n, infoEdgePane, false);
		});	
	}
	
	private void updateInfoElement(Element n, TabPane pane, boolean addNode) {
		VBox vbox = new VBox(5);

		if (n.getAttribute("ui.selected") != null && n.getAttribute("ui.selected") instanceof Boolean 
				&& (boolean) n.getAttribute("ui.selected") && limit.get() <= LIMIT_ATTRIBUTE) {
			vbox.getChildren().add(new Separator());
			n.attributeKeys().forEach(attr -> {
				// If it's a alterable value
				if ( n.getAttribute(attr) instanceof String || n.getAttribute(attr) instanceof Integer 
						|| n.getAttribute(attr) instanceof Double || n.getAttribute(attr) instanceof Boolean) {
					
					TextField info = new TextField() ;
					info.setText(n.getAttribute(attr)+"");
					info.addEventFilter(ActionEvent.ACTION, e -> {
						Object value = null;
						if(info.getText() != null && !info.getText().isEmpty()) {
							if ( "true".equalsIgnoreCase(info.getText()) || "false".equals(info.getText())) {
								value = Boolean.parseBoolean(info.getText());
								System.out.println("Boolean "+value);
							}
							else if (isNumeric(info.getText())) {
								value = Double.parseDouble(info.getText().trim());
								System.out.println("DOUBLE");	
							}
							else {
								value = info.getText();
								System.out.println("String");
							}
						}
						
						n.setAttribute(attr, value);
						updateInfoPane();
					});							
					
					HBox hbox = new HBox();
					Label labelAttr = new Label(attr); labelAttr.setPadding(new Insets(5, 5, 0, 5));
					hbox.getChildren().add(labelAttr);
					hbox.getChildren().add(info);
					
					vbox.getChildren().add(hbox);
				}
			});
			
			Button addAtribute = new Button("Add");
			addAtribute.addEventFilter(ActionEvent.ACTION, e -> newAttributePane(n.getId(), addNode));
			vbox.getChildren().add(addAtribute);
			
			Tab tab = new Tab(n.getId());
			tab.setContent(new ScrollPane(vbox));
			pane.getTabs().add(tab);
			
			limit.incrementAndGet();
		}
	}

	private void newAttributePane(String id, boolean isNode) {
		Stage attributePane = new Stage();
		attributePane.setTitle("Set Attribute");
		attributePane.initStyle(StageStyle.UTILITY);
		
		VBox pane = new VBox(2);
		
		ListView<String> list = new ListView<String>();
		List<String> items ;
		if ( isNode )
			items = graph.nodes().map(n -> n.getId()).collect(Collectors.toList());
		else
			items = graph.edges().map(e -> e.getId()).collect(Collectors.toList());

		list.setItems(FXCollections.observableArrayList(items));
		list.getItems().add("All");
		list.getSelectionModel().select(id);
		pane.getChildren().add(list);
		
		BorderPane hbox = new BorderPane();
		Label l1 = new Label("Attribute") ; l1.setPadding(new Insets(5, 5, 0, 5));
		TextField text1 = new TextField();
		hbox.setLeft(l1); hbox.setRight(text1);
		pane.getChildren().add(hbox);
		
		hbox = new BorderPane();
		Label l2 = new Label("Value") ; l2.setPadding(new Insets(5, 5, 0, 5));
		TextField text2 = new TextField();
		hbox.setLeft(l2); hbox.setRight(text2);
		pane.getChildren().add(hbox);
		
		Button b = new Button("+Add");
		BorderPane bPane = new BorderPane(b);
		pane.getChildren().add(bPane);
		
		b.addEventFilter(ActionEvent.ACTION, ev -> {
			if ( !text1.getText().isEmpty() && !text2.getText().isEmpty() ) {
				
				final Object value ;
				if ( "true".equalsIgnoreCase(text2.getText()) || "false".equals(text2.getText())) {
					value = Boolean.parseBoolean(text2.getText());
				}
				else if (isNumeric(text2.getText())) {
					value = Double.parseDouble(text2.getText());
					System.out.println("DOUBLE");	
				}
				else {
					value = text2.getText();
					System.out.println("String");
				}
				
				
				if(!list.getSelectionModel().getSelectedItem().equals("All")) {
					if ( isNode )
						graph.getNode(list.getSelectionModel().getSelectedItem()).setAttribute(text1.getText(), value);
					else
						graph.getEdge(list.getSelectionModel().getSelectedItem()).setAttribute(text1.getText(), value);

				}
				else {
					if ( isNode )
						graph.nodes().forEach(n -> n.setAttribute(text1.getText(), value));
					else
						graph.edges().forEach(e -> e.setAttribute(text1.getText(), value));

				}
			}
			attributePane.close();
			updateInfoPane();
		});
		
		
		
		attributePane.setScene(new Scene(pane));
		attributePane.show();
	}
}
