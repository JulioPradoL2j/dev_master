package net.sf.l2j.gameserver.network.serverpackets;

import java.text.DecimalFormat;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.model.FakePc;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object.PolyType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

public abstract class AbstractNpcInfo extends L2GameServerPacket
{
	protected int _x, _y, _z, _heading;
	protected int _idTemplate;
	protected boolean _isAttackable, _isSummoned;
	protected int _mAtkSpd, _pAtkSpd;
	protected int _runSpd, _walkSpd;
	protected int _rhand, _lhand, _chest, _enchantEffect;
	protected double _collisionHeight, _collisionRadius;
	protected int _clanCrest, _allyCrest, _allyId, _clanId;
	
	protected String _name = "", _title = "";
	
	public AbstractNpcInfo(Creature cha)
	{
		_isSummoned = cha.isShowSummonAnimation();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_heading = cha.getHeading();
		_mAtkSpd = cha.getMAtkSpd();
		_pAtkSpd = cha.getPAtkSpd();
		_runSpd = cha.getStat().getBaseRunSpeed();
		_walkSpd = cha.getStat().getBaseWalkSpeed();
	}
	
	/**
	 * Packet for Npcs
	 */
	public static class NpcInfo extends AbstractNpcInfo
	{
		private final L2Npc _npc;
		
		public NpcInfo(L2Npc cha, Creature attacker)
		{
			super(cha);
			_npc = cha;
			
			_enchantEffect = _npc.getEnchantEffect();
			_isAttackable = _npc.isAutoAttackable(attacker);
			
			// Support for polymorph.
			if (_npc.getPolyType() == PolyType.NPC)
			{
				_idTemplate = _npc.getPolyTemplate().getIdTemplate();
				_rhand = _npc.getPolyTemplate().getRightHand();
				_lhand = _npc.getPolyTemplate().getLeftHand();
				_collisionHeight = _npc.getPolyTemplate().getCollisionHeight();
				_collisionRadius = _npc.getPolyTemplate().getCollisionRadius();
			}
			else
			{
				_idTemplate = _npc.getTemplate().getIdTemplate();
				_rhand = _npc.getRightHandItem();
				_lhand = _npc.getLeftHandItem();
				_collisionHeight = _npc.getCollisionHeight();
				_collisionRadius = _npc.getCollisionRadius();
			}
			
			// NAME: se não for server-side, usa o do template (evita null/"" e garante consistência)
			_name = _npc.getTemplate().isUsingServerSideName() ? _npc.getName() : _npc.getTemplate().getName();
			
			_title = _npc.getTemplate().isUsingServerSideTitle() ? _npc.getTitle() : _npc.getTemplate().getTitle();
			if (_title == null)
				_title = "";
			
			if (Config.SHOW_NPC_LVL && _npc instanceof L2MonsterInstance)
				_title = "Lv " + _npc.getLevel() + (_npc.getTemplate().getAggroRange() > 0 ? "* " : " ") + _title;
			
			// NPC crest system
			if (Config.SHOW_NPC_CREST && _npc.getCastle() != null && _npc.getCastle().getOwnerId() != 0)
			{
				L2Clan clan = ClanTable.getInstance().getClan(_npc.getCastle().getOwnerId());
				_clanCrest = clan.getCrestId();
				_clanId = clan.getClanId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
			}
		}
		
