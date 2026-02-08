package net.sf.l2j.mods.interfaces;

import java.util.List;
import java.util.Set;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.mods.actor.FakePlayer;

 
public interface IPickup
{
	int PICKUP_RADIUS = 700;
	long PICKUP_LOCK_TIME = 5000; // 5s
	
	Set<Integer> FOREIGN_ALLOWED_ITEM_IDS = Set.of(
		// Items List
		57, // Adena
		3470, // Gold Bar
		6392, // Glittering Medal
		6393, // Glittering Medal
		5575, // Festival Adena
		5574 // Ancient Adena
	);
	
	default boolean handlePickUp(FakePlayer fake)
	{
		if (fake == null || fake.isDead())
			return false;
		
		if (fake.isPrivateBuying() || fake.isPrivateSelling() || fake.isPrivateManufactureing())
			return false;
		
		if (fake.isPickingUp())
		{
			return handlePickupTarget(fake);
		}
		
		List<L2Object> visibles = L2World.getVisibleObjects(fake, PICKUP_RADIUS);
		if (visibles.isEmpty())
			return false;
		
		ItemInstance nearest = null;
		double nearestDist = Double.MAX_VALUE;
		
		for (L2Object obj : visibles)
		{
			if (!(obj instanceof ItemInstance))
				continue;
			
			ItemInstance item = (ItemInstance) obj;
			
			if (!canPickup(fake, item))
				continue;
			
			double dist = fake.getDistanceSq(item);
			if (dist < nearestDist)
			{
				nearestDist = dist;
				nearest = item;
			}
		}
		
		if (nearest == null)
			return false;
		
		if (nearest.getOwnerId() != 0 && nearest.getOwnerId() != fake.getObjectId())
		{
			if (!isForeignPickupAllowed(nearest))
				return false;
			
		}
		
		if (!nearest.tryReservePickup(fake.getObjectId()))
			return false;
		
		fake.startPickup(nearest, PICKUP_LOCK_TIME);
		return handlePickupTarget(fake);
		
	}
	
	/*
	 * =============================== CONTINUAÇÃO DO PICKUP ===============================
	 */
	private static boolean handlePickupTarget(FakePlayer fake)
	{
		ItemInstance item = fake.getPickupTarget();
		if (item == null)
			return false;
		
		if (!item.isVisible())
		{
			fake.clearPickup();
			return false;
		}
		
		// anda até o item
		if (!fake.isInsideRadius(item, 40, true, false))
		{
			fake.getAI().setIntention(CtrlIntention.MOVE_TO, new Location(item.getX() + Rnd.get(-5, 7), item.getY() + Rnd.get(-2, 4), item.getZ()));
			return true;
		}
		
		// pega
		fake.doPickupItem(item);
		
		return true;
	}
	
	/*
	 * =============================== VALIDAÇÃO ===============================
	 */
	private static boolean canPickup(FakePlayer fake, ItemInstance item)
	{
		if (item == null || !item.isVisible())
			return false;
		
		if (!fake.isInsideRadius(item, PICKUP_RADIUS, true, false))
			return false;
		
		if (!fake.getInventory().validateCapacity(item))
			return false;
		
		if (!fake.getInventory().validateWeight(item.getCount() * item.getItem().getWeight()))
			return false;
		
		if (fake.isInsideZone(ZoneId.PEACE) && fake.isAttackingNow())
			return false;
		
		return true;
	}
	
	private static boolean isForeignPickupAllowed(ItemInstance item)
	{
		if (item == null)
			return false;
		
		if (FOREIGN_ALLOWED_ITEM_IDS.contains(item.getItemId()))
			return true;
		
		switch (item.getItem().getCrystalType())
		{
			case D:
			case B:
			case C:
			case A:
			case S:
				return true;
			default:
				return false;
		}
	}
	
}
