import java.applet.Applet;

public class AppletLauncher extends Applet {

	private static final long serialVersionUID = 1L;

	public void init() {
		new PassiveGraphicsRuntime(640, 480);
	}
}