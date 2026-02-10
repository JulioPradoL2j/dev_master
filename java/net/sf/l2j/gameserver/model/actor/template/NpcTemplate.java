package net.sf.l2j.gameserver.model.actor.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.datatables.xml.HerbDropData;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.MinionData;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.scriptings.EventType;
import net.sf.l2j.gameserver.scriptings.Quest;
import net.sf.l2j.gameserver.templates.StatsSet;

public class NpcTemplate extends CharTemplate
{
	public static enum AIType
	{
		DEFAULT,
		ARCHER,
		MAGE,
		HEALER,
		CORPSE
	}
	
	public static enum Race
	{
		UNKNOWN,
		UNDEAD,
		MAGICCREATURE,
		BEAST,
		ANIMAL,
		PLANT,
		HUMANOID,
		SPIRIT,
		ANGEL,
		DEMON,
		DRAGON,
		GIANT,
		BUG,
		FAIRIE,
		HUMAN,
		ELVE,
		DARKELVE,
		ORC,
		DWARVE,
		OTHER,
		NONLIVING,
		SIEGEWEAPON,
		DEFENDINGARMY,
		MERCENARIE;
		
		public static final Race[] VALUES = values();
	}
	
	protected static final Logger _log = Logger.getLogger(NpcTemplate.class.getName());
	
	private final int _npcId;
	private final int _idTemplate;
	private final String _type;
	private boolean _usingServerSideName;
	private final String _name;
	private boolean _usingServerSideTitle;
	private final String _title;
	private final boolean _cantBeChampionMonster;
	private final byte _level;
	private final int _exp;
	private final int _sp;
	private final int _rHand;
	private final int _lHand;
	private final int _enchantEffect;
	private final int _corpseTime;
	
	private int _dropHerbGroup;
	private Race _race = Race.UNKNOWN;
	private AIType _aiType;
	
	private final int _ssCount;
	private final int _ssRate;
	private final int _spsCount;
	private final int _spsRate;
	private final int _aggroRange;
	
	private String[] _clans;
	private int _clanRange;
	private int[] _ignoredIds;
	
	private final boolean _canMove;
	private final boolean _isSeedable;
	
	private final List<L2Skill> _buffSkills = new ArrayList<>();
	private final List<L2Skill> _debuffSkills = new ArrayList<>();
	private final List<L2Skill> _healSkills = new ArrayList<>();
	private final List<L2Skill> _longRangeSkills = new ArrayList<>();
	private final List<L2Skill> _shortRangeSkills = new ArrayList<>();
	private final List<L2Skill> _suicideSkills = new ArrayList<>();
	
	private List<DropCategory> _categories;
	private List<MinionData> _minions;
	private final List<ClassId> _teachInfo = new ArrayList<>();
	private final Map<Integer, L2Skill> _skills = new LinkedHashMap<>();
	private final Map<EventType, List<Quest>> _questEvents = new HashMap<>();
	
