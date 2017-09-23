package com.smeanox.games.aj1.world;

import com.smeanox.games.aj1.audio.SfxManager;

public class RepeatButton extends Rect {

	private SfxManager currentTask;

	public RepeatButton(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public SfxManager getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(SfxManager currentTask) {
		this.currentTask = currentTask;
	}

	@Override
	public void click(float x, float y) {
		if (currentTask != null) {
			currentTask.stop();
			currentTask.playThenNow();
		}
	}
}
