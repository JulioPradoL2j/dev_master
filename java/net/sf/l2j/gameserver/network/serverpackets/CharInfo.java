package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.event.ctf.CTFConfig;
import net.sf.l2j.event.ctf.CTFEvent;
import net.sf.l2j.event.fortress.FOSConfig;
import net.sf.l2j.event.fortress.FOSEvent;
import net.sf.l2j.event.tournament.ArenaConfig;
import net.sf.l2j.event.tvt.TvTAreasLoader;
import net.sf.l2j.event.tvt.TvTEvent;
import net.sf.l2j.events.eventpvp.PvPEvent;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.skills.AbnormalEffect;

public class CharInfo extends L2GameServerPacket
{
	private final Player _activeChar;
	private final Inventory _inv;
	
	public CharInfo(Player cha)
	{
		_activeChar = cha;
		_inv = _activeChar.getInventory();
		
	}
	
	@Override
	protected final void writeImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (getClient() == null)
			return; // Ou outro tratamento adequado
			
		boolean gmSeeInvis = false;
		
		if (_activeChar.getAppearance().getInvisible())
		{
			Player tmp = getClient().getActiveChar();
			if (tmp != null && tmp.isGM())
				gmSeeInvis = true;
		}
		if (player.isHidingPlayers() && _activeChar.isInsideZone(ZoneId.PEACE))
			return;
		
