import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

public class Main {
	static Map<String, String> readSettings() {
		Map<String, String> settings = new HashMap<String, String>();

		String path = "";
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/Library/Application Support/ByRikard/IJAG";
		else
			path = System.getenv("APPDATA") + "/ByRikard/IJAG";
		String settingsPath = path + "/settings.txt";
		
		new File(path).mkdirs();
		try {
			String content = "UseActiveRenderer:false";
			File file = new File(settingsPath);

			if (!file.exists()) {
				file.createNewFile();

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			}

		} catch (IOException e) {
		}

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
		}
		return settings;
	}

	public static void main(String[] args) {
		Map<String, String> settings = readSettings();
		new Runtime(640, 480,settings);
	}
}
