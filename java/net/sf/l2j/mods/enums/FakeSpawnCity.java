package net.sf.l2j.mods.enums;

import net.sf.l2j.gameserver.model.Location;

public enum FakeSpawnCity
{
	// LOW / BASE
	TALKING_ISLAND(new Location(-81400, 246456, -3688)),
	ELVEN_VILLAGE(new Location(46072, 41752, -3504)),
	DARK_ELVEN_VILLAGE(new Location(28391, 11017, -4235)),
	ORC_VILLAGE(new Location(-56693, -113610, -690)),
	DWARVEN_VILLAGE(new Location(109560, -173816, -552));
	
	private final Location _base;
	
	FakeSpawnCity(Location base)
	{
		_base = base;
	}
	
	public Location getBase()
	{
		return _base;
	}
}
