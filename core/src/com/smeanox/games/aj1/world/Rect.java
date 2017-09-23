package com.smeanox.games.aj1.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Rect {
	private float x, y, width, height;

	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getCenterX(){
		return x + width / 2;
	}

	public float getCenterY(){
		return y + height / 2;
	}

	public boolean isInside(float tx, float ty) {
		return x <= tx && tx <= x + width && y <= ty && ty <= y + height;
	}

	public void draw(ShapeRenderer shapeRenderer){
		shapeRenderer.rect(x, y, width, height);
	}

	public abstract void click(float x, float y);
}
