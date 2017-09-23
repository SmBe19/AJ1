package com.smeanox.games.aj1.world;

import com.smeanox.games.aj1.audio.Sfx;

public class LabTable extends Rect {

	public LabTable(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	@Override
	public void click(float x, float y) {
		Sfx.get("tc").manager().play();
	}

}
