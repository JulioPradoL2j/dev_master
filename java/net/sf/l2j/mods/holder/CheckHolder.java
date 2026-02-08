package net.sf.l2j.mods.holder;

public class CheckHolder
{
	private int _id;
	private int _value;
	
	public CheckHolder(int id, int value)
	{
		_id = id;
		_value = value;
	}
	
	@Override
	public String toString()
	{
		return "CheckHolder [id=" + _id + " value=" + _value + "]";
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getValue()
	{
		return _value;
	}
}
