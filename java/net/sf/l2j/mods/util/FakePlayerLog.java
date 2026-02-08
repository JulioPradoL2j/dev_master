package net.sf.l2j.mods.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.mods.actor.FakePlayer;

 
public final class FakePlayerLog
{
	private static final Logger LOGGER = Logger.getLogger("FakePlayerAI");
	private static final Set<Integer> REPORTED = ConcurrentHashMap.newKeySet();
	
	private FakePlayerLog()
	{
	}
	
	public static void logOnce(FakePlayer fp, String message, Throwable t)
	{
		// loga uma vez por FakePlayer
		if (!REPORTED.add(fp.getObjectId()))
			return;
		
		LOGGER.log(Level.SEVERE, "[FakePlayer:" + fp.getName() + " | id=" + fp.getObjectId() + "] " + message, t);
	}
	
	
}