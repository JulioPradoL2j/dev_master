package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import net.sf.l2j.gameserver.datatables.xml.DressMeData;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class ApplySkins implements IItemHandler
{
	private static final long COOLDOWN_MS = 10_000L;
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		if (item == null)
			return;
			
		//  Opção A: itemId aponta direto para a entrada do DressMeData via itemId
		// (ou seja, no DressMeData.xml, <dress itemId="ITEM_ID" ...>)
		final DressMeHolder dress = DressMeData.getInstance().getByItemId(item.getItemId());
		
		if (dress == null)
		{
			player.sendMessage("Visual not found for this item.");
			return;
		}
		
		// VIP gate (ajuste o método real)
		if (dress.isVip() && !player.isVip())
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
		
		if (player.getWeaponSkin() != null && player.getWeaponSkin().getItemId() == item.getItemId())
		{
			player.sendMessage("This weapon look is already active.");
			player.removeDressMeWeapon();
			return;
		}
		if (player.getArmorSkin() != null && player.getArmorSkin().getItemId() == item.getItemId())
		{
			player.sendMessage("This armor look is already active.");
			player.removeDressMeArmor();
			return;
		}
		
		player.applyDressMe(dress, Player.DressMeSource.ITEM, item.getItemId());
		player.setLastDressMeSummonTime(now);
	}
}
