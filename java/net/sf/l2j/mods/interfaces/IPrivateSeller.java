package net.sf.l2j.mods.interfaces;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.data.FakePrivateSellData;
import net.sf.l2j.mods.holder.PrivateSellHolder;
 
public interface IPrivateSeller
{
	/*
	 * =============================== STATE ===============================
	 */
	
	class PrivateSellState
	{
		boolean selling;
		long sellUntil;
		long nextAllowedSell;
	}
	
	ConcurrentMap<FakePlayer, PrivateSellState> STATES = new ConcurrentHashMap<>();
	
	/*
	 * =============================== CONFIG ===============================
	 */
	
	long MIN_SELL_TIME = 5 * 60000; // 5 min
	long MAX_SELL_TIME = 5 * 60000; // 5 min
	
	long MIN_COOLDOWN = 30 * 60000; // 30 min
	long MAX_COOLDOWN = 60 * 60000; // 60 min
	
	int MAX_ITEMS = Rnd.get(1, 2);
	
	/*
	 * =============================== MAIN ===============================
	 */
	
	default void handlePrivateSell(FakePlayer player)
	{
		if (player.isDead())
			return;
		
		if (!player.isInsideZone(ZoneId.TOWN))
			return;
		
		if (player.isBusy() || player.getTarget() != null || player.isPrivateBuying() || player.isPrivateManufactureing())
			return;
		
		PrivateSellState st = STATES.computeIfAbsent(player, p -> new PrivateSellState());
		long now = System.currentTimeMillis();
		
		// ===============================
		// LOJA ATIVA
		// ===============================
		if (player.isPrivateSelling())
		{
			if (player.getSellList().getItems().stream().noneMatch(i -> i.getCount() > 0))
			{
				stopSelling(player, st);
				return;
			}
			
			if (now >= st.sellUntil)
			{
				stopSelling(player, st);
			}
			return;
		}
		
		// ===============================
		// COOLDOWN
		// ===============================
		if (now < st.nextAllowedSell)
			return;
			
		// ===============================
		// INICIA VENDA (modo já escolhido)
		// ===============================
		startSelling(player, st);
	}
	
	/*
	 * =============================== START SELL ===============================
	 */
	
	private static void startSelling(FakePlayer player, PrivateSellState st)
	{
		int packId = FakePrivateSellData.getInstance().getRandomPackId();
		List<PrivateSellHolder> allItems = FakePrivateSellData.getInstance().getPrivateSellId(packId);
		
		if (allItems.isEmpty())
			return;
			
		// ===============================
		// TÍTULO DA LOJA (id = 0)
		// ===============================
		List<String> titles = allItems.stream().filter(h -> h.getId() == 0).map(PrivateSellHolder::getTitle).filter(t -> !t.isEmpty()).collect(Collectors.toList());
		
		String storeTitle = titles.isEmpty() ? "Private Store - Sell" : titles.get(Rnd.get(titles.size()));
		
		// ===============================
		// ITENS REAIS (id > 0)
		// ===============================
		List<PrivateSellHolder> sellItems = allItems.stream().filter(h -> h.getId() > 0).collect(Collectors.toList());
		
		if (sellItems.isEmpty())
			return;
		
		// embaralha e limita
		Collections.shuffle(sellItems);
		sellItems = sellItems.stream().limit(MAX_ITEMS).collect(Collectors.toList());
		
		// ===============================
		// ABRE LOJA
		// ===============================
		player.startPrivateSell(storeTitle);
		
		for (PrivateSellHolder h : sellItems)
		{
			ItemInstance item = player.addItem("PrivateSell", h.getId(), h.getValue(), player, false);
			
			if (item == null)
				continue;
			
			if (h.getEnchant() > 0)
				item.setEnchantLevel(h.getEnchant());
			
			int price = h.getPrinceValue();
			player.addSellItem(item, item.getCount(), price);
		}
		
		player.broadcastPacket(new PrivateStoreMsgSell(player));
		player.broadcastCharInfo();
		
		long sellTime = Rnd.get(MIN_SELL_TIME, MAX_SELL_TIME);
		st.sellUntil = System.currentTimeMillis() + sellTime;
		st.selling = true;
	}
	
	/*
	 * =============================== STOP SELL ===============================
	 */
	
	private static void stopSelling(FakePlayer player, PrivateSellState st)
	{
		player.stopPrivateSell();
		
		st.selling = false;
		st.sellUntil = 0;
		st.nextAllowedSell = System.currentTimeMillis() + Rnd.get(MIN_COOLDOWN, MAX_COOLDOWN);
		
	}
	
}
