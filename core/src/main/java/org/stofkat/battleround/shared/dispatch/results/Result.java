package org.stofkat.battleround.shared.dispatch.results;

import java.io.Serializable;

import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public interface Result extends Serializable {
	void processResult(EngineInterface engineInterface) throws EngineException;
}
