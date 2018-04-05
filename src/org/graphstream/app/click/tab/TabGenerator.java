package org.graphstream.app.click.tab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.app.click.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Management of Generator Tab in Toolbox
 * Create a new graph with the chosen algorithm
 * @author hicham
 */
public class TabGenerator {
	private Controller controller ;
	private ArrayList<Button> generators;
	
	private TextField iteration ;
	
	public TabGenerator(Controller controller) {
		this.controller = controller ;
		generators = new ArrayList<>();
		
		iteration = new TextField("7");
		iteration.textProperty().addListener(new ChangeListener<String>() {
			// Force Number
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	iteration.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		/* ==== Get all generators from package org.graphstream.algorithm.generator ==== */
		
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
			.setScanners(new SubTypesScanner(false), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(FilterBuilder.parsePackages("+org.graphstream.algorithm.generator")));

		Set<Class<? extends Generator>> allClassOfPackage = reflections.getSubTypesOf(Generator.class);
		List<Class<? extends Generator>> nameGenerators = allClassOfPackage.stream()
				.filter(s -> !s.getName().contains("$"))
				.collect(Collectors.toList()) ;
		
		for(Class<? extends Generator> clas : nameGenerators) {
			String name = clas.getName();
			try {
				Class<?> clazz = Class.forName(name);
				Object object = clazz.newInstance();

				if (object instanceof Generator) {
					String onlyName = (name.split("\\.")[name.split("\\.").length-1]).replace("GraphGenerator", "").replace("Generator", "");
					Button b = new Button(onlyName);
					b.setTooltip(new Tooltip(onlyName));
					b.setPrefSize(149, 30);
					
					b.addEventFilter(ActionEvent.ACTION, e -> {	
						try {
							Object instance = clazz.newInstance();
							Generator generator = (Generator) instance;

							launchGenerator(generator);
						}
						catch (Exception ex) {}

					});
					
					generators.add(b);
				}
			}
			catch (Exception ex) {}
		}
	}

	private void launchGenerator(Generator generator) {
		controller.getGraph().clear();
		controller.getTabGraph().resetView();
		controller.getTabGraph().updateCameraView();	
		controller.getPanel().getViewer().enableAutoLayout();
				
		generator.addSink( controller.getGraph() );
		generator.begin();
		int i = 0;
		
		int nEvent = 1 ;
		if (!iteration.getText().equals(""))
			nEvent = Integer.parseInt(iteration.getText());
		
		while ( i < nEvent ) {
			generator.nextEvents(); 
			i += 1;				
			System.err.println("Generator step "+i);
		}
		generator.end();
		generator.clearSinks();

		new Thread(() -> {
			try {
				Thread.sleep(3000);
				controller.getPanel().getViewer().disableAutoLayout();
			} catch (InterruptedException e1) {}
		}).start();		
	}

	public Tab getTab() {
		Tab tab = new Tab();
		tab.setText("Generator");
		tab.setOnClosed(e -> controller.closeTab(e));
		
		TitledPane buildGenPane = new TitledPane();
		buildGenPane.setText("Generator");
		
		VBox generatorBox = new VBox() ;
		
		HBox line = new HBox();
		boolean endLine = false ;
		for(Button b : generators) {
			if (!endLine) {
				line = new HBox();
				line.getChildren().add(b);
			}
			else {
				line.getChildren().add(b);
				generatorBox.getChildren().add(line);
			}
			
			endLine = !endLine ;
		}
		
		if (endLine)
			generatorBox.getChildren().add(line);
		
		buildGenPane.setContent(new ScrollPane(generatorBox));
		
		// ===========================
		VBox hbox = new VBox();
		line = new HBox();
		Label l = new Label("Iterations : ");
		l.setPrefWidth(90);
		iteration.setPrefWidth(220);
		l.setPadding(new Insets(5, 5, 0, 5));
		line.getChildren().add(l); line.getChildren().add(iteration);
		
		hbox.getChildren().add(line);
		hbox.getChildren().add(buildGenPane);
		//hbox.getChildren().add(styleTitlePane);

		hbox.setAlignment(Pos.TOP_CENTER);
		tab.setContent(hbox);
		

		return tab;
	}

}
