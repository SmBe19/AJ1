package com.smeanox.games.aj1.world;

public class Ingredient extends Rect {

	private final char name;

	public Ingredient(float x, float y, float width, float height, char name) {
		super(x, y, width, height);
		this.name = name;
	}

	public char getName() {
		return name;
	}

	@Override
	public void click(float x, float y) {

	}

	@Override
	public void startDrag(float x, float y) {

	}

	@Override
	public void endDrag(float x, float y) {

	}
}
