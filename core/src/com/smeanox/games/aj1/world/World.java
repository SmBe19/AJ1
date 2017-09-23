package com.smeanox.games.aj1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.aj1.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class World {

	private float totalTime;
	private final LabTable labTable;
	private final List<Shelf> shelfs;
	private final List<Ingredient> ingredients;
	private final List<String> tasks;
	private String nextLevelName;
	private float shelfHeight;
	private int currentTask;
	private final StringBuilder currentTableValue;
	private final Vector2 dragOffset, dragOriginalPosition;
	private Ingredient dragIngredient;

	public World() {
		labTable = new LabTable(Consts.TABLE_LEFT, Consts.TABLE_OFFSET, Consts.TABLE_WIDTH, Consts.TABLE_HEIGHT);
		shelfs = new ArrayList<Shelf>();
		ingredients = new ArrayList<Ingredient>();
		tasks = new ArrayList<String>();
		totalTime = 0;
		dragOffset = new Vector2();
		dragOriginalPosition = new Vector2();
		currentTableValue = new StringBuilder();
		currentTableValue.setLength(Consts.TABLE_INGREDIENT_COUNT);
	}

	public void loadFile(String fileName) {
		Scanner cin = new Scanner(Gdx.files.internal("lvl/" + fileName).reader());
		int shelfCount = cin.nextInt();
		int ingredientCount = cin.nextInt();
		int taskCount = cin.nextInt();
		nextLevelName = cin.next();

		shelfs.clear();
		shelfHeight = Consts.SHELF_TOTAL_HEIGHT / shelfCount;
		for (int i = 0; i < shelfCount; i++) {
			shelfs.add(new Shelf(Consts.SHELF_LEFT, Consts.SHELF_OFFSET + i * shelfHeight, Consts.SHELF_WIDTH, shelfHeight));
		}

		ingredients.clear();
		for (int i = 0; i < ingredientCount; i++) {
			char name = cin.next().charAt(0);
			int shelf = cin.nextInt();
			int x = cin.nextInt();
			ingredients.add(new Ingredient(Consts.SHELF_LEFT + x * Consts.INGREDIENT_WIDTH, Consts.SHELF_OFFSET + shelf * shelfHeight, Consts.INGREDIENT_WIDTH, shelfHeight, name));
		}

		tasks.clear();
		for (int i = 0; i < taskCount; i++) {
			tasks.add(cin.next());
		}

		currentTask = 0;
	}

	public LabTable getLabTable() {
		return labTable;
	}

	public List<Shelf> getShelfs() {
		return shelfs;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public Ingredient findIngredient(float x, float y) {
		return findIngredient(x, y, null);
	}

	public Ingredient findIngredient(float x, float y, Ingredient butNotThis){
		for (Ingredient ingredient : ingredients) {
			if (ingredient != butNotThis && ingredient.isInside(x, y)) {
				return ingredient;
			}
		}
		return null;
	}

	public Shelf findShelf(float x, float y) {
		for (Shelf shelf : shelfs) {
			if (shelf.isInside(x, y)) {
				return shelf;
			}
		}
		return null;
	}

	public Rect findRect(float x, float y){
		Ingredient ingredient = findIngredient(x, y);
		if (ingredient != null) {
			return ingredient;
		}
		Shelf shelf = findShelf(x, y);
		if (shelf != null) {
			return shelf;
		}
		if (labTable.isInside(x, y)) {
			return labTable;
		}
		return null;
	}

	public void update(float delta){
		totalTime += delta;
	}

	public void click(float x, float y){
		Rect rect = findRect(x, y);
		if (rect != null) {
			rect.click(x, y);
		}
	}

	public void startDrag(float x, float y){
		dragIngredient = findIngredient(x, y);
		if (dragIngredient == null){
			System.out.println("[Nothing grabbed]");
		} else {
			dragOriginalPosition.set(dragIngredient.getX(), dragIngredient.getY());
			dragOffset.set(x - dragIngredient.getX(), y - dragIngredient.getY());
			boolean onTable = labTable.isInside(x, y);
			if (onTable){
				System.out.println("[Ingredient grabbed on Lab table]");
			} else {
				System.out.println("[Ingredient grabbed on Shelf]");
			}
		}
	}

	public void dragging(float x, float y){
		if (dragIngredient != null) {
			dragIngredient.setX(x - dragOffset.x);
			dragIngredient.setY(y - dragOffset.y);
		}
	}

	private boolean shuffleLeft(Ingredient ingredient) {
		Ingredient other = findIngredient(ingredient.getX(), ingredient.getCenterY(), ingredient);
		if (other != null){
			other.setX(ingredient.getX() - other.getWidth());
			if (other.getX() < Consts.SHELF_LEFT) {
				other.setX(Consts.SHELF_LEFT);
				shuffleRight(other);
			} else {
				shuffleLeft(other);
			}
		}
		return other != null;
	}

	private boolean shuffleRight(Ingredient ingredient) {
		Ingredient other = findIngredient(ingredient.getX() + ingredient.getWidth(), ingredient.getCenterY(), ingredient);
		if (other != null) {
			other.setX(ingredient.getX() + ingredient.getWidth());
			if (other.getX() + other.getWidth() > Consts.SHELF_RIGHT) {
				other.setX(Consts.SHELF_RIGHT - other.getWidth());
				shuffleLeft(other);
			} else {
				shuffleRight(other);
			}
		}
		return other != null;
	}

	private boolean shuffleAround(Ingredient ingredient){
		boolean shuffled = false;
		shuffled |= shuffleLeft(dragIngredient);
		shuffled |= shuffleRight(dragIngredient);
		shuffled |= shuffleLeft(dragIngredient);
		return shuffled;
	}

	private int countIngredientsInRect(Rect rect, Ingredient butWithout) {
		int cnt = 0;
		for (Ingredient ingredient : ingredients) {
			if (butWithout != ingredient && rect.isInside(ingredient.getCenterX(), ingredient.getCenterY())) {
				cnt++;
			}
		}
		return cnt;
	}

	private boolean checkSolution(){
		for (int i = 0; i < Consts.TABLE_INGREDIENT_COUNT; i++) {
			currentTableValue.setCharAt(i, ' ');
		}
		for (Ingredient ingredient : ingredients) {
			if (labTable.isInside(ingredient.getCenterX(), ingredient.getCenterY())) {
				int x = (int) ((ingredient.getCenterX() - Consts.TABLE_LEFT) / Consts.INGREDIENT_WIDTH);
				currentTableValue.setCharAt(x, ingredient.getName());
			}
		}
		int ax = 0;
		String task = tasks.get(currentTask);
		for (int i = 0; i < Consts.TABLE_INGREDIENT_COUNT; i++) {
			if (currentTableValue.charAt(i) == ' '){
				continue;
			}
			if (ax < task.length() && currentTableValue.charAt(i) == task.charAt(ax)) {
				ax++;
			} else {
				return false;
			}
		}
		return ax == task.length();
	}

	public void endDrag(float x, float y){
		if (dragIngredient != null) {
			Shelf shelf = findShelf(x, y);
			if (shelf != null && countIngredientsInRect(shelf, dragIngredient) < Consts.SHELF_INGREDIENT_COUNT) {
				dragIngredient.setHeight(shelf.getHeight());
				dragIngredient.setY(shelf.getY());
				dragIngredient.setX(Math.max(Consts.SHELF_LEFT, Math.min(Consts.SHELF_RIGHT - dragIngredient.getWidth(), dragIngredient.getX())));
				if (shuffleAround(dragIngredient)){
					System.out.println("[Shuffle on Shelf]");
				}
				System.out.println("[Ingredient dropped on Shelf]");
			} else if (labTable.isInside(x, y) && countIngredientsInRect(labTable, dragIngredient) < Consts.TABLE_INGREDIENT_COUNT) {
				dragIngredient.setHeight(labTable.getHeight());
				dragIngredient.setY(labTable.getY());
				dragIngredient.setX(Math.max(Consts.TABLE_LEFT, Math.min(Consts.TABLE_RIGHT - dragIngredient.getWidth(), dragIngredient.getX())));
				if (shuffleAround(dragIngredient)){
					System.out.println("[Shuffle on Lab table]");
				}
				System.out.println("[Ingredient dropped on Lab table]");
			} else {
				dragIngredient.setX(dragOriginalPosition.x);
				dragIngredient.setY(dragOriginalPosition.y);
				System.out.println("[Ingredient dropped on floor]");
			}
			if (checkSolution()){
				currentTask++;
				if (currentTask == tasks.size()) {
					System.out.println("[Next level]");
					loadFile(nextLevelName);
				} else {
					System.out.println("[Next task]");
				}
			}
			dragIngredient = null;
		}
	}
}
