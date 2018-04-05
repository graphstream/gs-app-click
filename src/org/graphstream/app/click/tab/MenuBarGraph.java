package org.graphstream.app.click.tab;

import org.graphstream.app.click.Controller;
import org.graphstream.graph.implementations.MultiGraph;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Managment of the menu bar
 * @author hicham
 */
public class MenuBarGraph {
	
	private MenuBar menu ;
	private TabGraph tabGraph ;
	private CSSGenerator help ;
	private Controller controller ;
	
	private CheckMenuItem tool ;
	private CheckMenuItem cssGenerator ;
	
	public MenuBarGraph(Controller controller, TabGraph tabGraph) {
		this.menu = new MenuBar();
		this.controller = controller ;
		this.tabGraph = tabGraph ;
		init();
	}
	
	private void init() {
		// --- Menu File
        Menu menuFile = new Menu("File");
        MenuItem itemNew = new MenuItem("New Graph");
        itemNew.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		controller.initGraph(new MultiGraph("Graph"));
        		tabGraph.cameraBuildConfig();
        	}
        });
        
        MenuItem itemSave = new MenuItem("Save Graph");
        itemSave.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		tabGraph.save();
        	}
        });
        
        MenuItem itemLoad = new MenuItem("Load Graph");
        itemLoad.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		tabGraph.load();
        	}
        });
        
        MenuItem itemSaveCSS = new MenuItem("Save CSS");
        itemSaveCSS.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		//tabGraph.save();
        		help.saveCSS();
        	}
        });
        
        MenuItem itemLoadCSS = new MenuItem("Load CSS");
        itemLoadCSS.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		help.loadCSS();
        	}
        });
        
        MenuItem itemSnap = new MenuItem("Snapshot");
        itemSnap.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		tabGraph.snap();
        	}
        });
        
        menuFile.getItems().addAll(itemNew, new SeparatorMenuItem(), itemSave, itemLoad, new SeparatorMenuItem(), itemSaveCSS, itemLoadCSS, new SeparatorMenuItem(), itemSnap);
        
        // ====================================== Menu View
        
        Menu menuView = new Menu("View");
        Menu submenuTool = new Menu("Toolbar");
        
        tool = new CheckMenuItem("Display");
        tool.setSelected(true);
        tool.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
            Boolean oldValue, Boolean newValue) {
                if (newValue) {
                	controller.getStageToolbox().show();
                }
                else {
                	controller.getStageToolbox().close();
                }
            }
        });
        
        MenuItem tabs = new MenuItem("Reset tabs");
        tabs.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		controller.restoreTab();
        	}
        });
        
        submenuTool.getItems().addAll(tool, tabs);
        
        cssGenerator = new CheckMenuItem("CSS Generator");
        cssGenerator.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
            Boolean oldValue, Boolean newValue) {
                if (newValue) {
                	help.createCSSHelp(controller.getStageToolbox(), controller.getStageGraph().getX(), controller.getStageGraph().getY());
                }
                else {
                	help.getStageCSS().close();
                }
            }
        });
        
        MenuItem itemHelp = new MenuItem("Help");
        itemHelp.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		help.newStageWeb(controller.getStageGraph());
        	}
        });
        
        menuView.getItems().addAll(submenuTool, cssGenerator, new SeparatorMenuItem(), itemHelp);
        
        // ===============================
        
        menu.getMenus().addAll(menuFile, menuView);
	}

	public MenuBar getMenuBar() {
		return menu;
	}

	public void closeToolbox() {
		tool.setSelected(false);
	}

	public void openCSS() {
		cssGenerator.setSelected(true);
	}
	
	public void closeCSS() {
		cssGenerator.setSelected(false);
	}

	public void setHelpCSS(CSSGenerator help) {
		this.help = help ;
	}
}
