package com.smeanox.games.aj1.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.aj1.Consts;
import com.smeanox.games.aj1.world.Ingredient;
import com.smeanox.games.aj1.world.Shelf;
import com.smeanox.games.aj1.world.World;

public class GameScreen implements Screen{

	private final Texture hand;
	private final SpriteBatch spriteBatch;
	private final ShapeRenderer shapeRenderer;
	private final Camera camera;
	private final Vector3 mousePosition;
	private final Vector3 mouseDownPosition;

	private World world;

	private boolean oldMouseDown, dragging;

	public GameScreen() {
		hand = new Texture(Gdx.files.internal("hand.png"));
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		mousePosition = new Vector3();
		mouseDownPosition = new Vector3();
		world = new World();

		world.loadFile("lvl1.txt");
	}

	@Override
	public void show() {

	}

	private void update(float delta){
		world.update(delta);

		boolean mouseDown = Gdx.input.isTouched();

		if (mouseDown && !oldMouseDown){
			mouseDownPosition.set(mousePosition);
		}

		if (!dragging && oldMouseDown && mouseDown && mousePosition.dst2(mouseDownPosition) > Consts.DRAG_DISTANCE){
			world.startDrag(mousePosition.x, mousePosition.y);
			dragging = true;
		}

		if (dragging && oldMouseDown && mouseDown) {
			world.dragging(mousePosition.x, mousePosition.y);
		}

		if (!mouseDown && oldMouseDown){
			if (dragging){
				world.endDrag(mousePosition.x, mousePosition.y);
				dragging = false;
			} else {
				world.click(mousePosition.x, mousePosition.y);
			}
		}

		oldMouseDown = mouseDown;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePosition);

		update(delta);

		if (Consts.DRAW_LAB) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(1, 0, 0, 1);
			world.getLabTable().draw(shapeRenderer);
			shapeRenderer.setColor(0.5f, 0.5f, 1, 1);
			for (Shelf shelf : world.getShelfs()) {
				shelf.draw(shapeRenderer);
			}
			shapeRenderer.setColor(0, 1, 0, 1);
			for (Ingredient ingredient : world.getIngredients()){
				ingredient.draw(shapeRenderer);
			}
			shapeRenderer.end();
		}

		spriteBatch.begin();
		spriteBatch.draw(hand, mousePosition.x - 10, mousePosition.y - hand.getHeight() + 10);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		float ratio = ((float) width) / height;

		camera.viewportWidth = Consts.DESIGN_HEIGHT * ratio;
		camera.viewportHeight = Consts.DESIGN_HEIGHT;
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
