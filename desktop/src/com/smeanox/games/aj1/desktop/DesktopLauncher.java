package com.smeanox.games.aj1.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.smeanox.games.aj1.AJ1;
import com.smeanox.games.aj1.Consts;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Consts.DESIGN_WIDTH;
		config.height = Consts.DESIGN_HEIGHT;
		config.title = Consts.GAME_NAME;
		new LwjglApplication(new AJ1(), config);
	}
}
