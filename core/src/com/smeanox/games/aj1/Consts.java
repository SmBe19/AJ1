package com.smeanox.games.aj1;

public class Consts {
	public static final boolean DEBUG = true;
	public static final boolean DRAW_LAB = true;

	public static final String GAME_NAME = "Alchemy";
	public static final int DESIGN_WIDTH = 800;
	public static final int DESIGN_HEIGHT = 480;

	public static final float DRAG_DISTANCE = 10;

	public static final float SHELF_LEFT = -DESIGN_WIDTH / 3f;
	public static final float SHELF_WIDTH = DESIGN_WIDTH * 2f / 3;
	public static final float SHELF_RIGHT = SHELF_LEFT + SHELF_WIDTH;
	public static final float SHELF_OFFSET = -DESIGN_HEIGHT / 16f;
	public static final float SHELF_TOTAL_HEIGHT = DESIGN_HEIGHT / 2f;
	public static final float TABLE_LEFT = SHELF_LEFT;
	public static final float TABLE_WIDTH = SHELF_WIDTH;
	public static final float TABLE_RIGHT = TABLE_LEFT + TABLE_WIDTH;
	public static final float TABLE_OFFSET = -DESIGN_HEIGHT / 2f + DESIGN_HEIGHT / 8f;
	public static final float TABLE_HEIGHT = DESIGN_HEIGHT / 4f;
	public static final int SHELF_INGREDIENT_COUNT = 10;
	public static final float INGREDIENT_WIDTH = SHELF_WIDTH / Consts.SHELF_INGREDIENT_COUNT;
	public static final int TABLE_INGREDIENT_COUNT = ((int) (TABLE_WIDTH / INGREDIENT_WIDTH + 0.5f));
}