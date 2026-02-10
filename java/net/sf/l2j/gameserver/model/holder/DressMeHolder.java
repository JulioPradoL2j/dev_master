package net.sf.l2j.gameserver.model.holder;

import net.sf.l2j.gameserver.templates.StatsSet;

public class DressMeHolder
{
	private int _skillId;
	private int _ItemId;
	private int _actionId;
	private final String _name;
	private final boolean _isVip;
	private final DressMeVisualType _type;
	
	private int _chestId, _legsId, _glovesId, _feetId, _helmetId;
	private int _rHandId, _lHandId, _lrHandId;
	private String _weaponTypeVisual;
	
	public DressMeHolder(StatsSet set)
	{
		_skillId = set.getInteger("skillId", 0);
		_ItemId = set.getInteger("itemId", 0);
		_actionId = set.getInteger("actionUse", 0);
		_name = set.getString("name", "");
		_type = DressMeVisualType.valueOf(set.getString("type", "ARMOR"));
		_isVip = set.getBool("isVip", false);
	}
	
	public void setVisualSet(StatsSet set)
	{
		_chestId = set.getInteger("chest", 0);
		_legsId = set.getInteger("legs", 0);
		_glovesId = set.getInteger("gloves", 0);
		_feetId = set.getInteger("feet", 0);
		_helmetId = set.getInteger("helmet", 0);
	}
	
	public void setWeaponSet(StatsSet set)
	{
		_rHandId = set.getInteger("rhand", 0);
		_lHandId = set.getInteger("lhand", 0);
		_lrHandId = set.getInteger("lrhand", 0);
		_weaponTypeVisual = set.getString("type", "");
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getItemId()
	{
		return _ItemId;
	}
	
	public int getActionId()
	{
		return _actionId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isVip()
	{
		return _isVip;
	}
	
	public DressMeVisualType getType()
	{
		return _type;
	}
	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getLegsId()
	{
		return _legsId;
	}
	
	public int getGlovesId()
	{
		return _glovesId;
	}
	
	public int getFeetId()
	{
		return _feetId;
	}
	
	public int getHelmetId()
	{
		return _helmetId;
	}
	
	public String getWeaponTypeVisual()
	{
		return _weaponTypeVisual;
	}
	
	public int getRightHandId()
	{
		return _rHandId;
	}
	
	public int getLeftHandId()
	{
		return _lHandId;
	}
	
	public int getTwoHandId()
	{
		return _lrHandId;
	}
	
}