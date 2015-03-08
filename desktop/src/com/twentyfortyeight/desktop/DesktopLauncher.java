package com.twentyfortyeight.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.twentyfortyeight.TwentyFortyEight;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 720;
		config.height = 720;
		
		new LwjglApplication(new TwentyFortyEight(), config);
	}
}
