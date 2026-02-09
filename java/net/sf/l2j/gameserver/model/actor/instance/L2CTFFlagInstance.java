package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Arrays;

import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class L2CTFFlagInstance extends L2NpcInstance
{
	private static final String flagsPath = "data/html/mods/events/ctf/flags/";
	
	public L2CTFFlagInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player playerInstance, int val)
	{
		if (playerInstance == null)
			return;
		
		if (CTFEvent.isStarting() || CTFEvent.isStarted())
		{
			final String flagTitle = getTitle();
			final String team = CTFEvent.getParticipantTeam(playerInstance.getObjectId()).getName();
			final String enemyteam = CTFEvent.getParticipantEnemyTeam(playerInstance.getObjectId()).getName();
			
			// player talking to friendly flag
			if (flagTitle.equals(team))
			{
				// team flag is missing
				if (CTFEvent.getEnemyCarrier(playerInstance) != null)
				{
					final String htmContent = HtmCache.getInstance().getHtm(flagsPath + "flag_friendly_missing.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", playerInstance.getName());
					playerInstance.sendPacket(npcHtmlMessage);
				}
				// player has returned with enemy flag
				else if (playerInstance == CTFEvent.getTeamCarrier(playerInstance))
				{
					if (CTFConfig.CTF_EVENT_CAPTURE_SKILL > 0)
						playerInstance.broadcastPacket(new MagicSkillUse(playerInstance, CTFConfig.CTF_EVENT_CAPTURE_SKILL, 1, 1, 1));
					
					CTFEvent.removeFlagCarrier(playerInstance);
					CTFEvent.getParticipantTeam(playerInstance.getObjectId()).increasePoints();
					CTFEvent.broadcastScreenMessage("Team " + team + " has scored by capturing Team " + enemyteam + "'s flag!", 7);
					CTFEvent.updateTitlePoints();
				}
				// go get the flag
				else
				{
					final String htmContent = HtmCache.getInstance().getHtm(flagsPath + "flag_friendly.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", playerInstance.getName());
					playerInstance.sendPacket(npcHtmlMessage);
				}
			}
			else
			{
				// player talking to enemy flag
				// player has flag
				if (CTFEvent.playerIsCarrier(playerInstance))
				{
					final String htmContent = HtmCache.getInstance().getHtm(flagsPath + "flag_enemy.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", playerInstance.getName());
					playerInstance.sendPacket(npcHtmlMessage);
				}
				// enemy flag is missing
				else if (CTFEvent.getTeamCarrier(playerInstance) != null)
				{
					final String htmContent = HtmCache.getInstance().getHtm(flagsPath + "flag_enemy_missing.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%player%", CTFEvent.getTeamCarrier(playerInstance).getName());
					playerInstance.sendPacket(npcHtmlMessage);
				}
				// take flag
				else
				{
					if (CTFConfig.CTF_EVENT_CAPTURE_SKILL > 0)
						playerInstance.broadcastPacket(new MagicSkillUse(playerInstance, CTFConfig.CTF_EVENT_CAPTURE_SKILL, 1, 1, 1));
					
					// 1. Salva armas antigas
					CTFEvent.setCarrierUnequippedWeapons(playerInstance, playerInstance.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND), playerInstance.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND));
					
					// 2. Desequipa arma antiga
					playerInstance.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_RHAND);
					playerInstance.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LHAND);
					
					// 4. Cria e equipa a flag
					ItemInstance flagItem = ItemTable.getInstance().createItem("CTF", CTFEvent.getEnemyTeamFlagId(playerInstance), 1, playerInstance, null);
					ItemInstance[] removed = playerInstance.getInventory().equipItemAndRecord(flagItem);
					
					// 5. Atualiza inventário e visual
					InventoryUpdate iu = new InventoryUpdate();
					iu.addItems(Arrays.asList(removed));
					iu.addItem(flagItem);
					playerInstance.sendPacket(iu);
					
					// 6. Bloqueia inventário
					playerInstance.getInventory().blockAllItems();
					
					// 7. Atualiza visual
					playerInstance.broadcastUserInfo();
					
					// 8. Marca como carregador
					CTFEvent.setTeamCarrier(playerInstance);
					CTFEvent.broadcastScreenMessage("Team " + team + " has taken the Team " + enemyteam + " flag!", 5);
					playerInstance.broadcastPacket(new SocialAction(playerInstance, 16));
				}
			}
		}
		playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
	}
}