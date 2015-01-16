package org.stofkat.battleround.desktop.files;

import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineException.EngineExceptions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class DesktopFileHandleResolver implements FileHandleResolver {
	
	private String levelFileName;
	
	public DesktopFileHandleResolver(String levelFileName) {
		this.levelFileName = levelFileName;
	}
	
	public FileHandle getDirectory() {
		// Override this method.
		String fileSeparator = System.getProperty("file.separator");
		String levelImagesPath = ".." + fileSeparator + "assets"
				+ fileSeparator + "data" + fileSeparator + "levels"
				+ fileSeparator + levelFileName + "_images" + fileSeparator;

		FileHandle directory = Gdx.files.internal(levelImagesPath);
		return directory;
	}
	
	/**
	 * @throws IllegalArgumentException
	 *             I'd rather use an EngineException, but I don't want to fork
	 *             libgdx so I've packed it in an IllegalArgumentException.
	 */
	@Override
	public FileHandle resolve(String fileName) {
		FileHandle directory = getDirectory();
		
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(new EngineException(EngineExceptions.PATH_IS_NOT_A_DIRECTORY));
		}
		
		for (FileHandle fileHandle : getDirectory().list()) {
			if (fileHandle.name().equals(fileName)) {
				return fileHandle;
			}
		}
	
		throw new IllegalArgumentException(new EngineException(EngineExceptions.FILE_NOT_FOUND));
	}

}
