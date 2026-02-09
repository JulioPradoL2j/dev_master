package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.event.bossevent.KTBConfig;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.lastman.LMConfig;
import net.sf.l2j.event.lastman.LMEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.item.type.ActionType;
import net.sf.l2j.gameserver.model.item.type.ArmorType;
import net.sf.l2j.gameserver.model.item.type.CrystalType;
import net.sf.l2j.gameserver.model.item.type.EtcItemType;
import net.sf.l2j.gameserver.model.item.type.WeaponType;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.PetItemList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scriptings.Quest;
import net.sf.l2j.gameserver.scriptings.QuestState;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.gameserver.util.FloodProtectors;
import net.sf.l2j.gameserver.util.FloodProtectors.Action;
import net.sf.l2j.itemstime.TimeItemData;
import net.sf.l2j.itemstime.TimedItemManager;

public final class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;
	
	/*
	 * public static class WeaponEquipTask implements Runnable { ItemInstance _item; L2PcInstance _activeChar; public WeaponEquipTask(ItemInstance it, L2PcInstance character) { _item = it; _activeChar = character; }
	 * @Override public void run() { _activeChar.useEquippableItem(_item, false); } }
	 */
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.USER_ITEM))
		{
			return;
		}
		
		// if (activeChar.isInStoreMode())
		// {
		// activeChar.sendPacket(SystemMessageId.ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE);
		// return;
		// }
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
			return;
		}
		
		final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if (item == null)
			return;
		
		if (activeChar.isInStoreMode() && !(item.getItemId() == Config.ITEM_PERMITIDO_PARA_USAR_NA_LOJA_ID))
		{
			activeChar.sendPacket(SystemMessageId.ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE);
			return;
		}
		
		if (item.getItem().getType2() == Item.TYPE2_QUEST)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		if (activeChar.isAlikeDead() || activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAfraid())
			return;
		
		if (Config.WYVERN_PROTECTION && activeChar.isInsideZone(ZoneId.PEACE) && Config.LISTID_RESTRICT.contains(item.getItemId()))
		{
			activeChar.sendMessage("You can not use this item here within the city.");
			return;
		}
		if (KTBEvent.isPlayerParticipant(activeChar.getObjectId()) && KTBEvent.isStarted())
		{
			if (KTBConfig.KTB_LISTID_RESTRICT.contains(Integer.valueOf(item.getItemId())))
			{
				activeChar.sendMessage("You can not use this item during KTB.");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		// Check if the item is a timed item
		long remainingTime = TimedItemManager.getInstance().getRemainingTime(item.getObjectId());
		if (remainingTime > 0)
		{
			long days = remainingTime / (60 * 60 * 24); // Dias
			long hours = (remainingTime % (60 * 60 * 24)) / (60 * 60); // Horas
			long minutes = (remainingTime % (60 * 60)) / 60; // Minutos
			long seconds = remainingTime % 60; // Segundos
			
			activeChar.sendMessage("Your item will expire in " + days + " day(s), " + hours + " hour(s), " + minutes + " min(s), and " + seconds + " second(s).");
		}
		else
		{
			// Verifica se o item tem configuração de tempo no XML
			if (TimeItemData.getInstance().isTimedItem(item.getItemId()))
			{
				activeChar.sendMessage("This item has expired.");
			}
		}
		
		if (activeChar.isInOlympiadMode() && Config.LISTID_RESTRICT_OLY.contains(item.getItemId()))
		{
			activeChar.sendMessage("Can only be used -> " + item.getItemName() + " in Olympiad.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!(activeChar.isClanLeader()) && item.getItemId() == 6841 && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
			return;
		}
		
		if (LMEvent.isStarted() && LMEvent.isPlayerParticipant(activeChar.getObjectId()))
		{
			if (LMConfig.LM_LISTID_RESTRICT.contains(Integer.valueOf(item.getItemId())))
			{
				activeChar.sendMessage("You can not use this item during LM.");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		if (Config.WYVERN_PROTECTION_BOSS_ZONE && activeChar.isInsideZone(ZoneId.BOSS) && Config.LISTID_RESTRICT.contains(item.getItemId()))
		{
			activeChar.sendMessage("You can not use this item here within the Boss Zone.");
			return;
		}
		if (Config.ALT_DISABLE_BOW_CLASSES)
		{
			if (item.getItem() instanceof Weapon && ((Weapon) item.getItem()).getItemType() == WeaponType.BOW && !activeChar.isGM() && !activeChar.isInOlympiadMode())
			{
				if (Config.DISABLE_BOW_CLASSES.contains(activeChar.getClassId().getId()))
				{
					activeChar.sendMessage("This item can not be equipped by your class");
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		if (Config.NOTALLOWEDUSEHEAVY.contains(activeChar.getClassId().getId()) && !activeChar.isGM() && !activeChar.isInOlympiadMode() && Config.ENABLE_NO_USE_SETSOLY)
		{
			if (item.getItemType() == ArmorType.HEAVY)
			{
				activeChar.sendMessage(Config.WELCOME_MESSAGE_ANTIHERVY);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		if (Config.NOTALLOWEDUSEMAGIC.contains(activeChar.getClassId().getId()) && !activeChar.isGM() && !activeChar.isInOlympiadMode() && Config.ENABLE_NO_USE_SETSOLY)
		{
			if (item.getItemType() == ArmorType.MAGIC)
			{
				activeChar.sendMessage(Config.WELCOME_MESSAGE_ANTIROBE);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		if (Config.NOTALLOWEDUSELIGHT.contains(activeChar.getClassId().getId()) && !activeChar.isGM() && !activeChar.isInOlympiadMode() && Config.ENABLE_NO_USE_SETSOLY)
		{
			if (item.getItemType() == ArmorType.LIGHT)
			{
				activeChar.sendMessage(Config.WELCOME_MESSAGE_ANTILIGHT);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		if (activeChar.isInArenaEvent() || activeChar.isArenaProtection())
		{
			if (ArenaConfig.TOURNAMENT_LISTID_RESTRICT.contains(Integer.valueOf(item.getItemId())))
			{
				activeChar.sendMessage("You can not use this item during Tournament.");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if (activeChar.isGM())
			activeChar.sendPacket(new CreatureSay(0, Say2.PARTY, "", "{ID}  " + item.getItemId() + "   {NAME}  " + item.getItemName()));
		
		if (!Config.KARMA_PLAYER_CAN_TELEPORT && activeChar.getKarma() > 0)
		{
			final IntIntHolder[] sHolders = item.getItem().getSkills();
			if (sHolders != null)
			{
				for (IntIntHolder sHolder : sHolders)
				{
					final L2Skill skill = sHolder.getSkill();
					if (skill != null && (skill.getSkillType() == L2SkillType.TELEPORT || skill.getSkillType() == L2SkillType.RECALL))
						return;
				}
			}
		}
		
		if (activeChar.isFishing() && item.getItem().getDefaultAction() != ActionType.fishingshot)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		/*
		 * The player can't use pet items if no pet is currently summoned. If a pet is summoned and player uses the item directly, it will be used by the pet.
		 */
		if (item.isPetItem())
		{
			// If no pet, cancels the use
			if (!activeChar.hasPet())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_EQUIP_PET_ITEM);
				return;
			}
			
			final L2PetInstance pet = ((L2PetInstance) activeChar.getPet());
			
			if (!pet.canWear(item.getItem()))
			{
				activeChar.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
				return;
			}
			
			if (pet.isDead())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET);
				return;
			}
			
			if (!pet.getInventory().validateCapacity(item))
			{
				activeChar.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return;
			}
			
			if (!pet.getInventory().validateWeight(item, 1))
			{
				activeChar.sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return;
			}
			
			activeChar.transferItem("Transfer", _objectId, 1, pet.getInventory(), pet);
			
			// Equip it, removing first the previous item.
			if (item.isEquipped())
			{
				pet.getInventory().unEquipItemInSlot(item.getLocationSlot());
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(item));
			}
			else
			{
				pet.getInventory().equipPetItem(item);
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PUT_ON_S1).addItemName(item));
			}
			
			activeChar.sendPacket(new PetItemList(pet));
			pet.updateAndBroadcastStatus(1);
			return;
		}
		if (!activeChar.getInventory().canManipulateWithItemId(item.getItemId()))
			return;
		if (!Config.LISTID_RESTRICT_OLY.contains(item.getItemId()) && activeChar.isInOlympiadMode())
		{
			if (Config.OLLY_GRADE_A && item.getItem().getCrystalType() == CrystalType.S && (activeChar.isInOlympiadMode() || activeChar.isOlympiadProtection() || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar)))
			{
				activeChar.sendMessage("[Olympiad]: Items Grade S cannot be used in Olympiad Event");
				return;
			}
		}
		if (Config.BLOCK_REGISTER_ITEMS_OLYMPIAD_ENCHANT)
		{
			if (item.getEnchantLevel() > Config.ALT_OLY_ENCHANT_LIMIT && (activeChar.isInOlympiadMode() || activeChar.isOlympiadProtection() || OlympiadManager.getInstance().isRegistered(activeChar) || OlympiadManager.getInstance().isRegisteredInComp(activeChar)))
			{
				activeChar.sendMessage("Equipment with enchant level above +" + Config.ALT_OLY_ENCHANT_LIMIT + " can not be used while registered in olympiad.");
				return;
			}
		}
		if (!item.isEquipped())
		{
			if (!item.getItem().checkCondition(activeChar, activeChar, true))
				return;
		}
		
		// if ((item.getItemId() == 8663 || item.getItemId() == 4422 || item.getItemId() == 4423 || item.getItemId() == 4424) && ((activeChar._inEventTvT && (TvT.is_teleport() || TvT.is_started())) || (activeChar._inEventCTF && (CTF.is_teleport() || CTF.is_started()))))
		// return;
		
		// if ((item.getItemId() == 1538 || item.getItemId() == 3958 || item.getItemId() == 5858 || item.getItemId() == 5859 || item.getItemId() == 9156) && ((activeChar._inEventTvT && TvT.is_started() && !Config.TVT_ALLOW_POTIONS) || (activeChar._inEventCTF && CTF.is_started() &&
		// !Config.CTF_ALLOW_POTIONS)))
		// {
		// activeChar.sendMessage("You can not use this item in Combat/Event mode..");
		// return;
		// }
		// if ((item.getItemId() == 1538 || item.getItemId() == 3958 || item.getItemId() == 5858 || item.getItemId() == 5859 || item.getItemId() == 9156) && (activeChar._inEventCTF && activeChar._haveFlagCTF))
		// {
		// activeChar.sendMessage("This item can not be used Potions when you have the flag..");
		// return;
		// }
		
		if (item.isEquipable())
		{
			// if (activeChar.isCastingNow() || activeChar.isCastingSimultaneouslyNow())
			// if (activeChar.isCastingNow() || activeChar.isCastingSimultaneouslyNow() || (activeChar._inEventCTF && activeChar._haveFlagCTF && (item.getItem().getBodyPart() == Item.SLOT_LR_HAND || item.getItem().getBodyPart() == Item.SLOT_L_HAND || item.getItem().getBodyPart() ==
			// Item.SLOT_R_HAND)))
			// {
			// if (activeChar._inEventCTF && activeChar._haveFlagCTF)
			// activeChar.sendMessage("This item can not be equipped when you have the flag.");
			// else
			// activeChar.sendPacket(SystemMessageId.CANNOT_USE_ITEM_WHILE_USING_MAGIC);
			// return;
			// }
			if (activeChar.isCastingNow() || activeChar.isCastingSimultaneouslyNow())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_USE_ITEM_WHILE_USING_MAGIC);
				return;
			}
			switch (item.getItem().getBodyPart())
			{
				case Item.SLOT_LR_HAND:
				case Item.SLOT_L_HAND:
				case Item.SLOT_R_HAND:
				{
					if (activeChar.isMounted())
					{
						activeChar.sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
						return;
					}
					
					// Don't allow weapon/shield equipment if a cursed weapon is equipped
					if (activeChar.isCursedWeaponEquipped())
						return;
					
					break;
				}
			}
			
			if (activeChar.isCursedWeaponEquipped() && item.getItemId() == 6408) // Don't allow to put formal wear
				return;
			
			if (activeChar.isAttackingNow())
				ThreadPool.schedule(() -> {
					final ItemInstance itemToTest = activeChar.getInventory().getItemByObjectId(_objectId);
					if (itemToTest == null)
						return;
					
					activeChar.useEquippableItem(itemToTest, false);
				}, activeChar.getAttackEndTime() - System.currentTimeMillis());
			
			else
				activeChar.useEquippableItem(item, true);
		}
		else
		{
			if (activeChar.isCastingNow() && !(item.isPotion() || item.isElixir()))
				return;
			
			if (activeChar.getAttackType() == WeaponType.FISHINGROD && item.getItem().getItemType() == EtcItemType.LURE)
			{
				activeChar.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
				activeChar.broadcastUserInfo();
				
				sendPacket(new ItemList(activeChar, false));
				return;
			}
			
			final IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(activeChar, item, _ctrlPressed);
			
			for (Quest quest : item.getQuestEvents())
			{
				QuestState state = activeChar.getQuestState(quest.getName());
				if (state == null || !state.isStarted())
					continue;
				
				quest.notifyItemUse(item, activeChar, activeChar.getTarget());
			}
		}
	}
	
	public static final int[] FAKE_WEAPON_SKILLS =
	{
		24502,
		24503
	};
}