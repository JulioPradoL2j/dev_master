package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public final class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private int _type; // 1 = on : 0 = off;
	
	@Override
	protected void readImpl()
	{
		_itemId = readD();
		_type = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (!activeChar.isInStoreMode() && activeChar.getActiveRequester() == null && !activeChar.isDead())
		{
			ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);
			if (item == null)
				return;
			
			if (_type == 1)
			{
				if (!activeChar.getInventory().canManipulateWithItemId(item.getItemId()))
				{
					activeChar.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
					return;
				}
				if (_itemId < 6535 || _itemId > 6540)
				{
				    // Attempt to charge first shot on activation
				    if (_itemId == 6645 || _itemId == 6646 || _itemId == 6647)
				    {
				        // Não importa se o jogador tem um pet ou não, prosseguir com a ativação do SoulShot
				        if (activeChar.getPet() != null)
				        {
				            // Cannot activate bss automation during Olympiad.
				            if (_itemId == 6647 && activeChar.isInOlympiadMode())
				            {
				                activeChar.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
				                return;
				            }

				            if (_itemId == 6645)
				            {
				                if (activeChar.getPet().getSoulShotsPerHit() > item.getCount())
				                {
				                    activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
				                    return;
				                }
				            }
				            else
				            {
				                if (activeChar.getPet().getSpiritShotsPerHit() > item.getCount())
				                {
				                    activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS_FOR_PET);
				                    return;
				                }
				            }

				            // start the auto soulshot use for pet
				            activeChar.addAutoSoulShot(_itemId);
				            activeChar.sendPacket(new ExAutoSoulShot(_itemId, _type));
				            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO).addItemName(_itemId));
				            activeChar.rechargeShots(true, true);
				            activeChar.getPet().rechargeShots(true, true);
				        }
				        else
				        {
				            // Se o jogador não tem pet, ativar o uso de Soulshot para o próprio jogador
				            activeChar.addAutoSoulShot(_itemId);
				            activeChar.sendPacket(new ExAutoSoulShot(_itemId, _type));
				            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO).addItemName(_itemId));
				            activeChar.rechargeShots(true, false); // Não recarregar para o pet, apenas para o jogador
				        
				        }
					}
					else
					{
						// Cannot activate bss automation during Olympiad.
						if (_itemId >= 3947 && _itemId <= 3952 && activeChar.isInOlympiadMode())
						{
							activeChar.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
							return;
						}
						
						// Activate the visual effect
						activeChar.addAutoSoulShot(_itemId);
						activeChar.sendPacket(new ExAutoSoulShot(_itemId, _type));
						
						// start the auto soulshot use
						if (activeChar.getActiveWeaponItem() != activeChar.getFistsWeaponItem() && item.getItem().getCrystalType() == activeChar.getActiveWeaponItem().getCrystalType())
							activeChar.rechargeShots(true, true);
						else
						{
							if ((_itemId >= 2509 && _itemId <= 2514) || (_itemId >= 3947 && _itemId <= 3952) || _itemId == 5790)
								activeChar.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
							else
								activeChar.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
						}
						
						// In both cases (match/mismatch), that message is displayed.
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO).addItemName(_itemId));
					}
				}
				 
			}
			else if (_type == 0)
			{
				// cancel the auto soulshot use
				activeChar.removeAutoSoulShot(_itemId);
				activeChar.sendPacket(new ExAutoSoulShot(_itemId, _type));
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED).addItemName(_itemId));
			}
		}
	}
}