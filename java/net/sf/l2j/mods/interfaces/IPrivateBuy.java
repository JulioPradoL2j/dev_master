package net.sf.l2j.mods.interfaces;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.data.FakePrivateBuyData;
import net.sf.l2j.mods.holder.PrivateBuyHolder;

public interface IPrivateBuy
{
	/*
	 * =============================== STATE ===============================
	 */
	
	class PrivateBuyState
	{
		boolean buying;
		long buyUntil;
		long nextAllowedBuy;
	}
	
	ConcurrentMap<FakePlayer, PrivateBuyState> STATES = new ConcurrentHashMap<>();
	
	/*
	 * =============================== CONFIG ===============================
	 */
	
	long MIN_BUY_TIME = 5 * 60000; // 5 min
	long MAX_BUY_TIME = 5 * 60000; // 5 min
	
	long MIN_COOLDOWN = 30 * 60000; // 30 min
	long MAX_COOLDOWN = 60 * 60000; // 60 min
	
	int MAX_ITEMS = Rnd.get(1, 2);
	
	/*
	 * =============================== MAIN ===============================
	 */
	
	default void handlePrivateBuy(FakePlayer player)
	{
		if (player.isDead())
			return;
		
		if (!player.isInsideZone(ZoneId.TOWN))
			return;
		
		if (player.isBusy() || player.getTarget() != null || player.isPrivateSelling() || player.isPrivateManufactureing())
			return;
		
		PrivateBuyState st = STATES.computeIfAbsent(player, p -> new PrivateBuyState());
		long now = System.currentTimeMillis();
		
		// Loja ativa
		if (player.isPrivateBuying())
		{
			// comprou tudo antes do tempo
			if (player.getBuyList().getItems().stream().noneMatch(ti -> ti.getCount() > 0))
			{
				stopBuying(player, st);
				return;
			}
			
			// tempo acabou
			if (now >= st.buyUntil)
			{
				stopBuying(player, st);
				return;
			}
			
			return;
		}
		
		// Cooldown
		if (now < st.nextAllowedBuy)
			return;
		
		startBuying(player, st);
	}
	
	/*
	 * =============================== START BUY ===============================
	 */
	
	private static void startBuying(FakePlayer player, PrivateBuyState st)
	{
		int packId = FakePrivateBuyData.getInstance().getRandomPackId();
		List<PrivateBuyHolder> allItems = FakePrivateBuyData.getInstance().getPrivateBuyId(packId);
		
		if (allItems.isEmpty())
			return;
			
		// ===============================
		// TÍTULO DA LOJA (id = 0)
		// ===============================
		List<String> titles = allItems.stream().filter(h -> h.getId() == 0).map(PrivateBuyHolder::getTitle).filter(t -> !t.isEmpty()).collect(Collectors.toList());
		
		String storeTitle = titles.isEmpty() ? "Private Store - Buy" : titles.get(Rnd.get(titles.size()));
		
		// ===============================
		// ITENS REAIS (id > 0)
		// ===============================
		List<PrivateBuyHolder> buyItems = allItems.stream().filter(h -> h.getId() > 0).collect(Collectors.toList());
		
		if (buyItems.isEmpty())
			return;
		
		// embaralha e limita
		Collections.shuffle(buyItems);
		buyItems = buyItems.stream().limit(MAX_ITEMS).collect(Collectors.toList());
		
		// ===============================
		// ABRE LOJA
		// ===============================
		player.startPrivateBuy(storeTitle);
		
		for (PrivateBuyHolder h : buyItems)
		{
			ItemInstance item = player.addItem("PrivateBuy", h.getId(), h.getValue(), player, false);
			
			if (item == null)
				continue;
			
			if (h.getEnchant() > 0)
				item.setEnchantLevel(h.getEnchant());
			
			int price = h.getPrinceValue();
			player.addBuyItem(item, item.getCount(), price);
		}
		
		player.broadcastPacket(new PrivateStoreMsgBuy(player), 2500);
		player.broadcastCharInfo();
		
		long buyTime = Rnd.get(MIN_BUY_TIME, MAX_BUY_TIME);
		st.buyUntil = System.currentTimeMillis() + buyTime;
		st.buying = true;
	}
	
	/*
	 * =============================== STOP BUY ===============================
	 */
	
	private static void stopBuying(FakePlayer player, PrivateBuyState st)
	{
		player.stopPrivateBuy();
		
		st.buying = false;
		st.buyUntil = 0;
		st.nextAllowedBuy = System.currentTimeMillis() + Rnd.get(MIN_COOLDOWN, MAX_COOLDOWN);
	}
	
}