		writeC(0x03);
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getHeading());
		writeD(_activeChar.getObjectId());
		if (_activeChar.isInsideZone(ZoneId.PVP_CUSTOM) && Config.ENABLE_NAME_TITLE_PVPEVENT && PvPEvent.getInstance().isActive())
		{
			writeS(Config.STRING_NAME_PVPEVENT);
		}
		else if (_activeChar.isInLMEvent())
		{
			writeS("Last Man");
		}
		else if (_activeChar.isInDMEvent())
		{
			writeS("Enemy");
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_2_NAME);
		}
		else if (_activeChar.isInCTFEvent())
		{
			byte playerTeamId = CTFEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + CTFConfig.CTF_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + CTFConfig.CTF_EVENT_TEAM_2_NAME);
		}
		else if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			TvTAreasLoader.Area chosenArea = TvTEvent.getChosenArea(); // acesso à área atual
			String team1Name = (chosenArea != null) ? chosenArea.team1Name : "Team 1";
			String team2Name = (chosenArea != null) ? chosenArea.team2Name : "Team 2";
			
			if (playerTeamId == 0)
				writeS("Team " + team2Name); // troca aqui
			else if (playerTeamId == 1)
				writeS("Team " + team1Name);
			
		}
		
		else
		{
			writeS(_activeChar.getName());
		}
		// writeS(_activeChar.getName());
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex().ordinal());
		
		if (_activeChar.getClassIndex() == 0)
			writeD(_activeChar.getClassId().getId());
		else
			writeD(_activeChar.getBaseClass());
		
		DressMeHolder armorSkin = _activeChar.getArmorSkin();
		DressMeHolder weaponSkin = _activeChar.getWeaponSkin();
		
		final int hairallItemId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL);
		
		final int hairallVisual = _activeChar.isDressMeDisableHair() ? hairallItemId : (armorSkin != null && hairallItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairallItemId);
		
		writeD(hairallVisual);
		
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		
		int rhand = _inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
		int lhand = _inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND);
		
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
		
		int glovesOId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES);
		int chestOId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST);
		int legsOId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS);
		int feetOId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET);
		
		writeD((armorSkin != null && glovesOId > 0 && armorSkin.getGlovesId() > 0) ? armorSkin.getGlovesId() : glovesOId);
		writeD((armorSkin != null && chestOId > 0 && armorSkin.getChestId() > 0) ? armorSkin.getChestId() : chestOId);
		writeD((armorSkin != null && legsOId > 0 && armorSkin.getLegsId() > 0) ? armorSkin.getLegsId() : legsOId);
		writeD((armorSkin != null && feetOId > 0 && armorSkin.getFeetId() > 0) ? armorSkin.getFeetId() : feetOId);
		
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
		writeD(rhand);
		
		final int hairItemId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR);
		
		final int hairVisual = _activeChar.isDressMeDisableHair() ? hairItemId : (armorSkin != null && hairItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : hairItemId);
		
		writeD(hairVisual);
		
		final int faceItemId = _inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE);
		
		final int faceVisual = _activeChar.isDressMeDisableHair() ? faceItemId : (armorSkin != null && faceItemId > 0 && armorSkin.getHelmetId() > 0 ? armorSkin.getHelmetId() : faceItemId);
		
		writeD(faceVisual);
		
		// c6 new h's
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
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
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(_activeChar.getPvpFlag());
		writeD(_activeChar.getKarma());
		
		writeD(_activeChar.getMAtkSpd());
		writeD(_activeChar.getPAtkSpd());
		
		writeD(_activeChar.getPvpFlag());
		writeD(_activeChar.getKarma());
		
		int _runSpd = _activeChar.getStat().getBaseRunSpeed();
		int _walkSpd = _activeChar.getStat().getBaseWalkSpeed();
		int _swimSpd = _activeChar.getStat().getBaseSwimSpeed();
		writeD(_runSpd); // base run speed
		writeD(_walkSpd); // base walk speed
		writeD(_swimSpd); // swim run speed
		writeD(_swimSpd); // swim walk speed
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_activeChar.isFlying() ? _runSpd : 0); // fly run speed
		writeD(_activeChar.isFlying() ? _walkSpd : 0); // fly walk speed
		writeF(_activeChar.getStat().getMovementSpeedMultiplier()); // run speed multiplier
		writeF(_activeChar.getStat().getAttackSpeedMultiplier()); // attack speed multiplier
		
		if (_activeChar.getMountType() != 0)
		{
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionRadius());
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionHeight());
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
		
		if (gmSeeInvis)
		{
			writeS("Invisible");
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
		
		else if (_activeChar.isSellBuff())
		{
			writeS(Config.NEW_TITLE_SELLBUFF);
		}
		else if (Config.ENABLE_NEW_TITLE_AUTOFARM && _activeChar.isAutoFarm())
		{
			writeS(Config.NEW_TITLE_AUTOFARM);
		}
		else if (_activeChar.isInDMEvent())
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
		else
			writeS(_activeChar.getTitle());
		
		if (Config.BLOCK_CREST_PVPEVENT && _activeChar.isInsideZone(ZoneId.PVP_CUSTOM) || _activeChar.isInLMEvent() || _activeChar.isInFOSEvent() || _activeChar.isInTVTEvent() || _activeChar.isInCTFEvent())
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		else
		{
			writeD(_activeChar.getClanId());
			writeD(_activeChar.getClanCrestId());
			writeD(_activeChar.getAllyId());
			writeD(_activeChar.getAllyCrestId());
		}
		
		writeD(0);
		
		writeC(_activeChar.isSitting() ? 0 : 1); // standing = 1 sitting = 0
		writeC(_activeChar.isRunning() ? 1 : 0); // running = 1 walking = 0
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		
		if (gmSeeInvis)
			writeC(0);
		else
			writeC(_activeChar.getAppearance().getInvisible() ? 1 : 0); // invisible = 1 visible =0
			
		writeC(_activeChar.getMountType()); // 1 on strider 2 on wyvern 0 no mount
		writeC(_activeChar.getStoreType().getId()); // 1 - sellshop
		
		writeH(_activeChar.getCubics().size());
		for (int id : _activeChar.getCubics().keySet())
			writeH(id);
		
		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);
		
		if (gmSeeInvis)
			writeD((_activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()));
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
		
		writeC(_activeChar.getRecomLeft());
		writeH(_activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeD(_activeChar.getClassId().getId());
		
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		if (player.isDisableGlowWeapon())
			writeC(0);
		else
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
		writeC(_activeChar.isNoble() ? 1 : 0); // Symbol on char menu ctrl+I
		// if (_activeChar.isHero() && (((TvT.is_started() || TvT.is_teleport()) && _activeChar._inEventTvT) || ((CTF.is_started() || CTF.is_teleport()) && _activeChar._inEventCTF)))
		// {
		// writeC(0x00);
		// }
		// else
		// {
		writeC((_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA)) ? 1 : 0); // Hero Aura
		// }
		
		writeC(_activeChar.isFishing() ? 1 : 0); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
		
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
		else if (_activeChar.isInCTFEvent())
		{
			byte playerTeamId = CTFEvent.getParticipantTeamId(_activeChar.getObjectId());
			
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
		else if (_activeChar.isAio() && Config.ALLOW_AIO_NCOLOR)
		{
			writeD(Config.AIO_NCOLOR);
		}
		else
		{
			writeD(_activeChar.getAppearance().getNameColor());
		}
		
		writeD(0x00); // isRunning() as in UserInfo?
		
		writeD(_activeChar.getPledgeClass());
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