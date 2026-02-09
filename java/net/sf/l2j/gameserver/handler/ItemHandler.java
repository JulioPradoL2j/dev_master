package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSoulShot;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpice;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpiritShot;
import net.sf.l2j.gameserver.handler.itemhandlers.BlessedSpiritShot;
import net.sf.l2j.gameserver.handler.itemhandlers.Book;
import net.sf.l2j.gameserver.handler.itemhandlers.Calculator;
import net.sf.l2j.gameserver.handler.itemhandlers.Elixir;
import net.sf.l2j.gameserver.handler.itemhandlers.EnchantScrolls;
import net.sf.l2j.gameserver.handler.itemhandlers.FishShots;
import net.sf.l2j.gameserver.handler.itemhandlers.Harvester;
import net.sf.l2j.gameserver.handler.itemhandlers.ItemSkills;
import net.sf.l2j.gameserver.handler.itemhandlers.Keys;
import net.sf.l2j.gameserver.handler.itemhandlers.Maps;
import net.sf.l2j.gameserver.handler.itemhandlers.MercTicket;
import net.sf.l2j.gameserver.handler.itemhandlers.PaganKeys;
import net.sf.l2j.gameserver.handler.itemhandlers.PetFood;
import net.sf.l2j.gameserver.handler.itemhandlers.Recipes;
import net.sf.l2j.gameserver.handler.itemhandlers.RollingDice;
import net.sf.l2j.gameserver.handler.itemhandlers.ScrollOfResurrection;
import net.sf.l2j.gameserver.handler.itemhandlers.Seed;
import net.sf.l2j.gameserver.handler.itemhandlers.SevenSignsRecord;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulCrystals;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulShots;
import net.sf.l2j.gameserver.handler.itemhandlers.SpecialXMas;
import net.sf.l2j.gameserver.handler.itemhandlers.SpiritShot;
import net.sf.l2j.gameserver.handler.itemhandlers.SummonItems;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.AioItemClick;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.AllyNameChange;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.AugmentSkills;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.BoxRewards;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.ClanFull;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.ClassItem;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.ClickMenu;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.DeletePk;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.GradeDReward;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.HeroItemClick;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.LevelCoin;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.LuckBox;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.NameChange;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.NoblesItem;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.OfflineClick;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.RoletaItem;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.SexCoin;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.TeleportBook;
import net.sf.l2j.gameserver.handler.itemhandlers.custom.VipItemClick;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _datatable = new HashMap<>();
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected ItemHandler()
	{
		//Items Handlers Customs
		if(Config.ENABLE_AIO_SYSTEM)
		{
			registerItemHandler(new AioItemClick());	
		}
		if(Config.ENABLE_VIP_SYSTEM)
		{
			registerItemHandler(new VipItemClick());	
		}
		
		registerItemHandler(new BoxRewards());
		registerItemHandler(new HeroItemClick());
		registerItemHandler(new AugmentSkills());
		registerItemHandler(new TeleportBook());
		registerItemHandler(new RoletaItem());
		registerItemHandler(new AllyNameChange());
		registerItemHandler(new ClanFull());
		registerItemHandler(new ClassItem());
		registerItemHandler(new ClickMenu());
		registerItemHandler(new DeletePk());
		registerItemHandler(new GradeDReward());
		registerItemHandler(new LevelCoin());
		registerItemHandler(new NameChange());
		registerItemHandler(new NoblesItem());
		registerItemHandler(new SexCoin());
		registerItemHandler(new OfflineClick());
		registerItemHandler(new LuckBox());
		//Fim items Handlers customs
		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new Book());
		registerItemHandler(new Calculator());
		registerItemHandler(new Elixir());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new FishShots());
		registerItemHandler(new Harvester());
		registerItemHandler(new ItemSkills());
		registerItemHandler(new Keys());
		registerItemHandler(new Maps());
		registerItemHandler(new MercTicket());
		registerItemHandler(new PaganKeys());
		registerItemHandler(new PetFood());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new Seed());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SoulCrystals());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new SummonItems());
	}
	
	public void registerItemHandler(IItemHandler handler)
	{
		_datatable.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getItemHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _datatable.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler _instance = new ItemHandler();
	}
}