package net.sf.l2j.mods.holder;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.templates.StatsSet;

public class FakeTemplate
{
	private final int classId;
	private final String title;
	private final int level;
	
	private final StatsSet appearance;
	private final List<FakeItem> items = new ArrayList<>();
	
	public FakeTemplate(StatsSet set)
	{
		classId = set.getInteger("classId");
		title = set.getString("title", "");
		level = set.getInteger("level", 1);
		
		appearance = set.getObject("appearance", StatsSet.class);
	}
	
	public int getClassId()
	{
		return classId;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public StatsSet getAppearance()
	{
		return appearance;
	}
	
	public List<FakeItem> getItems()
	{
		return items;
	}
	
	public void addItem(FakeItem item)
	{
		items.add(item);
	}
}
