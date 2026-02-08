package net.sf.l2j.mods.spawn;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.Location;

public final class SpawnPointGenerator
{
	private static final int MIN_RADIUS = 80;
	private static final int MAX_RADIUS = 800;
	private static final int MAX_TRIES = 15;
	
	private SpawnPointGenerator()
	{
	}
	
	public static Location findValid(int baseX, int baseY, int baseZ)
	{
		for (int i = 0; i < MAX_TRIES; i++)
		{
			double angle = Rnd.nextDouble() * Math.PI * 2;
			int radius = Rnd.get(MIN_RADIUS, MAX_RADIUS);
			
			int x = baseX + (int) (Math.cos(angle) * radius);
			int y = baseY + (int) (Math.sin(angle) * radius);
			
			Location loc = SpawnValidator.validate(x, y, baseZ);
			if (loc != null)
				return loc;
		}
		
		return SpawnValidator.validate(baseX, baseY, baseZ);
	}
}
