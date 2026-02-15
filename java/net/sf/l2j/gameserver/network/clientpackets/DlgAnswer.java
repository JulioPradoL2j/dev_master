package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.event.bossevent.KTBEvent;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.deathmath.DMEvent;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.lastman.LMEvent;
import net.sf.l2j.event.soloboss.SoloBoss;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.gameserver.datatables.xml.MerchantData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.util.Broadcast;

/**
 * @author BAN Format: cddd 1 aceitar _answer 0 recusar _answer
 */
public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		if (_messageId == SystemMessageId.S1_S2.getId())
		{
			
			if (_answer == 1)
			{
				
				// cancela se estado mudou
				if (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isInCombat() || activeChar.isCastingNow())
				{
					activeChar.clearPendingMerchantBuy();
					activeChar.sendMessage("Compra cancelada.");
					return;
				}
				
				final String category = activeChar.getPendingMerchantCategory();
				final String grade = activeChar.getPendingMerchantGrade();
				final int page = activeChar.getPendingMerchantPage();
				final int index = activeChar.getPendingMerchantIndex();
				
				
				
				MerchantData.getInstance().buy(activeChar, category, grade, page, index);
				// consome pendência antes de comprar (evita double confirm)
				activeChar.clearPendingMerchantBuy();
			}
			else
			{
				// NO / Cancel
				activeChar.clearPendingMerchantBuy();
				activeChar.sendMessage("Compra cancelada.");
			}
		}
		else if ((_messageId == SystemMessageId.S1.getId() && Config.TIME_RESS_CHARACTER_ALLOW) || (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() && !Config.TIME_RESS_CHARACTER_ALLOW) || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
		{
			activeChar.reviveAnswer(_answer);
		}
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
			activeChar.teleportAnswer(_answer, _requesterId);
		else if (_messageId == 1983 && Config.ALLOW_WEDDING)
			activeChar.EngageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			activeChar.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			activeChar.activateGate(_answer, 0);
		else if (Broadcast.pvp_register == true && _answer == 1)
		{
			if (activeChar.inObserverMode() || activeChar.isSellBuff() || CTFEvent.isPlayerParticipant(activeChar.getObjectId()) && CTFEvent.isStarted() || TvTEvent.isPlayerParticipant(activeChar.getObjectId()) && TvTEvent.isStarted() || activeChar.isAio() || activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || FOSEvent.isPlayerParticipant(activeChar.getObjectId()) && FOSEvent.isStarted() || DMEvent.isPlayerParticipant(activeChar.getObjectId()) && DMEvent.isStarted() || KTBEvent.isPlayerParticipant(activeChar.getObjectId()) && KTBEvent.isStarted() || LMEvent.isPlayerParticipant(activeChar.getObjectId()) && LMEvent.isStarted() || activeChar.isAlikeDead() || activeChar.isInArenaEvent() || activeChar.isArenaProtection() || activeChar.isOlympiadProtection() || activeChar.isInStoreMode() || activeChar.isRooted() || activeChar.getKarma() > 0 || activeChar.isInOlympiadMode() || activeChar.isOff() || activeChar.isOffShop() || activeChar.isFestivalParticipant() || activeChar.isArenaAttack() || activeChar.isInsideZone(ZoneId.BOSS) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.SIEGE) || activeChar.isInsideZone(ZoneId.SPOIL_AREA))
			{
				return;
			}
			activeChar.teleToLocation(Config.pvp_locx, Config.pvp_locy, Config.pvp_locz, 125);
		}
		else if (SoloBoss.boss_teleporter == true && _answer == 1)
		{
			if (activeChar.inObserverMode() || activeChar.isSellBuff() || CTFEvent.isPlayerParticipant(activeChar.getObjectId()) && CTFEvent.isStarted() || TvTEvent.isPlayerParticipant(activeChar.getObjectId()) && TvTEvent.isStarted() || activeChar.isAio() || activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isDead() || FOSEvent.isPlayerParticipant(activeChar.getObjectId()) && FOSEvent.isStarted() || DMEvent.isPlayerParticipant(activeChar.getObjectId()) && DMEvent.isStarted() || KTBEvent.isPlayerParticipant(activeChar.getObjectId()) && KTBEvent.isStarted() || LMEvent.isPlayerParticipant(activeChar.getObjectId()) && LMEvent.isStarted() || activeChar.isAlikeDead() || activeChar.isInArenaEvent() || activeChar.isArenaProtection() || activeChar.isOlympiadProtection() || activeChar.isInStoreMode() || activeChar.isRooted() || activeChar.getKarma() > 0 || activeChar.isInOlympiadMode() || activeChar.isOff() || activeChar.isOffShop() || activeChar.isFestivalParticipant() || activeChar.isArenaAttack() || activeChar.isInsideZone(ZoneId.BOSS) || activeChar.isInsideZone(ZoneId.FLAG_AREA_BOSS) || activeChar.isInsideZone(ZoneId.ARENA_EVENT) || activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || activeChar.isInsideZone(ZoneId.RAID_ZONE) || activeChar.isInsideZone(ZoneId.SIEGE) || activeChar.isInsideZone(ZoneId.SPOIL_AREA))
			{
				return;
			}
			activeChar.teleToLocation(Config.SOLO_BOSS_ID_ONE_LOC[0], Config.SOLO_BOSS_ID_ONE_LOC[1], Config.SOLO_BOSS_ID_ONE_LOC[2], 825);
		}
	}
}