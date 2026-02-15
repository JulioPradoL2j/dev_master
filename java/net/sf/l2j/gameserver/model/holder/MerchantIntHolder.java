package net.sf.l2j.gameserver.model.holder;

import net.sf.l2j.gameserver.datatables.ItemTable;

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
	
	public final String getGradeIcon()
	{
		switch (ItemTable.getInstance().getTemplate(_id).getCrystalType())
		{
			case S:
				return "<img src=symbol.grade_s width=14 height=14>";
			case A:
				return "<img src=symbol.grade_a width=14 height=14>";
			case B:
				return "<img src=symbol.grade_b width=14 height=14>";
			case C:
				return "<img src=symbol.grade_c width=14 height=14>";
			case D:
				return "<img src=symbol.grade_d width=14 height=14>";
			default:
				break;
		}
		return "<img height=5>";
	}
}