package com.smeanox.games.aj1.audio;

import java.util.HashMap;
import java.util.Map;

public class FoodNarrator {
	private boolean playing;
	private SfxManager endManager;
	private Map<String, Float> durations;
	private float timeout;

	public FoodNarrator() {
		durations = new HashMap<String, Float>();
		fillDurations();
		endManager = new SfxManager(){
			@Override
			public SfxManager play() {
				playing = false;
				return this;
			}
		};
	}

	private void fillDurations(){
		durations.put("", 0f);
	}

	public void play(int food) {
		playing = true;

		timeout = 0.5f;
		SfxManager startMgr = SfxManager.empty();
		SfxManager mgr = startMgr.then(2, Sfx.get("food_start"));
		mgr = mgr.then(1, SfxManager.empty());

		if (food == 0){
			mgr = mgr.then(timeout, Sfx.get("food_0."));
		} else {
			if (food < 0){
				mgr = mgr.then(timeout, Sfx.get("food_minus."));
				food = -food;
			}
			int thousand = food / 1000;
			if (thousand > 0) {
				mgr = transformLessThanThousand(mgr, thousand);
				mgr = mgr.then(timeout, Sfx.get("food_1000."));
			}
			mgr = transformLessThanThousand(mgr, food % 1000);
		}
		mgr = mgr.then(timeout, Sfx.get("food_end").manager());
		mgr = mgr.then(2, endManager);

		startMgr.play();
	}

	private SfxManager transformLessThanThousand(SfxManager mgr, int number){
		if (number >= 1000){
			throw new IllegalArgumentException();
		}
		int hundred = number / 100;
		if (hundred > 0){
			mgr = mgr.then(timeout, Sfx.get("food_" + hundred + "."));
			mgr = mgr.then(timeout, Sfx.get("food_100."));
		}
		return transformLessThanHundred(mgr, number % 100);
	}

	private SfxManager transformLessThanHundred(SfxManager mgr, int number){
		if (number >= 100){
			throw new IllegalArgumentException();
		}
		if (number == 0){
			return mgr;
		}
		if (number <= 20) {
			return mgr.then(timeout, Sfx.get("food_" + number + "."));
		}
		int tens = number / 10;
		mgr = mgr.then(timeout, Sfx.get("food_" + tens + "0."));
		if (number % 10 != 0) {
			return mgr.then(timeout, Sfx.get("food_" + (number % 10) + "."));
		} else {
			return mgr;
		}
	}

	public boolean isPlaying() {
		return playing;
	}
}
