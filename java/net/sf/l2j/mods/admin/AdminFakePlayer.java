package net.sf.l2j.mods.admin;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.factory.FakePlayerFactory;
import net.sf.l2j.mods.manager.FakePlayerManager;

 

public class AdminFakePlayer implements IAdminCommandHandler
{
	
	private static final int PAGE_SIZE = 8;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_fake",
		"admin_fake_list",
		"admin_fake_create",
		"admin_fake_create_race",
		"admin_fake_spawn_here",
		"admin_fake_random_low",
		"admin_fake_random_high",
		"admin_fake_level",
		"admin_fake_tp",
		"admin_fake_go",
		"admin_fake_despawn",
		"admin_fake_delete"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player admin)
	{
		String[] args = command.split(" ");
		
		switch (args[0])
		{
			case "admin_fake":
				showIndex(admin);
				break;
			
			case "admin_fake_create":
				showCreateMenu(admin);
				break;
			
			case "admin_fake_create_race":
			{
				if (args.length < 2)
				{
					admin.sendMessage("Usage: //fake_create_race <HUMAN|ELF|DARK_ELF|ORC|DWARF>");
					return true;
				}
				
				showRaceClasses(admin, args[1]);
				break;
			}
			
			case "admin_fake_list":
			{
				String filter = args.length > 1 ? args[1] : null;
				int page = args.length > 2 ? Integer.parseInt(args[2]) : 1;
				showPanel(admin, filter, page);
				break;
			}
			
			case "admin_fake_spawn_here":
				handleSpawnHere(admin, args);
				break;
			
			case "admin_fake_tp":
				handleTeleport(admin, args);
				break;
			case "admin_fake_go":
				handleTeleportGO(admin, args);
				break;
			case "admin_fake_level":
				handleLevel(admin, args);
				break;
			
			case "admin_fake_despawn":
				handleDespawn(admin, args);
				break;
			case "admin_fake_delete":
				handleDelete(admin, args);
				break;
			case "admin_fake_random_low":
				handleSpawnRandomLow(admin);
				break;
			
			case "admin_fake_random_high":
				handleSpawnRandomHigh(admin);
				break;
			
		}
		
		return true;
	}
	
	private static void handleSpawnRandomHigh(Player admin)
	{
		List<ClassId> pool = new ArrayList<>();
		
		// ===============================
		// Filtra SOMENTE 3ª job com AI
		// ===============================
		for (ClassId cid : FakePlayer.getAllAIs().keySet())
		{
			if (cid.level() == 3)
				pool.add(cid);
		}
		
		if (pool.isEmpty())
		{
			admin.sendMessage("No 3rd class FakePlayer AIs registered.");
			return;
		}
		
		// ===============================
		// Escolhe aleatória
		// ===============================
		ClassId classId = pool.get(Rnd.get(pool.size()));
		
		FakePlayer fake = FakePlayerFactory.create(classId.getId(), admin.getX(), admin.getY(), admin.getZ());
		
		admin.sendMessage("High Class FakePlayer spawned: " + fake.getName() + " (" + classId.name().replace("_", " ") + ")");
		
		fake.onActionShift(admin);
	}
	
	private static void handleSpawnRandomLow(Player admin)
	{
		List<ClassId> pool = new ArrayList<>();
		
		for (ClassId cid : FakePlayer.getAllAIs().keySet())
		{
			if (cid.level() <= 1) // fighter/mystic base
				pool.add(cid);
		}
		
		if (pool.isEmpty())
		{
			admin.sendMessage("No low class FakePlayer AIs registered.");
			return;
		}
		
		ClassId classId = pool.get(Rnd.get(pool.size()));
		
		FakePlayer fake = FakePlayerFactory.create(classId.getId(), admin.getX(), admin.getY(), admin.getZ());
		
		admin.sendMessage("Low Class FakePlayer spawned: " + fake.getName() + " (" + classId.name().replace("_", " ") + ")");
		
		fake.onActionShift(admin);
	}
	
	private static void handleSpawnHere(Player admin, String[] args)
	{
		if (args.length < 2)
		{
			admin.sendMessage("Usage: //fake_spawn_here <classId | className>");
			return;
		}
		
		ClassId classId = null;
		
		// ===============================
		// classId numérico (ordinal)
		// ===============================
		if (isNumeric(args[1]))
		{
			int id = Integer.parseInt(args[1]);
			
			if (id < 0 || id >= ClassId.VALUES.length)
			{
				admin.sendMessage("Invalid classId: " + id);
				return;
			}
			
			classId = ClassId.VALUES[id];
		}
		else
		{
			// ===============================
			// classId por nome (ADVENTURER, DUELIST, etc)
			// ===============================
			try
			{
				classId = ClassId.valueOf(args[1].toUpperCase());
			}
			catch (Exception e)
			{
				admin.sendMessage("Invalid class name: " + args[1]);
				return;
			}
		}
		
		// ===============================
		// Segurança extra
		// ===============================
		if (classId == null)
		{
			admin.sendMessage("Invalid classId.");
			return;
		}
		
		// ===============================
		// Verifica se existe AI registrada
		// ===============================
		if (!FakePlayer.getAllAIs().containsKey(classId))
		{
			admin.sendMessage("No FakePlayer AI registered for: " + classId);
			return;
		}
		
		// ===============================
		// Spawn na posição do admin
		// ===============================
		FakePlayer fake = FakePlayerFactory.create(classId.getId(), admin.getX(), admin.getY(), admin.getZ());
		
		admin.sendMessage("FakePlayer spawned: " + fake.getName() + " (" + formatClassName(classId) + ")");
		
		fake.onActionShift(admin);
	}
	
	private static boolean isNumeric(String str)
	{
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}
	
	private static void handleDespawn(Player admin, String[] args)
	{
		if (args.length < 2)
			return;
		
		int objectId = Integer.parseInt(args[1]);
		L2Object obj = L2World.getInstance().getObject(objectId);
		if (obj != null)
		{
			if (!(obj instanceof FakePlayer))
				return;
			
			FakePlayer fake = (FakePlayer) obj;
			
			fake.abortAttack();
			fake.abortCast();
			
			fake.deleteMe();
			AdminFakePlayer.getInstance().useAdminCommand("admin_fake_list", admin);
			admin.sendMessage("FakePlayer " + fake.getName() + " despawned.");
		}
	}
	
	private static void handleDelete(Player admin, String[] args)
	{
		if (args.length < 2)
			return;
		
		int objectId = Integer.parseInt(args[1]);
		L2Object obj = L2World.getInstance().getObject(objectId);
		if (obj != null)
		{
			if (!(obj instanceof FakePlayer))
				return;
			
			FakePlayer fake = (FakePlayer) obj;
			
			fake.abortAttack();
			fake.abortCast();
			
			fake.deleteMe();
			fake.deleteCharByObjId(fake.getObjectId());
			AdminFakePlayer.getInstance().useAdminCommand("admin_fake_list", admin);
			admin.sendMessage("FakePlayer " + fake.getName() + " despawned.");
		}
	}
	
	private static void handleLevel(Player admin, String[] args)
	{
		if (args.length < 2)
			return;
		
		int objectId = Integer.parseInt(args[1]);
		L2Object obj = L2World.getInstance().getObject(objectId);
		if (obj != null)
		{
			if (!(obj instanceof FakePlayer))
				return;
			
			FakePlayer fake = (FakePlayer) obj;
			
			int newLevel = admin.getStat().getLevel();
			
			fake.getStat().setLevel((byte) newLevel);
			fake.setCurrentHp(fake.getMaxHp());
			fake.setCurrentMp(fake.getMaxMp());
			fake.setCurrentCp(fake.getMaxCp());
			
			fake.broadcastUserInfo();
			
			admin.sendMessage("FakePlayer " + fake.getName() + " level set to " + newLevel + ".");
			fake.onActionShift(admin);
		}
	}
	
	private static void handleTeleport(Player admin, String[] args)
	{
		int objectId = Integer.parseInt(args[1]);
		L2Object obj = L2World.getInstance().getObject(objectId);
		if (obj != null)
		{
			if (obj instanceof FakePlayer)
			{
				FakePlayer fake = (FakePlayer) obj;
				if (fake.getParty() != null)
				{
					for (Player member : fake.getParty().getPartyMembers())
					{
						if (member instanceof FakePlayer)
						{
							FakePlayer fp = (FakePlayer) member;
							
							if (!fp.isDead())
							{
								if (fp.isPrivateBuying())
									fp.stopPrivateBuy();
								
								if (fp.isPrivateSelling())
									fp.stopPrivateSell();
								
								fp.teleToLocation(admin.getX(), admin.getY(), admin.getZ(), 75);
							}
							
						}
					}
				}
				else
				{
					fake.teleToLocation(admin.getX(), admin.getY(), admin.getZ(), 75);
				}
				
				fake.onActionShift(admin);
				
			}
		}
	}
	
	private static void handleTeleportGO(Player admin, String[] args)
	{
		int objectId = Integer.parseInt(args[1]);
		L2Object obj = L2World.getInstance().getObject(objectId);
		if (obj != null)
		{
			if (obj instanceof FakePlayer)
			{
				FakePlayer fake = (FakePlayer) obj;
				
				if (fake.isPrivateBuying())
					fake.stopPrivateBuy();
				
				if (fake.isPrivateSelling())
					fake.stopPrivateSell();
				
				admin.teleToLocation(fake.getX(), fake.getY(), fake.getZ(), 75);
				fake.onActionShift(admin);
			}
		}
	}
	
	private static void showPanel(Player admin, String filter, int page)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder(4096);
		
		sb.append("<html><body>");
		sb.append("<center><font color=\"LEVEL\">FakePlayer Manager</font></center><br>");
		
		// ================= SEARCH =================
		sb.append("<table width=200>");
		sb.append("<tr>");
		
		sb.append("<td align=right width=140>");
		sb.append("<font color=\"LEVEL\">Search:</font>");
		sb.append("</td>");
		
		sb.append("<td align=center width=80>");
		sb.append("<edit var=\"filter\" width=120>");
		sb.append("</td>");
		
		sb.append("<td align=left width=80>");
		sb.append("<button value=\"Search\" action=\"bypass -h admin_fake_list $filter 1\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td>");
		
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br>");
		
		// ================= LIST =================
		List<FakePlayer> list = FakePlayerManager.getInstance().getFakePlayers();
		
		if (filter != null && filter.length() >= 3)
		{
			List<FakePlayer> filtered = new ArrayList<>();
			
			for (FakePlayer fp : list)
			{
				if (fp.getName().toLowerCase().contains(filter.toLowerCase()))
					filtered.add(fp);
			}
			
			list = filtered;
		}
		
		int total = list.size();
		int maxPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
		page = Math.min(page, maxPage);
		
		int from = (page - 1) * PAGE_SIZE;
		int to = Math.min(from + PAGE_SIZE, total);
		
		sb.append("<table width=300>");
		
		for (int i = from; i < to; i++)
		{
			FakePlayer fake = list.get(i);
			
			sb.append("<tr>");
			sb.append("<td width=130>").append(fake.getName()).append("</td>");
			sb.append("<td width=60>").append(fake.getCurrentAction()).append("</td>");
			
			sb.append("<td>");
			sb.append("<button value=\"TP\" action=\"bypass -h admin_fake_tp ").append(fake.getObjectId()).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append("<button value=\"GO\" action=\"bypass -h admin_fake_go ").append(fake.getObjectId()).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append("<button value=\"DEL\" action=\"bypass -h admin_fake_despawn ").append(fake.getObjectId()).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			sb.append("</td>");
			sb.append("</tr>");
		}
		
		sb.append("</table>");
		
		// ================= PAGES =================
		sb.append("<br>");
		sb.append("<table width=300>");
		sb.append("<tr>");
		
		sb.append("<td align=center width=40>");
		sb.append("<button value=\"<<\" action=\"bypass -h admin_fake_list ").append(filter == null ? "" : filter).append(" 1\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td>");
		
		sb.append("<td align=center width=40>");
		if (page > 1)
		{
			sb.append("<button value=\"<\" action=\"bypass -h admin_fake_list ").append(filter == null ? "" : filter).append(" ").append(page - 1).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		}
		sb.append("</td>");
		
		sb.append("<td align=center width=140>");
		sb.append("<font color=\"LEVEL\">Page ").append(page).append(" / ").append(maxPage).append("</font>");
		sb.append("</td>");
		
		sb.append("<td align=center width=40>");
		if (page < maxPage)
		{
			sb.append("<button value=\">\" action=\"bypass -h admin_fake_list ").append(filter == null ? "" : filter).append(" ").append(page + 1).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		}
		sb.append("</td>");
		
		sb.append("<td align=center width=40>");
		sb.append("<button value=\">>\" action=\"bypass -h admin_fake_list ").append(filter == null ? "" : filter).append(" ").append(maxPage).append("\" width=30 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td>");
		
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<center>");
		sb.append("<table width=260>");
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Menu FakePlayer\" action=\"bypass -h admin_fake\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		admin.sendPacket(html);
	}
	
	private static void showIndex(Player admin)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder(2048);
		
		sb.append("<html><body>");
		sb.append("<center><font color=\"LEVEL\">FakePlayer Manager</font></center><br>");
		sb.append("<center>");
		
		sb.append("<table width=300>");
		
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Create FakePlayer\" action=\"bypass -h admin_fake_create\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"List FakePlayers\" action=\"bypass -h admin_fake_list\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Random low\" action=\"bypass -h admin_fake_random_low\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Random higt\" action=\"bypass -h admin_fake_random_high\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		
		sb.append("</table>");
		sb.append("</center>");
		sb.append("<br><center>Total Fakes: <font color=\"LEVEL\">").append(FakePlayerManager.getInstance().getFakePlayersCount()).append("</font></center>");
		
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		admin.sendPacket(html);
	}
	
	private static void showCreateMenu(Player admin)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder(2048);
		
		sb.append("<html><body>");
		sb.append("<center><font color=\"LEVEL\">Create FakePlayer</font></center><br>");
		sb.append("<center>");
		
		sb.append("<table width=300>");
		
		addRaceButton(sb, "Human", "HUMAN");
		addRaceButton(sb, "Elf", "ELF");
		addRaceButton(sb, "Dark Elf", "DARK_ELF");
		addRaceButton(sb, "Orc", "ORC");
		addRaceButton(sb, "Dwarf", "DWARF");
		
		sb.append("</table>");
		sb.append("<br><table width=300>");
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Menu FakePlayer\" action=\"bypass -h admin_fake\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		sb.append("</table>");
		
		sb.append("</center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		admin.sendPacket(html);
	}
	
	private static void addRaceButton(StringBuilder sb, String name, String race)
	{
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"").append(name).append("\" action=\"bypass -h admin_fake_create_race ").append(race).append("\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
	}
	
	private static void showRaceClasses(Player admin, String race)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder(4096);
		
		sb.append("<html><body>");
		sb.append("<center><font color=\"LEVEL\">").append(race).append("</font></center><br>");
		sb.append("<center>");
		sb.append("<table width=300>");
		
		for (ClassId cid : FakePlayer.getAllAIs().keySet())
		{
			if (!isClassOfRace(cid, race))
				continue;
			
			boolean mystic = isMystic(cid);
			String color = mystic ? "6AAEFF" : "FF6A6A";
			String className = cid.name().replace("_", " ");
			
			sb.append("<tr>");
			sb.append("<td>");
			sb.append("<font color=\"").append(color).append("\">");
			sb.append("<a action=\"bypass -h admin_fake_spawn_here ").append(cid.getId()).append("\">");
			sb.append(className);
			sb.append("</a>");
			sb.append("</font>");
			sb.append("</td>");
			sb.append("</tr>");
		}
		
		sb.append("</table>");
		
		sb.append("<br><table width=300>");
		sb.append("<tr><td align=center>");
		sb.append("<button value=\"Race FakePlayer\" action=\"bypass -h admin_fake_create\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		admin.sendPacket(html);
	}
	
	private static boolean isClassOfRace(ClassId cid, String race)
	{
		switch (race)
		{
			case "HUMAN":
				return cid == ClassId.HUMAN_FIGHTER || cid == ClassId.HUMAN_MYSTIC || cid == ClassId.DUELIST || cid == ClassId.PHOENIX_KNIGHT || cid == ClassId.SAGGITARIUS || cid == ClassId.ADVENTURER || cid == ClassId.ARCHMAGE || cid == ClassId.SOULTAKER || cid == ClassId.CARDINAL || cid == ClassId.TREASURE_HUNTER || cid == ClassId.HAWKEYE;
			
			case "ELF":
				return cid == ClassId.ELVEN_FIGHTER || cid == ClassId.ELVEN_MYSTIC || cid == ClassId.WIND_RIDER || cid == ClassId.MOONLIGHT_SENTINEL || cid == ClassId.MYSTIC_MUSE || cid == ClassId.ELEMENTAL_MASTER;
			
			case "DARK_ELF":
				return cid == ClassId.DARK_FIGHTER || cid == ClassId.DARK_MYSTIC || cid == ClassId.GHOST_HUNTER || cid == ClassId.GHOST_SENTINEL || cid == ClassId.STORM_SCREAMER;
			
			case "ORC":
				return cid == ClassId.ORC_FIGHTER || cid == ClassId.ORC_MYSTIC || cid == ClassId.TITAN || cid == ClassId.GRAND_KHAVATARI || cid == ClassId.DOMINATOR || cid == ClassId.DOOMCRYER;
			
			case "DWARF":
				return cid == ClassId.DWARVEN_FIGHTER || cid == ClassId.FORTUNE_SEEKER || cid == ClassId.MAESTRO;
		}
		return false;
	}
	
	private static String formatClassName(ClassId classId)
	{
		String name = classId.name().toLowerCase().replace("_", " ");
		
		StringBuilder sb = new StringBuilder(name.length());
		boolean upper = true;
		
		for (char c : name.toCharArray())
		{
			if (upper)
			{
				sb.append(Character.toUpperCase(c));
				upper = false;
			}
			else
			{
				sb.append(c);
			}
			
			if (c == ' ')
				upper = true;
		}
		
		return sb.toString();
	}
	
	private static boolean isMystic(ClassId cid)
	{
		switch (cid)
		{
			// HUMAN
			case HUMAN_MYSTIC:
			case ARCHMAGE:
			case SOULTAKER:
			case CARDINAL:
				
				// ELF
			case ELVEN_MYSTIC:
			case MYSTIC_MUSE:
			case ELEMENTAL_SUMMONER:
				// DARK ELF
			case DARK_MYSTIC:
			case STORM_SCREAMER:
				
				// ORC
			case ORC_MYSTIC:
			case DOMINATOR:
			case DOOMCRYER:
				
				return true;
			default:
				return false;
		}
	}
	public static AdminFakePlayer getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final AdminFakePlayer INSTANCE = new AdminFakePlayer();
	}
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}