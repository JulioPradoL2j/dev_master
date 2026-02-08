package net.sf.l2j.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.util.Util;

public class MoveBackwardToLocation extends L2GameClientPacket
{
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	
	private int _moveMovement;
	
	@Override
	protected void readImpl()
	{
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		
		try
		{
			_moveMovement = readD(); // is 0 if cursor keys are used 1 if mouse is used
		}
		catch (BufferUnderflowException e)
		{
			if (Config.L2WALKER_PROTECTION)
			{
				Player activeChar = getClient().getActiveChar();
				Util.handleIllegalPlayerAction(activeChar, activeChar.getName() + " is trying to use L2Walker.", Config.DEFAULT_PUNISH);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getActiveEnchantItem() != null)
		{
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(EnchantResult.CANCELLED);
			activeChar.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
		}
		
		if (_targetX == _originX && _targetY == _originY && _targetZ == _originZ)
		{
			activeChar.sendPacket(new StopMove(activeChar));
			return;
		}
		
		// Correcting targetZ from floor level to head level
		_targetZ += activeChar.getCollisionHeight();
		
		if (activeChar.getTeleMode() > 0)
		{
			if (activeChar.getTeleMode() == 1)
				activeChar.setTeleMode(0);
			
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.teleToLocation(_targetX, _targetY, _targetZ, 0);
			return;
		}
		
		double dx = _targetX - _originX;
		double dy = _targetY - _originY;
		
		// Movimento cancelado se tive obstaculo colado, ant bug
		if (_moveMovement == 0)
		{
			double distance = Math.sqrt(dx * dx + dy * dy);
			double checkDistance = Math.min(175, distance);
			
			double normX = dx / distance;
			double normY = dy / distance;
			
			int checkX = _originX + (int) (normX * checkDistance);
			int checkY = _originY + (int) (normY * checkDistance);
			int checkZ = _originZ;
			
			if (!GeoEngine.getInstance().canMoveToTarget(_originX, _originY, _originZ, checkX, checkY, checkZ))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		if ((dx * dx + dy * dy) > 98010000) // 9900*9900
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		int geoZ = GeoEngine.getInstance().getHeight(_originX, _originY, _originZ);
		
		// margem de segurança (100~200 é ideal)
		if (_originZ < geoZ - 200)
		{
			if (!activeChar.canFixLimbo())
				return;
			
			
			activeChar.stopMove(null);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);

			activeChar.teleToLocation(_originX, _originY, geoZ, 75);
			activeChar.markLimboFixed();
			
			return;
		}
		activeChar.getAI().setIntention(CtrlIntention.MOVE_TO, new Location(_targetX, _targetY, _targetZ));
		 
	}
}