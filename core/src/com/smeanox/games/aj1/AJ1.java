package com.smeanox.games.aj1;

import com.badlogic.gdx.Game;
import com.smeanox.games.aj1.screen.GameScreen;

public class AJ1 extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
