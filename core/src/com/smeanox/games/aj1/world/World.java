package com.smeanox.games.aj1.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.aj1.Consts;
import com.smeanox.games.aj1.audio.FoodNarrator;
import com.smeanox.games.aj1.audio.Sfx;
import com.smeanox.games.aj1.audio.SfxManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class World {

	private float totalTime;
	private final LabTable labTable;
	private final RepeatButton repeatButton;
	private final ConfirmButton confirmButton;
	private final List<Shelf> shelfs;
	private final List<Ingredient> ingredients;
	private final List<String> tasks;
	private final List<String> narrations;
	private final FoodNarrator foodNarrator;
	private Music narrationMusic;
	private String nextLevelName;
	private float shelfHeight;
	private int currentTask;
	private float currentTaskStartTime;
	private float currentTaskParTime;
	private float food;
	private final StringBuilder currentTableValue;
	private final Vector2 dragOffset, dragOriginalPosition;
	private Ingredient dragIngredient;
	private SfxManager tickSfx, taskSfx;
	private boolean playedTie;
	private boolean inPreStartPhase;
	private float playingIfSound;
	private boolean playedNarrationMusic, playedFoodNarration;

	public World() {
		labTable = new LabTable(Consts.TABLE_LEFT, Consts.TABLE_OFFSET, Consts.TABLE_WIDTH, Consts.TABLE_HEIGHT);
		repeatButton = new RepeatButton(Consts.LEFT_INFTY, Consts.BOTTOM_INFTY, Consts.TABLE_LEFT - 10 - Consts.LEFT_INFTY, Consts.TOP_INFTY - Consts.BOTTOM_INFTY);
		confirmButton = new ConfirmButton(Consts.TABLE_RIGHT + 10, Consts.BOTTOM_INFTY, Consts.RIGHT_INFTY - 10 - Consts.TABLE_RIGHT, Consts.TOP_INFTY - Consts.BOTTOM_INFTY, this);
		shelfs = new ArrayList<Shelf>();
		ingredients = new ArrayList<Ingredient>();
		tasks = new ArrayList<String>();
		narrations = new ArrayList<String>();
		foodNarrator = new FoodNarrator();
		totalTime = 0;
		dragOffset = new Vector2();
		dragOriginalPosition = new Vector2();
		currentTableValue = new StringBuilder();
		currentTableValue.setLength(Consts.TABLE_INGREDIENT_COUNT);
		food = Consts.FOOD_START;
		playingIfSound = 0;
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

		narrations.clear();
		tasks.clear();
		for (int i = 0; i < taskCount; i++) {
			narrations.add(cin.next());
			tasks.add(cin.next());
		}

		currentTask = 0;
		preStartTask();
	}

	private void finishTask(){
		if (tickSfx != null) {
			tickSfx.stop();
		}
		if (taskSfx != null) {
			taskSfx.stop();
		}
		repeatButton.setCurrentTask(null);
		Sfx.get("if").manager().play();
		playingIfSound = 5;
		float usedTime = totalTime - currentTaskStartTime;
		food += Math.min(Consts.FOOD_MAX_CHANGE, Math.max(-Consts.FOOD_MAX_CHANGE, (currentTaskParTime - usedTime) * Consts.FOOD_TIME_MULTIPLIER));
	}

	void startNextTask(){
		finishTask();
		currentTask++;
		if (currentTask == tasks.size()) {
			loadFile(nextLevelName);
		} else {
			preStartTask();
		}
	}

	private void preStartTask(){
		inPreStartPhase = true;
		narrationMusic = Gdx.audio.newMusic(Gdx.files.internal("nar/" + narrations.get(currentTask)));
		playedNarrationMusic = false;
		playedFoodNarration = false;
	}

	private void startTask(){
		inPreStartPhase = false;
		String task = tasks.get(currentTask);
		SfxManager mgr;
		tickSfx = SfxManager.empty();
		mgr = tickSfx.then(task.length() * Consts.TASK_SOUND_LENGTH, Sfx.get("tis"));
		mgr = mgr.then(0.6f, Sfx.get("tit"));
		mgr.repeat(5.7f);
		tickSfx.play();

		taskSfx = SfxManager.empty();
		mgr = taskSfx;
		for (int i = 0; i < task.length(); i++) {
			mgr = mgr.then(Consts.TASK_SOUND_LENGTH, Sfx.get("in_" + task.charAt(i)));
		}
		// mgr.then(Consts.TASK_REPEAT_TIME, taskSfx);
		taskSfx.playThenNow();
		repeatButton.setCurrentTask(taskSfx);

		playedTie = false;
		currentTaskStartTime = totalTime;
		currentTaskParTime = Math.max(Consts.PAR_TIME_MIN, task.length() * Consts.PAR_TIME_PER_CHAR);
	}

	public void skipNarration(){
		if (narrationMusic != null) {
			narrationMusic.stop();
		}
	}

	public LabTable getLabTable() {
		return labTable;
	}

	public RepeatButton getRepeatButton() {
		return repeatButton;
	}

	public ConfirmButton getConfirmButton() {
		return confirmButton;
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
		if (repeatButton.isInside(x, y)) {
			return repeatButton;
		}
		if (confirmButton.isInside(x, y)) {
			return confirmButton;
		}
		return null;
	}

	public void update(float delta){
		totalTime += delta;

		if (inPreStartPhase){
			if (playingIfSound > 0){
				playingIfSound -= delta;
			} else if (!playedNarrationMusic) {
				narrationMusic.play();
				playedNarrationMusic = true;
			} else if (narrationMusic.isPlaying()) {
				// Do nothing and wait
			} else if (!playedFoodNarration) {
				foodNarrator.play((int)food);
				playedFoodNarration = true;
			} else if (foodNarrator.isPlaying()) {
				// Do nothing and wait
			} else {
				startTask();
			}
		} else {
			if (!playedTie && totalTime - currentTaskStartTime > currentTaskParTime) {
				tickSfx.stop();
				Sfx.get("tie").manager().play();
				playedTie = true;
			}
		}
	}

	public void click(float x, float y){
		Rect rect = findRect(x, y);
		if (rect != null) {
			rect.click(x, y);
		}
	}

	public void startDrag(float x, float y){
		if (inPreStartPhase){
			return;
		}
		dragIngredient = findIngredient(x, y);
		if (dragIngredient != null) {
			dragOriginalPosition.set(dragIngredient.getX(), dragIngredient.getY());
			dragOffset.set(x - dragIngredient.getX(), y - dragIngredient.getY());
			boolean onTable = labTable.isInside(x, y);
			if (onTable){
				Sfx.get("tg").manager().play();
			} else {
				Sfx.get("sg").manager().play();
			}
		}
	}

	public void dragging(float x, float y){
		if (dragIngredient != null) {
			dragIngredient.setX(x - dragOffset.x);
			dragIngredient.setY(y - dragOffset.y);
		}
	}

	private int shuffleLeft(Ingredient ingredient) {
		Ingredient other = findIngredient(ingredient.getX(), ingredient.getCenterY(), ingredient);
		if (other != null){
			other.setX(ingredient.getX() - other.getWidth());
			if (other.getX() < Consts.SHELF_LEFT) {
				other.setX(Consts.SHELF_LEFT);
				shuffleRight(other);
			} else {
				shuffleLeft(other);
				return 1;
			}
		}
		return 0;
	}

	private int shuffleRight(Ingredient ingredient) {
		Ingredient other = findIngredient(ingredient.getX() + ingredient.getWidth(), ingredient.getCenterY(), ingredient);
		if (other != null) {
			other.setX(ingredient.getX() + ingredient.getWidth());
			if (other.getX() + other.getWidth() > Consts.SHELF_RIGHT) {
				other.setX(Consts.SHELF_RIGHT - other.getWidth());
				shuffleLeft(other);
			} else {
				shuffleRight(other);
				return 2;
			}
		}
		return 0;
	}

	private int shuffleAround(Ingredient ingredient){
		int val = 0;
		val |= shuffleLeft(dragIngredient);
		val |= shuffleRight(dragIngredient);
		val |= shuffleLeft(dragIngredient);
		return val;
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

	boolean checkSolution(){
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
				int shuffle = shuffleAround(dragIngredient);
				SfxManager sfx = SfxManager.empty();
				SfxManager sfxOrig = sfx;
				if (MathUtils.randomBoolean()){
					if ((shuffle & 1) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ss")).pan(Consts.PAN_RIGHT, Consts.PAN_LEFT, Consts.SHUFFLE_TIME);
					}
					if ((shuffle & 2) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ss")).pan(Consts.PAN_LEFT, Consts.PAN_RIGHT, Consts.SHUFFLE_TIME);
					}
				} else {
					if ((shuffle & 2) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ss")).pan(Consts.PAN_LEFT, Consts.PAN_RIGHT, Consts.SHUFFLE_TIME);
					}
					if ((shuffle & 1) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ss")).pan(Consts.PAN_RIGHT, Consts.PAN_LEFT, Consts.SHUFFLE_TIME);
					}
				}
				sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("sd"));
				sfxOrig.playThenNow();
			} else if (labTable.isInside(x, y) && countIngredientsInRect(labTable, dragIngredient) < Consts.TABLE_INGREDIENT_COUNT) {
				dragIngredient.setHeight(labTable.getHeight());
				dragIngredient.setY(labTable.getY());
				dragIngredient.setX(Math.max(Consts.TABLE_LEFT, Math.min(Consts.TABLE_RIGHT - dragIngredient.getWidth(), dragIngredient.getX())));
				int shuffle = shuffleAround(dragIngredient);
				SfxManager sfx = SfxManager.empty();
				SfxManager sfxOrig = sfx;
				if (MathUtils.randomBoolean()){
					if ((shuffle & 1) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ts")).pan(Consts.PAN_RIGHT, Consts.PAN_LEFT, Consts.SHUFFLE_TIME);
					}
					if ((shuffle & 2) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ts")).pan(Consts.PAN_LEFT, Consts.PAN_RIGHT, Consts.SHUFFLE_TIME);
					}
				} else {
					if ((shuffle & 2) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ts")).pan(Consts.PAN_LEFT, Consts.PAN_RIGHT, Consts.SHUFFLE_TIME);
					}
					if ((shuffle & 1) > 0) {
						sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("ts")).pan(Consts.PAN_RIGHT, Consts.PAN_LEFT, Consts.SHUFFLE_TIME);
					}
				}
				sfx = sfx.then(Consts.SHUFFLE_TIME, Sfx.get("td"));
				sfxOrig.playThenNow();
			} else {
				dragIngredient.setX(dragOriginalPosition.x);
				dragIngredient.setY(dragOriginalPosition.y);
				Sfx.get("id").manager().play();
			}
			dragIngredient = null;
		}
	}
}
