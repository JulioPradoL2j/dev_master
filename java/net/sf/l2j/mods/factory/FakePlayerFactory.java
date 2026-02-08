package net.sf.l2j.mods.factory;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.appearance.PcAppearance;
import net.sf.l2j.gameserver.model.actor.template.PcTemplate;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.model.base.Sex;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.type.CrystalType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.data.FakeNameData;
import net.sf.l2j.mods.data.FakePlayerData;
import net.sf.l2j.mods.engine.FakePlayerRestoreQueue;
import net.sf.l2j.mods.holder.FakeItem;
import net.sf.l2j.mods.holder.FakeTemplate;
import net.sf.l2j.mods.manager.FakePlayerManager;

 
public final class FakePlayerFactory
{
	private FakePlayerFactory()
	{
		
	}
	
	public static FakePlayer create(int classId, int x, int y, int z)
	{
		// ===============================
		// TEMPLATE XML
		// ===============================
		final FakeTemplate tpl = FakePlayerData.getInstance().getTemplate(classId);
		if (tpl == null)
			throw new IllegalArgumentException("FakeTemplate inexistente para classId " + classId);
			
		// ===============================
		// PC TEMPLATE REAL (CORE)
		// ===============================
		final PcTemplate pcTemplate = CharTemplateTable.getInstance().getTemplate(classId);
		if (pcTemplate == null)
			throw new IllegalStateException("PcTemplate inexistente para classId " + classId);
			
		// ===============================
		// APPEARANCE
		// ===============================
		final byte face = (byte) tpl.getAppearance().getInteger("face", 0);
		final byte hairColor = (byte) tpl.getAppearance().getInteger("hairColor", 0);
		final byte hairStyle = (byte) tpl.getAppearance().getInteger("hairStyle", 0);
		
		final Sex sex = Rnd.get(2) == 0 ? Sex.MALE : Sex.FEMALE;
		
		final String name = FakeNameData.getInstance().getRandomName(sex);
		
		String accountName = "AutoPilot_" + name + "AI";
		
		final PcAppearance appearance = new PcAppearance(face, hairColor, hairStyle, sex);
		int objectId = IdFactory.getInstance().getNextId();
		
		// ===============================
		// PLAYER
		// ===============================
		Player.create(objectId, pcTemplate, accountName, name, hairStyle, hairColor, face, sex);
		final FakePlayer fake = new FakePlayer(objectId, pcTemplate, accountName, appearance);
		fake.registerAccount();
		// ===============================
		// IDENTIDADE
		// ===============================
		fake.setClassId(classId);
		if (!fake.isSubClassActive())
		{
			fake.setBaseClass(classId);
		}
		
		fake.setName(name);
		fake.setTitle(tpl.getTitle());
		
		byte lvl = (byte) tpl.getLevel();
		if (lvl >= 1 && lvl <= Experience.MAX_LEVEL)
		{
			long pXp = fake.getExp();
			long tXp = Experience.LEVEL[lvl];
			
			if (pXp > tXp)
				fake.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				fake.addExpAndSp(tXp - pXp, 0);
		}
		
		// ===============================
		// INVENTÁRIO / EQUIPAMENTOS
		// ===============================
		for (FakeItem item : tpl.getItems())
		{
			if (item.getItemId() != 0)
			{
				final ItemInstance inst = fake.addItem("items", item.getItemId(), item.getCount(), fake, false);
				
				if (item.isEquipped())
				{
					if (ItemTable.getInstance().getTemplate(inst.getItemId()).getCrystalType() != CrystalType.NONE)
						inst.setEnchantLevel(Rnd.get(5, 25));
					fake.getInventory().equipItem(inst);
				}
			}
		}
		fake.getStatus().setCurrentHpMp(fake.getMaxHp(), fake.getMaxMp());
		fake.getStatus().setCurrentCp(fake.getMaxCp());
		fake.rewardSkills();
		
		// ===============================
		// SPAWN REAL
		// ===============================
		fake.setXYZInvisible(x, y, z);
		
		fake.setOnlineStatus(true, false);
		fake.deleteMe();
		if (FakePlayerManager.getInstance().getPlayer(objectId) == null)
			FakePlayerRestoreQueue.add(objectId);
		return fake;
	}
}
