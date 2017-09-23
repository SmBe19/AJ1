package com.smeanox.games.aj1.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashSet;
import java.util.Set;

public class SfxManager {

	private static Set<SfxManager> managers = new HashSet<SfxManager>();

	public static void updateAll(float delta){
		for (SfxManager manager : new HashSet<SfxManager>(managers)) {
			manager.update(delta);
		}
	}

	public static SfxManager empty(){
		return new SfxManager();
	}

	private Sfx sfx;
	private SfxManager then;
	private float thenTimeout;
	private float panFrom, panTo, panTime;
	private float panProgress;
	private float repeatTimeout, repeatProgress;
	private boolean repeat;
	private Sound sound;
	private long id;

	public SfxManager(Sfx sfx) {
		this.sfx = sfx;
		id = -1;
	}

	public SfxManager(){
	}

	public SfxManager play(){
		if (sfx == null){
			if (then != null) {
				then.play();
			}
		} else {
			sound = sfx.getRandomSound();
			id = sound.play();
			panProgress = 0;
			repeatProgress = 0;
			if (then != null || panTime > 0 || repeat) {
				managers.add(this);
			}
		}
		return this;
	}

	public SfxManager stop(){
		if (sound != null){
			sound.stop(id);
		}
		if (then != null) {
			then.stop();
		}
		return this;
	}

	public SfxManager then(float timeout, Sfx sfx){
		if (sfx == null) {
			then = null;
		} else {
			then = sfx.manager();
		}
		thenTimeout = timeout;
		return then;
	}

	public SfxManager repeat(float repeatTimeout){
		this.repeatTimeout = repeatTimeout;
		repeat = true;
		return this;
	}

	public SfxManager noRepeat(){
		repeat = false;
		return this;
	}

	public SfxManager pan(float panFrom, float panTo, float panTime) {
		this.panFrom = panFrom;
		this.panTo = panTo;
		this.panTime = panTime;
		return this;
	}

	public void update(float delta){
		if (then != null) {
			thenTimeout -= delta;
			if (thenTimeout <= 0) {
				then.play();
				managers.remove(this);
			}
		}
		if (repeat){
			repeatProgress += delta;
			if (repeatProgress > repeatTimeout) {
				managers.remove(this);
				play();
			}
		}
		if (panTime > 0) {
			sound.setPan(id, MathUtils.lerp(panFrom, panTo, panProgress / panTime), 1f);
			panProgress += delta;
			if (panProgress > panTime) {
				sound.setPan(id, panTo, 1f);
			}
		}
	}
}
