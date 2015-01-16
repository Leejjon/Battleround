package org.stofkat.battleround.html.engine.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class GwtFileHandleResolver implements FileHandleResolver {
	
	private String levelFileName;
	
	public GwtFileHandleResolver(String levelFileName) {
		this.levelFileName = levelFileName;
	}
	
	@Override
	public FileHandle resolve(String fileName) {
		return Gdx.files.internal("data/levels/" + levelFileName.toLowerCase() + "_images/" + fileName);
	}
}
