package com.smeanox.games.aj1.world;

import com.smeanox.games.aj1.audio.Sfx;

public class ConfirmButton extends Rect {

	World world;

	public ConfirmButton(float x, float y, float width, float height, World world) {
		super(x, y, width, height);
		this.world = world;
	}

	@Override
	public void click(float x, float y) {
		if (world.checkSolution()){
			world.startNextTask();
		} else {
			Sfx.get("iw").manager().play();
		}
	}
}
