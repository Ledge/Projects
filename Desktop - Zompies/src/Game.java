public class Game {
	public static void main(String[] args) {
		//Map<String, String> settings = readSettings();
		Settings settings = new Settings("IJAG");
		settings.read();
		
		if (settings.get("UseActiveRenderer").equalsIgnoreCase("true")) {
			System.out.println("Using active renderer");
			while (true) {
				new GraphicsRuntime(640, 480);
			}
		} else {
			System.out.println("Using passive renderer");
			new PassiveGraphicsRuntime(640, 480);
		}
	}
}
