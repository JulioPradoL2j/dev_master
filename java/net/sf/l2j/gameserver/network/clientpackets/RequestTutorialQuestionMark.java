package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.RemoteClassMaster;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.scriptings.QuestState;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	int _number;
	
	@Override
	protected void readImpl()
	{
		_number = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		if (RemoteClassMaster.onTutorialQuestionMark(player, _number))
			return;
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("QM" + _number + "", null, player);
	}
}