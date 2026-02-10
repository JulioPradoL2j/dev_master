package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.events.eventpvp.PvPEvent;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.model.L2Object.PolyType;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.skills.AbnormalEffect;

public class UserInfo extends L2GameServerPacket
{
	private final Player _activeChar;
	private int _relation;
	
	public UserInfo(Player character)
	{
		_activeChar = character;
		
		_relation = _activeChar.isClanLeader() ? 0x40 : 0;
		
		if (_activeChar.getSiegeState() == 1)
			_relation |= 0x180;
		if (_activeChar.getSiegeState() == 2)
			_relation |= 0x80;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x04);
		
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getHeading());
		writeD(_activeChar.getObjectId());
		
		if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_2_NAME);
		}
		else
		{
			writeS((_activeChar.getPolyTemplate() != null) ? _activeChar.getPolyTemplate().getName() : _activeChar.getName());
		}
		// writeS((_activeChar.getPolyTemplate() != null) ? _activeChar.getPolyTemplate().getName() : _activeChar.getName());
		
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex().ordinal());
		
		if (_activeChar.getClassIndex() == 0)
			writeD(_activeChar.getClassId().getId());
		else
			writeD(_activeChar.getBaseClass());
		
		DressMeHolder armorSkin = _activeChar.getArmorSkin();
		DressMeHolder weaponSkin = _activeChar.getWeaponSkin();
		
		writeD(_activeChar.getLevel());
		writeQ(_activeChar.getExp());
		writeD(_activeChar.getSTR());
		writeD(_activeChar.getDEX());
		writeD(_activeChar.getCON());
		writeD(_activeChar.getINT());
		writeD(_activeChar.getWIT());
		writeD(_activeChar.getMEN());
		writeD(_activeChar.getMaxHp());
		writeD((int) _activeChar.getCurrentHp());
		writeD(_activeChar.getMaxMp());
		writeD((int) _activeChar.getCurrentMp());
		writeD(_activeChar.getSp());
		writeD(_activeChar.getCurrentLoad());
		writeD(_activeChar.getMaxLoad());
		
		writeD(_activeChar.getActiveWeaponItem() != null ? 40 : 20); // 20 no weapon, 40 weapon equipped
		
		
		final int hairallItemIdOb = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIRALL);
		
		final int hairallVisualOb = _activeChar.isDressMeDisableHair() ? hairallItemIdOb : (armorSkin != null && hairallItemIdOb > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairallItemIdOb);
		
		writeD(hairallVisualOb);
		
		 	
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
		
		int rhandObj = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
		int lhandObj = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND);
		
		if (_activeChar.isDressMe() && weaponSkin != null)
		{
			String equippedWeaponType = "";
			ItemInstance weaponInstance = _activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			
			if (weaponInstance != null && weaponInstance.getItem() instanceof Weapon)
			{
				Weapon weapon = (Weapon) weaponInstance.getItem();
				equippedWeaponType = weapon.getItemType().toString().toLowerCase();
			}
			
			if (equippedWeaponType.equalsIgnoreCase(weaponSkin.getWeaponTypeVisual()))
			{
				
				if (weaponSkin.getTwoHandId() > 0)
				{
					rhandObj = weaponSkin.getTwoHandId();
					lhandObj = 0;
				}
				else
				{
					if (weaponSkin.getRightHandId() > 0 && rhandObj > 0)
						rhandObj = weaponSkin.getRightHandId();
					if (weaponSkin.getLeftHandId() > 0 && lhandObj > 0)
						lhandObj = weaponSkin.getLeftHandId();
				}
				
			}
		}
		
		// Armas
		writeD(rhandObj); // PaperdollItemId RHAND
		writeD(lhandObj); // PaperdollItemId LHAND
		
		int glovesOId = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES);
		int chestOId = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST);
		int legsOId = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS);
		int feetOId = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET);
		
		writeD((armorSkin != null && glovesOId > 0 && armorSkin.getGlovesId() > 0) ? armorSkin.getGlovesId() : glovesOId);
		writeD((armorSkin != null && chestOId > 0 && armorSkin.getChestId() > 0) ? armorSkin.getChestId() : chestOId);
		writeD((armorSkin != null && legsOId > 0 && armorSkin.getLegsId() > 0) ? armorSkin.getLegsId() : legsOId);
		writeD((armorSkin != null && feetOId > 0 && armorSkin.getFeetId() > 0) ? armorSkin.getFeetId() : feetOId);
		
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_BACK));
		
		writeD(rhandObj);
		
		final int hairItemIdOb = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR);
		
		final int hairVisualOb = _activeChar.isDressMeDisableHair() ? hairItemIdOb : (armorSkin != null && hairItemIdOb > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairItemIdOb);
		
		writeD(hairVisualOb);
		
		final int faceItemIdOb = _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FACE);
		
		final int faceVisualOb = _activeChar.isDressMeDisableHair() ? faceItemIdOb : (armorSkin != null && faceItemIdOb > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : faceItemIdOb);
		
		writeD(faceVisualOb);
		
		final int hairallItemId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL);
		
		final int hairallVisual = _activeChar.isDressMeDisableHair() ? hairallItemId : (armorSkin != null && hairallItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairallItemId);
		
		writeD(hairallVisual);
		
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR));
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK));
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		
		int rhand = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
		int lhand = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND);
		
		if (_activeChar.isDressMe() && weaponSkin != null)
		{
			String equippedWeaponType = "";
			ItemInstance weaponInstance = _activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			
			if (weaponInstance != null && weaponInstance.getItem() instanceof Weapon)
			{
				Weapon weapon = (Weapon) weaponInstance.getItem();
				equippedWeaponType = weapon.getItemType().toString().toLowerCase();
			}
			
			if (equippedWeaponType.equalsIgnoreCase(weaponSkin.getWeaponTypeVisual()))
			{
				
				if (weaponSkin.getTwoHandId() > 0)
				{
					rhand = weaponSkin.getTwoHandId();
					lhand = 0;
				}
				else
				{
					if (weaponSkin.getRightHandId() > 0 && rhand > 0)
						rhand = weaponSkin.getRightHandId();
					if (weaponSkin.getLeftHandId() > 0 && lhand > 0)
						lhand = weaponSkin.getLeftHandId();
				}
				
			}
		}
		writeD(rhand); // PaperdollItemId RHAND
		writeD(lhand); // PaperdollItemId LHAND
		
		int glovesId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES);
		int chestId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST);
		int legsId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS);
		int feetId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET);
		
		writeD((armorSkin != null && glovesId > 0 && armorSkin.getGlovesId() > 0) ? armorSkin.getGlovesId() : glovesId);
		writeD((armorSkin != null && chestId > 0 && armorSkin.getChestId() > 0) ? armorSkin.getChestId() : chestId);
		writeD((armorSkin != null && legsId > 0 && armorSkin.getLegsId() > 0) ? armorSkin.getLegsId() : legsId);
		writeD((armorSkin != null && feetId > 0 && armorSkin.getFeetId() > 0) ? armorSkin.getFeetId() : feetId);
		
		writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_BACK));
		writeD(rhand);
		
		final int hairItemId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR);
		
		final int hairVisual = _activeChar.isDressMeDisableHair() ? hairItemId : (armorSkin != null && hairItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairItemId);
		
		writeD(hairVisual);
		
		final int faceItemId = _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FACE);
		
		final int faceVisual = _activeChar.isDressMeDisableHair() ? faceItemId : (armorSkin != null && faceItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : faceItemId);
		
		writeD(faceVisual);
		
		// c6 new h's
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		// end of c6 new h's
		
		writeD(_activeChar.getPAtk(null));
		writeD(_activeChar.getPAtkSpd());
		writeD(_activeChar.getPDef(null));
		writeD(_activeChar.getEvasionRate(null));
		writeD(_activeChar.getAccuracy());
		writeD(_activeChar.getCriticalHit(null, null));
		writeD(_activeChar.getMAtk(null, null));
		
		writeD(_activeChar.getMAtkSpd());
		writeD(_activeChar.getPAtkSpd());
		
		writeD(_activeChar.getMDef(null, null));
		
		writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		writeD(_activeChar.getKarma());
		
		int _runSpd = _activeChar.getStat().getBaseRunSpeed();
		int _walkSpd = _activeChar.getStat().getBaseWalkSpeed();
		int _swimSpd = _activeChar.getStat().getBaseSwimSpeed();
		writeD(_runSpd); // base run speed
		writeD(_walkSpd); // base walk speed
		writeD(_swimSpd); // swim run speed
		writeD(_swimSpd); // swim walk speed
		writeD(0);
		writeD(0);
		writeD(_activeChar.isFlying() ? _runSpd : 0); // fly run speed
		writeD(_activeChar.isFlying() ? _walkSpd : 0); // fly walk speed
		writeF(_activeChar.getStat().getMovementSpeedMultiplier()); // run speed multiplier
		writeF(_activeChar.getStat().getAttackSpeedMultiplier()); // attack speed multiplier
		
		L2Summon pet = _activeChar.getPet();
		if (_activeChar.getMountType() != 0 && pet != null)
		{
			writeF(pet.getCollisionRadius());
			writeF(pet.getCollisionHeight());
		}
		else
		{
			if (Config.ENABLE_DWARF_WEAPON_SIZE)
			{
				writeF(_activeChar.getBaseTemplate().getCollisionRadius(_activeChar.getBaseTemplate().getRace()));
				writeF(_activeChar.getBaseTemplate().getCollisionHeight());
			}
			else
			{
				writeF(_activeChar.getCollisionRadius());
				writeF(_activeChar.getCollisionHeight());
			}
		}
		
		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		writeD(_activeChar.isGM() ? 1 : 0); // builder level
		
		// writeS((_activeChar.getPolyType() != PolyType.DEFAULT) ? "Morphed" : _activeChar.getTitle());
		
		if (_activeChar.isInDMEvent())
		{
			writeS("Kills: " + _activeChar.getDMPointScore());
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Score: " + _activeChar.getFOSPointScore());
			
			if (playerTeamId == 1)
				writeS("Score: " + _activeChar.getFOSPointScore());
		}
		else if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Kills: " + _activeChar.getPointScore());
			
			if (playerTeamId == 1)
				writeS("Kills: " + _activeChar.getPointScore());
		}
		else if (_activeChar.isSellBuff())
		{
			writeS(Config.NEW_TITLE_SELLBUFF);
		}
		else if (_activeChar.isInsideZone(ZoneId.PVP_CUSTOM) && Config.ENABLE_NAME_TITLE_PVPEVENT && PvPEvent.getInstance().isActive())
		{
			int rank = PvPEvent.getPlayerRank(_activeChar);
			int kills = PvPEvent.getEventPvp(_activeChar);
			
			if (rank > 0 && kills > 0)
			{
				String title = "Rank - " + rank + " | Kills - " + kills;
				writeS(title); // Exibe visualmente sem alterar o título real
			}
			else
			{
				writeS(_activeChar.getTitle()); // Exibe o título original
			}
		}
		
		else
		{
			writeS((_activeChar.getPolyType() != PolyType.DEFAULT) ? "Morphed" : (Config.ENABLE_NEW_TITLE_AUTOFARM && _activeChar.isAutoFarm()) ? Config.NEW_TITLE_AUTOFARM : _activeChar.getTitle());
		}
		
		/*
		 * if (Config.BLOCK_CREST_PVPEVENT && _activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || _activeChar.isInLMEvent() || _activeChar.isInFOSEvent()) { writeD(0); writeD(0); writeD(0); writeD(0); } else {
		 */
		writeD(_activeChar.getClanId());
		writeD(_activeChar.getClanCrestId());
		writeD(_activeChar.getAllyId());
		writeD(_activeChar.getAllyCrestId()); // ally crest id
		// }
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeD(_relation);
		writeC(_activeChar.getMountType()); // mount type
		writeC(_activeChar.getStoreType().getId());
		writeC(_activeChar.hasDwarvenCraft() ? 1 : 0);
		writeD(_activeChar.getPkKills());
		writeD(_activeChar.getPvpKills());
		
		writeH(_activeChar.getCubics().size());
		for (int id : _activeChar.getCubics().keySet())
			writeH(id);
		
		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);
		
		if (_activeChar.getAppearance().getInvisible() && _activeChar.isGM())
			writeD(_activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask());
		else if (_activeChar.isAio() && Config.EFFECT_AIO_BUFF_CHARACTER)
		{
			writeD(_activeChar.getAbnormalEffect() | AbnormalEffect.FLAME.getMask());
		}
		else if (Config.PLAYER_SPAWN_PROTECTION > 0 && _activeChar.isSpawnProtected())
		{
			writeD(0x400000);
		}
		else
			writeD(_activeChar.getAbnormalEffect());
		writeC(0x00);
		
		writeD(_activeChar.getClanPrivileges());
		
		writeH(_activeChar.getRecomLeft()); // c2 recommendations remaining
		writeH(_activeChar.getRecomHave()); // c2 recommendations received
		writeD(_activeChar.getMountNpcId() > 0 ? _activeChar.getMountNpcId() + 1000000 : 0);
		writeH(_activeChar.getInventoryLimit());
		
		writeD(_activeChar.getClassId().getId());
		writeD(0x00); // special effects? circles around player...
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		writeC(_activeChar.isMounted() ? 0 : _activeChar.getEnchantEffect());
		
		if (_activeChar.getTeam() == 1)
			writeC(0x01); // team circle around feet 1= Blue, 2 = red
		else if (Config.ENABLE_AURA_AUTOFARM && _activeChar.isAutoFarm())
			writeC(0x01);
		else if (ArenaConfig.ENABLE_AURA_TOURNAMENT && _activeChar.isTeamTour1())
		{
			writeC(ArenaConfig.AURA_COLOR_TEAM1);
		}
		else if (ArenaConfig.ENABLE_AURA_TOURNAMENT && _activeChar.isTeamTour2())
		{
			writeC(ArenaConfig.AURA_COLOR_TEAM2);
		}
		else if (_activeChar.getTeam() == 2)
			writeC(0x02); // team circle around feet 1= Blue, 2 = red
		else
			writeC(0x00); // team circle around feet 1= Blue, 2 = red
			
		writeD(_activeChar.getClanCrestLargeId());
		writeC(_activeChar.isNoble() ? 1 : 0); // 0x01: symbol on char menu ctrl+I
		// if (_activeChar.isHero() && (((TvT.is_started() || TvT.is_teleport()) && _activeChar._inEventTvT) || ((CTF.is_started() || CTF.is_teleport()) && _activeChar._inEventCTF)))
		// {
		// writeC(0x00);
		// }
		// else
		// {
		writeC(_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 1 : 0); // 0x01: Hero Aura
		// }
		
		writeC(_activeChar.isFishing() ? 1 : 0); // Fishing Mode
		
		Location loc = _activeChar.getFishingLoc();
		if (loc != null)
		{
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
		}
		
		if (_activeChar.isInDMEvent())
		{
			writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
				
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
				
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInCTFEvent())
		{
			byte playerTeamId = CTFEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
				
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isAio() && Config.ALLOW_AIO_NCOLOR)
		{
			writeD(Config.AIO_NCOLOR);
		}
		else
		{
			writeD(_activeChar.getAppearance().getNameColor());
		}
		
		// new c5
		writeC(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window
		
		writeD(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
		writeD(_activeChar.getPledgeType());
		
		if (_activeChar.isAio() && Config.ALLOW_AIO_TCOLOR)
		{
			writeD(Config.AIO_TCOLOR);
		}
		else if (_activeChar.isSellBuff())
		{
			writeD(0xFF00);
		}
		else
		{
			writeD(_activeChar.getAppearance().getTitleColor());
		}
		
		if (_activeChar.isCursedWeaponEquipped())
			writeD(CursedWeaponsManager.getInstance().getCurrentStage(_activeChar.getCursedWeaponEquippedId()) - 1);
		else
			writeD(0x00);
	}
}