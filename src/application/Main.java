package application;
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class Main extends Application {
	public Stage mainStage;
	//public ArrayList<ProfileMaker> profiles;
	public HashMap<String, ProfileMaker> profiles;
	public ListView<String> list;
	public static Properties prop;
	public static String oPath;
	public static String dPath;
	public static String globPath;
	public static String defaultPath;
	public OutputStream output;
	@Override
	public void start(Stage primaryStage) throws IOException {
		startMainStage();
		try {
			mainStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startMainStage() throws IOException {
		mainStage = new Stage();
		mainStage.setTitle("File Transfer App");
		BorderPane root = new BorderPane();
		root.setCenter(getVBox());
		Scene scene = new Scene(root,400,150);
		mainStage.setMinWidth(400);
		mainStage.setMinHeight(150);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		mainStage.setScene(scene);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private VBox getVBox() throws IOException {
		VBox box = new VBox();
		HBox oHbox = new HBox();
		prepProfiles();
		ComboBox<String> profileSelector = new ComboBox<String>();
		profileSelector.getItems().addAll(profiles.keySet());
		TextField t1 = new TextField(profiles.get(profileSelector.getSelectionModel().getSelectedItem()).getOriginPath());
		t1.prefWidthProperty().bind(mainStage.widthProperty());
		HBox dHbox = new HBox();
		TextField t2 = new TextField(profiles.get(profileSelector.getSelectionModel().getSelectedItem()).getDestinationPath());
		t2.prefWidthProperty().bind(mainStage.widthProperty());
		profileSelector.getSelectionModel().selectedItemProperty().addListener(e -> {
			String s = profileSelector.getSelectionModel().getSelectedItem();
			ProfileMaker p = profiles.get(s);
			t1.setText(p.getOriginPath());
			t2.setText(p.getDestinationPath());
		});
		// Directories
		DirectoryChooser dfc = new DirectoryChooser();
		dfc.setTitle("Destination");
		DirectoryChooser ofc = new DirectoryChooser();
		ofc.setTitle("Origin");
		
		Button oButt = new Button("...");
		oButt.setOnAction(e -> {
			File file = ofc.showDialog(mainStage);
			if (file != null) {
				try {
					ProfileMaker curr = profiles.get(profileSelector.getSelectionModel().selectedItemProperty().get());
					curr.setOPath(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				t1.setText(file.getAbsolutePath());
			}
		});
		Button dButt = new Button("...");
		dButt.setOnAction(e -> {
			File file = dfc.showDialog(mainStage);
			if (file != null) {
				try {
					ProfileMaker curr = profiles.get(profileSelector.getSelectionModel().selectedItemProperty().get());
					curr.setDPath(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				t2.setText(file.getAbsolutePath());
			}
		});
		Button transfer = new Button("Transfer");
		transfer.setOnAction(e -> {
			try {
				profiles.get(profiles.get(profileSelector.getSelectionModel().getSelectedItem())).transfer();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		Button swap = new Button("Swap");
		swap.setOnAction(e -> {
			try {
				String temp = t1.getText();
				t1.setText(t2.getText());
				t2.setText(temp);
				ProfileMaker curr = profiles.get(profileSelector.getSelectionModel().getSelectedItem());
				curr.setOPath(t1.getText());
				curr.setDPath(t2.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		Label origin = new Label("Origin");
		Label dest = new Label("Destination");
		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(transfer, swap);
		oHbox.getChildren().addAll(t1, oButt);
		dHbox.getChildren().addAll(t2, dButt);
		box.getChildren().addAll(getMenu(), profileSelector, origin, oHbox, dest, dHbox, buttonBox);
		return box;
	}

	// Configs(properties)
	// OLD
//	public static void propFile() {
//		prop = new Properties();
//		globPath = System.getProperty("user.dir");
//		InputStream input = null;
//		OutputStream output = null;
//		try {
//			input = new FileInputStream(globPath + "/" + "config.properties");
//			prop.load(input);
//			oPath = prop.getProperty("originpath");
//			dPath = prop.getProperty("destpath");
//			input.close();
//		}
//		catch (IOException e){
//			try {
//				output = new FileOutputStream(globPath + "/" + "config.properties");
//				prop.setProperty("originpath", "default");
//				prop.setProperty("destpath", "default");
//				oPath = prop.getProperty("originpath");
//				dPath = prop.getProperty("destpath");
//				prop.store(output, null);
//				output.close();
//
//			} catch (IOException f) {
//				f.printStackTrace();
//			}
//		}
//	}
	
//	private void editPath(File file, int num) throws IOException {
//		output = new FileOutputStream(globPath + "config.properties");
//		if (num == 0) {
//			prop.setProperty("originpath", file.getAbsolutePath());
//			oPath = prop.getProperty("originpath");
//			prop.store(output, "changed orign");
//		} else {
//			prop.setProperty("destpath", file.getAbsolutePath());
//			dPath = prop.getProperty("destpath");
//			prop.store(output, "changed destination");
//		}
//		output.close();
//	}
	
	private MenuBar getMenu() {
		MenuBar bar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem a1 = new MenuItem("Profiles");
		a1.setOnAction(e -> {
			Stage s = profileEditor();
			s.show();
		});
		menuFile.getItems().add(a1);
		bar.getMenus().add(menuFile);
		return bar;
	}
	
	// configs rewrite
	public static void propFile() {
		prop = new Properties();
		defaultPath = System.getProperty("user.dir");
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(defaultPath + "/" + "config.properties");
			prop.load(input);
			globPath = prop.getProperty("profPath");
			input.close();
			//input = new FileInputStream()
		}
		catch (IOException e) {
			try {
				output = new FileOutputStream(defaultPath + "/" + "config.properties");
				globPath = defaultPath;
				prop.setProperty("profPath", defaultPath);
				prop.store(output, null);
				output.close();

			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}
	
	public void prepProfiles() throws IOException {
		File folder = new File(globPath);
		File[] fileList = folder.listFiles();
		if (fileList.length == 0) {
			ProfileMaker pr = new ProfileMaker("default");
			profiles.put("default", pr);
		}
		for (File file : fileList) {
			if (file.isFile()) {
				String s = file.getName();
				s = s.substring(s.lastIndexOf("/") + 1, s.indexOf("."));
				try {
					ProfileMaker p = new ProfileMaker(s);
					p.readFile(file);
					profiles.put(s, p);
				} catch (IOException x){
					x.printStackTrace();
				}
			}
		}
	}
	
	public Stage profileEditor() {
		Stage editor = new Stage();
		list = new ListView<String>();
		list.getItems().addAll(profiles.keySet());
		Button add = addProfile();
		Button rem = remProfile();
		Button edit = new Button("Edit Profile");
		Button done = new Button("Done");
		done.setOnAction(e -> {
			editor.close();
		});
		ListView<String> folderList = new ListView<String>();
		list.getSelectionModel().selectedItemProperty().addListener(f -> {
			String selected = list.getSelectionModel().getSelectedItem();
			folderList.getItems().removeAll();
			folderList.getItems().addAll(profiles.get(selected).getFolders());
		});
		edit.setOnAction(i -> {
			showEditor(list.getSelectionModel().getSelectedItem());
		});
		VBox bBox = new VBox();
		bBox.getChildren().addAll(add, rem, edit, done);
		BorderPane b = new BorderPane();
		GridPane g = new GridPane();
		g.getChildren().addAll(list, folderList);
		b.setRight(bBox);
		b.setCenter(g);
		Scene scene = new Scene(b, 400, 400);
		editor.setScene(scene);
		editor.setTitle("Editor");
		return editor;
	}
	
	public Button addProfile() {
		Button newprof = new Button("Add Profile");
		newprof.setOnAction(e -> {
			Stage addNewProf = new Stage();
			BorderPane b = new BorderPane();
			GridPane g = new GridPane();
			TextField t = new TextField();
			Button create = new Button("Create New Profile");
			b.setBottom(create);
			b.setCenter(t);
			Scene n = new Scene(b, 150, 50);
			addNewProf.setTitle("Create New Profile");
			addNewProf.setScene(n);
			create.setOnAction(f -> {
				//profiles.add(new ProfileMaker(t.getText()));
				try {
					profiles.put(t.getText(), new ProfileMaker(t.getText()));
				} catch (IOException x) {
					x.printStackTrace();
				}
				list.getItems().add(t.getText());
				addNewProf.close();
			});
		});
		return newprof;
	}
	
	public Button remProfile() {
		Button b = new Button("Remove Profile");
		b.setOnAction(e -> {
			String s = list.getSelectionModel().getSelectedItem();
			list.getItems().remove(s);
			profiles.remove(s);
			File f = FileUtils.getFile(globPath + "/" + s + ".txt");
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		return b;
	}
	
	private <E> void Swap(E e1, E e2) {
		E temp = e1;
		e1 = e2;
		e2 = temp;
	}
	
	public void showEditor(String s) {
		Stage stage = new Stage();
		ProfileMaker p = profiles.get(s);
		TextField name = new TextField(p.getName());
		TextField oPath = new TextField(p.getOriginPath());
		TextField dPath = new TextField(p.getDestinationPath());
		Button swap = new Button("Swap Paths");
		swap.setOnAction(e -> {
			Swap(oPath, dPath);
		});
		Button save = new Button("Save");
		save.setOnAction(e -> {
			try {
				p.setName(name.getText());
				p.setOPath(oPath.getText());
				p.setDPath(dPath.getText());
			} catch (IOException x) {
				x.printStackTrace();
			}
			stage.close();
		});
		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> {
			stage.close();
		});
		
//		Scene scene = new Scene();
	}
	
	public String getGlobPath() {
		return globPath;
	}
	
	public static void main(String[] args) {
		propFile();
		launch(args);
	}
}