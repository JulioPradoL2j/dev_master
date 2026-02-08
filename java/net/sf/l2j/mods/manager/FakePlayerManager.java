package net.sf.l2j.mods.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.mods.actor.FakePlayer;

 

public final class FakePlayerManager
{
	private final List<FakePlayer> _fakePlayers = new CopyOnWriteArrayList<>();
	private final Map<Integer, FakePlayer> _byObjectId = new ConcurrentHashMap<>();
	
	public void register(FakePlayer player)
	{
		_fakePlayers.add(player);
		_byObjectId.put(player.getObjectId(), player);
	}
	
	public void unregister(FakePlayer player)
	{
		_fakePlayers.remove(player);
		_byObjectId.remove(player.getObjectId());
	}
	
	public boolean registerIfAbsent(FakePlayer player)
	{
		return _byObjectId.putIfAbsent(player.getObjectId(), player) == null;
	}
	
	public FakePlayer getPlayer(int objectId)
	{
		return _byObjectId.get(objectId);
	}
	
	public boolean isOnline(int objectId)
	{
		return _byObjectId.containsKey(objectId);
	}
	
	public List<FakePlayer> getFakePlayers()
	{
		return _fakePlayers;
	}
	
	public Collection<FakePlayer> values()
	{
		return _byObjectId.values();
	}
	
	public int getFakePlayersCount()
	{
		return _fakePlayers.size();
	}
	
	public static FakePlayerManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final FakePlayerManager INSTANCE = new FakePlayerManager();
	}
}
