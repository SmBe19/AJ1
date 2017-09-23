package com.smeanox.games.aj1.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Sfx {
	private static Map<String, Sfx> cache = new HashMap<String, Sfx>();

	public static Sfx get(String prefix){
		if (!cache.containsKey(prefix)){
			cache.put(prefix, new Sfx(prefix));
		}
		return cache.get(prefix);
	}

	private List<Sound> sounds;

	private Sfx(final String prefix){
		sounds = new ArrayList<Sound>();
		Scanner cin = new Scanner(Gdx.files.internal("sfx/index.txt").reader());
		while(cin.hasNext()){
			String filename = cin.next();
			if (filename.startsWith(prefix)){
				sounds.add(Gdx.audio.newSound(Gdx.files.internal("sfx").child(filename)));
			}
		}
		if (sounds.size() == 0) {
			throw new IllegalArgumentException("Sound effect not found: " + prefix);
		}
	}

	public Sound getRandomSound(){
		int sfxidx = MathUtils.random(sounds.size() - 1);
		Sound sound = sounds.get(sfxidx);
		return sound;
	}

	public SfxManager manager(){
		return new SfxManager(this);
	}
}
