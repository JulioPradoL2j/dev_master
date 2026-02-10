package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.skillhandlers.BalanceLife;
import net.sf.l2j.gameserver.handler.skillhandlers.Blow;
import net.sf.l2j.gameserver.handler.skillhandlers.Cancel;
import net.sf.l2j.gameserver.handler.skillhandlers.CombatPointHeal;
import net.sf.l2j.gameserver.handler.skillhandlers.Continuous;
import net.sf.l2j.gameserver.handler.skillhandlers.CpDamPercent;
import net.sf.l2j.gameserver.handler.skillhandlers.Craft;
import net.sf.l2j.gameserver.handler.skillhandlers.Disablers;
import net.sf.l2j.gameserver.handler.skillhandlers.DrainSoul;
import net.sf.l2j.gameserver.handler.skillhandlers.Dummy;
import net.sf.l2j.gameserver.handler.skillhandlers.Extractable;
import net.sf.l2j.gameserver.handler.skillhandlers.Fishing;
import net.sf.l2j.gameserver.handler.skillhandlers.FishingSkill;
import net.sf.l2j.gameserver.handler.skillhandlers.GetPlayer;
import net.sf.l2j.gameserver.handler.skillhandlers.GiveSp;
import net.sf.l2j.gameserver.handler.skillhandlers.Harvest;
import net.sf.l2j.gameserver.handler.skillhandlers.Heal;
import net.sf.l2j.gameserver.handler.skillhandlers.HealPercent;
import net.sf.l2j.gameserver.handler.skillhandlers.InstantJump;
import net.sf.l2j.gameserver.handler.skillhandlers.ManaHeal;
import net.sf.l2j.gameserver.handler.skillhandlers.Manadam;
import net.sf.l2j.gameserver.handler.skillhandlers.Mdam;
import net.sf.l2j.gameserver.handler.skillhandlers.Pdam;
import net.sf.l2j.gameserver.handler.skillhandlers.Resurrect;
import net.sf.l2j.gameserver.handler.skillhandlers.Sow;
import net.sf.l2j.gameserver.handler.skillhandlers.Spoil;
import net.sf.l2j.gameserver.handler.skillhandlers.StrSiegeAssault;
import net.sf.l2j.gameserver.handler.skillhandlers.SummonFriend;
import net.sf.l2j.gameserver.handler.skillhandlers.Sweep;
import net.sf.l2j.gameserver.handler.skillhandlers.TakeCastle;
import net.sf.l2j.gameserver.handler.skillhandlers.Unlock;
import net.sf.l2j.gameserver.handler.skillhandlers.applySkins;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public class SkillHandler
{
	private final Map<Integer, ISkillHandler> _datatable = new HashMap<>();
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected SkillHandler()
	{
		registerSkillHandler(new applySkins());
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Blow());
		registerSkillHandler(new Cancel());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new CpDamPercent());
		registerSkillHandler(new Craft());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Dummy());
		registerSkillHandler(new Extractable());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new GiveSp());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new Heal());
		registerSkillHandler(new HealPercent());
		registerSkillHandler(new InstantJump());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
	}
	
	public void registerSkillHandler(ISkillHandler handler)
	{
		for (L2SkillType t : handler.getSkillIds())
			_datatable.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getSkillHandler(L2SkillType skillType)
	{
		return _datatable.get(skillType.ordinal());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler _instance = new SkillHandler();
	}
}