		@Override
		protected void writeImpl()
		{
			FakePc fpc = _npc.getFakePc();
			if (fpc != null)
			{
				writeC(0x03);
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				writeD(_npc.getObjectId());
				writeS(fpc.name); // visible name
				writeD(fpc.race);
				writeD(fpc.sex);
				writeD(fpc.classId);
				
				writeD(0x00); // PAPERDOLL_HAIR
				writeD(0x00); //
				writeD(fpc.rightHand); // PAPERDOLL_RHAND
				writeD(fpc.leftHand); // PAPERDOLL_LHAND
				writeD(fpc.gloves); // PAPERDOLL_GLOVES
				writeD(fpc.chest); // PAPERDOLL_CHEST
				writeD(fpc.legs); // PAPERDOLL_LEGS
				writeD(fpc.feet); // PAPERDOLL_FEET
				writeD(fpc.hair); // PAPERDOLL_HAIR
				writeD(fpc.rightHand); // PAPERDOLL_RHAND
				writeD(fpc.hair); // PAPERDOLL_HAIR
				writeD(fpc.hair2); // PAPERDOLL_HAIR2
				
				write('H', 0, 24);
				
				writeD(0x00);
				writeD(0x00);
				
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				
				writeD(0x00);
				writeD(0x00);
				
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd); // swim run speed
				writeD(_walkSpd); // swim walk speed
				writeD(_runSpd); // fly run speed
				writeD(_walkSpd); // fly walk speed
				writeD(_runSpd);
				writeD(_walkSpd);
				writeF(_npc.getStat().getMovementSpeedMultiplier()); // _activeChar.getProperMultiplier()
				writeF(_npc.getStat().getAttackSpeedMultiplier()); // _activeChar.getAttackSpeedMultiplier()
				
				writeF(fpc.radius);
				writeF(fpc.height);
				
				writeD(fpc.hairStyle);
				writeD(fpc.hairColor);
				writeD(fpc.face);
				
				if (_npc instanceof L2MonsterInstance)
					writeS(fpc.title + " - HP " + new DecimalFormat("#.##").format(100.0 * _npc.getCurrentHp() / _npc.getMaxHp()) + "%"); // visible title
				else
					writeS(fpc.title);
				
				writeD(fpc.clanId); // clan id
				writeD(fpc.clanCrest); // clan crest id
				writeD(fpc.allyId); // ally id
				writeD(fpc.allyCrest); // ally crest id
				
				writeD(0x00);
				writeC(0x01); // standing = 1 sitting = 0
				
				writeC(_npc.isRunning() ? 1 : 0); // running = 1 walking = 0
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				
				write('C', 0, 3);
				
				writeH(0x00); // cubic count
				
				writeC(0x00); // invisible = 1 visible =0
				writeD(0x00); // abnormal effect
				// writeD(0x800000); // abnormal effect original //Aura azul em volta NPC
				writeC(0x00); // isFlying() ? 2 : 0
				writeH(0x00); // getRecomHave(): Blue value for name (0 = white, 255 = pure blue)
				
				writeD(fpc.classId);
				writeD(0x00); // ?
				writeD(0x00); // ?
				writeC(fpc.enchant);
				writeC(0x00); // team circle around feet 1= Blue, 2 = red
				
				writeD(0x00); // getClanCrestLargeId()
				
				writeC(0x00); // isNoble(): Symbol on char menu ctrl+I
				writeC(fpc.hero); // Hero Aura
				
				writeC(0x00); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
				write('D', 0, 3);
				
				writeD(fpc.nameColor);
				writeD(_heading);
				writeD(0x00); // pledge class
				writeD(0x00); // pledge type
				writeD(fpc.titleColor);
				writeD(0x00); // cursed weapon level
			}
			else
			{
				
				writeC(0x16);
				
				writeD(_npc.getObjectId());
				writeD(_idTemplate + 1000000);
				writeD(_isAttackable ? 1 : 0);
				
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				
				writeD(0x00);
				
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				
				writeF(_npc.getStat().getMovementSpeedMultiplier());
				writeF(_npc.getStat().getAttackSpeedMultiplier());
				
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_rhand);
				writeD(_chest);
				writeD(_lhand);
				
				writeC(1); // name above char
				writeC(_npc.isRunning() ? 1 : 0);
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				writeC(_isSummoned ? 2 : 0);
				
				writeS(_name);
				writeS(_title);
				
				if (Config.SHOW_RED_NAME_CHAMPION)
				{
					writeD(0);
					writeD(0);
					writeD(_npc.isChampion() || _npc.isChampionInvade() && Config.RED_NAME_CHAMPION_INVADE ? 0x9999 : 0x00);
				}
				else
				{
					writeD(0x00);
					writeD(0x00);
					writeD(0x00);
				}
				
				writeD(_npc.getAbnormalEffect());
				
				writeD(_clanId);
				writeD(_clanCrest);
				writeD(_allyId);
				writeD(_allyCrest);
				
