package com.smeanox.games.aj1.world;


public class Shelf extends Rect {

	public Shelf(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	@Override
	public void click(float x, float y) {
		System.out.println("[Shelf]");
	}

}
