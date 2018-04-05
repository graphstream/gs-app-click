package org.graphstream.app.click.tab;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.algorithm.measure.ChartMeasure;
import org.graphstream.algorithm.util.Parameter;
import org.graphstream.algorithm.util.Result;
import org.graphstream.app.click.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Management of Algorithm Tab in Toolbox
 * Prepares and apply the chosen algorithm
 * @author hicham
 */
public class TabAlgorithm {
	private Controller controller;
	
	private ArrayList<Button> algorithms;
	private ArrayList<Button> dynamicAlgorithms;

	public TabAlgorithm(Controller controller) {
		this.controller = controller;
		this.algorithms = new ArrayList<>();
		this.dynamicAlgorithms = new ArrayList<>();

		//runButton.addEventFilter(ActionEvent.ACTION, e -> parameterAlgo());
		
		/* ==== Get all algorithm from package org.graphstream.algorithm ==== */
		
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
			.setScanners(new SubTypesScanner(false), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(FilterBuilder.parsePackages("+org.graphstream.algorithm")));

		Set<Class<? extends Algorithm>> allClassOfPackage = reflections.getSubTypesOf(Algorithm.class);
		
		List<Class<? extends Algorithm>> nameAlgosNoDyn = allClassOfPackage.stream()
				.filter(s -> !s.getName().contains("$"))
				.filter(s -> { // Remove DynamicAlgorithm
					Class<?> clazz = s ;
					while ( !clazz.equals(Object.class) && !clazz.isInterface()) {
						for ( Class<?> i : clazz.getInterfaces())
							if(i.equals(DynamicAlgorithm.class))
								return false ;
						
						clazz = clazz.getSuperclass();
					}
					return true ;
				})
				.collect(Collectors.toList()) ;

		for(Class<? extends Algorithm> clas : nameAlgosNoDyn) {
			String name = clas.getName();

			try {
				Class<?> clazz = Class.forName(name);
				Object object = clazz.newInstance();

				if (object instanceof Algorithm) {
					String onlyName = (name.split("\\.")[name.split("\\.").length-1]).replace("GraphGenerator", "").replace("Generator", "");
					Button b = new Button(onlyName);
					b.setTooltip(new Tooltip(onlyName));
					b.setPrefSize(149, 30);
					
					b.addEventFilter(ActionEvent.ACTION, e -> {	
						try {
							Object instance = clazz.newInstance();
							parameterAlgo(instance);
						}
						catch (Exception ex) {}

					});
					
					algorithms.add(b);
				}
			}
			catch (Exception ex) {}
		}
		
		/* ==== Get all Dynamic algorithm from package org.graphstream.algorithm ==== */
		
		List<ClassLoader> classLoadersListDyn = new LinkedList<ClassLoader>();
		classLoadersListDyn.add(ClasspathHelper.contextClassLoader());
		
		Reflections reflectionsDyn = new Reflections(new ConfigurationBuilder()
			.setScanners(new SubTypesScanner(false), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersListDyn.toArray(new ClassLoader[0])))
		    .filterInputsBy(FilterBuilder.parsePackages("+org.graphstream.algorithm")));

		Set<Class<? extends DynamicAlgorithm>> allClassDynOfPackage = reflectionsDyn.getSubTypesOf(DynamicAlgorithm.class);
		
		List<Class<? extends DynamicAlgorithm>> nameAlgosDyn = allClassDynOfPackage.stream()
				.filter(s -> !s.getName().contains("$"))
				.filter(s -> { // Remove ChartMeasure
					Class<?> clazz = s ;
					while ( !clazz.equals(Object.class) && !clazz.isInterface()) {
						if(clazz.equals(ChartMeasure.class))
							return false ;
						
						clazz = clazz.getSuperclass();
					}
					return true ;
				})
				.collect(Collectors.toList()) ;
	
