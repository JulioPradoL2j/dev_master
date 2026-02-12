package net.sf.l2j.gameserver.model.holder;

public class MerchantIntHolder
{
	private int _id;
	private int _value;
	
	public MerchantIntHolder(int id, int value)
	{
		_id = id;
		_value = value;
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