package net.sf.l2j.mods.spawn;

import net.sf.l2j.mods.enums.TownActivityMode;

public final class TownActivityState
{
	private TownActivityMode mode = TownActivityMode.NONE;
	
	public TownActivityMode getMode()
	{
		return mode;
	}
	
	public boolean isActive()
	{
		return mode != TownActivityMode.NONE;
	}
	
	public void activate(TownActivityMode newMode)
	{
		mode = newMode;
		
	}
	
	public void reset()
	{
		mode = TownActivityMode.NONE;
		
	}
}