				writeC(_npc.isFlying() ? 2 : 0);
				writeC(0x00);
				
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_enchantEffect);
				writeD(_npc.isFlying() ? 1 : 0);
			}
		}
		
		private void write(char type, int value, int times)
		{
			for (int i = 0; i < times; i++)
			{
				switch (type)
				{
					case 'C':
						writeC(value);
						break;
					case 'D':
						writeD(value);
						break;
					case 'F':
						writeF(value);
						break;
					case 'H':
						writeH(value);
						break;
				}
			}
		}
	}
	
	/**
	 * Packet for summons
	 */
	public static class SummonInfo extends AbstractNpcInfo
	{
		private final L2Summon _summon;
		private final Player _owner;
		private int _summonAnimation = 0;
		
		public SummonInfo(L2Summon cha, Player attacker, int val)
		{
			super(cha);
			_summon = cha;
			_owner = _summon.getOwner();
			
			_summonAnimation = val;
			if (_summon.isShowSummonAnimation())
				_summonAnimation = 2; // override for spawn
				
			_isAttackable = _summon.isAutoAttackable(attacker);
			_rhand = _summon.getWeapon();
			_lhand = 0;
			_chest = _summon.getArmor();
			_enchantEffect = _summon.getTemplate().getEnchantEffect();
			_title = (_owner == null || !_owner.isOnline()) ? "" : _owner.getName();
			_idTemplate = _summon.getTemplate().getIdTemplate();
			
			_collisionHeight = _summon.getCollisionHeight();
			_collisionRadius = _summon.getCollisionRadius();
			
			// NPC crest system
			if (Config.SHOW_SUMMON_CREST && _owner != null && _owner.getClan() != null)
			{
				L2Clan clan = ClanTable.getInstance().getClan(_owner.getClanId());
				_clanCrest = clan.getCrestId();
				_clanId = clan.getClanId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
			}
		}
		
		@Override
		protected void writeImpl()
		{
			if (_owner != null && _owner.getAppearance().getInvisible())
				return;
			
			writeC(0x16);
			
			writeD(_summon.getObjectId());
			writeD(_idTemplate + 1000000);
			writeD(_isAttackable ? 1 : 0);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_summon.getStat().getMovementSpeedMultiplier());
			writeF(_summon.getStat().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(_chest);
			writeD(_lhand);
			
			writeC(1); // name above char
			writeC(_summon.isRunning() ? 1 : 0);
			writeC(_summon.isInCombat() ? 1 : 0);
			writeC(_summon.isAlikeDead() ? 1 : 0);
			writeC(_summonAnimation);
			
			writeS(_name);
			writeS(_title);
			
			writeD(_summon instanceof L2PetInstance ? 0x00 : 0x01);
			writeD(_summon.getPvpFlag());
			writeD(_summon.getKarma());
			
			writeD(_summon.getAbnormalEffect());
			
			writeD(_clanId);
			writeD(_clanCrest);
			writeD(_allyId);
			writeD(_allyCrest);
			
			writeC(0x00);
			writeC(_summon.getTeam());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
	
	/**
	 * Packet for morphed PCs
	 */
	public static class PcMorphInfo extends AbstractNpcInfo
	{
		private final Player _pc;
		private final NpcTemplate _template;
		private final int _swimSpd;
		
		public PcMorphInfo(Player cha, NpcTemplate template)
		{
			super(cha);
			_pc = cha;
			_template = template;
			
			_swimSpd = cha.getStat().getBaseSwimSpeed();
			
			_rhand = _template.getRightHand();
			_lhand = _template.getLeftHand();
			
			_collisionHeight = _template.getCollisionHeight();
			_collisionRadius = _template.getCollisionRadius();
			
			_enchantEffect = _template.getEnchantEffect();
		}
		
		@Override
		protected void writeImpl()
		{
			writeC(0x16);
			
			writeD(_pc.getObjectId());
			writeD(_pc.getPolyId() + 1000000);
			writeD(1);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_swimSpd);
			writeD(_swimSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_pc.getStat().getMovementSpeedMultiplier());
			writeF(_pc.getStat().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(0);
			writeD(_lhand);
			
			writeC(1); // name above char
			writeC(_pc.isRunning() ? 1 : 0);
			writeC(_pc.isInCombat() ? 1 : 0);
			writeC(_pc.isAlikeDead() ? 1 : 0);
			writeC(0); // 0 = teleported, 1 = default, 2 = summoned
			
			writeS(_name);
			writeS(_title);
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeD(_pc.getAbnormalEffect());
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeC(0x00);
			writeC(0x00);
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
	
}