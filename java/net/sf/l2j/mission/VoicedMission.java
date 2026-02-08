package net.sf.l2j.mission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

public class VoicedMission implements IVoicedCommandHandler 
{
	public static final VoicedMission getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static final String[] VOICED_COMMANDS = new String[] { "select_m" };
	public static final Logger _log = Logger.getLogger(VoicedMission.class.getName());
	
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("select_m"))
		{
			if (!Config.ACTIVE_MISSION) 
			{
				activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
				return false;
			} 
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try 
			{
				String type = st.nextToken();
				if (type.startsWith("tvt"))
				{
					if (!Config.ACTIVE_MISSION_TVT)
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION) 
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_tvt_hwid(activeChar.getHWID())) 
					{
						info_tvt(activeChar);
					} 
					else
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/tvt.htm");
						msg = msg.replaceAll("%tvt_cont%", "" + Config.MISSION_TVT_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getTvTCont() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.isTvTCompleted())
						{
							msg = msg.replaceAll("%tvt%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						}
						else if (activeChar.getTvTCont() >= Config.MISSION_TVT_CONT)
						{
							msg = msg.replaceAll("%tvt%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link tvt_mission\" value=\"Reward!\">");
						} 
						else
						{
							msg = msg.replaceAll("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				}
				 
				else if (type.startsWith("raid")) 
				{
					if (!Config.ACTIVE_MISSION_RAID)
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION) 
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_raid_hwid(activeChar.getHWID())) 
					{
						info_raid(activeChar);
					} 
					else
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/raid.htm");
						msg = msg.replaceAll("%raid_cont%", "" + Config.MISSION_RAID_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getRaidCont() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.isRaidCompleted() || activeChar.check_raid_hwid(activeChar.getHWID())) 
						{
							msg = msg.replaceAll("%raid%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getRaidCont() >= Config.MISSION_RAID_CONT)
						{
							msg = msg.replaceAll("%raid%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link raid_mission\" value=\"Reward!\">");
						} 
						else
						{
							msg = msg.replaceAll("%raid%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				} 
				else if (type.startsWith("party_mob"))
				{
					if (!Config.ACTIVE_MISSION_PARTY_MOB)
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION) 
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_party_mob_hwid(activeChar.getHWID()))
					{
						info_party_mob(activeChar);
					} 
					else
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/party_mob.htm");
						msg = msg.replaceAll("%party_mob_cont%", "" + Config.MISSION_PARTY_MOB_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getPartyMonsterKills() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.isPartyMobCompleted())
						{
							msg = msg.replaceAll("%party_mob%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getPartyMonsterKills() >= Config.MISSION_PARTY_MOB_CONT)
						{
							msg = msg.replaceAll("%party_mob%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link party_mob_mission\" value=\"Reward!\">");
						} 
						else
						{
							msg = msg.replaceAll("%party_mob%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				}
				else if (type.startsWith("mob"))
				{
					if (!Config.ACTIVE_MISSION_MOB)
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION)
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_mob_hwid(activeChar.getHWID()))
					{
						info_mob(activeChar);
					}
					else 
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/mob.htm");
						msg = msg.replaceAll("%mob_cont%", "" + Config.MISSION_MOB_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getMonsterKills() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.isMobCompleted()) 
						{
							msg = msg.replaceAll("%mob%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getMonsterKills() >= Config.MISSION_MOB_CONT) 
						{
							msg = msg.replaceAll("%mob%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link mob_mission\" value=\"Reward!\">");
						} 
						else 
						{
							msg = msg.replaceAll("%mob%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				} 
				 else if (type.startsWith("1x1")) 
				 {
						if (!Config.ACTIVE_MISSION_1X1) 
						{
							activeChar.sendMessage("[WARN]: Temporariamente desativado..");
							return false;
						} 
						if (!Config.ACTIVE_MISSION)
						{
							activeChar.sendMessage("[WARN]: Disponivel em breve..");
							return false;
						} 
						if (activeChar.check_1x1_hwid(activeChar.getHWID()))
						{
							info_1x1(activeChar);
						} 
						else
						{
							String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/1x1.htm");
							msg = msg.replaceAll("%1x1_cont%", "" + Config.MISSION_1X1_CONT + "");
							msg = msg.replaceAll("%cont%", "" + activeChar.getTournament1x1Cont() + "");
							msg = msg.replaceAll("%name%", "");
							if (activeChar.is1x1Completed())
							{
								msg = msg.replaceAll("%1x1%", "<font color=\"FF0000\">Received</font>");
								msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
							}
							else if (activeChar.getTournament1x1Cont() >= Config.MISSION_1X1_CONT)
							{
								msg = msg.replaceAll("%1x1%", "<font color=\"5EA82E\">Completed</font>");
								msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 1x1_mission\" value=\"Reward!\">");
							}
							else 
							{
								msg = msg.replaceAll("%1x1%", "<font color=\"FF0000\">Not Completed</font>");
								msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
							} 
							activeChar.sendPacket(new TutorialShowHtml(msg));
						} 
					}
				else if (type.startsWith("3x3"))
				{
					if (!Config.ACTIVE_MISSION_3X3) 
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION)
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_2x2_hwid(activeChar.getHWID()))
					{
						info_2x2(activeChar);
					} 
					else
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/3x3.htm");
						msg = msg.replaceAll("%3x3_cont%", "" + Config.MISSION_3X3_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getTournament2x2Cont() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.is2x2Completed())
						{
							msg = msg.replaceAll("%3x3%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getTournament2x2Cont() >= Config.MISSION_3X3_CONT) 
						{
							msg = msg.replaceAll("%3x3%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 3x3_mission\" value=\"Reward!\">");
						} 
						else 
						{
							msg = msg.replaceAll("%3x3%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				} 
				else if (type.startsWith("5x5")) 
				{
					if (!Config.ACTIVE_MISSION_5X5) 
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION)
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_5x5_hwid(activeChar.getHWID()))
					{
						info_5x5(activeChar);
					} 
					else 
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/5x5.htm");
						msg = msg.replaceAll("%5x5_cont%", "" + Config.MISSION_5X5_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getTournament5x5Cont() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.is5x5Completed()) 
						{
							msg = msg.replaceAll("%5x5%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getTournament5x5Cont() >= Config.MISSION_5X5_CONT) 
						{
							msg = msg.replaceAll("%5x5%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 5x5_mission\" value=\"Reward!\">");
						} 
						else 
						{
							msg = msg.replaceAll("%5x5%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				} 
				else if (type.startsWith("9x9"))
				{
					if (!Config.ACTIVE_MISSION_9X9)
					{
						activeChar.sendMessage("[WARN]: Temporariamente desativado..");
						return false;
					} 
					if (!Config.ACTIVE_MISSION)
					{
						activeChar.sendMessage("[WARN]: Disponivel em breve..");
						return false;
					} 
					if (activeChar.check_9x9_hwid(activeChar.getHWID())) 
					{
						info_9x9(activeChar);
					} 
					else
					{
						String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/9x9.htm");
						msg = msg.replaceAll("%9x9_cont%", "" + Config.MISSION_9X9_CONT + "");
						msg = msg.replaceAll("%cont%", "" + activeChar.getTournament9x9Cont() + "");
						msg = msg.replaceAll("%name%", "");
						if (activeChar.is9x9Completed())
						{
							msg = msg.replaceAll("%9x9%", "<font color=\"FF0000\">Received</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						else if (activeChar.getTournament9x9Cont() >= Config.MISSION_9X9_CONT)
						{
							msg = msg.replaceAll("%9x9%", "<font color=\"5EA82E\">Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 9x9_mission\" value=\"Reward!\">");
						} 
						else
						{
							msg = msg.replaceAll("%9x9%", "<font color=\"FF0000\">Not Completed</font>");
							msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
						} 
						activeChar.sendPacket(new TutorialShowHtml(msg));
					} 
				} 
			} catch (Exception e)
			{
				activeChar.sendMessage("WARN: Comando nao existe..");
			} 
		} 
		return true;
	}
	
	public static void info_tvt(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE tvt_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/tvt.htm");
	                msg = msg.replaceAll("%tvt_cont%", String.valueOf(Config.MISSION_TVT_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTvTCont()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.isTvTCompleted()) {
	                    msg = msg.replaceAll("%tvt%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getTvTCont() >= Config.MISSION_TVT_CONT) {
	                    msg = msg.replaceAll("%tvt%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link tvt_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	        
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "Chargeback_Hwid: " + e.getMessage(), e);
	    }
	}

	
	/*public static void info_pvp(Player activeChar)
	{
		String name = "Nao recebida";
		Connection con = null;
		try
		{
			con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE pvp_hwid=?");
			statement.setString(1, activeChar.getHWID());
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				name = rset.getString("char_name");
				String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/pvp.htm");
				msg = msg.replaceAll("%pvp_cont%", "" + Config.MISSION_PVP_CONT + "");
				msg = msg.replaceAll("%cont%", "" + activeChar.getPvPCont() + "");
				msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
				if (activeChar.isPvPCompleted())
				{
					msg = msg.replaceAll("%pvp%", "<font color=\"FF0000\">Received</font>");
					msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
				} 
				else if (activeChar.getPvPCont() >= Config.MISSION_PVP_CONT) 
				{
					msg = msg.replaceAll("%pvp%", "<font color=\"5EA82E\">Completed</font>");
					msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link pvp_mission\" value=\"Reward!\">");
				} else {
					msg = msg.replaceAll("%pvp%", "<font color=\"FF0000\">Not Completed</font>");
					msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
				} 
				activeChar.sendPacket(new TutorialShowHtml(msg));
			} 
			rset.close();
			statement.close();
		} catch (SQLException e) {
			_log.log(Level.WARNING, "Chargeback_Hwid: " + e.getMessage(), e);
		} 
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}*/
	
	public static void info_raid(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE raid_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/raid.htm");
	                msg = msg.replaceAll("%raid_cont%", String.valueOf(Config.MISSION_RAID_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getRaidCont()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.isRaidCompleted() || activeChar.check_raid_hwid(activeChar.getHWID())) {
	                    msg = msg.replaceAll("%raid%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getRaidCont() >= Config.MISSION_RAID_CONT) {
	                    msg = msg.replaceAll("%raid%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link raid_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%raid%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "Chargeback_Hwid: " + e.getMessage(), e);
	    }
	}

	
	public static void info_party_mob(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE party_mob_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/party_mob.htm");
	                msg = msg.replaceAll("%party_mob_cont%", String.valueOf(Config.MISSION_PARTY_MOB_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getPartyMonsterKills()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.isPartyMobCompleted()) {
	                    msg = msg.replaceAll("%party_mob%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getPartyMonsterKills() >= Config.MISSION_PARTY_MOB_CONT) {
	                    msg = msg.replaceAll("%party_mob%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link party_mob_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%party_mob%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "Chargeback_Hwid: " + e.getMessage(), e);
	    }
	}

	
	public static void info_mob(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE mob_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/mob.htm");
	                msg = msg.replaceAll("%mob_cont%", String.valueOf(Config.MISSION_MOB_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getMonsterKills()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.isMobCompleted()) {
	                    msg = msg.replaceAll("%mob%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getMonsterKills() >= Config.MISSION_MOB_CONT) {
	                    msg = msg.replaceAll("%mob%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link mob_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%mob%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "Chargeback_Hwid: " + e.getMessage(), e);
	    }
	}

	
	public static void info_1x1(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE tournament_1x1_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/1x1.htm");
	                msg = msg.replaceAll("%1x1_cont%", String.valueOf(Config.MISSION_1X1_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTournament1x1Cont()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.is1x1Completed()) {
	                    msg = msg.replaceAll("%1x1%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getTournament1x1Cont() >= Config.MISSION_1X1_CONT) {
	                    msg = msg.replaceAll("%1x1%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 1x1_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%1x1%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "info_1x1: " + e.getMessage(), e);
	    }
	}

//	public static void info_2x2(L2PcInstance activeChar) {
//	    String name = "Nao recebida";
//	    String sql = "SELECT char_name FROM characters_mission WHERE tournament_2x2_hwid=?";
//	    
//	    try (Connection con = ConnectionPool.getConnection();
//	         PreparedStatement statement = con.prepareStatement(sql)) {
//	        
//	        statement.setString(1, activeChar.getHWID());
//	        
//	        try (ResultSet rset = statement.executeQuery()) {
//	            while (rset.next()) {
//	                name = rset.getString("char_name");
//	                
//	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/2x2.htm");
//	                msg = msg.replaceAll("%2x2_cont%", String.valueOf(Config.MISSION_3X3_CONT));
//	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTournament2x2Cont()));
//	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
//	                
//	                if (activeChar.is2x2Completed()) {
//	                    msg = msg.replaceAll("%2x2%", "<font color=\"FF0000\">Received</font>");
//	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
//	                } else if (activeChar.getTournament2x2Cont() >= Config.MISSION_3X3_CONT) {
//	                    msg = msg.replaceAll("%2x2%", "<font color=\"5EA82E\">Completed</font>");
//	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 2x2_mission\" value=\"Reward!\">");
//	                } else {
//	                    msg = msg.replaceAll("%2x2%", "<font color=\"FF0000\">Not Completed</font>");
//	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
//	                }
//	                
//	                activeChar.sendPacket(new TutorialShowHtml(msg));
//	            }
//	        }
//	    } catch (SQLException e) {
//	        _log.log(Level.WARNING, "info_2x2: " + e.getMessage(), e);
//	    }
//	}
	public static void info_2x2(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE tournament_2x2_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                //ESTAVA 3X3
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/3x3.htm");
	           //     String msg = HtmCache.getInstance().getHtm("data/html/en/mods/menu/mission/3x3.htm");
	                msg = msg.replaceAll("%3x3_cont%", String.valueOf(Config.MISSION_3X3_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTournament2x2Cont()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.is2x2Completed()) {
	                    msg = msg.replaceAll("%3x3%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getTournament2x2Cont() >= Config.MISSION_3X3_CONT) {
	                    msg = msg.replaceAll("%3x3%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 3x3_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%3x3%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "info_2x2: " + e.getMessage(), e);
	    }
	}


	public static void info_5x5(Player activeChar) {
	    String name = "Nao recebida";
	    String sql = "SELECT char_name FROM characters_mission WHERE tournament_5x5_hwid=?";
	    
	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, activeChar.getHWID());
	        
	        try (ResultSet rset = statement.executeQuery()) {
	            while (rset.next()) {
	                name = rset.getString("char_name");
	                
	                String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/5x5.htm");
	                msg = msg.replaceAll("%5x5_cont%", String.valueOf(Config.MISSION_5X5_CONT));
	                msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTournament5x5Cont()));
	                msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	                
	                if (activeChar.is5x5Completed()) {
	                    msg = msg.replaceAll("%5x5%", "<font color=\"FF0000\">Received</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                } else if (activeChar.getTournament5x5Cont() >= Config.MISSION_5X5_CONT) {
	                    msg = msg.replaceAll("%5x5%", "<font color=\"5EA82E\">Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 5x5_mission\" value=\"Reward!\">");
	                } else {
	                    msg = msg.replaceAll("%5x5%", "<font color=\"FF0000\">Not Completed</font>");
	                    msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	                }
	                
	                activeChar.sendPacket(new TutorialShowHtml(msg));
	            }
	        }
	    } catch (SQLException e) {
	        _log.log(Level.WARNING, "info_5x5: " + e.getMessage(), e);
	    }
	}

	
	public static void info_9x9(Player activeChar) 
	{
	    String name = "Nao recebida";
	    Connection con = null;
	    PreparedStatement statement = null;
	    ResultSet rset = null;
	    try
	    {
	        con = ConnectionPool.getConnection();
	        statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE tournament_9x9_hwid=?");
	        statement.setString(1, activeChar.getHWID());
	        rset = statement.executeQuery();
	        
	        if (rset.next()) 
	        {
	            name = rset.getString("char_name");
	            String msg = HtmCache.getInstance().getHtm("data/html/mods/menu/mission/9x9.htm");
	            msg = msg.replaceAll("%9x9_cont%", String.valueOf(Config.MISSION_9X9_CONT));
	            msg = msg.replaceAll("%cont%", String.valueOf(activeChar.getTournament9x9Cont()));
	            msg = msg.replaceAll("%name%", "Recebido por: <font color=\"FF0000\">" + name + "</font>");
	            
	            if (activeChar.is9x9Completed()) 
	            {
	                msg = msg.replaceAll("%9x9%", "<font color=\"FF0000\">Received</font>");
	                msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	            }
	            else if (activeChar.getTournament9x9Cont() >= Config.MISSION_9X9_CONT)
	            {
	                msg = msg.replaceAll("%9x9%", "<font color=\"5EA82E\">Completed</font>");
	                msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link 9x9_mission\" value=\"Reward!\">");
	            } 
	            else
	            {
	                msg = msg.replaceAll("%9x9%", "<font color=\"FF0000\">Not Completed</font>");
	                msg = msg.replaceAll("%link%", "<button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link close\" value=\"Close\">");
	            } 
	            activeChar.sendPacket(new TutorialShowHtml(msg));
	        }
	    } 
	    catch (Exception e)
	    {
	        _log.log(Level.WARNING, "info_9x9: " + e.getMessage(), e);
	    }
	    finally
	    {
	        try
	        {
	            if (rset != null) rset.close();
	        }
	        catch (Exception e)
	        {
	            _log.log(Level.WARNING, "Erro fechando ResultSet em info_9x9", e);
	        }
	        try
	        {
	            if (statement != null) statement.close();
	        }
	        catch (Exception e)
	        {
	            _log.log(Level.WARNING, "Erro fechando PreparedStatement em info_9x9", e);
	        }
	        try
	        {
	            if (con != null) con.close();
	        }
	        catch (Exception e)
	        {
	            _log.log(Level.WARNING, "Erro fechando Connection em info_9x9", e);
	        }
	    }
	}

	
	public void Classes(String command, Player activeChar) 
	{
	    if (command.startsWith("tvt_mission")) 
	    {
	        if (!Config.ACTIVE_MISSION_TVT)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_tvt_hwid(activeChar.getHWID()) || activeChar.isTvTCompleted())
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getTvTCont() >= Config.MISSION_TVT_CONT && !activeChar.isTvTCompleted()) 
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET tvt_completed=?, tvt_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally
	            {
	                try
	                {
	                    if (stmt != null) stmt.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em Classes", e);
	                }
	                try
	                {
	                    if (con != null) con.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em Classes", e);
	                }
	            } 

	            activeChar.setTvTCompleted(true);

	            // Se quiser habilitar a recompensa em PC Bang, descomente abaixo e comente a recompensa do item
	            /*
	            if(Config.REWARD_PCPOINT)
	            {
	                activeChar.addPcBangScore(Config.PC_TVT);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_TVT);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_TVT, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_TVT_REWARD_ID, Config.MISSION_TVT_REWARD_AMOUNT, activeChar, true);
	            //}
	            
	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        }
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }
	

		/*if (command.startsWith("pvp_mission")) {
			if (!Config.ACTIVE_MISSION_PVP) {
				activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
				return;
			} 
			if (activeChar.check_pvp_hwid(activeChar.getHWID()) || activeChar.isPvPCompleted()) {
				activeChar.sendMessage("Recompensa ja foi retirada..");
				return;
			} 
			if (activeChar.getPvPCont() >= Config.MISSION_PVP_CONT && !activeChar.isPvPCompleted()) {
				if (!activeChar.check_obj_mission(activeChar.getObjectId()))
					activeChar.updateMission(); 
				Connection con = null;
				try {
					con = ConnectionPool.getConnection();
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET pvp_completed=?,pvp_hwid=? WHERE obj_Id=?");
					stmt.setInt(1, 1);
					stmt.setString(2, activeChar.getHWID());
					stmt.setInt(3, activeChar.getObjectId());
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					CloseUtil.close(con);
				} 
				activeChar.setPvPCompleted(true);
				
				if(Config.REWARD_PCPOINT){
					activeChar.addPcBangScore(Config.PC_PVP);
					SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
					sm.addNumber(Config.PC_PVP);
					activeChar.sendPacket(sm);
					sm = null;
					activeChar.updatePcBangWnd(Config.PC_PVP, true, false);
				}else{
					activeChar.addItem("Reward", Config.MISSION_PVP_REWARD_ID, Config.MISSION_PVP_REWARD_AMOUNT, activeChar, true);
				}
				activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
                activeChar.broadcastPacket(MSU);
				activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
				activeChar.broadcastPacket(new SocialAction(activeChar, 3));
				VoicedMenu.showMenuHtml(activeChar);
			} else {
				activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
			} 
			activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		}*/
		
	    else if (command.startsWith("raid_mission")) 
	    {
	        if (!Config.ACTIVE_MISSION_RAID)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_raid_hwid(activeChar.getHWID()) || activeChar.isRaidCompleted())
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getRaidCont() >= Config.MISSION_RAID_CONT && !activeChar.isRaidCompleted())
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET raid_completed=?, raid_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            } 
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally 
	            {
	                try
	                {
	                    if (stmt != null)
	                        stmt.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em raid_mission", e);
	                }
	                try
	                {
	                    if (con != null)
	                        con.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em raid_mission", e);
	                }
	            } 

	            activeChar.setRaidCompleted(true);

	            // Se quiser ativar recompensa por PC Bang, descomente e comente recompensa por item
	            /*
	            if(Config.REWARD_PCPOINT)
	            {
	                activeChar.addPcBangScore(Config.PC_RAID_IND);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_RAID_IND);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_RAID_IND, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_RAID_REWARD_ID, Config.MISSION_RAID_REWARD_AMOUNT, activeChar, true);
	            //}
	            
	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        } 
	        else
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }
	    else if (command.startsWith("party_mob_mission"))
	    {
	        if (!Config.ACTIVE_MISSION_PARTY_MOB)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_party_mob_hwid(activeChar.getHWID()) || activeChar.isPartyMobCompleted())
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getPartyMonsterKills() >= Config.MISSION_PARTY_MOB_CONT && !activeChar.isPartyMobCompleted())
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET party_mob_completed=?, party_mob_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally
	            {
	                try
	                {
	                    if (stmt != null)
	                        stmt.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em party_mob_mission", e);
	                }
	                try
	                {
	                    if (con != null)
	                        con.close();
	                }
	                catch (Exception e)
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em party_mob_mission", e);
	                }
	            } 

	            activeChar.setPartyMobCompleted(true);

	            // Se quiser usar recompensa por PC Bang, descomente esse bloco e comente o bloco de item
	            /*
	            if(Config.REWARD_PCPOINT)
	            {
	                activeChar.addPcBangScore(Config.PC_PARTYZONE);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_PARTYZONE);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_PARTYZONE, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_PARTY_MOB_REWARD_ID, Config.MISSION_PARTY_MOB_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);

	            if (!activeChar.isGM())
	            {
	                for (Player p : L2World.getInstance().getPlayers())
	                {
	                    if (p != null && p.isOnline())
	                        p.sendChatMessage(0, 16, ".", activeChar.getName() + " concluiu [Kill Party Monster x " + Config.MISSION_PARTY_MOB_CONT + "]  :.");
	                }
	            }
	        } 
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }

	    else if (command.startsWith("mob_mission")) 
	    {
	        if (!Config.ACTIVE_MISSION_MOB)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_mob_hwid(activeChar.getHWID()) || activeChar.isMobCompleted()) 
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getMonsterKills() >= Config.MISSION_MOB_CONT && !activeChar.isMobCompleted()) 
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET mob_completed=?, mob_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            } 
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally
	            {
	                try 
	                {
	                    if (stmt != null) stmt.close();
	                } 
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em mob_mission", e);
	                }
	                try 
	                {
	                    if (con != null) con.close();
	                } 
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em mob_mission", e);
	                }
	            } 

	            activeChar.setMobCompleted(true);

	            /*
	            if(Config.REWARD_PCPOINT)
	            {
	                activeChar.addPcBangScore(Config.PC_MOBS);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_MOBS);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_MOBS, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_MOB_REWARD_ID, Config.MISSION_MOB_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);

	            if (!activeChar.isGM())
	                for (Player p : L2World.getInstance().getPlayers())
	                    if (p != null && p.isOnline())
	                        p.sendChatMessage(0, 16, ".", activeChar.getName() + " concluiu [Kill Monster x " + Config.MISSION_MOB_CONT + "]  :.");  
	        }
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    } 
	    else if (command.startsWith("1x1_mission"))
	    {
	        if (!Config.ACTIVE_MISSION_1X1) 
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_1x1_hwid(activeChar.getHWID()) || activeChar.is1x1Completed())
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getTournament1x1Cont() >= Config.MISSION_1X1_CONT && !activeChar.is1x1Completed()) 
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET tournament_1x1_completed=?, tournament_1x1_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            } 
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally 
	            {
	                try 
	                {
	                    if (stmt != null) stmt.close();
	                } 
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em 1x1_mission", e);
	                }
	                try 
	                {
	                    if (con != null) con.close();
	                } 
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em 1x1_mission", e);
	                }
	            } 

	            activeChar.set1x1Completed(true);

	            /*
	            if(Config.REWARD_PCPOINT){
	                activeChar.addPcBangScore(Config.PC_1x1);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_1x1);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_1x1, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_1X1_REWARD_ID, Config.MISSION_1X1_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        } 
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }

		
	    else if (command.startsWith("3x3_mission"))
	    {
	        if (!Config.ACTIVE_MISSION_3X3)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_2x2_hwid(activeChar.getHWID()) || activeChar.is2x2Completed()) 
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getTournament2x2Cont() >= Config.MISSION_3X3_CONT && !activeChar.is2x2Completed())
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try 
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET tournament_2x2_completed=?, tournament_2x2_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally
	            {
	                try 
	                {
	                    if (stmt != null) stmt.close();
	                }
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em 2x2_mission", e);
	                }
	                try 
	                {
	                    if (con != null) con.close();
	                }
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em 2x2_mission", e);
	                }
	            } 

	            activeChar.set2x2Completed(true);
	            
	            /*
	            if(Config.REWARD_PCPOINT){
	                activeChar.addPcBangScore(Config.PC_2x2);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_2x2);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_2x2, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_3X3_REWARD_ID, Config.MISSION_3X3_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        }
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }

	    else if (command.startsWith("5x5_mission"))
	    {
	        if (!Config.ACTIVE_MISSION_5X5)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_5x5_hwid(activeChar.getHWID()) || activeChar.is5x5Completed())
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getTournament5x5Cont() >= Config.MISSION_5X5_CONT && !activeChar.is5x5Completed()) 
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET tournament_5x5_completed=?, tournament_5x5_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            } 
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            } 
	            finally 
	            {
	                try 
	                {
	                    if (stmt != null) stmt.close();
	                }
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando PreparedStatement em 5x5_mission", e);
	                }
	                try 
	                {
	                    if (con != null) con.close();
	                }
	                catch (Exception e) 
	                {
	                    _log.log(Level.WARNING, "Erro fechando Connection em 5x5_mission", e);
	                }
	            } 
	            activeChar.set5x5Completed(true);

	            /*
	            if(Config.REWARD_PCPOINT)
	            {
	                activeChar.addPcBangScore(Config.PC_5x5);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_5x5);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_5x5, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_5X5_REWARD_ID, Config.MISSION_5X5_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        } 
	        else 
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }

	    else if (command.startsWith("9x9_mission"))
	    {
	        if (!Config.ACTIVE_MISSION_9X9)
	        {
	            activeChar.sendMessage("[WARN]:The Mission is Temporarily unavailable.");
	            return;
	        } 
	        if (activeChar.check_9x9_hwid(activeChar.getHWID()) || activeChar.is9x9Completed()) 
	        {
	            activeChar.sendMessage("Recompensa ja foi retirada..");
	            activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	            return;
	        } 
	        if (activeChar.getTournament9x9Cont() >= Config.MISSION_9X9_CONT && !activeChar.is9x9Completed())
	        {
	            if (!activeChar.check_obj_mission(activeChar.getObjectId()))
	                activeChar.updateMission(); 

	            Connection con = null;
	            PreparedStatement stmt = null;
	            try
	            {
	                con = ConnectionPool.getConnection();
	                stmt = con.prepareStatement("UPDATE characters_mission SET tournament_9x9_completed=?, tournament_9x9_hwid=? WHERE obj_Id=?");
	                stmt.setInt(1, 1);
	                stmt.setString(2, activeChar.getHWID());
	                stmt.setInt(3, activeChar.getObjectId());
	                stmt.executeUpdate();
	            } 
	            catch (Exception e) 
	            {
	                e.printStackTrace();
	            } 
	            finally
	            {
	                try { if (stmt != null) stmt.close(); } catch (Exception e) { _log.warning("Erro fechando stmt 9x9_mission: " + e.getMessage()); }
	                try { if (con != null) con.close(); } catch (Exception e) { _log.warning("Erro fechando con 9x9_mission: " + e.getMessage()); }
	            } 

	            activeChar.set9x9Completed(true);

	            /*
	            if(Config.REWARD_PCPOINT){
	                activeChar.addPcBangScore(Config.PC_9x9);
	                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
	                sm.addNumber(Config.PC_9x9);
	                activeChar.sendPacket(sm);
	                sm = null;
	                activeChar.updatePcBangWnd(Config.PC_9x9, true, false);
	            }
	            else
	            {
	            */
	                activeChar.addItem("Reward", Config.MISSION_9X9_REWARD_ID, Config.MISSION_9X9_REWARD_AMOUNT, activeChar, true);
	            //}

	            activeChar.sendMessage("Parabens! Voce concluiu uma tarefa diaria.");
	            MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
	            activeChar.broadcastPacket(new SocialAction(activeChar, 3));
	            VoicedMenu.showMenuHtml(activeChar);
	        }
	        else
	        {
	            activeChar.sendMessage("Voce ainda nao finalizou essa tarefa.");
	        } 
	        activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    }
	}
	
	public static final void linkMission(Player player, String request)
	{
		getInstance().Classes(request, player);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	private static class SingletonHolder 
	{
		protected static final VoicedMission _instance = new VoicedMission();
	}
}
