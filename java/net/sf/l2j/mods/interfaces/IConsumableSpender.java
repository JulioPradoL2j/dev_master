package net.sf.l2j.mods.interfaces;

import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.mods.actor.FakePlayer;

 
public interface IConsumableSpender
{
	
	default void handleConsumable(FakePlayer fakePlayer, int consumableId)
	{
		if (fakePlayer.getInventory().getItemByItemId(consumableId) != null)
		{
			if (fakePlayer.getInventory().getItemByItemId(consumableId).getCount() <= 20)
			{
				fakePlayer.getInventory().addItem("", consumableId, 100, fakePlayer, null);
				
			}
		}
		else
		{
			fakePlayer.getInventory().addItem("", consumableId, 100, fakePlayer, null);
			ItemInstance consumable = fakePlayer.getInventory().getItemByItemId(consumableId);
			if (consumable.isEquipable())
				fakePlayer.getInventory().equipItem(consumable);
		}
	}
}