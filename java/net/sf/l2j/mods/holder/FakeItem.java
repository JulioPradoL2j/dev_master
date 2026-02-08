package net.sf.l2j.mods.holder;

public class FakeItem
{
	private final int _itemId;
	private final int _count;
	private final boolean _equipped;
	
	public FakeItem(int itemId, int count, boolean equipped)
	{
		_itemId = itemId;
		_count = count;
		_equipped = equipped;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public boolean isEquipped()
	{
		return _equipped;
	}
}