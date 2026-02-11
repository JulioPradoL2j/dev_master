package net.sf.l2j.mods;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.mods.admin.AdminFakePlayer;
import net.sf.l2j.mods.data.FakeChatData;
import net.sf.l2j.mods.data.FakeNameData;
import net.sf.l2j.mods.data.FakePlayerData;
import net.sf.l2j.mods.data.FakePrivateBuyData;
import net.sf.l2j.mods.data.FakePrivateSellData;
import net.sf.l2j.mods.engine.FakePlayerRestoreEngine;
import net.sf.l2j.mods.task.FakePlayerAiEngine;

public class FakesEngine
{
	public static final Logger LOGGER = Logger.getLogger(FakesEngine.class.getName());
	
	private static class SingletonHolder
	{
		protected static final FakesEngine _instance = new FakesEngine();
	}
	
	public static FakesEngine getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void onLoad()
	{
		FakePlayerRestoreEngine.getInstance().collectAll();
		loadData();
		registerAdmin();
		FakePlayerAiEngine.start();
	}
	
	private static void loadData()
	{
		FakeChatData.getInstance();
		FakePlayerData.getInstance();
		FakeNameData.getInstance();
		FakePrivateSellData.getInstance();
		FakePrivateBuyData.getInstance();
	}
	
	private static void registerAdmin()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFakePlayer());
	}
	
}
