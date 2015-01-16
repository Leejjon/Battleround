package org.stofkat.battleround.common;


/**
 * Every update result class needs to have an update number and round number.
 * This abstract class forces UpdateResults to have them.
 * 
 * And it implements the UpdateInterface so code that only cares about the
 * update number or round numbers can still request it.
 * 
 * @author Leejjon
 */
public abstract class UpdateResult implements Update {
	private static final long serialVersionUID = 100L;
	
	protected long updateNumber;
	
	protected long roundNumber;
	
	@Override
	public long getUpdateNumber() {
		return updateNumber;
	}
	
	@Override
	public long getRoundNumber() {
		return roundNumber;
	}
	
	@Override
	public void setUpdateNumber(long updateNumber) {
		this.updateNumber = updateNumber;
	}

	public void setRoundNumber(long roundNumber) {
		this.roundNumber = roundNumber;
	}
}
