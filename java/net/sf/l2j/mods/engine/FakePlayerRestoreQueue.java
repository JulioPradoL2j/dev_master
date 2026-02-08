package net.sf.l2j.mods.engine;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class FakePlayerRestoreQueue
{
	private static final Queue<Integer> QUEUE = new ConcurrentLinkedQueue<>();
	private static final Set<Integer> QUEUED = ConcurrentHashMap.newKeySet();

	public static void add(int objectId)
	{
		if (QUEUED.add(objectId))
			QUEUE.offer(objectId);
	}

	public static Integer poll()
	{
		Integer id = QUEUE.poll();
		if (id != null)
			QUEUED.remove(id);
		return id;
	}

	public static boolean isEmpty()
	{
		return QUEUE.isEmpty();
	}

	public static void clear()
	{
		QUEUE.clear();
		QUEUED.clear();
	}
}
