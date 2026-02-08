
package net.sf.l2j.gameserver.network.clientpackets;

import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.ITutorialHandler;
import net.sf.l2j.gameserver.handler.TutorialHandler;
import net.sf.l2j.gameserver.model.RemoteClassMaster;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.scriptings.QuestState;
import net.sf.l2j.mission.VoicedMission;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	String _bypass;
	
	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		if (RemoteClassMaster.onTutorialLink(player, _bypass))
			return;
		if (_bypass.equalsIgnoreCase("close"))
		{
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			return;
		}
		 
		 
		
		if (Config.ACTIVE_MISSION)
		{
			VoicedMission.linkMission(player, this._bypass);
		}
		if (_bypass.startsWith("-h"))
		{
			_bypass = _bypass.substring(2);
			
			if (_bypass.startsWith("_"))
				_bypass = _bypass.substring(1);
		}
		
		final ITutorialHandler handler = TutorialHandler.getInstance().getHandler(_bypass);
		
		if (handler != null)
		{
			String command = _bypass;
			String params = "";
			if (_bypass.indexOf("_") != -1)
			{
				command = _bypass.substring(0, _bypass.indexOf("_"));
				params = _bypass.substring(_bypass.indexOf("_") + 1, _bypass.length());
			}
			handler.useLink(command, player, params);
		}
		else
		{
			if (Config.DEBUG)
				_log.log(Level.WARNING, getClient() + " sent not handled RequestTutorialLinkHtml: [" + _bypass + "]");
		}
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent(_bypass, null, player);
	}
}
