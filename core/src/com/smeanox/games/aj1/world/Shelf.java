package com.smeanox.games.aj1.world;


import com.smeanox.games.aj1.audio.Sfx;

public class Shelf extends Rect {

	public Shelf(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	@Override
	public void click(float x, float y) {
		Sfx.get("sc").manager().play();
	}

}