		for(Class<? extends DynamicAlgorithm> clas : nameAlgosDyn) {
			String name = clas.getName();

			try {
				Class<?> clazz = Class.forName(name);
				Object object = clazz.newInstance();

				if (object instanceof DynamicAlgorithm) {
					String onlyName = (name.split("\\.")[name.split("\\.").length-1]).replace("GraphGenerator", "").replace("Generator", "");
					Button b = new Button(onlyName);
					b.setTooltip(new Tooltip(onlyName));
					b.setPrefSize(149, 30);
					
					b.addEventFilter(ActionEvent.ACTION, e -> {	
						try {
							Object instance = clazz.newInstance();
							parameterAlgo(instance);
						}
						catch (Exception ex) {}

					});
					
					dynamicAlgorithms.add(b);
				}
			}
			catch (Exception ex) {}
		}
	}

	private void parameterAlgo(Object instance) throws NoSuchMethodException, SecurityException {
		System.out.println(instance+" :");
		
		List<Method> methods = getMethodsAnnotated(instance.getClass(), Parameter.class);
		ArrayList<TextField> textParameters = new ArrayList<>();
		
		Stage stage = new Stage();
		stage.setTitle("Parameter Algo");
		stage.initStyle(StageStyle.UTILITY);
		
        VBox vbox = new VBox(5);
        
        Label nameAlgo = new Label(instance.getClass().getSimpleName());
        Label errorMessage = new Label();
        VBox nameAlgoCenter = new VBox(nameAlgo);
        nameAlgoCenter.getChildren().add(new Separator());
        nameAlgoCenter.getChildren().add(errorMessage);
        nameAlgoCenter.setAlignment(Pos.CENTER);
        vbox.getChildren().add(nameAlgoCenter);

        HBox line ;
        for(Method method : methods) {
        	line = new HBox();
        	String stringLabel = method.getName();
        	stringLabel = stringLabel.replaceAll("set", ""); // Remove "set" in name
        	stringLabel = stringLabel.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2"); // Add space after uppercase
        	Label label = new Label(stringLabel) ;
        	label.setPadding(new Insets(5, 5, 0, 5));
        	line.getChildren().add(label);
        	
        	for (Class<?> type : method.getParameterTypes()) {
        		TextField t = new TextField() ;
        		t.setPromptText(type.getName().replaceAll("org.graphstream.algorithm.", ""));
        		textParameters.add(t);
        		if ( !type.equals(String.class) && !(type.equals(Integer.class) || type.equals(Integer.TYPE))
        				&& !(type.equals(Double.class) || type.equals(Double.TYPE))
        				&& !(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) )
        			t.setDisable(true);
        		
        		if(method.getAnnotation(Parameter.class).value()) // if parameter is require
        			 t.setStyle("-fx-border-color: red;");
        			
        		line.getChildren().add(t);	
			}
        	
        	vbox.getChildren().add(line);
        }
        
        Button execute = new Button("Execute");
        execute.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	int iParameter = 0 ;
		    	boolean error = false ;
		    	
		    	// Prepare result window
				Stage stageResult = new Stage();
				stageResult.setTitle("Result");
				stageResult.initStyle(StageStyle.UTILITY);
				
				TextArea area = new TextArea();
				BorderPane resultBox = new BorderPane(area) ;
		    	
				try {
					// Initialize Algorithm
					((Algorithm)instance).init(controller.getGraph());
			
				} catch (Exception ex) {
					area.setText(area.getText()+"\nError : "+ex.getMessage());
					area.end();
					error = true ;
				}
		    	
				if (!error) {
			    	// Execute all methods when TextField is not empty
			    	for( Method method : methods) {
		    			if (textParameters.get(iParameter) != null && !textParameters.get(iParameter).getText().isEmpty()) {
				    		int nParameters = method.getParameterTypes().length;
				    		Object[] parameters = new Object[nParameters];
				    		
				    		int i = 0 ;
				    		for (Class<?> type : method.getParameterTypes()) {
				    			
				    			String value = textParameters.get(iParameter).getText();
			    			
			    				if(type.equals(String.class) ) {
			    					parameters[i] = value ;
			    				}
			    				else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			    					parameters[i] = Integer.parseInt(value) ;
			    				}
			    				else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
			    					parameters[i] = Double.parseDouble(value) ;
			    				}
			    				else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			    					parameters[i] = Boolean.parseBoolean(value) ;
			    				}
			    				
				    			i++;
				    		}
				    		
				    		try {
				    			System.out.println("Method "+method.getName()+" executed ");
								method.invoke(instance, parameters);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
								System.out.println("Error : "+e1.getMessage());
								e1.printStackTrace();
							}
		    			}
		    			else if ( textParameters.get(iParameter).getText().isEmpty() && method.getAnnotation(Parameter.class).value()) {
		    				errorMessage.setTextFill(Color.RED);
		    				errorMessage.setText("Parameter require");
		    				return ;
		    			}
		    			iParameter += method.getParameterTypes().length;
			    	}
			    	
					try {
						// Execute Algorithm
				    	((Algorithm)instance).compute();
	
				    	// Get Result
						List<Method> methods = getMethodsAnnotated(instance.getClass(), Result.class);
						
						/** ---------- Display result ---------- **/
						AtomicBoolean dynamicLoop = new AtomicBoolean(true) ;
						
						Button terminate = new Button("Terminate");
						terminate.addEventFilter(ActionEvent.ACTION, event -> {
							((DynamicAlgorithm)instance).terminate();
							dynamicLoop.set(false);
							terminate.setDisable(true);
						});
						
						// Invoke the result method
						area.setText(methods.get(0).invoke(instance, null).toString());
						
						if ( instance instanceof DynamicAlgorithm ) {						
							new Thread(() -> {
								while (dynamicLoop.get()) {
									try {
										area.setText(area.getText()+"\n--------------------------------------------\n");
										
										((Algorithm)instance).compute();
										area.setText(area.getText()+methods.get(0).invoke(instance, null).toString());
										area.end();
										Thread.sleep(1000);
									} 
									catch (Exception exception) {
										area.setText(area.getText()+"\n"+"Forced outage \n"+exception.toString());
										area.end();
										
										((DynamicAlgorithm)instance).terminate();
										terminate.setDisable(true);
										dynamicLoop.set(false);
									}
								}
							}).start();
							
							
							HBox centerTerminate = new HBox(terminate);
							centerTerminate.setAlignment(Pos.CENTER);
							resultBox.setBottom(centerTerminate);
						}
					} catch (Exception ex) {
						area.setText(area.getText()+"\nError : "+ex.getMessage());
						area.end();
					}
				}
					
				stage.close();
				stageResult.setScene(new Scene(resultBox));
				stageResult.show();
		    }
        });
    
        
        HBox executeCenter = new HBox();
        executeCenter.setAlignment(Pos.CENTER);
        executeCenter.getChildren().add(execute);
        vbox.getChildren().add(executeCenter);
		
		stage.setScene(new Scene(vbox));
		stage.show();
	}
	
	public static List<Method> getMethodsAnnotated(final Class<?> type, final Class<? extends Annotation> annotation) {
	    final List<Method> methods = new ArrayList<Method>();
	    Class<?> clazz = type;
	    while (clazz != Object.class) { 
	        final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(clazz.getDeclaredMethods()));       
	        for (final Method method : allMethods) {   	 
	        	if (method.isAnnotationPresent(annotation)) {
	                methods.add(method);
	            }
	        }
	        clazz = clazz.getSuperclass();
	    }
	    return methods;
	}

	public Tab getTab() {
		Tab tab = new Tab();
		tab.setText("Algorithm");
		tab.setOnClosed(e -> controller.closeTab(e));
		
		TitledPane buildAlgPane = new TitledPane();
		buildAlgPane.setText("Algorithm");
		
		VBox algoBox = new VBox() ;
		
		HBox line = new HBox();
		boolean endLine = false ;
		for(Button b : algorithms) {
			if (!endLine) {
				line = new HBox();
				line.getChildren().add(b);
			}
			else {
				line.getChildren().add(b);
				algoBox.getChildren().add(line);
			}
			
			endLine = !endLine ;
		}
		
		if (endLine)
			algoBox.getChildren().add(line);
		
		buildAlgPane.setContent(new ScrollPane(algoBox));

		// ===========================
		TitledPane buildDynPane = new TitledPane();
		buildDynPane.setText("Dynamic Algorithm");
		
		VBox algoDynBox = new VBox() ;
		
		line = new HBox();
		endLine = false ;
		for(Button b : dynamicAlgorithms) {
			if (!endLine) {
				line = new HBox();
				line.getChildren().add(b);
			}
			else {
				line.getChildren().add(b);
				algoDynBox.getChildren().add(line);
			}
			
			endLine = !endLine ;
		}
		
		if (endLine)
			algoDynBox.getChildren().add(line);
		
		buildDynPane.setContent(new ScrollPane(algoDynBox));
		
		// ===========================
		VBox hbox = new VBox();

		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.getChildren().add(buildAlgPane);
		hbox.getChildren().add(buildDynPane);
		tab.setContent(hbox);
		
		return tab;
	}
	
}