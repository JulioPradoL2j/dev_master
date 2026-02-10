package net.sf.l2j.gameserver.handler.skillhandlers;

import net.sf.l2j.gameserver.datatables.xml.DressMeData;
import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public class applySkins implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.DRESSME
	};
	
	// 10s global cooldown (igual seu exemplo)
	private static final long COOLDOWN_MS = 10_000L;
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		
		final DressMeHolder dress = DressMeData.getInstance().getBySkillId(skill.getId());
		if (dress == null)
		{
			player.sendMessage("Visual not found.");
			return;
		}
		
		// VIP gate (se você tiver player.isVip() / player.hasVip())
		if (dress.isVip() && !player.isVip()) // ajuste para o seu método real
		{
			player.sendMessage("This visual is VIP-only.");
			return;
		}
		
		// cooldown
		final long now = System.currentTimeMillis();
		if (now - player.getLastDressMeSummonTime() < COOLDOWN_MS)
		{
			player.sendMessage("You need to wait before using DressMe again.");
			return;
		}
		
		if (player.getArmorSkin() != null && player.getArmorSkin().getSkillId() == skill.getId())
		{
			player.sendMessage("This armor look is already active.");
			player.removeDressMeArmor();
			return;
		}
		if (player.getWeaponSkin() != null && player.getWeaponSkin().getSkillId() == skill.getId())
		{
			player.sendMessage("This weapon look is already active.");
			player.removeDressMeWeapon();
			return;
		}
		
		// aplica
		player.applyDressMe(dress, Player.DressMeSource.SKILL, skill.getId());
		player.setLastDressMeSummonTime(now);
		
	}
	
}
