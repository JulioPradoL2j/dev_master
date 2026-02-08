package net.sf.l2j.mods.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FakePlayerTaskLog
{
	private static final Logger LOGGER = Logger.getLogger("FakePlayerTask");
	private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
	
	private FakePlayerTaskLog()
	{
	}
	
	public static void logOnce(String key, String message, Throwable t)
	{
		if (!REPORTED.add(key))
			return;
		
		LOGGER.log(Level.SEVERE, message, t);
	}
}
