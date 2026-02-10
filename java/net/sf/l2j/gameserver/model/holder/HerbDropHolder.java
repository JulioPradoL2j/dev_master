package net.sf.l2j.gameserver.model.holder;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.templates.StatsSet;

public class HerbDropHolder
{
    private final int _npcId;
    private final int _baseGroup;
    private final boolean _merge;
    private final boolean _replace;

    private final List<StatsSet> _items = new ArrayList<>();

    public HerbDropHolder(StatsSet set)
    {
        _npcId = set.getInteger("id");
        _baseGroup = set.getInteger("baseGroup", 0);
        _merge = set.getBool("merge", false);
        _replace = set.getBool("replace", false);
    }

    public void addItem(StatsSet item)
    {
        _items.add(item);
    }

    public int getNpcId()
    {
        return _npcId;
    }

    public int getBaseGroup()
    {
        return _baseGroup;
    }

    public boolean isMerge()
    {
        return _merge;
    }

    public boolean isReplace()
    {
        return _replace;
    }

    public List<StatsSet> getItems()
    {
        return _items;
    }
}
