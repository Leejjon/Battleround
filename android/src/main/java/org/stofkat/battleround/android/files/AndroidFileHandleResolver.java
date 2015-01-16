package org.stofkat.battleround.android.files;

import java.util.Locale;

import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineException.EngineExceptions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class AndroidFileHandleResolver implements FileHandleResolver {
	
	private String levelFileName;
	
	public AndroidFileHandleResolver(String levelFileName) {
		this.levelFileName = levelFileName;
	}
	
	public FileHandle getDirectory() {
		String fileSeparator = System.getProperty("file.separator");
		String levelImagesPath = "data" + fileSeparator + "levels" + fileSeparator + levelFileName.toLowerCase(Locale.ENGLISH) + "_images" + fileSeparator;
		String realLevelImagesPath;

		realLevelImagesPath = levelImagesPath;

		return Gdx.files.getFileHandle(realLevelImagesPath, FileType.Internal);
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
