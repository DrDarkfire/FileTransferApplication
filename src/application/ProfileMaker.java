package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class ProfileMaker {
	private String name, oPath, dPath, globPath;
	private ArrayList<String> folders;
	
	public ProfileMaker(String name) throws IOException {
		this.name = name;
		globPath = getGlobal();
		oPath = globPath;
		dPath = globPath;
		save();
	}
	
	public ProfileMaker() {
		
	}
	
	public <E> void setOPath(E path) throws IOException {
		oPath = path.toString();
		save();
	}
	
	public <E> void setDPath(E path) throws IOException {
		dPath = path.toString();
		save();
	}
	
	public void setName(String name) throws IOException {
		this.name = name;
		save();
	}
	
	public ArrayList<String> getFolders() {
		return folders;
	}
	
	public String getName() {
		return name;
	}
	
	public String getOriginPath() {
		return oPath;
	}
	
	public String getDestinationPath() {
		return dPath;
	}
	
	public void addFolder(String folder) throws IOException {
		folders.add(folder);
		save();
	}
	
	public void remFolder(String folder) throws IOException {
		folders.remove(folder);
		save();
	}
	
	private void save() throws IOException {
		File f = new File(globPath + "/" + name + ".txt");
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println(oPath);
		writer.println(dPath);
		for (String s : folders)
			writer.println(s);
		writer.close();
	}
	
	public void readFile(File f) throws FileNotFoundException {
		Scanner scanner = new Scanner(f);
		oPath = scanner.nextLine();
		dPath = scanner.nextLine();
		while (scanner.hasNextLine()) {
			folders.add(scanner.nextLine());
		}
		scanner.close();
	}
	
	public void transfer() throws IOException {
		for (String folder : folders) {
			File f1 = new File(oPath + "/" + folder);
			File f2 = new File(dPath + "/" + folder);
			if (!f1.exists()) {
				f1.mkdir();
			}
			if (!f2.exists()) {
				f2.mkdir();
			}
			FileUtils.copyDirectory(f1, f2);
		}
	}
	
	public String getGlobal() throws IOException {
		InputStream input = new FileInputStream(System.getProperty("user.dir") + "/configs.properties");
		Properties prop = new Properties();
		prop.load(input);
		return prop.getProperty("profPath");
	}
}