	public NpcTemplate(StatsSet set)
	{
		super(set);
		
		_npcId = set.getInteger("id");
		_idTemplate = set.getInteger("idTemplate", _npcId);
		_type = set.getString("type");
		_name = set.getString("name");
		_usingServerSideName = set.getBool("usingServerSideName", false);
		_title = set.getString("title", "");
		_usingServerSideTitle = set.getBool("usingServerSideTitle", false);
		_cantBeChampionMonster = _title.equalsIgnoreCase("Quest Monster") || isType("Chest");
		_level = set.getByte("level", (byte) 1);
		_exp = set.getInteger("exp", 0);
		_sp = set.getInteger("sp", 0);
		_rHand = set.getInteger("rHand", 0);
		_lHand = set.getInteger("lHand", 0);
		_enchantEffect = set.getInteger("enchant", 0);
		_corpseTime = set.getInteger("corpseTime", 7);
		
		_dropHerbGroup = set.getInteger("dropHerbGroup", 0);
		if (_dropHerbGroup > 0 && HerbDropData.getInstance().getGroup(_dropHerbGroup) == null)
		{
			if (HerbDropData.getInstance().getOverride(_npcId) == null)
			{
				_log.warning("Missing dropHerbGroup information for npcId: " + _npcId + ", dropHerbGroup: " + _dropHerbGroup);
				_dropHerbGroup = 0;
			}
		}
		
		if (set.containsKey("raceId"))
			setRace(set.getInteger("raceId"));
		
		_aiType = set.getEnum("aiType", AIType.class, AIType.DEFAULT);
		
		_ssCount = set.getInteger("ssCount", 0);
		_ssRate = set.getInteger("ssRate", 0);
		_spsCount = set.getInteger("spsCount", 0);
		_spsRate = set.getInteger("spsRate", 0);
		_aggroRange = set.getInteger("aggro", 0);
		
		if (set.containsKey("clan"))
		{
			_clans = set.getStringArray("clan");
			_clanRange = set.getInteger("clanRange");
			
			if (set.containsKey("ignoredIds"))
				_ignoredIds = set.getIntegerArray("ignoredIds");
		}
		
		_canMove = set.getBool("canMove", true);
		_isSeedable = set.getBool("seedable", false);
		
		_categories = set.getList("drops");
		_minions = set.getList("minions");
		
		if (set.containsKey("teachTo"))
		{
			for (int classId : set.getIntegerArray("teachTo"))
				addTeachInfo(ClassId.VALUES[classId]);
		}
		
		addSkills(set.getList("skills"));
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
	
	public int getIdTemplate()
	{
		return _idTemplate;
	}
	
	public boolean isCustomNpc()
	{
		return _npcId != _idTemplate;
	}
	
	public String getType()
	{
		return _type;
	}
	
	/**
	 * Checks types, ignore case.
	 * @param t the type to check.
	 * @return true if the type are the same, false otherwise.
	 */
	public boolean isType(String t)
	{
		return _type.equalsIgnoreCase(t);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isUsingServerSideName()
	{
		return _usingServerSideName;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public boolean isUsingServerSideTitle()
	{
		return _usingServerSideTitle;
	}
	
	public boolean cantBeChampion()
	{
		return _cantBeChampionMonster;
	}
	
	public byte getLevel()
	{
		return _level;
	}
	
	public int getRewardExp()
	{
		return _exp;
	}
	
	public int getRewardSp()
	{
		return _sp;
	}
	
	public int getRightHand()
	{
		return _rHand;
	}
	
	public int getLeftHand()
	{
		return _lHand;
	}
	
	public int getEnchantEffect()
	{
		return _enchantEffect;
	}
	
	public int getCorpseTime()
	{
		return _corpseTime;
	}
	
	public int getDropHerbGroup()
	{
		return _dropHerbGroup;
	}
	
	public Race getRace()
	{
		return _race;
	}
	
	public void setRace(int raceId)
	{
		// Race.UNKNOWN is already the default value. No needs to handle it.
		if (raceId < 1 || raceId > 23)
			return;
		
		_race = Race.VALUES[raceId];
	}
	
	public AIType getAiType()
	{
		return _aiType;
	}
	
	public int getSsCount()
	{
		return _ssCount;
	}
	
	public int getSsRate()
	{
		return _ssRate;
	}
	
	public int getSpsCount()
	{
		return _spsCount;
	}
	
	public int getSpsRate()
	{
		return _spsRate;
	}
	
	public int getAggroRange()
	{
		return _aggroRange;
	}
	
	public String[] getClans()
	{
		return _clans;
	}
	
	public int getClanRange()
	{
		return _clanRange;
	}
	
	public int[] getIgnoredIds()
	{
		return _ignoredIds;
	}
	
	public boolean canMove()
	{
		return _canMove;
	}
	
	public boolean isSeedable()
	{
		return _isSeedable;
	}
	
	public void addShortOrLongRangeSkill(L2Skill skill)
	{
		if (skill.getCastRange() > 150)
			_longRangeSkills.add(skill);
		else if (skill.getCastRange() > 0)
			_shortRangeSkills.add(skill);
	}
	
	public List<L2Skill> getSuicideSkills()
	{
		return _suicideSkills;
	}
	
	public List<L2Skill> getHealSkills()
	{
		return _healSkills;
	}
	
	public List<L2Skill> getDebuffSkills()
	{
		return _debuffSkills;
	}
	
	public List<L2Skill> getBuffSkills()
	{
		return _buffSkills;
	}
	
	public List<L2Skill> getLongRangeSkills()
	{
		return _longRangeSkills;
	}
	
	public List<L2Skill> getShortRangeSkills()
	{
		return _shortRangeSkills;
	}
	
	/**
	 * @return the list of all possible UNCATEGORIZED drops of this L2NpcTemplate.
	 */
	public List<DropCategory> getDropData()
	{
		return _categories;
	}
	
	/**
	 * @return the list of all possible item drops of this L2NpcTemplate. (ie full drops and part drops, mats, miscellaneous & UNCATEGORIZED)
	 */
	public List<DropData> getAllDropData()
	{
		final List<DropData> list = new ArrayList<>();
		for (DropCategory tmp : _categories)
			list.addAll(tmp.getAllDrops());
		
		return list;
	}
	
	/**
	 * Add a drop to a given category. If the category does not exist, create it.
	 * @param drop
	 * @param categoryType
	 */
	public void addDropData(DropData drop, int categoryType)
	{
		synchronized (_categories)
		{
			// Category exists, stores the drop and return.
			for (DropCategory cat : _categories)
			{
				if (cat.getCategoryType() == categoryType)
				{
					cat.addDropData(drop, isType("L2RaidBoss") || isType("L2GrandBoss"));
					return;
				}
			}
			
			// Category doesn't exist, create and store it.
			final DropCategory cat = new DropCategory(categoryType);
			cat.addDropData(drop, isType("L2RaidBoss") || isType("L2GrandBoss"));
			
			_categories.add(cat);
		}
	}
	
	/**
	 * @return the list of all Minions that must be spawn with the L2Npc using this L2NpcTemplate.
	 */
	public List<MinionData> getMinionData()
	{
		return _minions;
	}
	
	public List<ClassId> getTeachInfo()
	{
		return _teachInfo;
	}
	
	public void addTeachInfo(ClassId classId)
	{
		_teachInfo.add(classId);
	}
	
	public boolean canTeach(ClassId classId)
	{
		return _teachInfo.contains((classId.level() == 3) ? classId.getParent() : classId);
	}
	
	public Map<Integer, L2Skill> getSkills()
	{
		return _skills;
	}
	
	public void addSkills(List<L2Skill> skills)
	{
		for (L2Skill skill : skills)
		{
			if (!skill.isPassive())
			{
				if (skill.isSuicideAttack())
					_suicideSkills.add(skill);
				else
				{
					switch (skill.getSkillType())
					{
						case BUFF:
						case CONT:
						case REFLECT:
							_buffSkills.add(skill);
							break;
						
						case HEAL:
						case HOT:
						case HEAL_PERCENT:
						case HEAL_STATIC:
						case BALANCE_LIFE:
						case MANARECHARGE:
						case MANAHEAL_PERCENT:
							_healSkills.add(skill);
							break;
						
						case DEBUFF:
						case ROOT:
						case SLEEP:
						case STUN:
						case PARALYZE:
						case POISON:
						case DOT:
						case MDOT:
						case BLEED:
						case MUTE:
						case FEAR:
						case CANCEL:
						case NEGATE:
						case WEAKNESS:
						case AGGDEBUFF:
							_debuffSkills.add(skill);
							break;
						
						case PDAM:
						case MDAM:
						case BLOW:
						case DRAIN:
						case CHARGEDAM:
						case FATAL:
						case DEATHLINK:
						case MANADAM:
						case CPDAMPERCENT:
						case GET_PLAYER:
						case INSTANT_JUMP:
						case AGGDAMAGE:
							addShortOrLongRangeSkill(skill);
							break;
					}
				}
			}
			_skills.put(skill.getId(), skill);
		}
	}
	
	public Map<EventType, List<Quest>> getEventQuests()
	{
		return _questEvents;
	}
	
	public List<Quest> getEventQuests(EventType EventType)
	{
		return _questEvents.get(EventType);
	}
	
	public void addQuestEvent(EventType eventType, Quest quest)
	{
		List<Quest> eventList = _questEvents.get(eventType);
		if (eventList == null)
		{
			eventList = new ArrayList<>();
			eventList.add(quest);
			_questEvents.put(eventType, eventList);
		}
		else
		{
			eventList.remove(quest);
			
			if (eventType.isMultipleRegistrationAllowed() || eventList.isEmpty())
				eventList.add(quest);
			else
				_log.warning("Quest event not allow multiple quest registrations. Skipped addition of EventType \"" + eventType + "\" for NPC \"" + getName() + "\" and quest \"" + quest.getName() + "\".");
		}
	}
	
	/**
	 * @return
	 */
	public boolean hasDrops()
	{
		// Verifica se o NPC tem drops associados
		// Supondo que o NPC armazena seus drops em uma lista
		return !_categories.isEmpty(); // Retorna true se houver drops na lista
	}
}