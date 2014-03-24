import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;

public class Settings {
	String rootPath;
	String settingsPath;
	private Map<String, String> settings = new HashMap<String, String>();

	public Settings(String name) {
		if(name == "")
			name = "Default";
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			rootPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/Library/Application Support/ByRikard/"+name;
		else
			rootPath = System.getenv("APPDATA") + "/ByRikard/"+name;
		settingsPath = rootPath + "/settings.conf";
	}

	public void resetToDefault() {
		settings.put("UseActiveRenderer", "false");
		settings.put("UpdateFrequancy", "16");
		settings.put("RenderFrequancy", "16");
		write();
	}

	public void write() {
		new File(rootPath).mkdirs();
		try {
			File file = new File(settingsPath);
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator<String> countryIterator = settings.keySet().iterator();
			while (countryIterator.hasNext()) {
				String key = countryIterator.next();
				//System.out.println(key + ":" + settings.get(key));
				bw.write(key + ":" + settings.get(key) + "\n");
			}
			bw.close();

		} catch (IOException e) {
		}
	}
	
	public void read() {
		try {
			FileInputStream fstream = new FileInputStream(settingsPath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			while ((strLine = br.readLine()) != null) {
				String[] tmp = strLine.split(":");
				settings.put(tmp[0], tmp[1]);
				i = i + 1;
			}
			in.close();
		} catch (Exception e) {
			resetToDefault();
		}
	}
	
	public void directWrite(String content){
		new File(rootPath).mkdirs();
		try {
			File file = new File(settingsPath);
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(content);

			bw.close();

		} catch (IOException e) {
		}
	}
	
	public String directRead(){
		String content = "";
		try {
			FileInputStream fstream = new FileInputStream(settingsPath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				content = content + "\n" + strLine;
			}
			in.close();
		} catch (Exception e) {
		}
		return content;
	}

	public String get(String key) {
		return settings.get(key);
	}

	public void set(String key, String value) {
		settings.put(key, value);
	}
}
