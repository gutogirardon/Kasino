package br.com.girardon.kasino;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import br.com.girardon.kasino.Kasino;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Kasino no Sabadaaçooo!");
		config.setWindowedMode(1280, 720);
		config.setResizable(false);
		config.setForegroundFPS(60);
		new Lwjgl3Application(new Kasino(), config);
	}
}
