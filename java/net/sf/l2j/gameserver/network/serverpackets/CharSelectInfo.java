package net.sf.l2j.gameserver.network.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.sf.l2j.gameserver.ConnectionPool;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.xml.DressMeData;
import net.sf.l2j.gameserver.model.CharSelectInfoPackage;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.OfflineMemos;

public class CharSelectInfo extends L2GameServerPacket
{
	private final String _loginName;
	private final int _sessionId;
	private int _activeId;
	private final CharSelectInfoPackage[] _characterPackages;
	
	public CharSelectInfo(String loginName, int sessionId)
	{
		_sessionId = sessionId;
		_loginName = loginName;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_activeId = -1;
	}
	
	public CharSelectInfo(String loginName, int sessionId, int activeId)
	{
		_sessionId = sessionId;
		_loginName = loginName;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_activeId = activeId;
	}
	
	public CharSelectInfoPackage[] getCharInfo()
	{
		return _characterPackages;
	}
	
	@Override
	protected final void writeImpl()
	{
		int size = (_characterPackages.length);
		
		writeC(0x13);
		writeD(size);
		
		long lastAccess = 0L;
		
		if (_activeId == -1)
		{
			for (int i = 0; i < size; i++)
				if (lastAccess < _characterPackages[i].getLastAccess())
				{
					lastAccess = _characterPackages[i].getLastAccess();
					_activeId = i;
				}
		}
		
		for (int i = 0; i < size; i++)
		{
			CharSelectInfoPackage charInfoPackage = _characterPackages[i];
			final int fakeWeaponObjectId = charInfoPackage.getFakeWeaponObjectId();
			final int fakeWeaponItemId = charInfoPackage.getFakeWeaponItemId();
			applyDressMeVisualsToPaperdoll(charInfoPackage);
			
			writeS(charInfoPackage.getName());
			writeD(charInfoPackage.getCharId());
			writeS(_loginName);
			writeD(_sessionId);
			writeD(charInfoPackage.getClanId());
			writeD(0x00); // ??
			
			writeD(charInfoPackage.getSex());
			writeD(charInfoPackage.getRace());
			
			if (charInfoPackage.getClassId() == charInfoPackage.getBaseClassId())
				writeD(charInfoPackage.getClassId());
			else
				writeD(charInfoPackage.getBaseClassId());
			
			writeD(0x01); // active ??
			
			writeD(charInfoPackage.getX());
			writeD(charInfoPackage.getY());
			writeD(charInfoPackage.getZ());
			
			writeF(charInfoPackage.getCurrentHp());
			writeF(charInfoPackage.getCurrentMp());
			
			writeD(charInfoPackage.getSp());
			writeQ(charInfoPackage.getExp());
			writeD(charInfoPackage.getLevel());
			
			writeD(charInfoPackage.getKarma());
			writeD(charInfoPackage.getPkKills());
			
			writeD(charInfoPackage.getPvPKills());
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_HAIRALL));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
			// writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(fakeWeaponObjectId == 0 ? charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND) : fakeWeaponObjectId);
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_CHEST));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_LEGS));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_FEET));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_BACK));
			// writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(fakeWeaponObjectId == 0 ? charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND) : fakeWeaponObjectId);
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
			writeD(charInfoPackage.getPaperdollObjectId(Inventory.PAPERDOLL_FACE));
			
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_REAR));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_NECK));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
			// writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(fakeWeaponItemId == 0 ? charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : fakeWeaponItemId);
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			// writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(fakeWeaponItemId == 0 ? charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : fakeWeaponItemId);
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
			
			writeD(charInfoPackage.getHairStyle());
			writeD(charInfoPackage.getHairColor());
			writeD(charInfoPackage.getFace());
			
			writeF(charInfoPackage.getMaxHp());
			writeF(charInfoPackage.getMaxMp());
			
			writeD((charInfoPackage.getAccessLevel() > -100) ? ((charInfoPackage.getDeleteTimer() > 0) ? (int) ((charInfoPackage.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0) : -1);
			writeD(charInfoPackage.getClassId());
			writeD((i == _activeId) ? 0x01 : 0x00);
			writeC((charInfoPackage.getEnchantEffect() > 127) ? 127 : charInfoPackage.getEnchantEffect());
			writeD(charInfoPackage.getAugmentationId());
		}
	}
	
	private static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName)
	{
		CharSelectInfoPackage charInfopackage;
		List<CharSelectInfoPackage> characterList = new ArrayList<>();
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT account_name, obj_Id, char_name, level, maxHp, curHp, maxMp, curMp, face, hairStyle, hairColor, sex, heading, x, y, z, exp, sp, karma, pvpkills, pkkills, clanid, race, classid, deletetime, cancraft, title, accesslevel, online, char_slot, lastAccess, base_class,fakeWeaponObjectId, fakeWeaponItemId FROM characters WHERE account_name=?");
			statement.setString(1, loginName);
			ResultSet charList = statement.executeQuery();
			
			while (charList.next())// fills the package
			{
				charInfopackage = restoreChar(charList);
				if (charInfopackage != null)
					characterList.add(charInfopackage);
			}
			
			charList.close();
			statement.close();
			
			return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not restore char info: " + e.getMessage(), e);
		}
		
		return new CharSelectInfoPackage[0];
	}
	
	private static void loadCharacterSubclassInfo(CharSelectInfoPackage charInfopackage, int ObjectId, int activeClassId)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT exp, sp, level FROM character_subclasses WHERE char_obj_id=? && class_id=? ORDER BY char_obj_id");
			statement.setInt(1, ObjectId);
			statement.setInt(2, activeClassId);
			ResultSet charList = statement.executeQuery();
			
			if (charList.next())
			{
				charInfopackage.setExp(charList.getLong("exp"));
				charInfopackage.setSp(charList.getInt("sp"));
				charInfopackage.setLevel(charList.getInt("level"));
			}
			
			charList.close();
			statement.close();
			
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not restore char subclass info: " + e.getMessage(), e);
		}
	}
	
	private static CharSelectInfoPackage restoreChar(ResultSet chardata) throws Exception
	{
		int objectId = chardata.getInt("obj_id");
		String name = chardata.getString("char_name");
		
		// See if the char must be deleted
		long deletetime = chardata.getLong("deletetime");
		if (deletetime > 0)
		{
			if (System.currentTimeMillis() > deletetime)
			{
				L2Clan clan = ClanTable.getInstance().getClan(chardata.getInt("clanid"));
				if (clan != null)
					clan.removeClanMember(objectId, 0);
				
				L2GameClient.deleteCharByObjId(objectId);
				return null;
			}
		}
		
		CharSelectInfoPackage charInfopackage = new CharSelectInfoPackage(objectId, name);
		charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));
		charInfopackage.setLevel(chardata.getInt("level"));
		charInfopackage.setMaxHp(chardata.getInt("maxhp"));
		charInfopackage.setCurrentHp(chardata.getDouble("curhp"));
		charInfopackage.setMaxMp(chardata.getInt("maxmp"));
		charInfopackage.setCurrentMp(chardata.getDouble("curmp"));
		charInfopackage.setKarma(chardata.getInt("karma"));
		charInfopackage.setPkKills(chardata.getInt("pkkills"));
		charInfopackage.setPvPKills(chardata.getInt("pvpkills"));
		charInfopackage.setFace(chardata.getInt("face"));
		charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
		charInfopackage.setHairColor(chardata.getInt("haircolor"));
		charInfopackage.setSex(chardata.getInt("sex"));
		
		charInfopackage.setExp(chardata.getLong("exp"));
		charInfopackage.setSp(chardata.getInt("sp"));
		charInfopackage.setClanId(chardata.getInt("clanid"));
		
		charInfopackage.setRace(chardata.getInt("race"));
		
		final int baseClassId = chardata.getInt("base_class");
		final int activeClassId = chardata.getInt("classid");
		
		charInfopackage.setX(chardata.getInt("x"));
		charInfopackage.setY(chardata.getInt("y"));
		charInfopackage.setZ(chardata.getInt("z"));
		charInfopackage.setFakeWeaponObjectId(chardata.getInt("fakeWeaponObjectId"));
		charInfopackage.setFakeWeaponItemId(chardata.getInt("fakeWeaponItemId"));
		// if is in subclass, load subclass exp, sp, lvl info
		if (baseClassId != activeClassId)
			loadCharacterSubclassInfo(charInfopackage, objectId, activeClassId);
		
		charInfopackage.setClassId(activeClassId);
		
		// Get the augmentation id for equipped weapon
		int weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
		if (weaponObjId < 1)
			weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
		
		if (weaponObjId > 0)
		{
			try (Connection con = ConnectionPool.getConnection())
			{
				PreparedStatement statement = con.prepareStatement("SELECT attributes FROM augmentations WHERE item_id=?");
				statement.setInt(1, weaponObjId);
				ResultSet result = statement.executeQuery();
				
				if (result.next())
				{
					int augment = result.getInt("attributes");
					charInfopackage.setAugmentationId(augment == -1 ? 0 : augment);
				}
				
				result.close();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Could not restore augmentation info: " + e.getMessage(), e);
			}
		}
		
		/*
		 * Check if the base class is set to zero and alse doesn't match with the current active class, otherwise send the base class ID. This prevents chars created before base class was introduced from being displayed incorrectly.
		 */
		if (baseClassId == 0 && activeClassId > 0)
			charInfopackage.setBaseClassId(activeClassId);
		else
			charInfopackage.setBaseClassId(baseClassId);
		
		charInfopackage.setDeleteTimer(deletetime);
		charInfopackage.setLastAccess(chardata.getLong("lastAccess"));
		
		return charInfopackage;
	}
	
	private static void applyDressMeVisualsToPaperdoll(CharSelectInfoPackage pkg)
	{
		final int objectId = pkg.getObjectId(); // o obj_Id do DB
		
		// Toggle
		final boolean hairOff = OfflineMemos.getInstance().getInt(objectId, "dressme.hairOff", 0) == 1;
		
		// ARMOR
		final int armorSrc = OfflineMemos.getInstance().getInt(objectId, "dressme.armor.src", 0);
		final int armorId = OfflineMemos.getInstance().getInt(objectId, "dressme.armor.id", 0);
		
		DressMeHolder armor = resolveOfflineDress(armorSrc, armorId);
		
		// fallback (migração/ids mistos)
		if (armor == null && armorId > 0)
		{
			armor = DressMeData.getInstance().getByItemId(armorId);
			if (armor == null)
				armor = DressMeData.getInstance().getBySkillId(armorId);
			if (armor == null)
				armor = DressMeData.getInstance().getByActionId(armorId);
		}
		
		if (armor != null)
		{
			pkg.setPaperdollVisual(Inventory.PAPERDOLL_GLOVES, armor.getGlovesId());
			pkg.setPaperdollVisual(Inventory.PAPERDOLL_CHEST, armor.getChestId());
			pkg.setPaperdollVisual(Inventory.PAPERDOLL_LEGS, armor.getLegsId());
			pkg.setPaperdollVisual(Inventory.PAPERDOLL_FEET, armor.getFeetId());
			
			// Coroa / acessório → SOMENTE HAIRALL
			if (!hairOff && armor.getHelmetId() > 0)
			{
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_HAIRALL, armor.getHelmetId());
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_HAIR, armor.getHelmetId());
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_FACE, armor.getHelmetId());
			}
			else
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_HAIRALL, 0);
		}
		
		// WEAPON
		final int wepSrc = OfflineMemos.getInstance().getInt(objectId, "dressme.weapon.src", 0);
		final int wepId = OfflineMemos.getInstance().getInt(objectId, "dressme.weapon.id", 0);
		
		DressMeHolder weapon = resolveOfflineDress(wepSrc, wepId);
		
		if (weapon == null && wepId > 0)
		{
			weapon = DressMeData.getInstance().getByItemId(wepId);
			if (weapon == null)
				weapon = DressMeData.getInstance().getBySkillId(wepId);
			if (weapon == null)
				weapon = DressMeData.getInstance().getByActionId(wepId);
		}
		
		if (weapon != null)
		{
			final int twoHand = weapon.getTwoHandId();
			
			if (twoHand > 0)
			{
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_RHAND, twoHand);
				pkg.setPaperdollVisual(Inventory.PAPERDOLL_LHAND, 0); // 2H regra
			}
			else
			{
				if (weapon.getRightHandId() > 0)
					pkg.setPaperdollVisual(Inventory.PAPERDOLL_RHAND, weapon.getRightHandId());
				if (weapon.getLeftHandId() > 0)
					pkg.setPaperdollVisual(Inventory.PAPERDOLL_LHAND, weapon.getLeftHandId());
			}
		}
	}
	
	private static DressMeHolder resolveOfflineDress(int src, int id)
	{
		if (id <= 0)
			return null;
		
		switch (src)
		{
			case 1: // SKILL
				return DressMeData.getInstance().getBySkillId(id);
			
			case 2: // ITEM
				return DressMeData.getInstance().getByItemId(id);
			
			case 3: // ACTION
				return DressMeData.getInstance().getByActionId(id);
			
			default:
			{
				// Fallback seguro (migração / configs antigas)
				DressMeHolder d = DressMeData.getInstance().getByItemId(id);
				if (d == null)
					d = DressMeData.getInstance().getBySkillId(id);
				if (d == null)
					d = DressMeData.getInstance().getByActionId(id);
				return d;
			}
		}
	}
	
}