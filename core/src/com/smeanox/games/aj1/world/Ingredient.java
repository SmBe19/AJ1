package com.smeanox.games.aj1.world;

import com.smeanox.games.aj1.audio.Sfx;

public class Ingredient extends Rect {

	private final char name;
	private final Sfx sfx;

	public Ingredient(float x, float y, float width, float height, char name) {
		super(x, y, width, height);
		this.name = name;
		this.sfx = Sfx.get("in_" + name);
	}

	public char getName() {
		return name;
	}

	@Override
	public void click(float x, float y) {
		sfx.manager().play();
	}
}
