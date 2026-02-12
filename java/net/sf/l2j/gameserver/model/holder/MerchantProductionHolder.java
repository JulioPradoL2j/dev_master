package net.sf.l2j.gameserver.model.holder;

import java.util.Collections;
import java.util.List;

import net.sf.l2j.gameserver.templates.StatsSet;

public class MerchantProductionHolder
{
	private final MerchantIntHolder _product; // id + amount
	private final List<MerchantIntHolder> _ingredients; // list id+amount
	private final int _enchantLevel;
	
	public MerchantProductionHolder(StatsSet set)
	{
		_product = set.getMerchantIntHolder("product");
		_ingredients = set.containsKey("ingredient") ? set.getMerchantHolderList("ingredient") : Collections.emptyList();
		_enchantLevel = set.getInteger("enchantLevel", 0);
	}
	
	public MerchantIntHolder getProduct()
	{
		return _product;
	}
	
	public List<MerchantIntHolder> getIngredients()
	{
		return _ingredients;
	}
	
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
}
