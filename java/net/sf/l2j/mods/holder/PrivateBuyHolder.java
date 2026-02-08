package net.sf.l2j.mods.holder;

import net.sf.l2j.Config;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.templates.StatsSet;

public class PrivateBuyHolder extends CheckHolder
{
	private final int _isEnchant;
	private final String _titleSms;
	
	public PrivateBuyHolder(StatsSet set)
	{
		super(set.getInteger("id"), set.getInteger("count"));
		_isEnchant = set.getInteger("enchant", 0);
		_titleSms = set.getString("title", "");
		
	}
	
	public final String getTitle()
	{
		return _titleSms;
	}
	
	public final int getEnchant()
	{
		return _isEnchant;
	}
	
	public final int getPrinceValue()
	{
		final int currency = Config.STORE_SELL_CURRENCY;
		
		final int refPrice = ItemTable.getInstance().getTemplate(getId()).getReferencePrice();
		
		// ADENA → preço oficial
		if (currency == 57)
		{
			return Math.max(1, refPrice);
		}

		int base;
		
		if (refPrice <= 0)
		{
			base = Rnd.get(1, 5);
		}
		else if (refPrice < 1000)
		{
			base = Rnd.get(1, 10);
		}
		else if (refPrice < 10000)
		{
			base = Rnd.get(5, 25);
		}
		else if (refPrice < 100000)
		{
			base = Rnd.get(10, 75);
		}
		else
		{
			base = Rnd.get(25, 150);
		}
		
		return Math.max(1, base);
	}
	
}
