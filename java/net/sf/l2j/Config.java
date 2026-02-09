package net.sf.l2j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.event.partyfarm.RewardHolder;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.type.CrystalType;
import net.sf.l2j.gameserver.model.olympiad.OlympiadPeriod;

public final class Config
{
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	
	public static final String CLANS_FILE = "./config/clans.properties";
	public static final String EVENTS_FILE = "./config/events.properties";
	public static final String GEOENGINE_FILE = "./config/geoengine.properties";
	public static final String HEXID_FILE = "./config/hexid.txt";
	public static final String LOGIN_CONFIGURATION_FILE = "./config/loginserver.properties";
	public static final String NPCS_FILE = "./config/npcs.properties";
	public static final String PLAYERS_FILE = "./config/players.properties";
	public static final String SERVER_FILE = "./config/server.properties";
	public static final String SIEGE_FILE = "./config/siege.properties";
	public static final String SIEGE_OTHERS_FILE = "./config/siege.properties";
	
	// Custom properties
	public static final String DEATH_BUFFS_FILE = "./config/DeathBuffLocation.properties";
	public static final String DIVERSOS_FILE = "./config/Diversos.properties";
	public static final String NEWCHARACTER = "./config/Personagem.properties";
	public static final String PET_MANAGER = "./config/PetManager.properties";
	public static final String CUSTOM_FILE = "./config/Custom.properties";
	public static final String PROTECION_FILE = "./config/Protect.properties";
	public static final String DONATE_SHOP = "./config/Donate.properties";
	public static final String BOSS_FILE = "./config/Boss.properties";
	public static final String COMMAND_FILE = "./config/Comandos.properties";
	public static final String PHYSICS_FILE = "./config/physics.properties";
	public static final String SAY_FILTER_FILE = "./config/SayFilter.txt";
	public static final String OFFLINE_BUFF_SHOP_FILE = "./config/SellBuffs.properties";
	
	public static final String PARTYFARM_EVENT = "./config/events/PartyFarm.properties";
	public static final String REWARD_SOLO_EVENT = "./config/events/RewardSoloEvent.properties";
	public static final String SPOIL_FILE = "./config/events/EventSpoil.properties";
	public static final String PVP_EVENT_FILE = "./config/events/PvPEvent.properties";
	public static final String MISSION_FILE = "./config/events/Mission.properties";
	public static final String SOLOBOSS_FILE = "./config/events/SoloBossEvent.properties";
	public static final String CHAMPION_EVENT = "./config/events/ChampionInvade.properties";
	
	// --------------------------------------------------
	// Clans settings
	// --------------------------------------------------
	
	/** Clans */
	public static boolean ALLOW_ALL_PLAYERS_CLAN_SKILLS;
	public static int ITEM_ID_BUY_CLAN_HALL;
	public static boolean ALLOW_CLAN_FULL;
	public static boolean DISABLE_ROYAL;
	public static int ALT_MAX_NUM_OF_MEMBERS_IN_CLAN;
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static int ALT_CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static boolean REMOVE_CASTLE_CIRCLETS;
	public static boolean ENABLE_DWARF_WEAPON_SIZE;
	public static int SIZE_WEAPON_DWARF;
	
	/** Aio Buffer */
	public static boolean ENABLE_AIO_SYSTEM;
	public static Map<Integer, Integer> AIO_SKILLS;
	public static boolean ALLOW_AIO_NCOLOR;
	public static int AIO_NCOLOR;
	public static boolean ALLOW_AIO_TCOLOR;
	public static int AIO_TCOLOR;
	public static boolean ALLOW_AIO_ITEM;
	public static int AIO_ITEMID;
	public static int AIO_ITEM;
	public static int AIO_DIAS;
	public static int AIO_ITEM2;
	public static int AIO_DIAS2;
	public static int AIO_ITEM3;
	public static int AIO_DIAS3;
	public static boolean ALLOW_AIO_IN_EVENTS;
	public static boolean ALLOW_AIO_USE_CM;
	public static boolean EFFECT_AIO_BUFF_CHARACTER;
	public static boolean FREE_ITEMS_MULTISELL_BETA;
	
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static ArrayList<String> FILTER_LIST = new ArrayList<>();
	public static int CHAT_FILTER_PUNISHMENT_PARAM1;
	public static int CHAT_FILTER_PUNISHMENT_PARAM2;
	public static int CHAT_FILTER_PUNISHMENT_PARAM3;
	public static String CHAT_FILTER_PUNISHMENT;
	public static String INTERFACE_KEY;
	public static int VERIFICATION_TIME;
	public static int DUALBOX_NUMBER_FARM;
	public static boolean BLOCK_DUALBOX_FARMZONE;
	
	/** Variaveis Champion Invade */
	public static int EVENT_CHAMPION_FARM_TIME;
	public static String[] EVENT_CHAMPION_FARM_INTERVAL_BY_TIME_OF_DAY;
	public static String CHAMPION_FARM_MESSAGE_TEXT;
	public static boolean CHAMPION_FARM_BY_TIME_OF_DAY;
	public static boolean START_CHAMPION_EVENT;
	public static boolean CHAMPION_MESSAGE_ENABLED;
	public static int CHAMPION_INVADE_FREQUENCY;
	public static String CHAMPION_MONSTER;
	public static List<Integer> LIST_NPC_CHAMPION_MONSTER = new ArrayList<>();
	public static String TITLE_CHAMPION_INVADE;
	public static int CHAMPION_INVADE_ENABLE_AURA;
	public static boolean RED_NAME_CHAMPION_INVADE;
	public static List<int[]> CHAMPION_INVADE_DROP = new ArrayList<>();
	public static boolean VIP_REWARD_BONUS_INVADE;
	
	/** Variaveis Solo Boss Event */
	public static String[] SOLO_BOSS_EVENT_INTERVAL_BY_TIME_OF_DAY; // Horarios
	public static boolean SCREN_MSG_BOSS_SOLO;
	public static boolean SOLO_BOSS_EVENT; // Ligar Evento
	public static int SOLO_BOSS_ID_ONE; // Boss 1
	public static int SOLO_BOSS_ID_TWO; // Boss 2
	public static int SOLO_BOSS_ID_THREE; // Boss 3
	public static int SOLO_BOSS_ID_FOUR; // Boss 4
	public static int SOLO_BOSS_ID_FIVE; // Boss 5
	
	public static int RANGE_SOLO_BOSS; // Range
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	// Recompensa Boss 1
	public static List<RewardHolder> SOLO_BOSS_REWARDS_ONE = new ArrayList<>();
	public static List<RewardHolder> SOLO_BOSS_REWARDS_TWO = new ArrayList<>();
	public static List<RewardHolder> SOLO_BOSS_REWARDS_THREE = new ArrayList<>();
	public static List<RewardHolder> SOLO_BOSS_REWARDS_FOUR = new ArrayList<>();
	public static List<RewardHolder> SOLO_BOSS_REWARDS_FIVE = new ArrayList<>();
	
	public static int[] SOLO_BOSS_ID_ONE_LOC = new int[3]; // Boss Loc 1
	public static int[] SOLO_BOSS_ID_TWO_LOC = new int[3]; // Boss Loc 2
	public static int[] SOLO_BOSS_ID_THREE_LOC = new int[3]; // Boss Loc 3
	public static int[] SOLO_BOSS_ID_FOUR_LOC = new int[3]; // Boss Loc 4
	public static int[] SOLO_BOSS_ID_FIVE_LOC = new int[3]; // Boss Loc 5
	
	public static int EVENT_SOLOBOSS_TIME; // Tempo de durasao
	
	/** Variaveis Mission */
	public static String BOSS_LIST_MISSION_ID;
	public static ArrayList<Integer> BOSS_LIST_MISSION = new ArrayList<>();
	public static boolean ACTIVE_MISSION;
	public static boolean ACTIVE_MISSION_TVT;
	public static int MISSION_TVT_CONT;
	public static int MISSION_TVT_REWARD_ID;
	public static int MISSION_TVT_REWARD_AMOUNT;
	public static boolean ACTIVE_MISSION_RAID;
	public static int MISSION_RAID_CONT;
	public static int MISSION_RAID_REWARD_ID;
	public static int MISSION_RAID_REWARD_AMOUNT;
	public static boolean ACTIVE_MISSION_MOB;
	public static int MISSION_MOB_CONT;
	public static boolean ACTIVE_MISSION_PARTY_MOB;
	public static int MISSION_PARTY_MOB_CONT;
	public static int MISSION_PARTY_MOB_REWARD_ID;
	public static int MISSION_PARTY_MOB_REWARD_AMOUNT;
	public static String MISSION_LIST_MOBS;
	public static ArrayList<Integer> MISSION_LIST_MONSTER = new ArrayList<>();
	public static int MISSION_MOB_REWARD_ID;
	public static int MISSION_MOB_REWARD_AMOUNT;
	
	public static boolean ACTIVE_MISSION_1X1;
	public static int MISSION_1X1_CONT;
	public static int MISSION_1X1_REWARD_ID;
	public static int MISSION_1X1_REWARD_AMOUNT;
	
	public static boolean ACTIVE_MISSION_3X3;
	public static int MISSION_3X3_CONT;
	public static int MISSION_3X3_REWARD_ID;
	public static int MISSION_3X3_REWARD_AMOUNT;
	public static boolean ACTIVE_MISSION_5X5;
	public static int MISSION_5X5_CONT;
	public static int MISSION_5X5_REWARD_ID;
	public static int MISSION_5X5_REWARD_AMOUNT;
	public static boolean ACTIVE_MISSION_9X9;
	public static int MISSION_9X9_CONT;
	public static int MISSION_9X9_REWARD_ID;
	public static int MISSION_9X9_REWARD_AMOUNT;
	public static String[] CLEAR_MISSION_INTERVAL_BY_TIME_OF_DAY;
	
	public static boolean PVP_EVENT_ENABLED;
	public static String[] PVP_EVENT_INTERVAL;
	public static int PVP_EVENT_RUNNING_TIME;
	public static final Map<Integer, List<int[]>> PVP_EVENT_RANK_REWARDS = new HashMap<>();
	public static boolean ALLOW_SPECIAL_PVP_REWARD;
	public static List<int[]> PVP_SPECIAL_ITEMS_REWARD;
	public static boolean SCREN_MSG_PVP;
	public static int pvp_locx;
	public static int pvp_locy;
	public static int pvp_locz;
	public static String NAME_PVP;
	public static boolean NO_INVITE_PVPEVENT;
	public static boolean BLOCK_PVPEVENTCLASS;
	public static String STRING_NAME_PVPEVENT;
	public static boolean ZONEPVPEVENT_ALLOW_INTERFERENCE;
	public static boolean ENABLE_NAME_TITLE_PVPEVENT;
	public static boolean BLOCK_CREST_PVPEVENT;
	public static String WEAPON_ID_ENCHANT_RESTRICT;
	public static List<Integer> WEAPON_LIST_ID_ENCHANT_RESTRICT;
	public static String ARMOR_ID_ENCHANT_RESTRICT;
	public static List<Integer> ARMOR_LIST_ID_ENCHANT_RESTRICT;
	public static boolean PVP_ITEM_ENCHANT_EVENT;
	public static float PVP_ITEM_ENCHANT_WEAPON_CHANCE;
	public static float PVP_ITEM_ENCHANT_ARMOR_CHANCE;
	public static int CHECK_MIN_ENCHANT_WEAPON;
	public static int CHECK_MAX_ENCHANT_WEAPON;
	public static int CHECK_MIN_ENCHANT_ARMOR_JEWELS;
	public static int CHECK_MAX_ENCHANT_ARMOR_JEWELS;
	
	/** Party Farm Event */
	public static int EVENT_BEST_FARM_TIME;
	public static String[] EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY;
	public static int PARTY_FARM_MONSTER_DALAY;
	public static String PARTY_FARM_MESSAGE_TEXT;
	public static int monsterId;
	public static int MONSTER_LOCS_COUNT;
	public static int[][] MONSTER_LOCS;
	public static boolean PARTY_MESSAGE_ENABLED;
	public static boolean ENABLE_DUALBOX_PARTYFARM;
	// public static boolean PARTY_FARM_BY_TIME_OF_DAY;
	public static boolean START_PARTY;
	public static String PART_ZONE_MONSTERS_EVENT;
	public static List<Integer> PART_ZONE_MONSTERS_EVENT_ID;
	public static List<RewardHolder> PARTY_ZONE_REWARDS = new ArrayList<>();
	public static List<int[]> CUSTOM_SPAWN_PARTYFARM = new ArrayList<>();
	public static int AMOUNT_MONSTROS_SPAWN_PT;
	public static int RADIUS_SPAWN_LOC_PTFARM;
	public static boolean ENABLE_SPAWN_PARTYFARM_MONSTERS;
	
	/** RewardSoloEvent */
	public static int EVENT_SOLO_FARM_TIME;
	public static String[] EVENT_SOLO_FARM_INTERVAL_BY_TIME_OF_DAY;
	public static String SOLO_FARM_MESSAGE_TEXT;
	public static boolean SOLOL_MESSAGE_ENABLED;
	public static boolean SOLO_FARM_BY_TIME_OF_DAY;
	public static boolean START_SOLO_EVENT;
	public static boolean SOLO_MESSAGE_ENABLED;
	public static String CUSTOM_MONSTER;
	public static List<Integer> LIST_NPC_CUSTOM_MONSTER = new ArrayList<>();
	public static List<int[]> CUSTOM_MONSTER_DROP = new ArrayList<>();
	
	/** Spoil Event */
	public static int EVENT_SPOIL_FARM_TIME;
	public static String[] EVENT_SPOIL_FARM_INTERVAL_BY_TIME_OF_DAY;
	public static int SPOIL_FARM_MONSTER_DALAY;
	public static boolean BLOCK_AUTOFARM_SPOILZONE;
	public static String SPOIL_FARM_MESSAGE_TEXT;
	// public static int monsterId;
	public static int SPOILMONSTER_ID;
	public static int SPOIL_LOCS_COUNT;
	public static int[][] SPOIL_LOCS;
	public static int GKNPC_ID;
	public static int GK_LOCS_COUNT;
	public static int[][] GK_LOCS;
	public static boolean SPOIL_MESSAGE_ENABLED;
	public static boolean START_SPOIL;
	
	public static int BLOW_ATTACK_FRONT;
	public static int BLOW_ATTACK_SIDE;
	public static int BLOW_ATTACK_BEHIND;
	public static int BACKSTAB_ATTACK_FRONT;
	public static int BACKSTAB_ATTACK_SIDE;
	public static int BACKSTAB_ATTACK_BEHIND;
	public static float MAGIC_CRITICAL_POWER;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int ANTI_SS_BUG_1;
	public static int ANTI_SS_BUG_2;
	
	/** Chance modifier skills */
	public static boolean ENABLE_CUSTOM_CHANCE_SKILL;
	public static float SURRENDER_TO_FIRE;
	public static float VORTEX_TO_FIRE;
	public static float SURRENDER_TO_WIND;
	public static float WIND_VORTEX;
	public static float CURSE_GLOOM;
	public static float DARK_VORTEX;
	public static float SURRENDER_TO_WATER;
	public static float ICE_VORTEX;
	public static float LIGHT_VORTEX;
	public static float SILENCE;
	public static float SLEEP;
	public static float CURSE_FEAR;
	public static float ANCHOR;
	public static float CURSE_OF_DOOM;
	public static float CURSE_OF_ABYSS;
	public static float CANCELLATION;
	public static float MASS_SLOW;
	public static float MASS_FEAR;
	public static float MASS_GLOOM;
	public static float SLEEPING_CLOUD;
	public static float HEROIC_GRANDEUR;
	public static float HEROIC_DREAD;
	public static float STUNNING_SHOT;
	public static float HEX;
	public static float SHOCK_STOMP;
	public static float THUNDER_STORM;
	public static float SHIELD_STUN;
	public static float SHIELD_SLAM;
	public static float SHACKLE;
	public static float MASS_SHACKLING;
	public static float ARREST;
	public static float BLUFF;
	public static float SWITCH;
	public static float STUNNING_FIST;
	public static float FEAR_OVER;
	public static float SEAL_OF_SILENCE;
	public static float SEAL_OF_SUSPENSION;
	public static float STUN_ATTACK;
	public static float ARMOR_CRUSH;
	public static float SLOW;
	public static float SEAL_OF_DESPAIR;
	public static float DREAMING_SPIRIT;
	public static float SEAL_OF_BINDING;
	public static float MASS_WARRIOR_BANE;
	public static float MASS_MAGE_BANE;
	public static float SHIELD_BASH;
	public static float SHOCK_BLAST;
	public static boolean SKILL_MAX_CHANCE;
	public static List<Integer> SKILL_LIST_SUCCESS_IN_OLY = new ArrayList<>();
	public static List<Integer> SKILL_LIST_SUCCESS = new ArrayList<>();
	public static double SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD;
	public static double SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD;
	public static double SKILLS_MAX_CHANCE_SUCCESS;
	public static double SKILLS_MIN_CHANCE_SUCCESS;
	public static boolean CUSTOM_CHANCE_FOR_ALL_SKILL;
	public static double SKILLS_MAX_CHANCE;
	public static double SKILLS_MIN_CHANCE;
	
	/** PcBang Event Settings */
	public static int PCB_MIN_LEVEL;
	public static int PCB_POINT_MIN;
	public static int PCB_POINT_MAX;
	public static int PCB_CHANCE_DUAL_POINT;
	public static int PCB_INTERVAL;
	public static int PCB_COIN_ID;
	public static boolean PCB_ENABLE;
	
	public static boolean ALLOW_HEALER_NPC;
	public static int BUY_SKILL_ITEM;
	public static int BUY_SKILL_PRICE;
	public static int BUY_SKILL_MAX_SLOTS;
	public static int REMOVE_SKILL_COIN_ID;
	public static int REMOVE_SKILL_COIN_COUNT;
	public static int CHARGE_BACK_ITEM_ID;
	public static int CHARGE_BACK_ITEM_COUNT;
	public static boolean ENABLE_SKILL_MAIN_CLASS;
	public static boolean ENABLE_PENALTY_WATER_ZONE;
	public static boolean ENABLE_FINAL_BUFF_SLOT;
	public static boolean PLAYERS_NORMAIS_USED_RESTRICTION_ITEMS;
	public static boolean ENABLE_MP_REGE_ORCS_MYSTICS;
	public static List<Integer> ITEMS_NO_CONSUME;
	public static String[] GM_NAMES;
	public static boolean ENABLE_NAME_GMS_CHECK;
	public static List<Integer> IDMULTISELLLOGDONATE;
	public static boolean ENABLE_CHECK_DONATE_ITEMS;
	public static boolean ENABLE_TRADE_TRACK;
	public static boolean ENABLE_WH_LOG;
	public static boolean TRY_USED_ADMIN_COMMANDS;
	public static boolean CHAT_BAN_HTML;
	public static String CHAT_BAN_MESSAGE;
	public static int REMOVE_CHAT_BAN_ITEM_COUNT;
	public static Set<Integer> SKILLS_NO_ANIMATIONS = new HashSet<>();
	public static final List<Integer> NO_AUTO_LOOT_MOBS = new ArrayList<>();
	public static boolean ALLOW_CAST_BREAK;
	public static boolean BLOCK_REVIVE_TO_MOVIMENT;
	public static boolean ENABLE_CONTROL_CAST_OFICIAL;
	public static int MAIL_ITEM_EXPIRE_DAYS;
	public static int MAIL_SEND_TAX;
	public static boolean ALLOW_EMAIL_COMMAND;
	public static boolean TIME_RESS_CHARACTER_ALLOW;
	public static int TIME_RESS_CHARACTER;
	public static boolean ALLOW_ALLY_TARGET_PARTY;
	public static boolean ENABLE_FULL_PARTY_XP;
	
	/** Variaveis Custom Pet */
	public static final Map<Integer, Double> CUSTOM_PET_MULTIPLIERS = new HashMap<>();
	public static boolean ENABLE_CUSTOM_PET_MULTIPLIERS;
	public static double PET_HEAL_POWER_MULTIPLIER;
	
	/** Variaveis PickUp Time */
	public static int TIME_PICKUP_BOSS;
	public static int TIME_PICKUP_NORMAL;
	
	/** Chat trade global. */
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	public static boolean DISABLE_CAPSLOCK;
	public static int CUSTOM_GLOBAL_CHAT_TIME;
	public static boolean GLOBAL_CHAT_WITH_PVP;
	public static int GLOBAL_PVP_AMOUNT;
	public static boolean TRADE_CHAT_WITH_PVP;
	public static int TRADE_PVP_AMOUNT;
	public static int CUSTOM_HERO_CHAT_TIME;
	
	/** Variaveis Event Faenor */
	public static boolean ENABLE_EVENT_2008;
	public static boolean AUG_ITEM_TRADE;
	public static String STRING_MULTISELL_DONATE;
	
	/** Variaveis teleport Solo Farm */
	public static int[] GK_SPAWN_SOLOFARM_1 = new int[3];
	public static int[] GK_SPAWN_SOLOFARM_2 = new int[3];
	public static int[] GK_SPAWN_SOLOFARM_3 = new int[3];
	public static int[] GK_SPAWN_SOLOFARM_4 = new int[3];
	public static int[] GK_SPAWN_SOLOFARM_5 = new int[3];
	public static int[] GK_SPAWN_SOLOFARM_6 = new int[3];
	
	/** Variaveis Drop annuncie Items */
	public static String ANNUNCIE_ID_DROP;
	public static List<Integer> ANNUNCIE_LISTID_DROP;
	public static boolean ENABLE_ANNUNCIE_DROP_ITEMS;
	public static int DROP_ANNUNCIE_ID;
	
	/** Variaveis Community */
	public static int[] MARKETPLACE_FEE = new int[2];
	public static int COR_LOJA_AUCTION_ANUNCIE;
	public static boolean ENABLE_AUCTION_COMMUNITY;
	public static int TICKET_MARKE_PLACE;
	public static boolean ENABLE_GRADE_ITEMS_MARKETPLACE;
	public static List<CrystalType> GRADE_ITEMS_MARKETPLACE = new ArrayList<>();
	public static List<Integer> ITEMS_NO_RULES_MARKETPLACE;
	public static String ITEMS_NO_RULES_MARKETPLACE_ID;
	public static boolean ENABLE_TIME_ZONE_SYSTEM;
	public static double AUCTION_TAX_RATE;
	public static int AUCTION_TAX_MINIMUM_ITEM_COUNT;
	
	/** Variaveis Color Manager */
	public static int PVP_POINT_ID;
	public static int PVP_POINT_COUNT;
	public static int DONATE_COIN_COUNT;
	public static boolean ALLOW_NEW_COLOR_MANAGER;
	public static int DONATE_COIN_ID;
	
	/** Variaveis Protect */
	public static boolean TALK_CHAT_ALL_CONFIG;
	public static int TALK_CHAT_ALL_TIME;
	public static String[] FORBIDDEN_NAMES;
	public static boolean DISABLE_ATTACK_NPC_TYPE;
	public static boolean DISABLE_CHAT;
	public static String LETHAL_MONSTERS_PROTECT;
	public static List<Integer> LETHAL_MONSTERS_ID;
	public static boolean ALLOW_LIGHT_USE_HEAVY;
	public static String NOTALLOWCLASS;
	public static List<Integer> NOTALLOWEDUSEHEAVY;
	public static String WELCOME_MESSAGE_ANTIHERVY;
	public static boolean ALLOW_LIGHT_USE_MAGIC;
	public static String NOTALLOWCLASSMAGIC;
	public static List<Integer> NOTALLOWEDUSEMAGIC;
	public static String WELCOME_MESSAGE_ANTIROBE;
	public static boolean ALLOW_HEAVY_USE_LIGHT;
	public static String NOTALLOWCLASSE;
	public static List<Integer> NOTALLOWEDUSELIGHT;
	public static String WELCOME_MESSAGE_ANTILIGHT;
	public static boolean ENABLE_NO_USE_SETSOLY;
	public static boolean ALT_DISABLE_BOW_CLASSES;
	public static String DISABLE_BOW_CLASSES_STRING;
	public static ArrayList<Integer> DISABLE_BOW_CLASSES = new ArrayList<>();
	public static boolean WYVERN_PROTECTION;
	public static String ID_RESTRICT;
	public static List<Integer> LISTID_RESTRICT;
	public static boolean WYVERN_PROTECTION_BOSS_ZONE;
	public static String ID_RESTRICT_BOSS_ZONE;
	public static List<Integer> LISTID_RESTRICT_BOSS_ZONE;
	public static boolean REMOVE_WEAPON;
	public static boolean REMOVE_CHEST;
	public static boolean REMOVE_LEG;
	public static boolean BLOCK_EXIT_OLY;
	public static boolean ALLOW_DUALBOX;
	public static int ALLOWED_BOXES;
	public static boolean ALLOW_DUALBOX_OLY;
	public static String AGATHION_ID_RESTRICT;
	public static List<Integer> AGATHION_LISTID_RESTRICT;
	
	/** Vip settings */
	public static float VIP_XP_SP_RATE;
	public static float VIP_ADENA_RATE;
	public static float VIP_DROP_RATE;
	public static float VIP_SPOIL_RATE;
	
	public static boolean ALLOW_VIP_NCOLOR;
	public static boolean ENABLE_VIP_SYSTEM;
	public static int VIP_NCOLOR;
	public static boolean ALLOW_VIP_TCOLOR;
	public static int VIP_TCOLOR;
	public static boolean ENABLE_VIP_FREE;
	public static int VIP_DAYS_FREE;
	/** Donate Price services */
	public static int VIP_30_DAYS_PRICE;
	public static int VIP_60_DAYS_PRICE;
	public static int VIP_90_DAYS_PRICE;
	public static int VIP_ETERNAL_PRICE;
	public static int HERO_30_DAYS_PRICE;
	public static int HERO_60_DAYS_PRICE;
	public static int HERO_90_DAYS_PRICE;
	public static int HERO_ETERNAL_PRICE;
	public static int DONATE_NAME_PRICE;
	public static int DONATE_SEX_PRICE;
	public static int DONATE_CLASS_PRICE;
	public static int NOBL_ITEM_COUNT;
	public static int PK_ITEM_COUNT;
	public static int PK_CLEAN;
	public static int CLAN_ITEM_COUNT;
	public static int CLAN_REP_ITEM_COUNT;
	public static int CLAN_REPS;
	public static int CLAN_SKILL_ITEM_COUNT;
	public static int AUGM_ITEM_COUNT;
	public static int AUGM_PRICE_ALTERNATIVE;
	public static int PASSWORD_ITEM_COUNT;
	public static int ENCHANT_ITEM_COUNT;
	public static int ENCHANT_MAX_VALUE;
	
	/** Offline Shop */
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SET_SLEEP;
	public static int ITEM_PERMITIDO_PARA_USAR_NA_LOJA_ID;
	
	/** Variaveis Offline Player */
	public static boolean ENABLE_OFFLINE_PEACE_ZONE;
	public static boolean ENABLE_OFFLINE_PLAYER;
	public static boolean OFFLINE_PLAYER_SLEEP;
	public static boolean ALLOW_NEW_OFFLINE_TITLE;
	public static String TITLE_OFFLINE_PLAYER;
	public static boolean RESTORE_OFFLINERS_PLAYERS;
	public static int OFFLINE_MAX_DAYS_PLAYERS;
	
	/** Character Killing Monument */
	public static boolean CKM_ENABLED;
	public static long CKM_CYCLE_LENGTH;
	public static String CKM_PVP_NPC_TITLE;
	public static int CKM_PVP_NPC_TITLE_COLOR;
	public static int CKM_PVP_NPC_NAME_COLOR;
	public static String CKM_PK_NPC_TITLE;
	public static int CKM_PK_NPC_TITLE_COLOR;
	public static int CKM_PK_NPC_NAME_COLOR;
	public static int[][] CKM_PLAYER_REWARDS;
	
	/** Enchant */
	public static double ENCHANT_CHANCE_WEAPON_MAGIC;
	public static double ENCHANT_CHANCE_WEAPON_MAGIC_15PLUS;
	public static double ENCHANT_CHANCE_WEAPON_NONMAGIC;
	public static double ENCHANT_CHANCE_WEAPON_NONMAGIC_15PLUS;
	public static double ENCHANT_CHANCE_ARMOR;
	public static int ENCHANT_MAX_WEAPON;
	public static int ENCHANT_MAX_ARMOR;
	public static int ENCHANT_SAFE_MAX;
	public static int ENCHANT_SAFE_MAX_FULL;
	/** Variaveis ENCHANT System */
	public static HashMap<Integer, Integer> NORMAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> NORMAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> NORMAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static boolean ENCHANT_HERO_WEAPON;
	public static int ENCHANT_WEAPON_MAX;
	public static int ENCHANT_ARMOR_MAX;
	public static int ENCHANT_JEWELRY_MAX;
	public static int BLESSED_ENCHANT_WEAPON_MAX;
	public static int BLESSED_ENCHANT_ARMOR_MAX;
	public static int BLESSED_ENCHANT_JEWELRY_MAX;
	public static int BREAK_ENCHANT;
	public static int CRYSTAL_ENCHANT_MIN;
	public static int CRYSTAL_ENCHANT_WEAPON_MAX;
	public static int CRYSTAL_ENCHANT_ARMOR_MAX;
	public static int CRYSTAL_ENCHANT_JEWELRY_MAX;
	public static int DONATOR_ENCHANT_MIN;
	public static int DONATOR_ENCHANT_WEAPON_MAX;
	public static int DONATOR_ENCHANT_ARMOR_MAX;
	public static int DONATOR_ENCHANT_JEWELRY_MAX;
	public static boolean DONATOR_DECREASE_ENCHANT;
	public static boolean BLESSED_DECREASE_ENCHANT;
	public static boolean SYSTEM_ENCHANT_BLESS_REDUCED;
	public static int GOLD_WEAPON;
	public static int GOLD_ARMOR;
	public static boolean SCROLL_STACKABLE;
	public static boolean ENABLE_ENCHANT_ANNOUNCE;
	public static int ENCHANT_ANNOUNCE_LEVEL;
	public static boolean ANNUNCIE_FAILED_ENCHANT;
	public static int MIN_ENCHANT_FAILED;
	public static boolean ENABLE_COLOR_CHAT_ENCHANT;
	public static int CHAT_COLOR_ENCHANT;
	
	/** Manor */
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_PERIOD;
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	
	/** Clan Hall function */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	// --------------------------------------------------
	// Events settings
	// --------------------------------------------------
	
	/** Olympiad */
	public static String ID_RESTRICT_OLY;
	public static List<Integer> LISTID_RESTRICT_OLY;
	public static boolean ENABLED_RESET_SKILLS_OLY;
	public static List<int[]> CUSTOM_BUFFS_M = new ArrayList<>();
	public static List<int[]> CUSTOM_BUFFS_F = new ArrayList<>();
	public static boolean OLLY_GRADE_A;
	public static boolean BLOCK_REGISTER_ITEMS_OLYMPIAD_ENCHANT;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static boolean OLY_SKILL_PROTECT;
	public static List<Integer> OLY_SKILL_LIST = new ArrayList<>();
	public static boolean OLY_FIGHT;
	public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
	public static OlympiadPeriod ALT_OLY_PERIOD;
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	public static boolean ALT_OLY_END_ANNOUNCE;
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_OLY_WAIT_BATTLE;
	public static int ALT_OLY_WAIT_END;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	
	/** SevenSigns Festival */
	public static boolean ALT_GAME_CASTLE_DAWN;
	public static boolean ALT_GAME_CASTLE_DUSK;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	public static boolean ALT_SEVENSIGNS_LAZY_UPDATE;
	
	/** Four Sepulchers */
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	/** Wedding system */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Lottery */
	public static int ALT_LOTTERY_PRIZE;
	public static int ALT_LOTTERY_TICKET_PRICE;
	public static double ALT_LOTTERY_5_NUMBER_RATE;
	public static double ALT_LOTTERY_4_NUMBER_RATE;
	public static double ALT_LOTTERY_3_NUMBER_RATE;
	public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
	
	// --------------------------------------------------
	// GeoEngine
	// --------------------------------------------------
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static int COORD_SYNCHRONIZE;
	public static boolean ENABLE_GEODATA;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static boolean PATHFINDING;
	public static String PATHFIND_BUFFERS;
	public static int BASE_WEIGHT;
	public static int DIAGONAL_WEIGHT;
	public static int HEURISTIC_WEIGHT;
	public static int OBSTACLE_MULTIPLIER;
	public static int MAX_ITERATIONS;
	public static boolean DEBUG_PATH;
	
	// --------------------------------------------------
	// HexID
	// --------------------------------------------------
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// --------------------------------------------------
	// Loginserver
	// --------------------------------------------------
	
	public static String LOGIN_BIND_ADDRESS;
	public static int PORT_LOGIN;
	
	public static boolean ACCEPT_NEW_GAMESERVER;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	
	public static boolean LOG_LOGIN_CONTROLLER;
	
	public static boolean SHOW_LICENCE;
	
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// --------------------------------------------------
	// NPCs / Monsters
	// --------------------------------------------------
	
	/** Champion Mod */
	public static int CHAMPION_FREQUENCY;
	public static int CHAMP_MIN_LVL;
	public static int CHAMP_MAX_LVL;
	public static int CHAMPION_HP;
	public static int CHAMPION_REWARDS;
	public static int CHAMPION_ADENAS_REWARDS;
	public static double CHAMPION_HP_REGEN;
	public static double CHAMPION_ATK;
	public static double CHAMPION_SPD_ATK;
	public static int CHAMPION_REWARD;
	public static int CHAMPION_REWARD_ID;
	public static int CHAMPION_REWARD_QTY;
	public static int L2JMOD_CHAMPION_ENABLE_AURA;
	public static boolean AGGRESSION_CHAMPION;
	public static boolean SHOW_RED_NAME_CHAMPION;
	public static boolean BLOCK_SELL_ITEMS_ADENA;
	public static boolean ENABLE_USED_MOM;
	public static boolean ENABLE_FREE_GK_SEVENSINGS;
	public static String NPC_WITH_AURA_RED;
	public static List<Integer> LIST_NPC_WITH_AURA_RED;
	public static String NPC_WITH_AURA_BLUE;
	public static List<Integer> LIST_NPC_WITH_AURA_BLUE;
	public static String NPC_WITH_EFFECT;
	public static List<Integer> LIST_NPC_WITH_EFFECT = new ArrayList<>();
	public static boolean WAIT_TIME_FOR_SPAWN_ALL_NPCS;
	public static String SPAWN_ALL_NPCS_TIME_OPEN_SERVER;

	public static List<Integer> CUSTOM_NPCS_CLAN_Y_ALLY = new ArrayList<>();
	
	/** Misc */
	public static boolean ALLOW_CLASS_MASTERS;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ENABLE_NPC_SPAWN_ANNOUNCEMENT;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	public static boolean ALTERNATE_CLASS_MASTER;
	
	/** Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	public static String FIGHTER_BUFF;
	public static ArrayList<Integer> FIGHTER_BUFF_LIST = new ArrayList<>();
	public static String MAGE_BUFF;
	public static ArrayList<Integer> MAGE_BUFF_LIST = new ArrayList<>();
	
	/** Wyvern Manager */
	public static boolean WYVERN_ALLOW_UPGRADER;
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	/** Raid Boss */
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_DEFENCE_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	
	/** Grand Boss */
	public static int SPAWN_INTERVAL_AQ;
	public static int RANDOM_SPAWN_TIME_AQ;
	
	public static int SPAWN_INTERVAL_ANTHARAS;
	public static int RANDOM_SPAWN_TIME_ANTHARAS;
	public static int WAIT_TIME_ANTHARAS;
	
	public static int SPAWN_INTERVAL_BAIUM;
	public static int RANDOM_SPAWN_TIME_BAIUM;
	
	public static int SPAWN_INTERVAL_CORE;
	public static int RANDOM_SPAWN_TIME_CORE;
	
	public static int SPAWN_INTERVAL_FRINTEZZA;
	public static int RANDOM_SPAWN_TIME_FRINTEZZA;
	public static int WAIT_TIME_FRINTEZZA;
	public static int FRINTEZZA_MIN_PARTIES;
	public static int FRINTEZZA_MAX_PARTIES;
	public static boolean BYPASS_FRINTEZZA_PARTIES_CHECK;
	
	public static int SPAWN_INTERVAL_ORFEN;
	public static int RANDOM_SPAWN_TIME_ORFEN;
	
	public static int SPAWN_INTERVAL_SAILREN;
	public static int RANDOM_SPAWN_TIME_SAILREN;
	public static int WAIT_TIME_SAILREN;
	
	public static int SPAWN_INTERVAL_VALAKAS;
	public static int RANDOM_SPAWN_TIME_VALAKAS;
	public static int WAIT_TIME_VALAKAS;
	
	public static int SPAWN_INTERVAL_ZAKEN;
	public static int RANDOM_SPAWN_TIME_ZAKEN;
	
	/** Variaveis boss properties */
	public static boolean ENABLE_DUALBOX_BOSSZONE;
	public static boolean BLOCK_DUALBOX_RAIDZONE;
	public static boolean ALLOW_DIRECT_TP_TO_BOSS_ROOM;
	public static boolean NOBLESS_FROM_BOSS;
	public static int BOSS_ID;
	public static int RADIUS_TO_RAID;
	public static boolean PLAYERS_CAN_HEAL_RB;
	public static boolean ENABLE_BOSSZONE_FLAG;
	public static int RANGE_EPICBOSS;
	public static int RANGE_BOSS_LIMIT;
	public static boolean ENABLE_FULL_HP_RECALL_EPIC;
	public static boolean ENABLE_FULL_HP_RECALL_BOSS;
	public static int[] CUSTOM_SPAWN_ZAKEN = new int[3];
	
	/** Raid Info Settings */
	public static int RAID_BOSS_INFO_PAGE_LIMIT;
	public static int RAID_BOSS_DROP_PAGE_LIMIT;
	public static String RAID_BOSS_DATE_FORMAT;
	public static String RAID_BOSS_IDS;
	public static List<Integer> LIST_RAID_BOSS_IDS;
	public static String GRAND_BOSS_IDS;
	public static List<Integer> LIST_GRAND_BOSS_IDS;
	public static String LIST_ITENS_NOT_SHOW;
	public static List<Integer> NOT_SHOW_DROP_INFO;
	
	/** Quest grandboss */
	public static int QUEST_BAIUM;
	public static int QUEST_VALAKAS;
	public static int QUEST_ANTHARAS;
	public static int QUEST_SAILREN;
	public static int QUEST_FRINTEZZA;
	
	/** Variaveiis FlagKill Boss */
	public static boolean ALLOW_FLAG_ONKILL_BY_ID;
	public static String NPCS_FLAG_IDS;
	public static List<Integer> NPCS_FLAG_LIST = new ArrayList<>();
	public static int NPCS_FLAG_RANGE;
	
	/** Variaveis spawn Custom lista */
	public static int MIN_RESPAWN;
	public static int MAX_RESPAWN;
	public static boolean RESPAWN_CUSTOM;
	public static String RAID_RESPAWN_IDS;
	public static List<Integer> RAID_RESPAWN_IDS_LIST;
	
	/** Variaveis comandos */
	public static boolean ENABLE_COMMAND_XPON_OFF;
	public static String MESSAGE_XPON;
	public static String MESSAGE_XPOFF;
	public static boolean ENABLE_COMMAND_GOLDBAR;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int ID_NEW_GOLD_BAR;
	public static int BANKING_SYSTEM_ADENA;
	public static boolean ENABLE_COMMAND_MENU;
	public static boolean ENABLE_COMMAND_PASSWORD;
	public static boolean ENABLE_COMMAND_REPORT_CHAR;
	public static boolean ENABLE_COMMAND_REPAIR_CHAR;
	public static boolean ENABLE_COMMAND_CASTLES;
	public static boolean ENABLE_SHIFT_CLICK;
	public static boolean ENABLE_COMMAND_RANKING;
	public static String ID_LIST_JEWELES_BOSS_RANKING;
	public static String MESSAGE_VIP_EPIC;
	public static boolean COMMAND_EPIC_ISVIP;
	public static boolean ENABLE_COMMAND_RAID;
	public static boolean ENABLE_COMMAND_CLAN_NOTICE;
	public static boolean ENABLE_VOICED_DONATE;
	public static boolean ENABLE_AUTO_FARM_COMMAND;
	public static boolean ENABLE_COMMAND_VIP_AUTOFARM;
	public static boolean NO_USE_FARM_IN_PEACE_ZONE;
	public static boolean ENABLE_DUALBOX_AUTOFARM;
	public static int NUMBER_BOX_IP_AUTOFARM;
	public static boolean ENABLE_NEW_TITLE_AUTOFARM;
	public static boolean ENABLE_AURA_AUTOFARM;
	public static String NEW_TITLE_AUTOFARM;
	public static boolean ENABLE_COMMAND_AUTO_POTION;
	/** Variaveis Commands Buffs */
	public static boolean BUFFER_USECOMMAND;
	public static boolean ENABLE_COMMAND_VIP_BUFFS;
	public static String BUFFER_COMMAND2;
	public static boolean STATUS_CMD;
	public static boolean STATUS_CMD_INVENTORY;
	public static boolean STATUS_CMD_SKILLS;
	
	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int MAX_DRIFT_RANGE;
	public static long KNOWNLIST_UPDATE_INTERVAL;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	
	// --------------------------------------------------
	// Players
	// --------------------------------------------------
	
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALT_GAME_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	// public static boolean SUBCLASS_WITHOUT_QUESTS;
	public static boolean KEEP_SUBCLASS_SKILLS;
	public static int CUSTOM_SUBCLASS_LVL;
	public static int ALLOWED_SUBCLASS;
	
	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double ALT_WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean ALT_GAME_FREIGHTS;
	public static int ALT_GAME_FREIGHT_PRICE;
	
	/** Enchant */
	// public static double ENCHANT_CHANCE_WEAPON_MAGIC;
	// public static double ENCHANT_CHANCE_WEAPON_MAGIC_15PLUS;
	// public static double ENCHANT_CHANCE_WEAPON_NONMAGIC;
	// public static double ENCHANT_CHANCE_WEAPON_NONMAGIC_15PLUS;
	// public static double ENCHANT_CHANCE_ARMOR;
	// public static int ENCHANT_MAX_WEAPON;
	// public static int ENCHANT_MAX_ARMOR;
	// public static int ENCHANT_SAFE_MAX;
	// public static int ENCHANT_SAFE_MAX_FULL;
	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_BE_KILLED_IN_PZ;
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	public static boolean KARMA_PLAYER_CAN_USE_WH;
	
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_LEAVE_PARTY_LEADER;
	
	/** GMs & Admin Stuff */
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static int MASTERACCESS_LEVEL;
	public static int MASTERACCESS_NAME_COLOR;
	public static int MASTERACCESS_TITLE_COLOR;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	public static boolean SHOW_FILE_GM;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	
	/** Skills & Classes **/
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static int BUFFS_MAX_AMOUNT;
	
	/** Others Settings */
	public static boolean ENABLE_ALTERNATIVE_SKILL_DURATION;
	public static HashMap<Integer, Integer> SKILL_DURATION_LIST;
	public static boolean ALT_GAME_FREE_TELEPORT;
	public static int FREE_TELEPORT_UNTIL;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static boolean GM_SUPER_HASTE;
	public static boolean ENABLE_CLASS_DARK_Y_ELF;
	public static boolean ENABLE_ALL_CLASS_SUBCLASS;
	public static boolean ENABLE_CLASS_OVERLORD_Y_WARSMITH;
	public static boolean ANNOUNCE_VIP;
	public static boolean ANNOUNCE_CASTLE_LORDS;
	public static boolean HERO_SKILL_SUB;
	public static boolean ANNOUNCE_HERO;
	public static boolean INFINITY_SS;
	public static boolean INFINITY_ARROWS;
	public static boolean ANNOUNCE_DROP_ITEM;
	public static boolean RESTART_BY_TIME_OF_DAY;
	public static int RESTART_SECONDS;
	public static String[] RESTART_INTERVAL_BY_TIME_OF_DAY;
	public static boolean PVP_ACTIVAR;
	public static int PVP_REWARD;
	public static int PVP_CANTIDAD;
	public static boolean PK_ACTIVAR;
	public static int PK_REWARD;
	public static int PK_CANTIDAD;
	public static boolean ANNOUNCE_PK_KILL;
	public static boolean ANNOUNCE_PVP_KILL;
	public static String ANNOUNCE_PK_MSG;
	public static String ANNOUNCE_PVP_MSG;
	public static List<Integer> ALLOW_CUSTOM_CANCEL;
	public static int CUSTOM_CANCEL_SECONDS;
	public static String PROTECTED_SKILLS;
	public static String STRING_ADD_ITEM_COIN;
	public static String STRING_REMOVE_ITEM_COIN;
	public static boolean ENABLE_GRADE_ITEMS_SPAWNSHOP;
	public static List<CrystalType> GRADE_ITEMS_PAWNSHOP = new ArrayList<>();
	public static List<Integer> ITEMS_NO_RULES_PAWNSHOP;
	public static String ITEMS_NO_RULES_PAWNSHOP_ID;
	public static int AMOUNT_ADENA_ADD_PAWNSHOP;
	public static int AMOUNT_ADENA_REMOVE_PAWNSHOP;
	public static int ITEM_TICKET_SPAWNSHOP;
	public static boolean ALLOW_PVP_NAME_COLOR_SYSTEM;
	public static Map<Integer, Integer> PVP_COLORS = new HashMap<>();
	public static boolean ALLOW_PK_TITLE_COLOR_SYSTEM;
	public static Map<Integer, Integer> PK_COLORS = new HashMap<>();
	public static boolean ANNOUNCE_BOSS_ALIVE;
	public static boolean ANNOUNCE_RAIDBOS_KILL;
	public static boolean ANNOUNCE_GRANDBOS_KILL;
	public static boolean ENABLE_WARNING;
	public static int STORE_BUY_CURRENCY;
	public static int STORE_SELL_CURRENCY;
	public static String[] DAILY_REWARD_RESET_TIME;
	public static boolean ENABLE_REWARD_DAILY;
	public static boolean SHOW_TIME_IN_CHAT;
	public static int GLOBAL_VIP_TIME;
	public static int COR_CHAT_VIP;
	
	public static int CUSTOM_ITEM_ROULETTE;
	public static int ITEM_COUNT_ROLETA;
	/** Skills Argument BUFF */
	public static Map<Integer, Integer> AGATHION_SKILLS;
	public static int SKILL_BUFF_ARGUMENT_1;
	public static int SKILL_BUFF_ARGUMENT_2;
	public static int SKILL_BUFF_ARGUMENT_3;
	public static int SKILL_BUFF_ARGUMENT_4;
	public static int SKILL_BUFF_ARGUMENT_5;
	public static int SKILL_BUFF_ARGUMENT_6;
	public static int SKILL_BUFF_ARGUMENT_7;
	public static int SKILL_BUFF_ARGUMENT_8;
	public static int SKILL_BUFF_ARGUMENT_9;
	public static int SKILL_BUFF_ARGUMENT_10;
	public static int SKILL_BUFF_ARGUMENT_11;
	public static int SKILL_BUFF_ARGUMENT_12;
	public static int SKILL_BUFF_ARGUMENT_1_ID;
	public static int SKILL_BUFF_ARGUMENT_2_ID;
	public static int SKILL_BUFF_ARGUMENT_3_ID;
	public static int SKILL_BUFF_ARGUMENT_4_ID;
	public static int SKILL_BUFF_ARGUMENT_5_ID;
	public static int SKILL_BUFF_ARGUMENT_6_ID;
	public static int SKILL_BUFF_ARGUMENT_7_ID;
	public static int SKILL_BUFF_ARGUMENT_8_ID;
	public static int SKILL_BUFF_ARGUMENT_9_ID;
	public static int SKILL_BUFF_ARGUMENT_10_ID;
	public static int SKILL_BUFF_ARGUMENT_11_ID;
	public static int SKILL_BUFF_ARGUMENT_12_ID;
	public static int SKILL_BUFF_ARGUMENT_1_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_2_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_3_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_4_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_5_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_6_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_7_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_8_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_9_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_10_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_11_LEVEL;
	public static int SKILL_BUFF_ARGUMENT_12_LEVEL;
	public static boolean ALLOW_QUAKE_SYSTEM;
	
	/** Personagem */
	public static int STARTING_ADENA;
	public static boolean CUSTOM_STARTER_ITEMS_ENABLED;
	public static List<int[]> STARTING_CUSTOM_ITEMS_F = new ArrayList<>();
	public static List<int[]> STARTING_CUSTOM_ITEMS_M = new ArrayList<>();
	public static boolean ALLOW_CUSTOM_SPAWN_LOCATION;
	public static int[] CUSTOM_SPAWN_LOCATION = new int[3];
	public static boolean ALLOW_NEW_CHAR_TITLE;
	public static String NEW_CHAR_TITLE;
	public static boolean ALLOW_CUSTOM_CHAR_NOBLE;
	public static int STARTING_LEVEL;
	public static boolean STARTING_BUFFS;
	public static List<int[]> STARTING_BUFFS_M = new ArrayList<>();
	public static List<int[]> STARTING_BUFFS_F = new ArrayList<>();
	public static int UNSTUCK_TIME;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ENABLE_ALL_CLASS_TL_ELFVILLAGE;
	public static boolean ENABLE_WEAPONS_HERO_ALL;
	public static boolean OPEN_EFFECT_CLASSIC_TELEPORTER;
	public static int BOOK_ITEM_CUSTOM;
	public static boolean CUSTOM_TELEGIRAN_ON_DIE;
	public static boolean DISABLE_GRADE_PENALTY;
	public static boolean DISABLE_WEIGHT_PENALTY;
	public static boolean ENABLE_USE_SKILLS_BS;
	public static boolean TELEPORTER_SKILLS_ACTIVED;
	public static boolean ENABLE_USE_SKILLS_PETS;
	public static boolean USE_SKILLS_FORMALWEAR;
	public static boolean USE_ITEMS_FORMALWEAR;
	public static boolean NEW_PLAYER_EFFECT;
	public static boolean ESPECIAL_VIP_LOGIN;
	public static boolean AUTO_MACRO_MENU;
	public static boolean AUTO_MACRO_AUTOFARM;
	/** Skins Variaveis */

	public static int HERO_WEAPON_ENCHANT_LEVEL;
	public static boolean ENABLE_HERO_WEAPON_ENCHANT;
	
	// --------------------------------------------------
	// Server
	// --------------------------------------------------
	
	public static String GAMESERVER_HOSTNAME;
	public static int PORT_GAME;
	public static String EXTERNAL_HOSTNAME;
	public static String INTERNAL_HOSTNAME;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static boolean BLOCK_SERVER_PLAYERS;
	
	public static int MAX_ATTACKERS_NUMBER;
	public static int ATTACKERS_RESPAWN_DELAY;
	public static int MAX_DEFENDERS_NUMBER;
	public static int FLAGS_MAX_COUNT;
	public static int MINIMUM_CLAN_LEVEL;
	public static int SIEGE_LENGTH;
	public static boolean ENABLE_DOOR_KILL_MAGICS;
	public static boolean ENABLE_WINNNER_REWARD_SIEGE_CLAN;
	public static int SIEGE_REWARD_ITEM;
	public static int SIEGE_AMOUNT_REWARD;
	public static boolean ENABLE_CLAN_REGS_SIEGE;
	public static String MESSEGE_CLAN_REGISTRADOS;
	public static int DAY_TO_SIEGE;
	public static int SIEGE_HOUR_GLUDIO;
	public static int SIEGE_HOUR_DION;
	public static int SIEGE_HOUR_GIRAN;
	public static int SIEGE_HOUR_OREN;
	public static int SIEGE_HOUR_ADEN;
	public static int SIEGE_HOUR_INNADRIL;
	public static int SIEGE_HOUR_GODDARD;
	public static int SIEGE_HOUR_RUNE;
	public static int SIEGE_HOUR_SCHUT;
	public static boolean SIEGE_GLUDIO;
	public static boolean SIEGE_DION;
	public static boolean SIEGE_GIRAN;
	public static boolean SIEGE_OREN;
	public static boolean SIEGE_ADEN;
	public static boolean SIEGE_INNADRIL;
	public static boolean SIEGE_GODDARD;
	public static boolean SIEGE_RUNE;
	public static boolean SIEGE_SCHUT;
	public static int SIEGE_DAY_GLUDIO;
	public static int SIEGE_DAY_DION;
	public static int SIEGE_DAY_GIRAN;
	public static int SIEGE_DAY_OREN;
	public static int SIEGE_DAY_ADEN;
	public static int SIEGE_DAY_INNADRIL;
	public static int SIEGE_DAY_GODDARD;
	public static int SIEGE_DAY_RUNE;
	public static int SIEGE_DAY_SCHUT;
	public static boolean ENABLE_SIEGE_MANAGER;
	public static boolean DISABLED_CHAT_NPCS_SIEGE;
	
	/** Access to database */
	public static String DATABASE_URL;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int DELETE_DAYS;
	public static int MAXIMUM_ONLINE_USERS;
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;
	
	/** Jail & Punishements **/
	public static int DEFAULT_PUNISH;
	public static int DEFAULT_PUNISH_PARAM;
	
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	public static boolean SAVE_DROPPED_ITEM;
	
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_ADENA;
	public static float RATE_DROP_SEAL_STONES;
	public static double RATE_CONSUMABLE_COST;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;
	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	
	public static double RATE_DROP_COMMON_HERBS;
	public static double RATE_DROP_HP_HERBS;
	public static double RATE_DROP_MP_HERBS;
	public static double RATE_DROP_SPECIAL_HERBS;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean DEBUG;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	
	/** Deadlock Detector */
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean GMAUDIT;
	
	/** Community Board */
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	
	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	public static int USER_ITEM_TIME_ENCHANT;
	public static int USER_TIME_ENCHANT_SKILL;
	public static int NPC_ENCHANT_SKILL_ID;
	public static boolean ENABLE_ENCHANT_SKILL_NPCID;
	public static int USER_TIME_ARGUMENT_ITEM;
	public static int USER_ITEM_TIME;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;
	public static boolean DISABLE_TUTORIAL;
	
	// --------------------------------------------------
	// Those "hidden" settings haven't configs to avoid admins to fuck their server
	// You still can experiment changing values here. But don't say I didn't warn you.
	// --------------------------------------------------
	
	/** Threads & Packets size */
	public static int THREAD_P_EFFECTS = 6; // default 6 // Configura o número de threads para processar efeitos visuais, ideal para servidores com muitos efeitos simultâneos.
	public static int THREAD_P_GENERAL = 15; // default 15 // Determina o número de threads para tarefas gerais, melhora o processamento de múltiplas ações simultâneas no servidor.
	public static int GENERAL_PACKET_THREAD_CORE_SIZE = 4; // default 4 // Define o número de threads para processar pacotes de rede gerais, como movimento e ataques de jogadores.
	public static int IO_PACKET_THREAD_CORE_SIZE = 2; // default 2 // Define o número de threads para operações de I/O, como leitura e gravação de dados no banco de dados.
	public static int GENERAL_THREAD_CORE_SIZE = 4; // default 4 // Configura o número de threads para tarefas gerais não relacionadas a pacotes de rede ou IA, como movimentação simples de NPCs.
	public static int AI_MAX_THREAD = 10; // default 10 // Define o número de threads para processar a IA de NPCs e bosses, útil para servidores com muitos NPCs complexos.
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false; // default false
	
	/** MMO settings */
	/*
	 * public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20 public static int MMO_MAX_SEND_PER_PASS = 12; // default 12 public static int MMO_MAX_READ_PER_PASS = 12; // default 12 public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	 */
	public static int MMO_SELECTOR_SLEEP_TIME = 5; // default 20 20 20 5 5
	public static int MMO_MAX_READ_PER_PASS = 2250; // default 12 80 180 1500 2250
	public static int MMO_MAX_SEND_PER_PASS = 768; // default 12 80 180 512 768
	public static int MMO_HELPER_BUFFER_COUNT = 192; // default 20 20 20 128 192
	 
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = 80; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = 70; // + 1; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 15; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 120; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 15; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 5; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 5; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 25; // default 5
	
	
	/** PvP Zone */
	public static boolean ENABLE_PVP_ZONE_TIME;
	
	// --------------------------------------------------
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (IOException e)
		{
			_log.warning("Config: Error loading \"" + filename + "\" config.");
		}
		
		return result;
	}
	
	/**
	 * itemId1,itemNumber1;itemId2,itemNumber2... to the int[n][2] = [itemId1][itemNumber1],[itemId2][itemNumber2]...
	 * @param line
	 * @return an array consisting of parsed items.
	 */
	private static final int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
			return null;
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				_log.warning("Config: Error parsing entry -> \"" + valueSplit[0] + "\", should be itemId,itemNumber");
				return null;
			}
			
			result[i] = new int[2];
			try
			{
				result[i][0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item ID -> \"" + valueSplit[0] + "\"");
				return null;
			}
			
			try
			{
				result[i][1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item amount -> \"" + valueSplit[1] + "\"");
				return null;
			}
			i++;
		}
		return result;
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		ALLOW_ALL_PLAYERS_CLAN_SKILLS = Boolean.parseBoolean(clans.getProperty("ClanSkillsForAll", "false"));
		ITEM_ID_BUY_CLAN_HALL = clans.getProperty("ItemIDBuyClanHall", 57);
		ALLOW_CLAN_FULL = Boolean.parseBoolean(clans.getProperty("LimiteFullClan", "false"));
		DISABLE_ROYAL = Boolean.parseBoolean(clans.getProperty("DisableRoyal", "false"));
		ALT_MAX_NUM_OF_MEMBERS_IN_CLAN = Integer.parseInt(clans.getProperty("AltMaxNumOfMembersInClan", "20"));
		ALT_CLAN_JOIN_DAYS = clans.getProperty("MinutesBeforeJoinAClan", 5);
		ALT_CLAN_CREATE_DAYS = clans.getProperty("MinutesBeforeCreateAClan", 10);
		ALT_MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("AltMaxNumOfClansInAlly", 3);
		ALT_CLAN_MEMBERS_FOR_WAR = clans.getProperty("AltClanMembersForWar", 15);
		ALT_CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("AltClanWarPenaltyWhenEnded", 5);
		ALT_CLAN_DISSOLVE_DAYS = clans.getProperty("MinutesToPassToDissolveAClan", 7);
		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("MinutesBeforeJoinAllyWhenLeaved", 1);
		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("MinutesBeforeJoinAllyWhenDismissed", 1);
		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("MinutesBeforeAcceptNewClanWhenDismissed", 1);
		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("MinutesBeforeCreateNewAllyWhenDissolved", 10);
		ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("AltMembersCanWithdrawFromClanWH", false);
		REMOVE_CASTLE_CIRCLETS = clans.getProperty("RemoveCastleCirclets", true);
		
		ALT_MANOR_REFRESH_TIME = clans.getProperty("AltManorRefreshTime", 20);
		ALT_MANOR_REFRESH_MIN = clans.getProperty("AltManorRefreshMin", 0);
		ALT_MANOR_APPROVE_TIME = clans.getProperty("AltManorApproveTime", 6);
		ALT_MANOR_APPROVE_MIN = clans.getProperty("AltManorApproveMin", 0);
		ALT_MANOR_MAINTENANCE_PERIOD = clans.getProperty("AltManorMaintenancePeriod", 360000);
		ALT_MANOR_SAVE_ALL_ACTIONS = clans.getProperty("AltManorSaveAllActions", false);
		ALT_MANOR_SAVE_PERIOD_RATE = clans.getProperty("AltManorSavePeriodRate", 2);
		
		CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000);
		CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
		CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
		CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000);
		CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
		CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
		CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
		CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
		CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
		CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
		CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
		CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
		CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
		CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
		CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
		CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
		CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
		CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
		CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
		CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
		CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
		CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
		CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
		CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
		CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
		CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
		CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
		CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
		CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
		CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
		CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
		CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
		CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
		CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
		CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
		CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
		CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
		CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
		CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
		CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
		CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000);
		CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
		CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
		CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
		CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000);
		CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
		CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
		CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000);
		CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
		CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
	}
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		ID_RESTRICT_OLY = events.getProperty("ItemsPermitidoOly", "10");
		LISTID_RESTRICT_OLY = new ArrayList<>();
		for (String classId : ID_RESTRICT_OLY.split(","))
		{
			LISTID_RESTRICT_OLY.add(Integer.parseInt(classId));
		}
		ENABLED_RESET_SKILLS_OLY = Boolean.parseBoolean(events.getProperty("EnabledResetSkillsOly", "False"));
		String[] property_m = events.getProperty("OlympiadBuffsMage", "1204,2").split(";");
		CUSTOM_BUFFS_M.clear();
		for (String buff : property_m)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("OlympiadBuffsMage[Config.load()]: invalid config property -> OlympiadBuffsMage \"" + buff + "\"");
			else
			{
				try
				{
					CUSTOM_BUFFS_M.add(new int[]
					{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!buff.isEmpty())
						_log.warning("OlympiadBuffsMage[Config.load()]: invalid config property -> OlympiadBuffsMage \"" + buff + "\"");
				}
			}
		}
		
		String[] property_f = events.getProperty("OlympiadBuffsFighter", "1204,2").split(";");
		CUSTOM_BUFFS_F.clear();
		for (String buff : property_f)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("OlympiadBuffsFighter[Config.load()]: invalid config property -> OlympiadBuffsFighter \"" + buff + "\"");
			else
			{
				try
				{
					CUSTOM_BUFFS_F.add(new int[]
					{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!buff.isEmpty())
						_log.warning("OlympiadBuffsFighter[Config.load()]: invalid config property -> OlympiadBuffsFighter \"" + buff + "\"");
				}
			}
		}
		BLOCK_REGISTER_ITEMS_OLYMPIAD_ENCHANT = Boolean.parseBoolean(events.getProperty("EnableSafeEnchantItemsOlympiads", "False"));
		OLLY_GRADE_A = Boolean.parseBoolean(events.getProperty("AllowOllyGradeS", "False"));
		ALT_OLY_ENCHANT_LIMIT = Integer.parseInt(events.getProperty("AltOlyMaxEnchant", "-1"));
		OLY_SKILL_PROTECT = Boolean.parseBoolean(events.getProperty("OlySkillProtect", "True"));
		for (String id : events.getProperty("OllySkillId", "0").split(","))
		{
			OLY_SKILL_LIST.add(Integer.parseInt(id));
		}
		OLY_FIGHT = events.getProperty("AllowOlympiad", false);
		ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS = Boolean.parseBoolean(events.getProperty("AltOlyUseCustomPeriodSettings", "false"));
		ALT_OLY_PERIOD = OlympiadPeriod.valueOf(events.getProperty("AltOlyPeriod", "MONTH"));
		ALT_OLY_PERIOD_MULTIPLIER = Integer.parseInt(events.getProperty("AltOlyPeriodMultiplier", "1"));
		ALT_OLY_END_ANNOUNCE = Boolean.parseBoolean(events.getProperty("AltOlyEndAnnounce", "False"));
		ALT_OLY_START_TIME = events.getProperty("AltOlyStartTime", 18);
		ALT_OLY_MIN = events.getProperty("AltOlyMin", 0);
		ALT_OLY_CPERIOD = events.getProperty("AltOlyCPeriod", 21600000);
		ALT_OLY_BATTLE = events.getProperty("AltOlyBattle", 180000);
		ALT_OLY_WPERIOD = events.getProperty("AltOlyWPeriod", 604800000);
		ALT_OLY_VPERIOD = events.getProperty("AltOlyVPeriod", 86400000);
		ALT_OLY_WAIT_TIME = events.getProperty("AltOlyWaitTime", 30);
		ALT_OLY_WAIT_BATTLE = events.getProperty("AltOlyWaitBattle", 60);
		ALT_OLY_WAIT_END = events.getProperty("AltOlyWaitEnd", 40);
		ALT_OLY_START_POINTS = events.getProperty("AltOlyStartPoints", 18);
		ALT_OLY_WEEKLY_POINTS = events.getProperty("AltOlyWeeklyPoints", 3);
		ALT_OLY_MIN_MATCHES = events.getProperty("AltOlyMinMatchesToBeClassed", 5);
		ALT_OLY_CLASSED = events.getProperty("AltOlyClassedParticipants", 5);
		ALT_OLY_NONCLASSED = events.getProperty("AltOlyNonClassedParticipants", 9);
		ALT_OLY_CLASSED_REWARD = parseItemsList(events.getProperty("AltOlyClassedReward", "6651,50"));
		ALT_OLY_NONCLASSED_REWARD = parseItemsList(events.getProperty("AltOlyNonClassedReward", "6651,30"));
		ALT_OLY_GP_PER_POINT = events.getProperty("AltOlyGPPerPoint", 1000);
		ALT_OLY_HERO_POINTS = events.getProperty("AltOlyHeroPoints", 300);
		ALT_OLY_RANK1_POINTS = events.getProperty("AltOlyRank1Points", 100);
		ALT_OLY_RANK2_POINTS = events.getProperty("AltOlyRank2Points", 75);
		ALT_OLY_RANK3_POINTS = events.getProperty("AltOlyRank3Points", 55);
		ALT_OLY_RANK4_POINTS = events.getProperty("AltOlyRank4Points", 40);
		ALT_OLY_RANK5_POINTS = events.getProperty("AltOlyRank5Points", 30);
		ALT_OLY_MAX_POINTS = events.getProperty("AltOlyMaxPoints", 10);
		ALT_OLY_DIVIDER_CLASSED = events.getProperty("AltOlyDividerClassed", 3);
		ALT_OLY_DIVIDER_NON_CLASSED = events.getProperty("AltOlyDividerNonClassed", 3);
		ALT_OLY_ANNOUNCE_GAMES = events.getProperty("AltOlyAnnounceGames", true);
		
		ALT_GAME_CASTLE_DAWN = events.getProperty("AltCastleForDawn", true);
		ALT_GAME_CASTLE_DUSK = events.getProperty("AltCastleForDusk", true);
		ALT_FESTIVAL_MIN_PLAYER = events.getProperty("AltFestivalMinPlayer", 5);
		ALT_MAXIMUM_PLAYER_CONTRIB = events.getProperty("AltMaxPlayerContrib", 1000000);
		ALT_FESTIVAL_MANAGER_START = events.getProperty("AltFestivalManagerStart", 120000);
		ALT_FESTIVAL_LENGTH = events.getProperty("AltFestivalLength", 1080000);
		ALT_FESTIVAL_CYCLE_LENGTH = events.getProperty("AltFestivalCycleLength", 2280000);
		ALT_FESTIVAL_FIRST_SPAWN = events.getProperty("AltFestivalFirstSpawn", 120000);
		ALT_FESTIVAL_FIRST_SWARM = events.getProperty("AltFestivalFirstSwarm", 300000);
		ALT_FESTIVAL_SECOND_SPAWN = events.getProperty("AltFestivalSecondSpawn", 540000);
		ALT_FESTIVAL_SECOND_SWARM = events.getProperty("AltFestivalSecondSwarm", 720000);
		ALT_FESTIVAL_CHEST_SPAWN = events.getProperty("AltFestivalChestSpawn", 900000);
		ALT_SEVENSIGNS_LAZY_UPDATE = events.getProperty("AltSevenSignsLazyUpdate", true);
		
		FS_TIME_ATTACK = events.getProperty("TimeOfAttack", 50);
		FS_TIME_ENTRY = events.getProperty("TimeOfEntry", 3);
		FS_TIME_WARMUP = events.getProperty("TimeOfWarmUp", 2);
		FS_PARTY_MEMBER_COUNT = events.getProperty("NumberOfNecessaryPartyMembers", 4);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MUTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);
		
		ALLOW_WEDDING = events.getProperty("AllowWedding", false);
		WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);
		
		ALT_LOTTERY_PRIZE = events.getProperty("AltLotteryPrize", 50000);
		ALT_LOTTERY_TICKET_PRICE = events.getProperty("AltLotteryTicketPrice", 2000);
		ALT_LOTTERY_5_NUMBER_RATE = events.getProperty("AltLottery5NumberRate", 0.6);
		ALT_LOTTERY_4_NUMBER_RATE = events.getProperty("AltLottery4NumberRate", 0.2);
		ALT_LOTTERY_3_NUMBER_RATE = events.getProperty("AltLottery3NumberRate", 0.2);
		ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("AltLottery2and1NumberPrize", 200);
		
		ALT_FISH_CHAMPIONSHIP_ENABLED = events.getProperty("AltFishChampionshipEnabled", true);
		ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("AltFishChampionshipRewardItemId", 57);
		ALT_FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("AltFishChampionshipReward1", 800000);
		ALT_FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("AltFishChampionshipReward2", 500000);
		ALT_FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("AltFishChampionshipReward3", 300000);
		ALT_FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("AltFishChampionshipReward4", 200000);
		ALT_FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("AltFishChampionshipReward5", 100000);
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		GEODATA_PATH = geoengine.getProperty("GeoDataPath", "./data/geodata/");
		COORD_SYNCHRONIZE = geoengine.getProperty("CoordSynchronize", -1);
		ENABLE_GEODATA = geoengine.getProperty("EnableGeoData", false);
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		
		PATHFINDING = geoengine.getProperty("PathFinding", true);
		PATHFIND_BUFFERS = geoengine.getProperty("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
		BASE_WEIGHT = geoengine.getProperty("BaseWeight", 10);
		DIAGONAL_WEIGHT = geoengine.getProperty("DiagonalWeight", 14);
		OBSTACLE_MULTIPLIER = geoengine.getProperty("ObstacleMultiplier", 10);
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 20);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 3500);
		DEBUG_PATH = geoengine.getProperty("DebugPath", false);
	}
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final ExProperties hexid = initProperties(HEXID_FILE);
		SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
		HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			Properties hexSetting = new Properties();
			File file = new File(filename);
			file.createNewFile();
			
			OutputStream out = new FileOutputStream(file);
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			hexSetting.store(out, "the hexID to auth into login");
			out.close();
		}
		catch (Exception e)
		{
			_log.warning("Config: Failed to save hex ID to \"" + filename + "\" file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);
		String[] customAllianceNpcIds = npcs.getProperty("CustomNpcsScriptsClan", "").split(",");
		for (String id : customAllianceNpcIds)
		{
			id = id.trim();
			if (id.isEmpty())
				continue;
			
			try
			{
				CUSTOM_NPCS_CLAN_Y_ALLY.add(Integer.parseInt(id));
			}
			catch (NumberFormatException e)
			{
				_log.warning("CustomAllianceNpcs: ID invalido -> " + id);
			}
		}

		WAIT_TIME_FOR_SPAWN_ALL_NPCS = Boolean.parseBoolean(npcs.getProperty("WaitTimeForSpawnAllNpcs", "False"));
		SPAWN_ALL_NPCS_TIME_OPEN_SERVER = npcs.getProperty("SpawnAllNpcsTimeOpenServer", "18:00");
		
		NPC_WITH_EFFECT = npcs.getProperty("NpcWithEffect", "20700");
		LIST_NPC_WITH_EFFECT = new ArrayList<>();
		for (String listid : NPC_WITH_EFFECT.split(","))
		{
			LIST_NPC_WITH_EFFECT.add(Integer.parseInt(listid));
		}
		NPC_WITH_AURA_RED = npcs.getProperty("NpcWithAuraRed", "50009,50010");
		LIST_NPC_WITH_AURA_RED = new ArrayList<>();
		for (String val : NPC_WITH_AURA_RED.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_NPC_WITH_AURA_RED.add(npcId);
		}
		NPC_WITH_AURA_BLUE = npcs.getProperty("NpcWithAuraBlue", "50009,50010");
		LIST_NPC_WITH_AURA_BLUE = new ArrayList<>();
		for (String val : NPC_WITH_AURA_BLUE.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_NPC_WITH_AURA_BLUE.add(npcId);
		}
		CHAMPION_FREQUENCY = npcs.getProperty("ChampionFrequency", 0);
		CHAMP_MIN_LVL = npcs.getProperty("ChampionMinLevel", 20);
		CHAMP_MAX_LVL = npcs.getProperty("ChampionMaxLevel", 70);
		CHAMPION_HP = npcs.getProperty("ChampionHp", 8);
		CHAMPION_HP_REGEN = npcs.getProperty("ChampionHpRegen", 1.);
		CHAMPION_REWARDS = npcs.getProperty("ChampionRewards", 8);
		CHAMPION_ADENAS_REWARDS = npcs.getProperty("ChampionAdenasRewards", 1);
		CHAMPION_ATK = npcs.getProperty("ChampionAtk", 1.);
		CHAMPION_SPD_ATK = npcs.getProperty("ChampionSpdAtk", 1.);
		CHAMPION_REWARD = npcs.getProperty("ChampionRewardItem", 0);
		CHAMPION_REWARD_ID = npcs.getProperty("ChampionRewardItemID", 6393);
		CHAMPION_REWARD_QTY = npcs.getProperty("ChampionRewardItemQty", 1);
		L2JMOD_CHAMPION_ENABLE_AURA = Integer.parseInt(npcs.getProperty("ChampionEnableAura", "0"));
		AGGRESSION_CHAMPION = Boolean.parseBoolean(npcs.getProperty("DisableAgressionChampion", "False"));
		SHOW_RED_NAME_CHAMPION = Boolean.parseBoolean(npcs.getProperty("EnableRedNameChampion", "False"));
		ENABLE_FREE_GK_SEVENSINGS = Boolean.parseBoolean(npcs.getProperty("EnableFreeGKSevenSigns", "False"));
		ENABLE_USED_MOM = Boolean.parseBoolean(npcs.getProperty("EnableFreeMOM", "False"));
		BLOCK_SELL_ITEMS_ADENA = Boolean.parseBoolean(npcs.getProperty("DisablePriceAdenaItems", "False"));
		BUFFER_MAX_SCHEMES = npcs.getProperty("BufferMaxSchemesPerChar", 4); // 1
		BUFFER_STATIC_BUFF_COST = npcs.getProperty("BufferStaticCostPerBuff", -1);
		FIGHTER_BUFF = npcs.getProperty("FighterBuffList", "0");
		
		for (String id : FIGHTER_BUFF.trim().split(","))
		{
			int skillId = Integer.parseInt(id.trim());
			if (skillId > 0)
				FIGHTER_BUFF_LIST.add(skillId);
		}
		
		MAGE_BUFF = npcs.getProperty("MageBuffList", "0");
		
		for (String id : MAGE_BUFF.trim().split(","))
		{
			int skillId = Integer.parseInt(id.trim());
			if (skillId > 0)
				MAGE_BUFF_LIST.add(skillId);
			
		}
		ALTERNATE_CLASS_MASTER = npcs.getProperty("AlternateClassMaster", false);
		ALLOW_CLASS_MASTERS = npcs.getProperty("AllowClassMasters", false);
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		if (ALLOW_CLASS_MASTERS)
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		
		// ALT_GAME_FREE_TELEPORT = npcs.getProperty("AltFreeTeleporting", false);
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", true);
		ENABLE_NPC_SPAWN_ANNOUNCEMENT = npcs.getProperty("EnableNpcSpawnQuests", true);
		ALT_MOB_AGRO_IN_PEACEZONE = npcs.getProperty("AltMobAgroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_ALLOW_UPGRADER = npcs.getProperty("AllowWyvernUpgrader", true);
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		// RAID_HP_REGEN_MULTIPLIER = npcs.getProperty("RaidHpRegenMultiplier", 1.);
		// RAID_MP_REGEN_MULTIPLIER = npcs.getProperty("RaidMpRegenMultiplier", 1.);
		// RAID_DEFENCE_MULTIPLIER = npcs.getProperty("RaidDefenceMultiplier", 1.);
		// RAID_MINION_RESPAWN_TIMER = npcs.getProperty("RaidMinionRespawnTime", 300000);
		
		// RAID_DISABLE_CURSE = npcs.getProperty("DisableRaidCurse", false);
		// RAID_CHAOS_TIME = npcs.getProperty("RaidChaosTime", 30);
		// GRAND_CHAOS_TIME = npcs.getProperty("GrandChaosTime", 30);
		// MINION_CHAOS_TIME = npcs.getProperty("MinionChaosTime", 30);
		
		// SPAWN_INTERVAL_AQ = npcs.getProperty("AntQueenSpawnInterval", 36);
		// RANDOM_SPAWN_TIME_AQ = npcs.getProperty("AntQueenRandomSpawn", 17);
		
		// SPAWN_INTERVAL_ANTHARAS = npcs.getProperty("AntharasSpawnInterval", 264);
		// RANDOM_SPAWN_TIME_ANTHARAS = npcs.getProperty("AntharasRandomSpawn", 72);
		// WAIT_TIME_ANTHARAS = npcs.getProperty("AntharasWaitTime", 30) * 60000;
		
		// SPAWN_INTERVAL_BAIUM = npcs.getProperty("BaiumSpawnInterval", 168);
		// RANDOM_SPAWN_TIME_BAIUM = npcs.getProperty("BaiumRandomSpawn", 48);
		
		// SPAWN_INTERVAL_CORE = npcs.getProperty("CoreSpawnInterval", 60);
		// RANDOM_SPAWN_TIME_CORE = npcs.getProperty("CoreRandomSpawn", 23);
		
		// SPAWN_INTERVAL_FRINTEZZA = npcs.getProperty("FrintezzaSpawnInterval", 48);
		// RANDOM_SPAWN_TIME_FRINTEZZA = npcs.getProperty("FrintezzaRandomSpawn", 8);
		// WAIT_TIME_FRINTEZZA = npcs.getProperty("FrintezzaWaitTime", 1) * 60000;
		
		// SPAWN_INTERVAL_ORFEN = npcs.getProperty("OrfenSpawnInterval", 48);
		// RANDOM_SPAWN_TIME_ORFEN = npcs.getProperty("OrfenRandomSpawn", 20);
		
		// SPAWN_INTERVAL_SAILREN = npcs.getProperty("SailrenSpawnInterval", 36);
		// RANDOM_SPAWN_TIME_SAILREN = npcs.getProperty("SailrenRandomSpawn", 24);
		// WAIT_TIME_SAILREN = npcs.getProperty("SailrenWaitTime", 5) * 60000;
		
		// SPAWN_INTERVAL_VALAKAS = npcs.getProperty("ValakasSpawnInterval", 264);
		// RANDOM_SPAWN_TIME_VALAKAS = npcs.getProperty("ValakasRandomSpawn", 72);
		// WAIT_TIME_VALAKAS = npcs.getProperty("ValakasWaitTime", 30) * 60000;
		
		// SPAWN_INTERVAL_ZAKEN = npcs.getProperty("ZakenSpawnInterval", 60);
		// RANDOM_SPAWN_TIME_ZAKEN = npcs.getProperty("ZakenRandomSpawn", 20);
		
		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 300);
		KNOWNLIST_UPDATE_INTERVAL = npcs.getProperty("KnownListUpdateInterval", 1250);
		MIN_NPC_ANIMATION = npcs.getProperty("MinNPCAnimation", 20);
		MAX_NPC_ANIMATION = npcs.getProperty("MaxNPCAnimation", 40);
		MIN_MONSTER_ANIMATION = npcs.getProperty("MinMonsterAnimation", 10);
		MAX_MONSTER_ANIMATION = npcs.getProperty("MaxMonsterAnimation", 40);
		
		GRIDS_ALWAYS_ON = npcs.getProperty("GridsAlwaysOn", false);
		GRID_NEIGHBOR_TURNON_TIME = npcs.getProperty("GridNeighborTurnOnTime", 1);
		GRID_NEIGHBOR_TURNOFF_TIME = npcs.getProperty("GridNeighborTurnOffTime", 90);
	}
	
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		AUTO_LEARN_DIVINE_INSPIRATION = players.getProperty("AutoLearnDivineInspiration", true);
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 0);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALT_GAME_DELEVEL = players.getProperty("Delevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_QUEST_ITEMS = players.getProperty("MaximumSlotsForQuestItems", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		ALT_WEIGHT_LIMIT = players.getProperty("AltWeightLimit", 1);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		ALT_GAME_FREIGHTS = players.getProperty("AltGameFreights", false);
		ALT_GAME_FREIGHT_PRICE = players.getProperty("AltGameFreightPrice", 1000);
		
		// ENCHANT_CHANCE_WEAPON_MAGIC = players.getProperty("EnchantChanceMagicWeapon", 0.4);
		// ENCHANT_CHANCE_WEAPON_MAGIC_15PLUS = players.getProperty("EnchantChanceMagicWeapon15Plus", 0.2);
		// ENCHANT_CHANCE_WEAPON_NONMAGIC = players.getProperty("EnchantChanceNonMagicWeapon", 0.7);
		// ENCHANT_CHANCE_WEAPON_NONMAGIC_15PLUS = players.getProperty("EnchantChanceNonMagicWeapon15Plus", 0.35);
		// ENCHANT_CHANCE_ARMOR = players.getProperty("EnchantChanceArmor", 0.66);
		// ENCHANT_MAX_WEAPON = players.getProperty("EnchantMaxWeapon", 0);
		// ENCHANT_MAX_ARMOR = players.getProperty("EnchantMaxArmor", 0);
		// ENCHANT_SAFE_MAX = players.getProperty("EnchantSafeMax", 3);
		// ENCHANT_SAFE_MAX_FULL = players.getProperty("EnchantSafeMaxFull", 4);
		
		AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);
		
		KARMA_PLAYER_CAN_BE_KILLED_IN_PZ = players.getProperty("KarmaPlayerCanBeKilledInPeaceZone", false);
		KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", true);
		KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
		KARMA_PLAYER_CAN_USE_WH = players.getProperty("KarmaPlayerCanUseWareHouse", true);
		KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
		KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", "1147,425,1146,461,10,2368,7,6,2370,2369");
		
		String[] array = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(array[i]);
		
		array = KARMA_NONDROPPABLE_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(array[i]);
		
		// sorting so binarySearch can be used later
		Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
		Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);
		
		PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 15000);
		PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 30000);
		
		PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
		ALT_PARTY_RANGE = players.getProperty("AltPartyRange", 1600);
		ALT_PARTY_RANGE2 = players.getProperty("AltPartyRange2", 1400);
		ALT_LEAVE_PARTY_LEADER = players.getProperty("AltLeavePartyLeader", false);
		
		EVERYBODY_HAS_ADMIN_RIGHTS = players.getProperty("EverybodyHasAdminRights", false);
		MASTERACCESS_LEVEL = players.getProperty("MasterAccessLevel", 127);
		MASTERACCESS_NAME_COLOR = Integer.decode("0x" + players.getProperty("MasterNameColor", "00FF00"));
		MASTERACCESS_TITLE_COLOR = Integer.decode("0x" + players.getProperty("MasterTitleColor", "00FF00"));
		SHOW_FILE_GM = players.getProperty("InfoHTMLGMChat", false);
		GM_HERO_AURA = players.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", true);
		GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", true);
		GM_STARTUP_SILENCE = players.getProperty("GMStartupSilence", true);
		GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		ALT_BLACKSMITH_USE_RECIPES = players.getProperty("AltBlacksmithUseRecipes", true);
		
		AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
		ALT_GAME_MAGICFAILURES = players.getProperty("MagicFailures", true);
		ALT_GAME_SHIELD_BLOCKS = players.getProperty("AltShieldBlocks", false);
		ALT_PERFECT_SHLD_BLOCK = players.getProperty("AltPerfectShieldBlockRate", 10);
		LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = players.getProperty("AltSubClassWithoutQuests", false);
		KEEP_SUBCLASS_SKILLS = Boolean.parseBoolean(players.getProperty("AcumularSubClasse", "False"));
		// SUBCLASS_WITHOUT_QUESTS = players.getProperty("SubClassWithoutQuests", false);
		CUSTOM_SUBCLASS_LVL = players.getProperty("SubclassLevel", 40);
		ALLOWED_SUBCLASS = Integer.parseInt(players.getProperty("AllowedSubclass", "3"));
		
		BUFFS_MAX_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
		STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
	}
	
	/**
	 * Load Champion Invade
	 */
	private static final void loadChampionInvade()
	{
		final ExProperties ChampionInvade = initProperties(Config.CHAMPION_EVENT);
		CHAMPION_FARM_BY_TIME_OF_DAY = Boolean.parseBoolean(ChampionInvade.getProperty("ChampionInvadeEnabled", "false"));
		EVENT_CHAMPION_FARM_TIME = Integer.parseInt(ChampionInvade.getProperty("ChampionInvadeEventTime", "1"));
		EVENT_CHAMPION_FARM_INTERVAL_BY_TIME_OF_DAY = ChampionInvade.getProperty("ChampionInvadeStartTime", "20:00").split(",");
		CHAMPION_MESSAGE_ENABLED = Boolean.parseBoolean(ChampionInvade.getProperty("ChampionInvadeMessageEnabled", "false"));
		CHAMPION_FARM_MESSAGE_TEXT = ChampionInvade.getProperty("ScreenChampionInvadeMessageText", "Welcome to l2j server!");
		
		// Porcentagem para aparece champion
		CHAMPION_INVADE_FREQUENCY = Integer.parseInt(ChampionInvade.getProperty("ChampionPercent", "1"));
		
		// Lista de monstros que iram dar aparecer champion
		CHAMPION_MONSTER = ChampionInvade.getProperty("List_Champion", "0");
		LIST_NPC_CHAMPION_MONSTER = new ArrayList<>();
		for (String listid : CHAMPION_MONSTER.split(","))
		{
			LIST_NPC_CHAMPION_MONSTER.add(Integer.parseInt(listid));
		}
		
		// Titulo Champion Invade
		TITLE_CHAMPION_INVADE = ChampionInvade.getProperty("ChampionInvadeTitle", "");
		CHAMPION_INVADE_ENABLE_AURA = Integer.parseInt(ChampionInvade.getProperty("ChampionInvadeEnableAura", "0"));
		RED_NAME_CHAMPION_INVADE = Boolean.parseBoolean(ChampionInvade.getProperty("EnableChampionNameRED", "false"));
		
		// Recompensas durante evento ativo
		String[] a = ChampionInvade.getProperty("RewardList", "57,0").split(";");
		CHAMPION_INVADE_DROP.clear();
		for (String reward : a)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
				_log.warning("RewardList ERROR");
			else
			{
				try
				{
					CHAMPION_INVADE_DROP.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("RewardList ERROR");
				}
			}
		}
		
		// VIP GANHAR 2X NO DROP INVADE?
		VIP_REWARD_BONUS_INVADE = Boolean.parseBoolean(ChampionInvade.getProperty("VIPBonusDrop", "false"));
	}
	
	/**
	 * Load Solo Boss New
	 */
	private static final void loadSoloBoss()
	{
		final ExProperties bossSolo = initProperties(Config.SOLOBOSS_FILE);
		SOLO_BOSS_EVENT = bossSolo.getProperty("SoloBossEvent", false);
		SCREN_MSG_BOSS_SOLO = bossSolo.getProperty("AutoInviteSoloBoss", false);
		SOLO_BOSS_EVENT_INTERVAL_BY_TIME_OF_DAY = bossSolo.getProperty("SoloBossStartTime", "20:00").split(",");
		
		EVENT_SOLOBOSS_TIME = bossSolo.getProperty("TimeDurationEvent", 1);
		
		// Boss 1
		SOLO_BOSS_ID_ONE = bossSolo.getProperty("SoloBossOne", 1);
		String custom_spawn_locationOne = bossSolo.getProperty("SoloBossOne_Loc", "113852,-108766,-851");
		String custom_spawn_location_splittedOne[] = custom_spawn_locationOne.split(",");
		SOLO_BOSS_ID_ONE_LOC[0] = Integer.parseInt(custom_spawn_location_splittedOne[0]);
		SOLO_BOSS_ID_ONE_LOC[1] = Integer.parseInt(custom_spawn_location_splittedOne[1]);
		SOLO_BOSS_ID_ONE_LOC[2] = Integer.parseInt(custom_spawn_location_splittedOne[2]);
		
		// Boss 2
		SOLO_BOSS_ID_TWO = bossSolo.getProperty("SoloBossTWO", 1);
		String spawn_boss_two = bossSolo.getProperty("SoloBossTWO_Loc", "113852,-108766,-851");
		String custom_spawn_location_splitte2d[] = spawn_boss_two.split(",");
		SOLO_BOSS_ID_TWO_LOC[0] = Integer.parseInt(custom_spawn_location_splitte2d[0]);
		SOLO_BOSS_ID_TWO_LOC[1] = Integer.parseInt(custom_spawn_location_splitte2d[1]);
		SOLO_BOSS_ID_TWO_LOC[2] = Integer.parseInt(custom_spawn_location_splitte2d[2]);
		
		// Boss 3
		SOLO_BOSS_ID_THREE = bossSolo.getProperty("SoloBossTHREE", 1);
		String spawn_boss_THREE = bossSolo.getProperty("SoloBossTHREE_Loc", "113852,-108766,-851");
		String custom_spawn_location_splitte3d[] = spawn_boss_THREE.split(",");
		SOLO_BOSS_ID_THREE_LOC[0] = Integer.parseInt(custom_spawn_location_splitte3d[0]);
		SOLO_BOSS_ID_THREE_LOC[1] = Integer.parseInt(custom_spawn_location_splitte3d[1]);
		SOLO_BOSS_ID_THREE_LOC[2] = Integer.parseInt(custom_spawn_location_splitte3d[2]);
		
		// Boss 4
		SOLO_BOSS_ID_FOUR = bossSolo.getProperty("SoloBossFOUR", 1);
		String spawn_boss_FOUR = bossSolo.getProperty("SoloBossFOUR_Loc", "113852,-108766,-851");
		String custom_spawn_location_splitte4d[] = spawn_boss_FOUR.split(",");
		SOLO_BOSS_ID_FOUR_LOC[0] = Integer.parseInt(custom_spawn_location_splitte4d[0]);
		SOLO_BOSS_ID_FOUR_LOC[1] = Integer.parseInt(custom_spawn_location_splitte4d[1]);
		SOLO_BOSS_ID_FOUR_LOC[2] = Integer.parseInt(custom_spawn_location_splitte4d[2]);
		
		// Boss 5
		SOLO_BOSS_ID_FIVE = bossSolo.getProperty("SoloBossFIVE", 1);
		String spawn_boss_FIVE = bossSolo.getProperty("SoloBossFIVE_Loc", "113852,-108766,-851");
		String custom_spawn_location_splitte5d[] = spawn_boss_FIVE.split(",");
		SOLO_BOSS_ID_FIVE_LOC[0] = Integer.parseInt(custom_spawn_location_splitte5d[0]);
		SOLO_BOSS_ID_FIVE_LOC[1] = Integer.parseInt(custom_spawn_location_splitte5d[1]);
		SOLO_BOSS_ID_FIVE_LOC[2] = Integer.parseInt(custom_spawn_location_splitte5d[2]);
		
		// Range
		RANGE_SOLO_BOSS = bossSolo.getProperty("RangeRewardPlayers", 1);
		SOLO_BOSS_REWARDS_ONE = Config.parseReward(bossSolo, "ListaDeRecompensaBoss_1");
		SOLO_BOSS_REWARDS_TWO = Config.parseReward(bossSolo, "ListaDeRecompensaBoss_2");
		SOLO_BOSS_REWARDS_THREE = Config.parseReward(bossSolo, "ListaDeRecompensaBoss_3");
		SOLO_BOSS_REWARDS_FOUR = Config.parseReward(bossSolo, "ListaDeRecompensaBoss_4");
		SOLO_BOSS_REWARDS_FIVE = Config.parseReward(bossSolo, "ListaDeRecompensaBoss_5");
	}
	
	/**
	 * Load Mission
	 */
	private static final void loadMission()
	{
		final ExProperties Mission = initProperties(Config.MISSION_FILE);
		
		BOSS_LIST_MISSION_ID = Mission.getProperty("BossListMission", "");
		BOSS_LIST_MISSION = new ArrayList<>();
		for (String BOSS_id : BOSS_LIST_MISSION_ID.split(","))
		{
			if (!BOSS_id.equals(""))
				BOSS_LIST_MISSION.add(Integer.parseInt(BOSS_id));
		}
		ACTIVE_MISSION = Boolean.parseBoolean(Mission.getProperty("ActiveMission", "false"));
		ACTIVE_MISSION_TVT = Boolean.parseBoolean(Mission.getProperty("ActiveTvTMission", "false"));
		MISSION_TVT_CONT = Integer.parseInt(Mission.getProperty("TvTCont", "1"));
		MISSION_TVT_REWARD_ID = Integer.parseInt(Mission.getProperty("TvTReward", "6392"));
		MISSION_TVT_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("TvTRewardAmmount", "1"));
		
		ACTIVE_MISSION_RAID = Boolean.parseBoolean(Mission.getProperty("ActiveRaidMission", "false"));
		MISSION_RAID_CONT = Integer.parseInt(Mission.getProperty("RaidCont", "1"));
		MISSION_RAID_REWARD_ID = Integer.parseInt(Mission.getProperty("RaidReward", "6392"));
		MISSION_RAID_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("RaidRewardAmmount", "1"));
		
		ACTIVE_MISSION_MOB = Boolean.parseBoolean(Mission.getProperty("ActiveMobMission", "false"));
		MISSION_MOB_CONT = Integer.parseInt(Mission.getProperty("MobCont", "1"));
		
		ACTIVE_MISSION_PARTY_MOB = Boolean.parseBoolean(Mission.getProperty("ActivePartyMobMission", "false"));
		MISSION_PARTY_MOB_CONT = Integer.parseInt(Mission.getProperty("PartyMobCont", "1"));
		MISSION_PARTY_MOB_REWARD_ID = Integer.parseInt(Mission.getProperty("PartyMobReward", "6392"));
		MISSION_PARTY_MOB_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("PartyMobRewardAmmount", "1"));
		
		MISSION_LIST_MOBS = Mission.getProperty("ListMobs", "0");
		MISSION_LIST_MONSTER = new ArrayList<>();
		for (String id : MISSION_LIST_MOBS.split(","))
			MISSION_LIST_MONSTER.add(Integer.valueOf(Integer.parseInt(id)));
		MISSION_MOB_REWARD_ID = Integer.parseInt(Mission.getProperty("MobReward", "6392"));
		MISSION_MOB_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("MobRewardAmmount", "1"));
		
		ACTIVE_MISSION_1X1 = Boolean.parseBoolean(Mission.getProperty("Active1x1Mission", "false"));
		MISSION_1X1_CONT = Integer.parseInt(Mission.getProperty("1x1Cont", "1"));
		MISSION_1X1_REWARD_ID = Integer.parseInt(Mission.getProperty("1x1Reward", "6392"));
		MISSION_1X1_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("1x1RewardAmmount", "1"));
		
		ACTIVE_MISSION_3X3 = Boolean.parseBoolean(Mission.getProperty("Active3x3Mission", "false"));
		MISSION_3X3_CONT = Integer.parseInt(Mission.getProperty("3x3Cont", "1"));
		MISSION_3X3_REWARD_ID = Integer.parseInt(Mission.getProperty("3x3Reward", "6392"));
		MISSION_3X3_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("3x3RewardAmmount", "1"));
		
		ACTIVE_MISSION_5X5 = Boolean.parseBoolean(Mission.getProperty("Active5x5Mission", "false"));
		MISSION_5X5_CONT = Integer.parseInt(Mission.getProperty("5x5Cont", "1"));
		MISSION_5X5_REWARD_ID = Integer.parseInt(Mission.getProperty("5x5Reward", "6392"));
		MISSION_5X5_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("5x5RewardAmmount", "1"));
		
		ACTIVE_MISSION_9X9 = Boolean.parseBoolean(Mission.getProperty("Active9x9Mission", "false"));
		MISSION_9X9_CONT = Integer.parseInt(Mission.getProperty("9x9Cont", "1"));
		MISSION_9X9_REWARD_ID = Integer.parseInt(Mission.getProperty("9x9Reward", "6392"));
		MISSION_9X9_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("9x9RewardAmmount", "1"));
		
		CLEAR_MISSION_INTERVAL_BY_TIME_OF_DAY = Mission.getProperty("ClearMissionStartTime", "20:00").split(",");
	}
	
	/**
	 * Load Pvp Event
	 */
	private static final void loadPvpEvent()
	{
		final ExProperties pvpevent = initProperties(Config.PVP_EVENT_FILE);
		
		PVP_EVENT_ENABLED = pvpevent.getProperty("PvPEventEnabled", false);
		PVP_EVENT_INTERVAL = pvpevent.getProperty("PvPZEventInterval", "20:00").split(",");
		PVP_EVENT_RUNNING_TIME = pvpevent.getProperty("PvPZEventRunningTime", 120);
		
		PVP_EVENT_RANK_REWARDS.clear();
		for (String key : pvpevent.stringPropertyNames())
		{
			if (key.startsWith("Rank_"))
			{
				try
				{
					int rank = Integer.parseInt(key.substring(5)); // pega o número depois de "Rank_"
					String line = pvpevent.getProperty(key);
					if (line.isEmpty())
						continue;
					
					List<int[]> rewardList = new ArrayList<>();
					for (String part : line.split(";"))
					{
						String[] itemSplit = part.trim().split(",");
						if (itemSplit.length != 2)
							continue;
						
						int itemId = Integer.parseInt(itemSplit[0]);
						int amount = Integer.parseInt(itemSplit[1]);
						rewardList.add(new int[]
						{
							itemId,
							amount
						});
					}
					PVP_EVENT_RANK_REWARDS.put(rank, rewardList);
				}
				catch (Exception e)
				{
					_log.warning("Erro ao carregar recompensa do " + key + ": " + e.getMessage());
				}
			}
		}
		
		ALLOW_SPECIAL_PVP_REWARD = pvpevent.getProperty("SpecialPvpRewardEnabled", false);
		PVP_SPECIAL_ITEMS_REWARD = new ArrayList<>();
		String[] pvpSpeReward = pvpevent.getProperty("SpecialPvpItemsReward", "57,100000").split(";");
		for (String reward : pvpSpeReward)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
			{
				_log.warning("SpecialPvpItemsReward: inválido -> " + reward);
			}
			else
			{
				try
				{
					PVP_SPECIAL_ITEMS_REWARD.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("Erro ao converter SpecialPvpItemsReward -> " + reward);
				}
			}
		}
		
		SCREN_MSG_PVP = pvpevent.getProperty("SummonToPvPEnabled", false);
		pvp_locx = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocx", "1"));
		pvp_locy = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocy", "1"));
		pvp_locz = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocz", "1"));
		
		NAME_PVP = pvpevent.getProperty("NamePvPEvent", "PvPEvent:");
		NO_INVITE_PVPEVENT = pvpevent.getProperty("BlockInvitePTEvent", false);
		BLOCK_PVPEVENTCLASS = pvpevent.getProperty("BlockClassHealePvPEvent", false);
		STRING_NAME_PVPEVENT = pvpevent.getProperty("NewNameCharacterPVP", "Welcome to l2j server!");
		ZONEPVPEVENT_ALLOW_INTERFERENCE = pvpevent.getProperty("BlockInterfacePvPEvent", false);
		ENABLE_NAME_TITLE_PVPEVENT = pvpevent.getProperty("EnableTitleYNamePvPEvent", false);
		BLOCK_CREST_PVPEVENT = pvpevent.getProperty("BlockCrestInPvPEventZone", false);
		
		WEAPON_ID_ENCHANT_RESTRICT = pvpevent.getProperty("WeaponAllowedToEnchant", "");
		WEAPON_LIST_ID_ENCHANT_RESTRICT = new ArrayList<>();
		for (String id : WEAPON_ID_ENCHANT_RESTRICT.split(","))
			if (!id.isEmpty())
				WEAPON_LIST_ID_ENCHANT_RESTRICT.add(Integer.parseInt(id));
			
		ARMOR_ID_ENCHANT_RESTRICT = pvpevent.getProperty("ArmorAllowedToEnchant", "");
		ARMOR_LIST_ID_ENCHANT_RESTRICT = new ArrayList<>();
		for (String id : ARMOR_ID_ENCHANT_RESTRICT.split(","))
			if (!id.isEmpty())
				ARMOR_LIST_ID_ENCHANT_RESTRICT.add(Integer.parseInt(id));
			
		PVP_ITEM_ENCHANT_EVENT = pvpevent.getProperty("EnchantEquipByPvp", false);
		PVP_ITEM_ENCHANT_WEAPON_CHANCE = Float.parseFloat(pvpevent.getProperty("ChanceToEnchantWeapon", "1.0"));
		PVP_ITEM_ENCHANT_ARMOR_CHANCE = Float.parseFloat(pvpevent.getProperty("ChanceToEnchantArmor", "1.0"));
		
		CHECK_MIN_ENCHANT_WEAPON = pvpevent.getProperty("CheckMinEnchatWeapon", 0);
		CHECK_MAX_ENCHANT_WEAPON = pvpevent.getProperty("CheckMaxEnchatWeapon", 0);
		CHECK_MIN_ENCHANT_ARMOR_JEWELS = pvpevent.getProperty("CheckMinEnchatArmorJewels", 0);
		CHECK_MAX_ENCHANT_ARMOR_JEWELS = pvpevent.getProperty("CheckMaxEnchatArmorJewels", 0);
	}
	
	/**
	 * Loads Spoil Event,<br>
	 */
	private static final void loadSpoilEvent()
	{
		final ExProperties BestFarm = initProperties(SPOIL_FILE);
		BLOCK_AUTOFARM_SPOILZONE = Boolean.parseBoolean(BestFarm.getProperty("BlockAutoFarmSpoilZone", "10"));
		SPOIL_FARM_MONSTER_DALAY = Integer.parseInt(BestFarm.getProperty("MonsterDelay", "10"));
		START_SPOIL = Boolean.parseBoolean(BestFarm.getProperty("SpoilEventEnabled", "false"));
		EVENT_SPOIL_FARM_TIME = Integer.parseInt(BestFarm.getProperty("EventSpoilFarmTime", "1"));
		EVENT_SPOIL_FARM_INTERVAL_BY_TIME_OF_DAY = BestFarm.getProperty("SpoilFarmStartTime", "20:00").split(",");
		SPOIL_FARM_MESSAGE_TEXT = BestFarm.getProperty("ScreenSpoilFarmMessageText", "Welcome to l2j server!");
		String[] monsterLocs2 = BestFarm.getProperty("MonsterLoc", "").split(";");
		String[] locSplit3 = null;
		
		SPOIL_MESSAGE_ENABLED = Boolean.parseBoolean(BestFarm.getProperty("SpoilEventEnabled", "false"));
		SPOILMONSTER_ID = Integer.parseInt(BestFarm.getProperty("SpoilMonsterId", "1"));
		
		SPOIL_LOCS_COUNT = monsterLocs2.length;
		SPOIL_LOCS = new int[SPOIL_LOCS_COUNT][3];
		int g;
		for (int e = 0; e < SPOIL_LOCS_COUNT; e++)
		{
			locSplit3 = monsterLocs2[e].split(",");
			for (g = 0; g < 3; g++)
			{
				SPOIL_LOCS[e][g] = Integer.parseInt(locSplit3[g].trim());
			}
		}
		
		// gk
		String[] monsterLocs3 = BestFarm.getProperty("GkLoc", "").split(";");
		String[] locSplit5 = null;
		
		GKNPC_ID = Integer.parseInt(BestFarm.getProperty("GkEventID", "1"));
		
		GK_LOCS_COUNT = monsterLocs3.length;
		GK_LOCS = new int[GK_LOCS_COUNT][3];
		int g2;
		for (int e = 0; e < GK_LOCS_COUNT; e++)
		{
			locSplit5 = monsterLocs3[e].split(",");
			for (g2 = 0; g2 < 3; g2++)
			{
				GK_LOCS[e][g2] = Integer.parseInt(locSplit5[g2].trim());
			}
		}
	}
	
	/**
	 * Loads Reward Solo Event
	 */
	private static final void loadSoloEvent()
	{
		final ExProperties SoloEvent = initProperties(REWARD_SOLO_EVENT);
		SOLO_FARM_BY_TIME_OF_DAY = Boolean.parseBoolean(SoloEvent.getProperty("RewardSoloEventEnabled", "false"));
		EVENT_SOLO_FARM_TIME = Integer.parseInt(SoloEvent.getProperty("RewardSoloEventTime", "1"));
		EVENT_SOLO_FARM_INTERVAL_BY_TIME_OF_DAY = SoloEvent.getProperty("RewardSoloStartTime", "20:00").split(",");
		SOLO_MESSAGE_ENABLED = Boolean.parseBoolean(SoloEvent.getProperty("SoloEventMessageEnabled", "false"));
		SOLO_FARM_MESSAGE_TEXT = SoloEvent.getProperty("ScreenRewardSoloMessageText", "Welcome to l2j server!");
		
		// Lista de monstros que iram dar recompensa
		CUSTOM_MONSTER = SoloEvent.getProperty("List_Monster", "0");
		LIST_NPC_CUSTOM_MONSTER = new ArrayList<>();
		for (String listid : CUSTOM_MONSTER.split(","))
		{
			LIST_NPC_CUSTOM_MONSTER.add(Integer.parseInt(listid));
		}
		
		// Recompensas durante evento ativo
		String[] a = SoloEvent.getProperty("DropList_Monster", "57,0").split(";");
		CUSTOM_MONSTER_DROP.clear();
		for (String reward : a)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
				_log.warning("DropList_Monster ERROR");
			else
			{
				try
				{
					CUSTOM_MONSTER_DROP.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("DropList_Monster ERROR");
				}
			}
		}
		
	}
	
	/**
	 * Loads Party Farm Events,<br>
	 */
	private static final void loadPartyFarm()
	{
		final ExProperties BestFarm = initProperties(PARTYFARM_EVENT);
		ENABLE_SPAWN_PARTYFARM_MONSTERS = Boolean.parseBoolean(BestFarm.getProperty("EnableSpawnPartyFarmMonsters", "false"));
		PART_ZONE_MONSTERS_EVENT = BestFarm.getProperty("PartyEventMonster");
		PART_ZONE_MONSTERS_EVENT_ID = new ArrayList<>();
		for (String id : PART_ZONE_MONSTERS_EVENT.split(","))
			PART_ZONE_MONSTERS_EVENT_ID.add(Integer.parseInt(id));
		PARTY_ZONE_REWARDS = parseReward(BestFarm, "PartyZoneReward");
		
		PARTY_FARM_MONSTER_DALAY = Integer.parseInt(BestFarm.getProperty("MonsterDelay", "10"));
		// PARTY_FARM_BY_TIME_OF_DAY = Boolean.parseBoolean(BestFarm.getProperty("PartyFarmEventEnabled", "false"));
		START_PARTY = Boolean.parseBoolean(BestFarm.getProperty("PartyFarmEventEnabled", "false"));
		ENABLE_DUALBOX_PARTYFARM = Boolean.parseBoolean(BestFarm.getProperty("RenewalDualBoxPTFarm", "false"));
		EVENT_BEST_FARM_TIME = Integer.parseInt(BestFarm.getProperty("EventBestFarmTime", "1"));
		EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY = BestFarm.getProperty("BestFarmStartTime", "20:00").split(",");
		PARTY_MESSAGE_ENABLED = Boolean.parseBoolean(BestFarm.getProperty("ScreenPartyMessageEnable", "false"));
		PARTY_FARM_MESSAGE_TEXT = BestFarm.getProperty("ScreenPartyFarmMessageText", "Welcome to l2j server!");
		
		monsterId = Integer.parseInt(BestFarm.getProperty("MonsterId", "1"));
		// Recupera a string com os pontos de spawn (em formato "X,Y,Z")
		String custom_spawn_location = BestFarm.getProperty("SpawnPointMonster", "113852,-108766,-851");
		// Divide os pontos por ponto e cria um array para cada ponto
		String[] custom_spawn_locations = custom_spawn_location.split(";"); // Usando ; para separar os pontos
		for (String location : custom_spawn_locations)
		{
			// Divide cada ponto em suas coordenadas X, Y, Z
			String[] custom_spawn_location_splitted = location.split(",");
			// Cria um novo array para as coordenadas do ponto
			int[] spawnPoint = new int[3];
			spawnPoint[0] = Integer.parseInt(custom_spawn_location_splitted[0]); // X
			spawnPoint[1] = Integer.parseInt(custom_spawn_location_splitted[1]); // Y
			spawnPoint[2] = Integer.parseInt(custom_spawn_location_splitted[2]); // Z
			
			// Adiciona o ponto de spawn à lista
			CUSTOM_SPAWN_PARTYFARM.add(spawnPoint);
		}
		AMOUNT_MONSTROS_SPAWN_PT = Integer.parseInt(BestFarm.getProperty("QuantiyMonstersSpawns", "1"));
		RADIUS_SPAWN_LOC_PTFARM = Integer.parseInt(BestFarm.getProperty("RadiusSpawnMonsters", "1"));
		
		// String[] monsterLocs2 = BestFarm.getProperty("MonsterLoc", "").split(";");
		// String[] locSplit3 = null;
		// MONSTER_LOCS_COUNT = monsterLocs2.length;
		// MONSTER_LOCS = new int[MONSTER_LOCS_COUNT][3];
		// int g;
		// for (int e = 0; e < MONSTER_LOCS_COUNT; e++)
		// {
		// locSplit3 = monsterLocs2[e].split(",");
		// for (g = 0; g < 3; g++)
		// {
		// MONSTER_LOCS[e][g] = Integer.parseInt(locSplit3[g].trim());
		///	}
		// }
	}
	
	public static List<RewardHolder> parseReward(Properties propertie, String configName)
	{
		List<RewardHolder> auxReturn = new ArrayList<>();
		
		String aux = propertie.getProperty(configName).trim();
		for (String randomReward : aux.split(";"))
		{
			final String[] infos = randomReward.split(",");
			
			if (infos.length > 3)
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]), Integer.valueOf(infos[3])));
			else
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2])));
		}
		return auxReturn;
	}
	
	public static int ITEM_BUFF_SHOP;
	public static String SERVER_LOGO_SELLBUFF;
	public static String NEW_TITLE_SELLBUFF;
	public static boolean SELL_BUFF_ENABLED;
	public static List<String> SELL_BUFF_CLASS_LIST = new ArrayList<>();
	public static boolean ALLOW_PARTY_BUFFS;
	public static boolean ALLOW_CLAN_BUFFS;
	public static int SELL_BUFF_PUNISHED_PRICE;
	public static boolean SELL_BUFF_FILTER_ENABLED;
	public static int SELL_BUFF_MIN_LVL;
	public static int SELL_BUFFSET_MIN_LVL;
	public static boolean OFFLINE_SELLBUFF_ENABLED;
	public static boolean SELL_BUFF_SKILL_MP_ENABLED;
	public static double SELL_BUFF_SKILL_MP_MULTIPLIER;
	public static boolean SELL_BUFF_SKILL_ITEM_CONSUME_ENABLED;
	public static String BLOCK_SKILL_LIST_ID;
	public static List<Integer> BLOCK_SKILL_LIST = new ArrayList<>();
	public static boolean BLOCK_SELLBUFF_SUBCLASS;
	public static boolean ENABLE_EFFECT_ANIMATION_SELLBUFFS;
	
	/**
	 * Loads SELL BUFFS classes<br>
	 */
	private static final void loadSellBuffs()
	{
		final ExProperties SellBuffs = initProperties(OFFLINE_BUFF_SHOP_FILE);
		// NOVO
		ENABLE_EFFECT_ANIMATION_SELLBUFFS = Boolean.parseBoolean(SellBuffs.getProperty("EnableEffectAnimationSkills", "True"));
		BLOCK_SELLBUFF_SUBCLASS = Boolean.parseBoolean(SellBuffs.getProperty("BlockSellBuffSubClass", "True"));
		BLOCK_SKILL_LIST_ID = SellBuffs.getProperty("BlockSkillListSellBuffs");
		
		BLOCK_SKILL_LIST = new ArrayList<>();
		for (String id : BLOCK_SKILL_LIST_ID.split(","))
			BLOCK_SKILL_LIST.add(Integer.parseInt(id));
		
		SERVER_LOGO_SELLBUFF = (SellBuffs.getProperty("NameLogoServeSellBuff", "True"));
		ITEM_BUFF_SHOP = Integer.parseInt(SellBuffs.getProperty("ItemConsumedForBuffShop", "79"));
		NEW_TITLE_SELLBUFF = (SellBuffs.getProperty("NewTitleSellBuffs", "True"));
		
		SELL_BUFF_ENABLED = Boolean.parseBoolean(SellBuffs.getProperty("SellBuffEnabled", "True"));
		
		if (SELL_BUFF_ENABLED) // create map if system is enabled
		{
			String SELL_BUFF_CLASS_STRING = SellBuffs.getProperty("SellBuffClassList");
			int i = 0;
			for (String classId : SELL_BUFF_CLASS_STRING.split(","))
			{
				SELL_BUFF_CLASS_LIST.add(i, classId);
				i++;
			}
		}
		ALLOW_PARTY_BUFFS = Boolean.parseBoolean(SellBuffs.getProperty("AllowPartyBuffs", "True"));
		ALLOW_CLAN_BUFFS = Boolean.parseBoolean(SellBuffs.getProperty("AllowClanBuffs", "True"));
		SELL_BUFF_PUNISHED_PRICE = Integer.parseInt(SellBuffs.getProperty("SellBuffPunishedPrice", "10000"));
		SELL_BUFF_FILTER_ENABLED = Boolean.parseBoolean(SellBuffs.getProperty("SellBuffFilterEnabled", "True"));
		SELL_BUFF_MIN_LVL = Integer.parseInt(SellBuffs.getProperty("SellBuffMinLvl", "20"));
		SELL_BUFFSET_MIN_LVL = Integer.parseInt(SellBuffs.getProperty("SellBuffSetMinLvl", "79"));
		OFFLINE_SELLBUFF_ENABLED = Boolean.parseBoolean(SellBuffs.getProperty("OfflineSellbuffEnabled", "True"));
		SELL_BUFF_SKILL_MP_ENABLED = Boolean.parseBoolean(SellBuffs.getProperty("SellBuffSkillMpEnabled", "False"));
		SELL_BUFF_SKILL_MP_MULTIPLIER = Double.parseDouble(SellBuffs.getProperty("SellBuffSkillMpMultiplier", "1."));
		SELL_BUFF_SKILL_ITEM_CONSUME_ENABLED = Boolean.parseBoolean(SellBuffs.getProperty("SellBuffSkillItemConsumeEnabled", "True"));
	}
	
	/**
	 * Loads settings classes<br>
	 */
	private static final void loadClassDamage()
	{
		final ExProperties PHYSICSSetting = initProperties(PHYSICS_FILE);
		ANTI_SS_BUG_1 = PHYSICSSetting.getProperty("Delay", 2700);
		ANTI_SS_BUG_2 = PHYSICSSetting.getProperty("DelayNextAttack", 470000);
		BLOW_ATTACK_FRONT = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackFront", "50"));
		BLOW_ATTACK_SIDE = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackSide", "60"));
		BLOW_ATTACK_BEHIND = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackBehind", "70"));
		
		BACKSTAB_ATTACK_FRONT = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackFront", "0"));
		BACKSTAB_ATTACK_SIDE = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackSide", "0"));
		BACKSTAB_ATTACK_BEHIND = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackBehind", "70"));
		MAGIC_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("MagicCriticalPower", "3.0"));
		// Max patk speed and matk speed
		MAX_PATK_SPEED = Integer.parseInt(PHYSICSSetting.getProperty("MaxPAtkSpeed", "1500"));
		MAX_MATK_SPEED = Integer.parseInt(PHYSICSSetting.getProperty("MaxMAtkSpeed", "1999"));
		if (MAX_PATK_SPEED < 1)
		{
			MAX_PATK_SPEED = Integer.MAX_VALUE;
		}
		
		if (MAX_MATK_SPEED < 1)
		{
			MAX_MATK_SPEED = Integer.MAX_VALUE;
		}
		
		for (String id : PHYSICSSetting.getProperty("Skill_List_Olly", "0").split(","))
		{
			SKILL_LIST_SUCCESS_IN_OLY.add(Integer.parseInt(id));
		}
		
		for (String id : PHYSICSSetting.getProperty("Skill_List", "0").split(","))
		{
			SKILL_LIST_SUCCESS.add(Integer.parseInt(id));
		}
		SKILL_MAX_CHANCE = Boolean.parseBoolean(PHYSICSSetting.getProperty("Enable_Max_chance_skills", "true"));
		SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD = PHYSICSSetting.getProperty("SkillsMaxChanceSuccessInOlympiad", 40);
		SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD = PHYSICSSetting.getProperty("SkillsMinChanceSuccessInOlympiad", 40);
		SKILLS_MAX_CHANCE_SUCCESS = PHYSICSSetting.getProperty("SkillsMaxChanceSuccess", 40);
		SKILLS_MIN_CHANCE_SUCCESS = PHYSICSSetting.getProperty("SkillsMinChanceSuccess", 40);
		CUSTOM_CHANCE_FOR_ALL_SKILL = Boolean.parseBoolean(PHYSICSSetting.getProperty("Enable_Max_chance_for_all_skills", "true"));
		SKILLS_MAX_CHANCE = PHYSICSSetting.getProperty("MaxChance", 90);
		SKILLS_MIN_CHANCE = PHYSICSSetting.getProperty("MinChance", 20);
		
		ENABLE_CUSTOM_CHANCE_SKILL = Boolean.parseBoolean(PHYSICSSetting.getProperty("Enable_chance_skills", "true"));
		SURRENDER_TO_FIRE = Float.parseFloat(PHYSICSSetting.getProperty("Surrender_to_fire", "1.0"));
		VORTEX_TO_FIRE = Float.parseFloat(PHYSICSSetting.getProperty("Vortex_to_fire", "1.0"));
		SURRENDER_TO_WIND = Float.parseFloat(PHYSICSSetting.getProperty("Surrender_to_wind", "1.0"));
		WIND_VORTEX = Float.parseFloat(PHYSICSSetting.getProperty("Vortex_to_wind", "1.0"));
		CURSE_GLOOM = Float.parseFloat(PHYSICSSetting.getProperty("Curse_of_Gloom", "1.0"));
		DARK_VORTEX = Float.parseFloat(PHYSICSSetting.getProperty("Dark_vortex", "1.0"));
		SURRENDER_TO_WATER = Float.parseFloat(PHYSICSSetting.getProperty("Surrender_to_water", "1.0"));
		ICE_VORTEX = Float.parseFloat(PHYSICSSetting.getProperty("Ice_vortex", "1.0"));
		LIGHT_VORTEX = Float.parseFloat(PHYSICSSetting.getProperty("Light_vortex", "1.0"));
		SILENCE = Float.parseFloat(PHYSICSSetting.getProperty("Silence", "1.0"));
		SLEEP = Float.parseFloat(PHYSICSSetting.getProperty("Sleep", "1.0"));
		CURSE_FEAR = Float.parseFloat(PHYSICSSetting.getProperty("Curse_fear", "1.0"));
		ANCHOR = Float.parseFloat(PHYSICSSetting.getProperty("Anchor", "1.0"));
		CURSE_OF_DOOM = Float.parseFloat(PHYSICSSetting.getProperty("Curse_of_Doom", "1.0"));
		CURSE_OF_ABYSS = Float.parseFloat(PHYSICSSetting.getProperty("Curse_of_Abyss", "1.0"));
		CANCELLATION = Float.parseFloat(PHYSICSSetting.getProperty("Cancellation", "1.0"));
		MASS_SLOW = Float.parseFloat(PHYSICSSetting.getProperty("Mass_slow", "1.0"));
		MASS_FEAR = Float.parseFloat(PHYSICSSetting.getProperty("Mass_fear", "1.0"));
		MASS_GLOOM = Float.parseFloat(PHYSICSSetting.getProperty("Mass_gloom", "1.0"));
		SLEEPING_CLOUD = Float.parseFloat(PHYSICSSetting.getProperty("Sleeping_cloud", "1.0"));
		HEROIC_GRANDEUR = Float.parseFloat(PHYSICSSetting.getProperty("Heroic_grandeur", "1.0"));
		HEROIC_DREAD = Float.parseFloat(PHYSICSSetting.getProperty("Heroic_dread", "1.0"));
		STUNNING_SHOT = Float.parseFloat(PHYSICSSetting.getProperty("Stunning_shot", "1.0"));
		HEX = Float.parseFloat(PHYSICSSetting.getProperty("Hex", "1.0"));
		SHOCK_STOMP = Float.parseFloat(PHYSICSSetting.getProperty("Shock_stomp", "1.0"));
		THUNDER_STORM = Float.parseFloat(PHYSICSSetting.getProperty("Thunder_storm", "1.0"));
		SHIELD_STUN = Float.parseFloat(PHYSICSSetting.getProperty("Shield_stun", "1.0"));
		SHIELD_SLAM = Float.parseFloat(PHYSICSSetting.getProperty("Shield_slam", "1.0"));
		SHACKLE = Float.parseFloat(PHYSICSSetting.getProperty("Shackle", "1.0"));
		MASS_SHACKLING = Float.parseFloat(PHYSICSSetting.getProperty("Mass_shackling", "1.0"));
		ARREST = Float.parseFloat(PHYSICSSetting.getProperty("Arrest", "1.0"));
		BLUFF = Float.parseFloat(PHYSICSSetting.getProperty("Bluff", "1.0"));
		SWITCH = Float.parseFloat(PHYSICSSetting.getProperty("Switch", "1.0"));
		STUNNING_FIST = Float.parseFloat(PHYSICSSetting.getProperty("Stunning_fist", "1.0"));
		FEAR_OVER = Float.parseFloat(PHYSICSSetting.getProperty("Fear_over", "1.0"));
		SEAL_OF_SILENCE = Float.parseFloat(PHYSICSSetting.getProperty("Seal_of_Silence", "1.0"));
		SEAL_OF_SUSPENSION = Float.parseFloat(PHYSICSSetting.getProperty("Seal_of_Suspension", "1.0"));
		STUN_ATTACK = Float.parseFloat(PHYSICSSetting.getProperty("Stun_attack", "1.0"));
		ARMOR_CRUSH = Float.parseFloat(PHYSICSSetting.getProperty("Armor_crush", "1.0"));
		SLOW = Float.parseFloat(PHYSICSSetting.getProperty("Slow", "1.0"));
		SEAL_OF_DESPAIR = Float.parseFloat(PHYSICSSetting.getProperty("Seal_of_Despair", "1.0"));
		DREAMING_SPIRIT = Float.parseFloat(PHYSICSSetting.getProperty("Dreaming_spirit", "1.0"));
		SEAL_OF_BINDING = Float.parseFloat(PHYSICSSetting.getProperty("Seal_of_Binding", "1.0"));
		MASS_WARRIOR_BANE = Float.parseFloat(PHYSICSSetting.getProperty("Mass_Warrior_Bane", "1.0"));
		MASS_MAGE_BANE = Float.parseFloat(PHYSICSSetting.getProperty("Mass_Mage_Bane", "1.0"));
		SHIELD_BASH = Float.parseFloat(PHYSICSSetting.getProperty("Shield_Bash", "1.0"));
		SHOCK_BLAST = Float.parseFloat(PHYSICSSetting.getProperty("Shock_Blast", "1.0"));
	}
	
	/**
	 * Loads Commands Mods settings.<br>
	 */
	private static final void loadCommands()
	{
		final ExProperties commands = initProperties(COMMAND_FILE);
		STATUS_CMD = commands.getProperty("StatusCMD", false);
		STATUS_CMD_INVENTORY = commands.getProperty("StatusCMD_Inventory", false);
		STATUS_CMD_SKILLS = commands.getProperty("StatusCMD_Skils", false);
		BUFFER_COMMAND2 = commands.getProperty("BufferCommand", "buffs");
		ENABLE_COMMAND_VIP_BUFFS = Boolean.parseBoolean(commands.getProperty("EnableBuffOnlyVIP", "false"));
		BUFFER_USECOMMAND = Boolean.parseBoolean(commands.getProperty("BufferUseCommand", "false"));
		ENABLE_COMMAND_XPON_OFF = Boolean.parseBoolean(commands.getProperty("EnableCommandXP", "false"));
		MESSAGE_XPON = commands.getProperty("MessageXP-ON", "Xp!");
		MESSAGE_XPOFF = commands.getProperty("MessageXP-OFF", "Xp!");
		BANKING_SYSTEM_GOLDBARS = Integer.parseInt(commands.getProperty("BankingGoldbarCount", "1"));
		ID_NEW_GOLD_BAR = Integer.parseInt(commands.getProperty("IdGoldBarCustom", "1"));
		BANKING_SYSTEM_ADENA = Integer.parseInt(commands.getProperty("BankingAdenaCount", "5000"));
		ENABLE_COMMAND_GOLDBAR = Boolean.parseBoolean(commands.getProperty("EnableCommandGoldBar", "false"));
		ENABLE_COMMAND_CASTLES = Boolean.parseBoolean(commands.getProperty("EnableCommandCastles", "false"));
		ENABLE_COMMAND_RANKING = Boolean.parseBoolean(commands.getProperty("EnableCommandRanking", "false"));
		// Lista Jeweles Boss Ranking
		ID_LIST_JEWELES_BOSS_RANKING = commands.getProperty("ListRankingJewelesBossIDs", "10");
		ENABLE_COMMAND_REPORT_CHAR = Boolean.parseBoolean(commands.getProperty("EnableCommandReportChar", "false"));
		ENABLE_COMMAND_REPAIR_CHAR = Boolean.parseBoolean(commands.getProperty("EnableCommandRepairChar", "false"));
		ENABLE_COMMAND_PASSWORD = Boolean.parseBoolean(commands.getProperty("EnableCommandPassword", "false"));
		ENABLE_COMMAND_AUTO_POTION = Boolean.parseBoolean(commands.getProperty("EnableCommandAutoPotion", "false"));
		ENABLE_SHIFT_CLICK = Boolean.parseBoolean(commands.getProperty("EnableCommandShiftClick", "false"));
		ENABLE_COMMAND_CLAN_NOTICE = Boolean.parseBoolean(commands.getProperty("EnableCommandClanNotice", "false"));
		COMMAND_EPIC_ISVIP = Boolean.parseBoolean(commands.getProperty("EnableCommandEpicVIP", "True"));
		MESSAGE_VIP_EPIC = commands.getProperty("EpicVIPMessageText", "Forbidden to Use Enchant near the bank!");
		ENABLE_COMMAND_RAID = Boolean.parseBoolean(commands.getProperty("EnableCommandRaid", "false"));
		NO_USE_FARM_IN_PEACE_ZONE = Boolean.parseBoolean(commands.getProperty("BlockAutoFarmPeaceZone", "false"));
		ENABLE_DUALBOX_AUTOFARM = Boolean.parseBoolean(commands.getProperty("BlockAutoFarmIP", "false"));
		NUMBER_BOX_IP_AUTOFARM = Integer.parseInt(commands.getProperty("MaxBoxAutoFarmUsed", "1"));
		ENABLE_NEW_TITLE_AUTOFARM = Boolean.parseBoolean(commands.getProperty("EnableTitleAutoFarm", "false"));
		NEW_TITLE_AUTOFARM = commands.getProperty("NewTitleAutoFarm", "Farmando!");
		ENABLE_AURA_AUTOFARM = Boolean.parseBoolean(commands.getProperty("EnableAuraAutoFarm", "false"));
		ENABLE_AUTO_FARM_COMMAND = Boolean.parseBoolean(commands.getProperty("EnableCommandAutoFarm", "false"));
		ENABLE_COMMAND_VIP_AUTOFARM = Boolean.parseBoolean(commands.getProperty("EnableAutoFarmOnlyVip", "false"));
		ENABLE_COMMAND_MENU = Boolean.parseBoolean(commands.getProperty("EnableCommandMenu", "false"));
		ENABLE_VOICED_DONATE = Boolean.parseBoolean(commands.getProperty("EnableCommandDonate", "false"));
	}
	
	/**
	 * Loads Boss settings.<br>
	 */
	private static final void loadRaid()
	{
		final ExProperties raidinfo = initProperties(BOSS_FILE);
		NPCS_FLAG_RANGE = raidinfo.getProperty("RangeFlagPlayers", 1000);
		ALLOW_FLAG_ONKILL_BY_ID = raidinfo.getProperty("EnableFlagListBoss", false);
		NPCS_FLAG_IDS = raidinfo.getProperty("ListNpcsFlagPvP", "29020,29019,25517,25523,25524");
		NPCS_FLAG_LIST = new ArrayList<>();
		for (final String id : NPCS_FLAG_IDS.split(","))
		{
			NPCS_FLAG_LIST.add(Integer.parseInt(id));
		}
		QUEST_BAIUM = Integer.parseInt(raidinfo.getProperty("QuestBaium", "4295"));
		QUEST_VALAKAS = Integer.parseInt(raidinfo.getProperty("QuestValakas", "7267"));
		QUEST_ANTHARAS = Integer.parseInt(raidinfo.getProperty("QuestAntharas", "3865"));
		QUEST_SAILREN = Integer.parseInt(raidinfo.getProperty("QuestSailren", "8784"));
		QUEST_FRINTEZZA = Integer.parseInt(raidinfo.getProperty("QuestFrintezza", "8073"));
		
		RAID_RESPAWN_IDS = raidinfo.getProperty("Respawn_Raid_IDs");
		RAID_RESPAWN_IDS_LIST = new ArrayList<>();
		for (String id : RAID_RESPAWN_IDS.split(","))
			RAID_RESPAWN_IDS_LIST.add(Integer.parseInt(id));
		
		RESPAWN_CUSTOM = Boolean.parseBoolean(raidinfo.getProperty("RespawnCustom", "false"));
		MIN_RESPAWN = Integer.parseInt(raidinfo.getProperty("Respawn_raidboss", "4"));
		MAX_RESPAWN = Integer.parseInt(raidinfo.getProperty("Respawn_random_raidboss", "4"));
		
		NOBLESS_FROM_BOSS = raidinfo.getProperty("NoblessFromBoss", false);
		BOSS_ID = raidinfo.getProperty("BossId", 25325);
		RADIUS_TO_RAID = raidinfo.getProperty("RadiusToRaid", 1000);
		
		RAID_BOSS_INFO_PAGE_LIMIT = raidinfo.getProperty("RaidBossInfoPageLimit", 15);
		RAID_BOSS_DROP_PAGE_LIMIT = raidinfo.getProperty("RaidBossDropPageLimit", 15);
		RAID_BOSS_DATE_FORMAT = raidinfo.getProperty("RaidBossDateFormat", "MMM dd, HH:mm");
		RAID_BOSS_IDS = raidinfo.getProperty("RaidBossIds", "0,0");
		LIST_RAID_BOSS_IDS = new ArrayList<>();
		for (String val : RAID_BOSS_IDS.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_RAID_BOSS_IDS.add(npcId);
		}
		
		GRAND_BOSS_IDS = raidinfo.getProperty("GrandBossIds", "0,0");
		LIST_GRAND_BOSS_IDS = new ArrayList<>();
		for (String val : GRAND_BOSS_IDS.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_GRAND_BOSS_IDS.add(npcId);
		}
		
		LIST_ITENS_NOT_SHOW = raidinfo.getProperty("ExceptionItemList");
		NOT_SHOW_DROP_INFO = new ArrayList<>();
		for (String id : LIST_ITENS_NOT_SHOW.split(","))
		{
			NOT_SHOW_DROP_INFO.add(Integer.parseInt(id));
		}
		
		ENABLE_FULL_HP_RECALL_EPIC = raidinfo.getProperty("RecoverHPFullRecallEpic", false);
		RANGE_EPICBOSS = raidinfo.getProperty("ReturnEpicBossRange", 300);
		RANGE_BOSS_LIMIT = raidinfo.getProperty("ReturnBossRange", 300);
		ENABLE_BOSSZONE_FLAG = raidinfo.getProperty("EnableBossZoneFlag", false);
		BLOCK_DUALBOX_RAIDZONE = raidinfo.getProperty("BlockDualBoxRaidZone", false);
		ENABLE_DUALBOX_BOSSZONE = raidinfo.getProperty("BlockDualBoxBossZone", false);
		PLAYERS_CAN_HEAL_RB = raidinfo.getProperty("PlayersCanHealRb", false);
		ALLOW_DIRECT_TP_TO_BOSS_ROOM = raidinfo.getProperty("AllowDirectTeleportToBossRoom", false);
		RAID_HP_REGEN_MULTIPLIER = raidinfo.getProperty("RaidHpRegenMultiplier", 1.);
		RAID_MP_REGEN_MULTIPLIER = raidinfo.getProperty("RaidMpRegenMultiplier", 1.);
		RAID_DEFENCE_MULTIPLIER = raidinfo.getProperty("RaidDefenceMultiplier", 1.);
		RAID_MINION_RESPAWN_TIMER = raidinfo.getProperty("RaidMinionRespawnTime", 300000);
		ENABLE_FULL_HP_RECALL_BOSS = raidinfo.getProperty("RecoverHPFullRecallBoss", false);
		
		RAID_DISABLE_CURSE = raidinfo.getProperty("DisableRaidCurse", false);
		RAID_CHAOS_TIME = raidinfo.getProperty("RaidChaosTime", 30);
		GRAND_CHAOS_TIME = raidinfo.getProperty("GrandChaosTime", 30);
		MINION_CHAOS_TIME = raidinfo.getProperty("MinionChaosTime", 30);
		
		SPAWN_INTERVAL_AQ = raidinfo.getProperty("AntQueenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_AQ = raidinfo.getProperty("AntQueenRandomSpawn", 17);
		
		SPAWN_INTERVAL_ANTHARAS = raidinfo.getProperty("AntharasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_ANTHARAS = raidinfo.getProperty("AntharasRandomSpawn", 72);
		WAIT_TIME_ANTHARAS = raidinfo.getProperty("AntharasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_BAIUM = raidinfo.getProperty("BaiumSpawnInterval", 168);
		RANDOM_SPAWN_TIME_BAIUM = raidinfo.getProperty("BaiumRandomSpawn", 48);
		
		SPAWN_INTERVAL_CORE = raidinfo.getProperty("CoreSpawnInterval", 60);
		RANDOM_SPAWN_TIME_CORE = raidinfo.getProperty("CoreRandomSpawn", 23);
		
		SPAWN_INTERVAL_FRINTEZZA = raidinfo.getProperty("FrintezzaSpawnInterval", 48);
		RANDOM_SPAWN_TIME_FRINTEZZA = raidinfo.getProperty("FrintezzaRandomSpawn", 8);
		WAIT_TIME_FRINTEZZA = raidinfo.getProperty("FrintezzaWaitTime", 1) * 60000;
		FRINTEZZA_MIN_PARTIES = raidinfo.getProperty("FrintezzaMinParties", 4);
		FRINTEZZA_MAX_PARTIES = raidinfo.getProperty("FrintezzaMaxParties", 5);
		BYPASS_FRINTEZZA_PARTIES_CHECK = raidinfo.getProperty("CheckPartyFrintezza", false);
		
		SPAWN_INTERVAL_ORFEN = raidinfo.getProperty("OrfenSpawnInterval", 48);
		RANDOM_SPAWN_TIME_ORFEN = raidinfo.getProperty("OrfenRandomSpawn", 20);
		
		SPAWN_INTERVAL_SAILREN = raidinfo.getProperty("SailrenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_SAILREN = raidinfo.getProperty("SailrenRandomSpawn", 24);
		WAIT_TIME_SAILREN = raidinfo.getProperty("SailrenWaitTime", 5) * 60000;
		
		SPAWN_INTERVAL_VALAKAS = raidinfo.getProperty("ValakasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_VALAKAS = raidinfo.getProperty("ValakasRandomSpawn", 72);
		WAIT_TIME_VALAKAS = raidinfo.getProperty("ValakasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_ZAKEN = raidinfo.getProperty("ZakenSpawnInterval", 60);
		RANDOM_SPAWN_TIME_ZAKEN = raidinfo.getProperty("ZakenRandomSpawn", 20);
		
		String custom_spawn_location = raidinfo.getProperty("CustomSpawnZaken", "113852,-108766,-851");
		String custom_spawn_location_splitted[] = custom_spawn_location.split(",");
		CUSTOM_SPAWN_ZAKEN[0] = Integer.parseInt(custom_spawn_location_splitted[0]);
		CUSTOM_SPAWN_ZAKEN[1] = Integer.parseInt(custom_spawn_location_splitted[1]);
		CUSTOM_SPAWN_ZAKEN[2] = Integer.parseInt(custom_spawn_location_splitted[2]);
		
	}
	
	/**
	 * Loads Donate Settings.<br>
	 */
	private static final void loadDonate()
	{
		final ExProperties donator = initProperties(DONATE_SHOP);
		
		ENABLE_VIP_FREE = donator.getProperty("EnableVIPNewbieDays", true);
		VIP_DAYS_FREE = donator.getProperty("VipFreeDays", 200);
		ENABLE_VIP_SYSTEM = Boolean.parseBoolean(donator.getProperty("EnableVipSystem", "True"));
		ALLOW_VIP_NCOLOR = Boolean.parseBoolean(donator.getProperty("AllowVipNameColor", "True"));
		VIP_NCOLOR = Integer.decode("0x" + donator.getProperty("VipNameColor", "88AA88"));
		ALLOW_VIP_TCOLOR = Boolean.parseBoolean(donator.getProperty("AllowVipTitleColor", "True"));
		VIP_TCOLOR = Integer.decode("0x" + donator.getProperty("VipTitleColor", "88AA88"));
		// MESSAGE_VIP_ENTER = donator.getProperty("ScreenVIPMessageText", "Forbidden to Use Enchant near the bank!");
		// MESSAGE_VIP_EXIT = donator.getProperty("ScreenVIPMessageExitText", "Forbidden to Use Enchant near the bank!");
		// VIP_COIN_ID1 = Integer.parseInt(donator.getProperty("VipCoin", "6392"));
		// VIP_DAYS_ID1 = Integer.parseInt(donator.getProperty("VipCoinDays", "1"));
		// VIP_COIN_ID2 = Integer.parseInt(donator.getProperty("VipCoin2", "6393"));
		// VIP_DAYS_ID2 = Integer.parseInt(donator.getProperty("VipCoinDays2", "2"));
		// VIP_COIN_ID3 = Integer.parseInt(donator.getProperty("VipCoin3", "5557"));
		// VIP_DAYS_ID3 = Integer.parseInt(donator.getProperty("VipCoinDays3", "3"));
		// VIP_COIN_ID4 = Integer.parseInt(donator.getProperty("VipCoin4", "5557"));
		// VIP_DAYS_ID4 = Integer.parseInt(donator.getProperty("VipCoinDays4", "3"));
		
		VIP_30_DAYS_PRICE = Integer.parseInt(donator.getProperty("Vip_30_Days_Price", "30"));
		VIP_60_DAYS_PRICE = Integer.parseInt(donator.getProperty("Vip_60_Days_Price", "60"));
		VIP_90_DAYS_PRICE = Integer.parseInt(donator.getProperty("Vip_90_Days_Price", "90"));
		VIP_ETERNAL_PRICE = Integer.parseInt(donator.getProperty("Vip_Eternal_Price", "120"));
		
		DONATE_COIN_ID = Integer.parseInt(donator.getProperty("DonateCoin_Id", "9511"));
		
		HERO_30_DAYS_PRICE = Integer.parseInt(donator.getProperty("Hero_30_Days_Price", "30"));
		HERO_60_DAYS_PRICE = Integer.parseInt(donator.getProperty("Hero_60_Days_Price", "60"));
		HERO_90_DAYS_PRICE = Integer.parseInt(donator.getProperty("Hero_90_Days_Price", "90"));
		HERO_ETERNAL_PRICE = Integer.parseInt(donator.getProperty("Hero_Eternal_Price", "120"));
		DONATE_NAME_PRICE = Integer.parseInt(donator.getProperty("Change_Name_Price", "15"));
		DONATE_SEX_PRICE = Integer.parseInt(donator.getProperty("Change_Sex_Price", "15"));
		DONATE_CLASS_PRICE = Integer.parseInt(donator.getProperty("Change_Class_Price", "15"));
		NOBL_ITEM_COUNT = donator.getProperty("NoblesseItemCount", 100);
		PK_ITEM_COUNT = donator.getProperty("PkItemCount", 100);
		PK_CLEAN = donator.getProperty("PkCleanValue", 50);
		CLAN_ITEM_COUNT = donator.getProperty("ClanLvItemPrice", 100);
		CLAN_REP_ITEM_COUNT = donator.getProperty("ClanRepsCount", 100);
		CLAN_REPS = donator.getProperty("ClanReps", 20000);
		CLAN_SKILL_ITEM_COUNT = donator.getProperty("ClanSkillsItemCount", 100);
		AUGM_ITEM_COUNT = donator.getProperty("AugmentionItemCount", 100);
		AUGM_PRICE_ALTERNATIVE = donator.getProperty("AugmentionAlternativeItemCount", 100);
		PASSWORD_ITEM_COUNT = donator.getProperty("PasswordItemCount", 100);
		ENCHANT_ITEM_COUNT = donator.getProperty("EnchantItemCount", 100);
		ENCHANT_MAX_VALUE = donator.getProperty("MaxEnchantValue", 15);
		
		PVP_POINT_ID = donator.getProperty("ColorCoinID", 57);
		PVP_POINT_COUNT = donator.getProperty("ColorCoinCount", 200);
		DONATE_COIN_COUNT = donator.getProperty("DonateColorCoinCount", 200);
		ALLOW_NEW_COLOR_MANAGER = donator.getProperty("AllowNewColor", false);
		
	}
	
	/**
	 * Loads Protection Settings.<br>
	 */
	private static final void loadProtection()
	{
		final ExProperties Protection = initProperties(PROTECION_FILE);
		NO_AUTO_LOOT_MOBS.addAll(Arrays.stream(Protection.getProperty("NoAutoLootMobs", "").split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toList()));
		CHAT_BAN_HTML = Protection.getProperty("EnableChatBanHTML", false);
		CHAT_BAN_MESSAGE = Protection.getProperty("ChatBanMessage", "Chat banned from as respect the rules!");
		REMOVE_CHAT_BAN_ITEM_COUNT = Integer.parseInt(Protection.getProperty("AmountCoinRemove", "6392"));
		TRY_USED_ADMIN_COMMANDS = Protection.getProperty("TryUsedAdminCommands", false);
		ENABLE_WH_LOG = Boolean.parseBoolean(Protection.getProperty("EnableWarehouseTrack", "False"));
		ENABLE_TRADE_TRACK = Boolean.parseBoolean(Protection.getProperty("EnableTradeTrack", "False"));
		ENABLE_CHECK_DONATE_ITEMS = Boolean.parseBoolean(Protection.getProperty("RegistreMultisellItems", "False"));
		IDMULTISELLLOGDONATE = List.of(Protection.getProperty("IdMultisellLogDonate", "").replace(" ", "").split(",")).stream().map(String::hashCode).collect(Collectors.toList());
		ENABLE_NAME_GMS_CHECK = Boolean.parseBoolean(Protection.getProperty("CheckNamesGMS", "False"));
		GM_NAMES = Protection.getProperty("GmNames", "").split(",");
		USER_TIME_ARGUMENT_ITEM = Protection.getProperty("UserTimeArgumentItem", 4200);
		// Enchant Skill NPC
		NPC_ENCHANT_SKILL_ID = Integer.parseInt(Protection.getProperty("NpcEnchantSkillID", "6392"));
		USER_TIME_ENCHANT_SKILL = Protection.getProperty("UserTimeEnchantSkill", 4200);
		ENABLE_ENCHANT_SKILL_NPCID = Protection.getProperty("EnableProtectionEnchantSkill", true);
		// Enchant Skill NPC FIM
		BLOCK_DUALBOX_FARMZONE = Boolean.parseBoolean(Protection.getProperty("AntiDualBoxFarmZone", "false"));
		DUALBOX_NUMBER_FARM = Integer.parseInt(Protection.getProperty("NumberBoxFarmZone", "6392"));
		INTERFACE_KEY = Protection.getProperty("InterfaceKey", "");
		VERIFICATION_TIME = Protection.getProperty("VerificationTimer", 15) * 1000;
		USE_SAY_FILTER = Protection.getProperty("AllowSayFilter", false);
		CHAT_FILTER_PUNISHMENT = Protection.getProperty("SayFilterPunishment", "off");
		CHAT_FILTER_PUNISHMENT_PARAM1 = Protection.getProperty("SayFilterPunishmentParam1", 1);
		CHAT_FILTER_PUNISHMENT_PARAM2 = Protection.getProperty("SayFilterPunishmentParam2", 1000);
		CHAT_FILTER_CHARS = Protection.getProperty("SayFilterWrite", "********");
		// if (USE_SAY_FILTER)
		// {
		// try
		// {
		// LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File(SAY_FILTER_FILE))));
		// String line = null;
		// while ((line = lnr.readLine()) != null)
		// {
		// if (line.trim().length() == 0 || line.startsWith("#"))
		// {
		// continue;
		// }
		// FILTER_LIST.add(line.trim());
		// }
		// _log.info("Chat Filter: Loaded " + FILTER_LIST.size() + " words");
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// throw new Error("Failed to Load "+SAY_FILTER_FILE+" File.");
		// }
		// }
		if (USE_SAY_FILTER)
		{
			try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File(SAY_FILTER_FILE)))))
			{
				String line = null;
				while ((line = lnr.readLine()) != null)
				{
					if (line.trim().length() == 0 || line.startsWith("#"))
					{
						continue;
					}
					FILTER_LIST.add(line.trim());
				}
				_log.info("Chat Filter: Loaded " + FILTER_LIST.size() + " words");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new Error("Failed to Load " + SAY_FILTER_FILE + " File.");
			}
		}
		
		DISABLE_ATTACK_NPC_TYPE = Boolean.parseBoolean(Protection.getProperty("DisableAttackToNpcs", "False"));
		FORBIDDEN_NAMES = Protection.getProperty("ForbiddenNames", "").split(",");
		DISABLE_CHAT = Protection.getProperty("BlockChatAll", false);
		LETHAL_MONSTERS_PROTECT = Protection.getProperty("ProtectLethalMonsters");
		LETHAL_MONSTERS_ID = new ArrayList<>();
		for (String id : LETHAL_MONSTERS_PROTECT.split(","))
			LETHAL_MONSTERS_ID.add(Integer.parseInt(id));
		TALK_CHAT_ALL_CONFIG = Protection.getProperty("ChatAllProtection", false);
		TALK_CHAT_ALL_TIME = Protection.getProperty("TalkChatAllTime", 15);
		
		AGATHION_ID_RESTRICT = Protection.getProperty("ItemsNullAgathionID");
		
		AGATHION_LISTID_RESTRICT = new ArrayList<>();
		for (String id : AGATHION_ID_RESTRICT.split(","))
			AGATHION_LISTID_RESTRICT.add(Integer.parseInt(id));
		
		WYVERN_PROTECTION = Boolean.parseBoolean(Protection.getProperty("WyvernProtectionEnabled", "False"));
		ID_RESTRICT = Protection.getProperty("WyvernItemID", "10");
		LISTID_RESTRICT = new ArrayList<>();
		for (String classId : ID_RESTRICT.split(","))
		{
			LISTID_RESTRICT.add(Integer.parseInt(classId));
		}
		
		WYVERN_PROTECTION_BOSS_ZONE = Boolean.parseBoolean(Protection.getProperty("WyvernProtectBossZone", "False"));
		ID_RESTRICT_BOSS_ZONE = Protection.getProperty("ItemSummonWyvernID", "10");
		LISTID_RESTRICT_BOSS_ZONE = new ArrayList<>();
		for (String classId : ID_RESTRICT_BOSS_ZONE.split(","))
		{
			LISTID_RESTRICT_BOSS_ZONE.add(Integer.parseInt(classId));
		}
		// Novo sistema de class nao usar set Heavy
		ALLOW_LIGHT_USE_HEAVY = Boolean.parseBoolean(Protection.getProperty("AllowUseHeavySet", "False"));
		NOTALLOWCLASS = Protection.getProperty("NotAllowedUseHeavy", "");
		NOTALLOWEDUSEHEAVY = new ArrayList<>();
		for (String classId : NOTALLOWCLASS.split(","))
		{
			NOTALLOWEDUSEHEAVY.add(Integer.parseInt(classId));
		}
		WELCOME_MESSAGE_ANTIHERVY = Protection.getProperty("ScreenAntiHervyMessageText", "Your Class Can't Equip Heavy Type Armors!");
		// Novo sistema de class nao usar set Robe
		ALLOW_LIGHT_USE_MAGIC = Boolean.parseBoolean(Protection.getProperty("AllowUseMagicSet", "False"));
		NOTALLOWCLASSMAGIC = Protection.getProperty("NotAllowedUseMagic", "");
		NOTALLOWEDUSEMAGIC = new ArrayList<>();
		for (String classId : NOTALLOWCLASSMAGIC.split(","))
		{
			NOTALLOWEDUSEMAGIC.add(Integer.parseInt(classId));
		}
		WELCOME_MESSAGE_ANTIROBE = Protection.getProperty("ScreenAntiRobeMessageText", "Your Class Can't Equip Robe Type Armors!");
		// Novo sistema de class nao usar set Light
		ALLOW_HEAVY_USE_LIGHT = Boolean.parseBoolean(Protection.getProperty("AllowUseLightSet", "False"));
		NOTALLOWCLASSE = Protection.getProperty("NotAllowedUseLight", "");
		NOTALLOWEDUSELIGHT = new ArrayList<>();
		for (String classId : NOTALLOWCLASSE.split(","))
		{
			NOTALLOWEDUSELIGHT.add(Integer.parseInt(classId));
		}
		WELCOME_MESSAGE_ANTILIGHT = Protection.getProperty("ScreenAntiLightMessageText", "Your Class Can't Equip Light Type Armors!");
		ENABLE_NO_USE_SETSOLY = Boolean.parseBoolean(Protection.getProperty("EnableBlockAllSetRenstritionOly", "False"));
		REMOVE_WEAPON = Boolean.parseBoolean(Protection.getProperty("RemoveWeapon", "False"));
		REMOVE_CHEST = Boolean.parseBoolean(Protection.getProperty("RemoveChest", "False"));
		REMOVE_LEG = Boolean.parseBoolean(Protection.getProperty("RemoveLeg", "False"));
		ALT_DISABLE_BOW_CLASSES = Boolean.parseBoolean(Protection.getProperty("AltDisableBow", "False"));
		DISABLE_BOW_CLASSES_STRING = Protection.getProperty("DisableBowForClasses", "");
		DISABLE_BOW_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_BOW_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_BOW_CLASSES.add(Integer.parseInt(class_id));
		}
		BLOCK_EXIT_OLY = Boolean.parseBoolean(Protection.getProperty("AllowExitOlympiad", "True"));
		ALLOW_DUALBOX_OLY = Boolean.parseBoolean(Protection.getProperty("AllowDualBoxInOly", "True"));
		ALLOWED_BOXES = Integer.parseInt(Protection.getProperty("AllowedBoxes", "99"));
		ALLOW_DUALBOX = Boolean.parseBoolean(Protection.getProperty("AllowDualBox", "True"));
		
	}
	
	/**
	 * Loads aCis settings.
	 */
	private static final void loadCustomConfig()
	{
		final ExProperties enchant = initProperties(CUSTOM_FILE);
		
		ENABLE_PVP_ZONE_TIME = Boolean.parseBoolean( enchant.getProperty("EnablePvPZoneTime", "false"));
		ENABLE_TIME_ZONE_SYSTEM = Boolean.parseBoolean(enchant.getProperty("EnableFarmTimeEvent", "False"));
		ENABLE_GRADE_ITEMS_MARKETPLACE = Boolean.parseBoolean(enchant.getProperty("EnableGradeItemsMarket", "False"));
		for (String list : enchant.getProperty("GradeItemsMarketPlace", "A;S").split(";"))
			GRADE_ITEMS_MARKETPLACE.add(CrystalType.valueOf(list));
		ITEMS_NO_RULES_MARKETPLACE_ID = enchant.getProperty("ItemNoBlockMarketPlace", "0,0");
		ITEMS_NO_RULES_MARKETPLACE = new ArrayList<>();
		for (String val : ITEMS_NO_RULES_MARKETPLACE_ID.split(","))
		{
			int ItemsIDS = Integer.parseInt(val);
			ITEMS_NO_RULES_MARKETPLACE.add(ItemsIDS);
		}
		TICKET_MARKE_PLACE = Integer.parseInt(enchant.getProperty("TicketMarkePlace", "6392"));
		ENABLE_AUCTION_COMMUNITY = enchant.getProperty("EnableCommunityAuction", false);
		COR_LOJA_AUCTION_ANUNCIE = Integer.parseInt(enchant.getProperty("ColorChatAnnuncieAuction", "6392"));
		String[] properSplit = enchant.getProperty("CBBMarketPlaceFee", "0,0").split(",");
		try
		{
			MARKETPLACE_FEE[0] = Integer.parseInt(properSplit[0]);
			MARKETPLACE_FEE[1] = Integer.parseInt(properSplit[1]);
		}
		catch (NumberFormatException nfe)
		{
			if (properSplit.length > 0)
			{
				_log.warning("jmods: invalid config property -> MarketPlaceFee");
			}
		}
		try
		{
			AUCTION_TAX_RATE = Double.parseDouble(enchant.getProperty("AuctionTaxRate", "0.05"));
		}
		catch (NumberFormatException e)
		{
			_log.warning("Invalid value for AuctionTaxRate, using default 0.05");
			AUCTION_TAX_RATE = 0.05;
		}
		
		try
		{
			AUCTION_TAX_MINIMUM_ITEM_COUNT = Integer.parseInt(enchant.getProperty("AuctionTaxMinimumItemCount", "5"));
		}
		catch (NumberFormatException e)
		{
			_log.warning("Invalid value for AuctionTaxMinimumItemCount, using default 5");
			AUCTION_TAX_MINIMUM_ITEM_COUNT = 5;
		}
		
		DROP_ANNUNCIE_ID = Integer.parseInt(enchant.getProperty("ChatAnnuncieID", "1"));
		ENABLE_ANNUNCIE_DROP_ITEMS = Boolean.parseBoolean(enchant.getProperty("EnabledAnnuncieListItems", "True"));
		ANNUNCIE_ID_DROP = enchant.getProperty("AnnuncieItemsID");
		ANNUNCIE_LISTID_DROP = new ArrayList<>();
		for (String id : ANNUNCIE_ID_DROP.split(","))
			ANNUNCIE_LISTID_DROP.add(Integer.parseInt(id));
		// Solo Farm Random 1
		String custom_spawn_location = enchant.getProperty("TeleportRandomSoloFarm1", "113852,-108766,-851");
		String custom_spawn_location_splitted[] = custom_spawn_location.split(",");
		GK_SPAWN_SOLOFARM_1[0] = Integer.parseInt(custom_spawn_location_splitted[0]);
		GK_SPAWN_SOLOFARM_1[1] = Integer.parseInt(custom_spawn_location_splitted[1]);
		GK_SPAWN_SOLOFARM_1[2] = Integer.parseInt(custom_spawn_location_splitted[2]);
		
		// Solo Farm Random 2
		String custom_spawn_location2 = enchant.getProperty("TeleportRandomSoloFarm2", "113852,-108766,-851");
		String custom_spawn_location_splitted2[] = custom_spawn_location2.split(",");
		GK_SPAWN_SOLOFARM_2[0] = Integer.parseInt(custom_spawn_location_splitted2[0]);
		GK_SPAWN_SOLOFARM_2[1] = Integer.parseInt(custom_spawn_location_splitted2[1]);
		GK_SPAWN_SOLOFARM_2[2] = Integer.parseInt(custom_spawn_location_splitted2[2]);
		
		// Solo Farm Random 3
		String custom_spawn_location3 = enchant.getProperty("TeleportRandomSoloFarm3", "113852,-108766,-851");
		String custom_spawn_location_splitted3[] = custom_spawn_location3.split(",");
		GK_SPAWN_SOLOFARM_3[0] = Integer.parseInt(custom_spawn_location_splitted3[0]);
		GK_SPAWN_SOLOFARM_3[1] = Integer.parseInt(custom_spawn_location_splitted3[1]);
		GK_SPAWN_SOLOFARM_3[2] = Integer.parseInt(custom_spawn_location_splitted3[2]);
		
		// Solo Farm Random 4
		String custom_spawn_location4 = enchant.getProperty("TeleportRandomSoloFarm4", "113852,-108766,-851");
		String custom_spawn_location_splitted4[] = custom_spawn_location4.split(",");
		GK_SPAWN_SOLOFARM_4[0] = Integer.parseInt(custom_spawn_location_splitted4[0]);
		GK_SPAWN_SOLOFARM_4[1] = Integer.parseInt(custom_spawn_location_splitted4[1]);
		GK_SPAWN_SOLOFARM_4[2] = Integer.parseInt(custom_spawn_location_splitted4[2]);
		
		// Solo Farm Random 5
		String custom_spawn_location5 = enchant.getProperty("TeleportRandomSoloFarm5", "113852,-108766,-851");
		String custom_spawn_location_splitted5[] = custom_spawn_location5.split(",");
		GK_SPAWN_SOLOFARM_5[0] = Integer.parseInt(custom_spawn_location_splitted5[0]);
		GK_SPAWN_SOLOFARM_5[1] = Integer.parseInt(custom_spawn_location_splitted5[1]);
		GK_SPAWN_SOLOFARM_5[2] = Integer.parseInt(custom_spawn_location_splitted5[2]);
		
		// Solo Farm Random 6
		String custom_spawn_location6 = enchant.getProperty("TeleportRandomSoloFarm6", "113852,-108766,-851");
		String custom_spawn_location_splitted6[] = custom_spawn_location6.split(",");
		GK_SPAWN_SOLOFARM_6[0] = Integer.parseInt(custom_spawn_location_splitted6[0]);
		GK_SPAWN_SOLOFARM_6[1] = Integer.parseInt(custom_spawn_location_splitted6[1]);
		GK_SPAWN_SOLOFARM_6[2] = Integer.parseInt(custom_spawn_location_splitted6[2]);
		
		STRING_MULTISELL_DONATE = enchant.getProperty("MultisellDonateName", "MultisellNew");
		AUG_ITEM_TRADE = Boolean.valueOf(enchant.getProperty("AugItemTrade", "false"));
		ENABLE_EVENT_2008 = Boolean.valueOf(enchant.getProperty("EnableEventFaenor", "false"));
		
		TIME_PICKUP_NORMAL = Integer.parseInt(enchant.getProperty("PickUpTimeNormal", "18000"));
		TIME_PICKUP_BOSS = Integer.parseInt(enchant.getProperty("PickUpBoss", "18000"));
		
		DEFAULT_GLOBAL_CHAT = enchant.getProperty("GlobalChat", "ON");
		DEFAULT_TRADE_CHAT = enchant.getProperty("TradeChat", "ON");
		DISABLE_CAPSLOCK = Boolean.valueOf(enchant.getProperty("DisableCapsLock", "false"));
		CUSTOM_GLOBAL_CHAT_TIME = Integer.parseInt(enchant.getProperty("interval_ChatGlobal", "18000"));
		GLOBAL_CHAT_WITH_PVP = Boolean.valueOf(enchant.getProperty("GlobalChatWithPvP", "false"));
		GLOBAL_PVP_AMOUNT = Integer.parseInt(enchant.getProperty("GlobalPvPAmount", "500"));
		TRADE_CHAT_WITH_PVP = Boolean.valueOf(enchant.getProperty("TradeChatWithPvP", "false"));
		TRADE_PVP_AMOUNT = Integer.parseInt(enchant.getProperty("TradePvPAmount", "50"));
		CUSTOM_HERO_CHAT_TIME = Integer.parseInt(enchant.getProperty("interval_ChatHero", "18000"));
		
		PCB_ENABLE = Boolean.parseBoolean(enchant.getProperty("PcBangPointEnable", "true"));
		PCB_MIN_LEVEL = Integer.parseInt(enchant.getProperty("PcBangPointMinLevel", "20"));
		PCB_POINT_MIN = Integer.parseInt(enchant.getProperty("PcBangPointMinCount", "20"));
		PCB_POINT_MAX = Integer.parseInt(enchant.getProperty("PcBangPointMaxCount", "1000000"));
		PCB_COIN_ID = Integer.parseInt(enchant.getProperty("PCBCoinId", "0"));
		if (PCB_POINT_MAX < 1)
		{
			PCB_POINT_MAX = Integer.MAX_VALUE;
			
		}
		PCB_CHANCE_DUAL_POINT = Integer.parseInt(enchant.getProperty("PcBangPointDualChance", "20"));
		PCB_INTERVAL = Integer.parseInt(enchant.getProperty("PcBangPointTimeStamp", "900"));
		
		OFFLINE_TRADE_ENABLE = enchant.getProperty("OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = enchant.getProperty("OfflineCraftEnable", false);
		OFFLINE_MODE_IN_PEACE_ZONE = enchant.getProperty("OfflineModeInPeaceZone", false);
		OFFLINE_MODE_NO_DAMAGE = enchant.getProperty("OfflineModeNoDamage", false);
		OFFLINE_SET_SLEEP = enchant.getProperty("OfflineSetSleepEffect", false);
		RESTORE_OFFLINERS = enchant.getProperty("RestoreOffliners", false);
		OFFLINE_MAX_DAYS = enchant.getProperty("OfflineMaxDays", 10);
		OFFLINE_DISCONNECT_FINISHED = enchant.getProperty("OfflineDisconnectFinished", true);
		ITEM_PERMITIDO_PARA_USAR_NA_LOJA_ID = enchant.getProperty("UseItemId", 10);
		
		// Offline Player
		ENABLE_OFFLINE_PEACE_ZONE = enchant.getProperty("EnableOfflineInPeaceZone", false);
		ENABLE_OFFLINE_PLAYER = enchant.getProperty("EnableOfflineSystem", false);
		OFFLINE_PLAYER_SLEEP = enchant.getProperty("OfflinePlayerSleepEffect", false);
		ALLOW_NEW_OFFLINE_TITLE = enchant.getProperty("EnableTitleOfflinePlayer", false);
		TITLE_OFFLINE_PLAYER = enchant.getProperty("TitleOfflinePlayer", "Disconnected");
		RESTORE_OFFLINERS_PLAYERS = enchant.getProperty("RestoreOfflinePlayers", false);
		OFFLINE_MAX_DAYS_PLAYERS = enchant.getProperty("MaxDaysOnlinePlayers", 1);
		
		CKM_ENABLED = enchant.getProperty("CKMEnabled", false);
		CKM_CYCLE_LENGTH = enchant.getProperty("CKMCycleLength", 86400000);
		CKM_PVP_NPC_TITLE = enchant.getProperty("CKMPvPNpcTitle", "%kills% PvPs in the last 24h");
		CKM_PVP_NPC_TITLE_COLOR = Integer.decode("0x" + enchant.getProperty("CKMPvPNpcTitleColor", "00CCFF"));
		CKM_PVP_NPC_NAME_COLOR = Integer.decode("0x" + enchant.getProperty("CKMPvPNpcNameColor", "FFFFFF"));
		CKM_PK_NPC_TITLE = enchant.getProperty("CKMPKNpcTitle", "%kills% PKs in the last 24h");
		CKM_PK_NPC_TITLE_COLOR = Integer.decode("0x" + enchant.getProperty("CKMPKNpcTitleColor", "00CCFF"));
		CKM_PK_NPC_NAME_COLOR = Integer.decode("0x" + enchant.getProperty("CKMPKNpcNameColor", "FFFFFF"));
		CKM_PLAYER_REWARDS = parseItemsList(enchant.getProperty("CKMReward", "6651,50"));
		
		MIN_ENCHANT_FAILED = Integer.parseInt(enchant.getProperty("MinEnchantFailedAnnuncie", "16"));
		ANNUNCIE_FAILED_ENCHANT = Boolean.parseBoolean(enchant.getProperty("AnnounceFailedEnchant", "False"));
		ENABLE_ENCHANT_ANNOUNCE = Boolean.parseBoolean(enchant.getProperty("EnableEnchantAnnounce", "False"));
		ENCHANT_ANNOUNCE_LEVEL = Integer.parseInt(enchant.getProperty("EnchantAnnounceLevel", "16"));
		ENABLE_COLOR_CHAT_ENCHANT = Boolean.parseBoolean(enchant.getProperty("EnableColorChatEnchant", "False"));
		CHAT_COLOR_ENCHANT = Integer.parseInt(enchant.getProperty("ColorChatEnchantID", "16"));
		String[] propertySplit = enchant.getProperty("NormalWeaponEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_WEAPON_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("BlessWeaponEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_WEAPON_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("CrystalWeaponEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_WEAPON_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("DonatorWeaponEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_WEAPON_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("NormalArmorEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_ARMOR_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("BlessArmorEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_ARMOR_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("CrystalArmorEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_ARMOR_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("DonatorArmorEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_ARMOR_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("NormalJewelryEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_JEWELRY_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("BlessJewelryEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_JEWELRY_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("CrystalJewelryEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_JEWELRY_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("DonatorJewelryEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_JEWELRY_ENCHANT_LEVEL.put(Integer.valueOf(Integer.parseInt(writeData[0])), Integer.valueOf(Integer.parseInt(writeData[1])));
				}
				catch (NumberFormatException nfe)
				{
					if (DEBUG_PATH)
					{
						nfe.printStackTrace();
					}
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		ENCHANT_HERO_WEAPON = Boolean.parseBoolean(enchant.getProperty("EnableEnchantHeroWeapons", "False"));
		
		GOLD_WEAPON = Integer.parseInt(enchant.getProperty("IdEnchantDonatorWeapon", "10010"));
		
		GOLD_ARMOR = Integer.parseInt(enchant.getProperty("IdEnchantDonatorArmor", "10011"));
		
		ENCHANT_SAFE_MAX = Integer.parseInt(enchant.getProperty("EnchantSafeMax", "3"));
		
		ENCHANT_SAFE_MAX_FULL = Integer.parseInt(enchant.getProperty("EnchantSafeMaxFull", "4"));
		
		SCROLL_STACKABLE = Boolean.parseBoolean(enchant.getProperty("ScrollStackable", "False"));
		
		ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("EnchantWeaponMax", "25"));
		ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("EnchantArmorMax", "25"));
		ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("EnchantJewelryMax", "25"));
		
		BLESSED_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantWeaponMax", "25"));
		BLESSED_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantArmorMax", "25"));
		BLESSED_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantJewelryMax", "25"));
		
		BREAK_ENCHANT = Integer.valueOf(enchant.getProperty("BreakEnchant", "0")).intValue();
		
		CRYSTAL_ENCHANT_MIN = Integer.parseInt(enchant.getProperty("CrystalEnchantMin", "20"));
		CRYSTAL_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantWeaponMax", "25"));
		CRYSTAL_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantArmorMax", "25"));
		CRYSTAL_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantJewelryMax", "25"));
		
		DONATOR_ENCHANT_MIN = Integer.parseInt(enchant.getProperty("DonatorEnchantMin", "20"));
		DONATOR_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantWeaponMax", "25"));
		DONATOR_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantArmorMax", "25"));
		DONATOR_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantJewelryMax", "25"));
		DONATOR_DECREASE_ENCHANT = Boolean.valueOf(enchant.getProperty("DonatorDecreaseEnchant", "false")).booleanValue();
		BLESSED_DECREASE_ENCHANT = Boolean.valueOf(enchant.getProperty("BlessedDecreaseEnchant", "false")).booleanValue();
		SYSTEM_ENCHANT_BLESS_REDUCED = Boolean.parseBoolean(enchant.getProperty("EnableSystemBlessDecrease", "False"));
	}
	
	/**
	 * Loads New Pet Manager settings.<br>
	 */
	private static final void loadPetManagerConfig()
	{
		try
		{
			final ExProperties petManagerProps = initProperties(PET_MANAGER);
			
			// Le a flag de habilitação
			String enable = petManagerProps.getProperty("EnableCustomPetMultipliers", "true").trim();
			ENABLE_CUSTOM_PET_MULTIPLIERS = Boolean.parseBoolean(enable);
			
			if (!ENABLE_CUSTOM_PET_MULTIPLIERS)
			{
				System.out.println("[Config] Custom Pet Multipliers esta desativado.");
				return;
			}
			
			for (String key : petManagerProps.stringPropertyNames())
			{
				// Ignora flags que nao sao NPC IDs
				if (key.equalsIgnoreCase("EnableCustomPetMultipliers") || key.equalsIgnoreCase("PetHealMultiplier"))
					continue;
				
				try
				{
					int npcId = Integer.parseInt(key.trim());
					double mult = Double.parseDouble(petManagerProps.getProperty(key).trim());
					CUSTOM_PET_MULTIPLIERS.put(npcId, mult);
				}
				catch (NumberFormatException e)
				{
					System.err.println("[Config] NPC ID ou multiplicador inválido em PetManager: " + key);
				}
			}
			
			// Lê multiplicador global de cura dos pets
			PET_HEAL_POWER_MULTIPLIER = Double.parseDouble(petManagerProps.getProperty("PetHealMultiplier", "1.0"));
			
		}
		catch (Exception e)
		{
			System.err.println("[Config] Erro ao carregar PET_MANAGER: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads New Char Classic settings.<br>
	 */
	private static final void loadPlayerConfig()
	{
		final ExProperties newbiechar = initProperties(NEWCHARACTER);
		ENABLE_FULL_PARTY_XP = Boolean.parseBoolean(newbiechar.getProperty("EnableSamePartyXpSp", "False"));
		ALLOW_ALLY_TARGET_PARTY = Boolean.parseBoolean(newbiechar.getProperty("AllowOverBuffsPartyNormal", "False"));
		TIME_RESS_CHARACTER_ALLOW = Boolean.parseBoolean(newbiechar.getProperty("AllowRessTimeCharacter", "false"));
		TIME_RESS_CHARACTER = Integer.parseInt(newbiechar.getProperty("RessTimeCharacter", "5"));
		ALLOW_EMAIL_COMMAND = newbiechar.getProperty("AllowEmailSystem", false);
		MAIL_ITEM_EXPIRE_DAYS = Integer.parseInt(newbiechar.getProperty("MailItemExpireDays", "7"));
		MAIL_SEND_TAX = Integer.parseInt(newbiechar.getProperty("MailSendTax", "10000"));
		ENABLE_CONTROL_CAST_OFICIAL = Boolean.parseBoolean(newbiechar.getProperty("AllowCastOficialMagicSkills", "false"));
		BLOCK_REVIVE_TO_MOVIMENT = Boolean.parseBoolean(newbiechar.getProperty("BlockMovimentReviveAnimation", "false"));
		ALLOW_CAST_BREAK = Boolean.parseBoolean(newbiechar.getProperty("AllowCastBreak", "false"));
		SKILLS_NO_ANIMATIONS = Arrays.stream(newbiechar.getProperty("SkillsNoAnimations", "").split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
		
		String[] noConsumeItems = newbiechar.getProperty("ItemsNoConsumedID", "").split(",");
		ITEMS_NO_CONSUME = new ArrayList<>();
		
		for (String id : noConsumeItems)
		{
			try
			{
				ITEMS_NO_CONSUME.add(Integer.parseInt(id.trim()));
			}
			catch (NumberFormatException e)
			{
				_log.warning("Invalid item ID in ItemsNoConsumedID: " + id);
			}
		}
		
		ENABLE_MP_REGE_ORCS_MYSTICS = newbiechar.getProperty("OrcsMysticsRenegerationMPHit", false);
		PLAYERS_NORMAIS_USED_RESTRICTION_ITEMS = newbiechar.getProperty("FreeItemsRenstriction", false);
		ENABLE_FINAL_BUFF_SLOT = newbiechar.getProperty("EnabledFinalBuffInterface", false);
		ENABLE_PENALTY_WATER_ZONE = newbiechar.getProperty("DisableLowSpeedWaterZone", false);
		ENABLE_SKILL_MAIN_CLASS = newbiechar.getProperty("EnableSKillOnlyMainClass", false);
		ALLOW_HEALER_NPC = newbiechar.getProperty("AllowNpchtml", false);
		BUY_SKILL_ITEM = newbiechar.getProperty("BuySkillItemId", 15);
		BUY_SKILL_PRICE = newbiechar.getProperty("BuySkillItemCount", 15);
		BUY_SKILL_MAX_SLOTS = newbiechar.getProperty("BuySkillMaxSlots", 15);
		REMOVE_SKILL_COIN_ID = newbiechar.getProperty("RemoveSkillCoinId", 57);
		REMOVE_SKILL_COIN_COUNT = newbiechar.getProperty("RemoveSkillCoinCount", 57);
		CHARGE_BACK_ITEM_ID = newbiechar.getProperty("ChargeBackCoinId", 57);
		CHARGE_BACK_ITEM_COUNT = newbiechar.getProperty("ChargeBackCoinCount", 57);
		FREE_ITEMS_MULTISELL_BETA = Boolean.parseBoolean(newbiechar.getProperty("FreeItemsMultisellBT", "False"));
		EFFECT_AIO_BUFF_CHARACTER = Boolean.parseBoolean(newbiechar.getProperty("EnableEffectAIOBuffer", "False"));
		ALLOW_AIO_USE_CM = Boolean.parseBoolean(newbiechar.getProperty("AllowAioUseClassMaster", "False"));
		ALLOW_AIO_IN_EVENTS = Boolean.parseBoolean(newbiechar.getProperty("AllowAioInEvents", "False"));
		AIO_ITEM = Integer.parseInt(newbiechar.getProperty("AioCoin", "10"));
		AIO_DIAS = Integer.parseInt(newbiechar.getProperty("AioDays", "10"));
		AIO_ITEM2 = Integer.parseInt(newbiechar.getProperty("AioCoin2", "10"));
		AIO_DIAS2 = Integer.parseInt(newbiechar.getProperty("AioDays2", "10"));
		AIO_ITEM3 = Integer.parseInt(newbiechar.getProperty("AioCoin3", "10"));
		AIO_DIAS3 = Integer.parseInt(newbiechar.getProperty("AioDays3", "10"));
		/** AIO System */
		ENABLE_AIO_SYSTEM = Boolean.parseBoolean(newbiechar.getProperty("EnableAioSystem", "True"));
		ALLOW_AIO_NCOLOR = Boolean.parseBoolean(newbiechar.getProperty("AllowAioNameColor", "True"));
		AIO_NCOLOR = Integer.decode("0x" + newbiechar.getProperty("AioNameColor", "88AA88"));
		ALLOW_AIO_TCOLOR = Boolean.parseBoolean(newbiechar.getProperty("AllowAioTitleColor", "True"));
		AIO_TCOLOR = Integer.decode("0x" + newbiechar.getProperty("AioTitleColor", "88AA88"));
		AIO_ITEMID = Integer.parseInt(newbiechar.getProperty("ItemIdAio", "9945"));
		ALLOW_AIO_ITEM = Boolean.parseBoolean(newbiechar.getProperty("AllowAIOItem", "False"));
		if (ENABLE_AIO_SYSTEM) // create map if system is enabled
		{
			String[] VipSkillsSplit = newbiechar.getProperty("AioSkills", "").split(";");
			AIO_SKILLS = new HashMap<>(VipSkillsSplit.length);
			for (String skill : VipSkillsSplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					System.out.println("[AIO System]: invalid config property in players.properties -> AioSkills \"" + skill + "\"");
				}
				else
				{
					try
					{
						AIO_SKILLS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.equals(""))
						{
							System.out.println("[AIO System]: invalid config property in players.props -> AioSkills \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		ENABLE_DWARF_WEAPON_SIZE = Boolean.parseBoolean(newbiechar.getProperty("EnableDwarfWeaponSize", "False"));
		SIZE_WEAPON_DWARF = newbiechar.getProperty("SizeWeaponDwarf", 0);
		ENABLE_WEAPONS_HERO_ALL = Boolean.parseBoolean(newbiechar.getProperty("BuyHeroWeaponsAll", "False"));
		ENABLE_HERO_WEAPON_ENCHANT = Boolean.parseBoolean(newbiechar.getProperty("RewardWeaponEnchantHero", "False"));
		HERO_WEAPON_ENCHANT_LEVEL = newbiechar.getProperty("EnchantWeaponHero", 0);
		ENABLE_ALL_CLASS_TL_ELFVILLAGE = Boolean.parseBoolean(newbiechar.getProperty("EnableAllClassElfToVillage", "False"));
		ALT_GAME_CANCEL_BOW = Boolean.parseBoolean(newbiechar.getProperty("AltGameCancelByBow", "False"));
		ALT_GAME_CANCEL_CAST = Boolean.parseBoolean(newbiechar.getProperty("AltGameCancelByCast", "False"));
		OPEN_EFFECT_CLASSIC_TELEPORTER = Boolean.parseBoolean(newbiechar.getProperty("EnableEffectTeleporter", "False"));
		AUTO_MACRO_AUTOFARM = Boolean.parseBoolean(newbiechar.getProperty("AutoMacroAutoFarmNewbie", "False"));
		AUTO_MACRO_MENU = Boolean.parseBoolean(newbiechar.getProperty("AutoMacroMenuNewbie", "False"));
		ENABLE_USE_SKILLS_BS = Boolean.parseBoolean(newbiechar.getProperty("DeadSkillsUsedAll", "False"));
		TELEPORTER_SKILLS_ACTIVED = Boolean.parseBoolean(newbiechar.getProperty("SkillsContinue", "False"));
		ENABLE_USE_SKILLS_PETS = Boolean.parseBoolean(newbiechar.getProperty("SkillsContinuePets", "False"));
		USE_SKILLS_FORMALWEAR = Boolean.parseBoolean(newbiechar.getProperty("UseSkillsEquipedWear", "False"));
		USE_ITEMS_FORMALWEAR = Boolean.parseBoolean(newbiechar.getProperty("EquipedItemsForWear", "False"));
		CUSTOM_TELEGIRAN_ON_DIE = Boolean.parseBoolean(newbiechar.getProperty("CustomTeleportOnDie", "false"));
		BOOK_ITEM_CUSTOM = Integer.parseInt(newbiechar.getProperty("BookCustomItemID", "0"));
		
		DISABLE_GRADE_PENALTY = Boolean.parseBoolean(newbiechar.getProperty("DisableGradePenalty", "False"));
		DISABLE_WEIGHT_PENALTY = Boolean.parseBoolean(newbiechar.getProperty("DisableWeightPenalty", "False"));
		UNSTUCK_TIME = Integer.parseInt(newbiechar.getProperty("UnstuckTime", "0"));
		NEW_PLAYER_EFFECT = Boolean.parseBoolean(newbiechar.getProperty("NewPlayerEffect", "false"));
		ESPECIAL_VIP_LOGIN = Boolean.parseBoolean(newbiechar.getProperty("CharVipEffectLogin", "false"));
 
		ALLOW_CUSTOM_CHAR_NOBLE = Boolean.parseBoolean(newbiechar.getProperty("CustomStartNobles", "False"));
		STARTING_ADENA = newbiechar.getProperty("StartingAdena", 100);
		ALLOW_CUSTOM_SPAWN_LOCATION = Boolean.parseBoolean(newbiechar.getProperty("AllowCustomSpawnLocation", "false"));
		String custom_spawn_location = newbiechar.getProperty("CustomSpawnLocation", "113852,-108766,-851");
		String custom_spawn_location_splitted[] = custom_spawn_location.split(",");
		CUSTOM_SPAWN_LOCATION[0] = Integer.parseInt(custom_spawn_location_splitted[0]);
		CUSTOM_SPAWN_LOCATION[1] = Integer.parseInt(custom_spawn_location_splitted[1]);
		CUSTOM_SPAWN_LOCATION[2] = Integer.parseInt(custom_spawn_location_splitted[2]);
		ALLOW_NEW_CHAR_TITLE = Boolean.parseBoolean(newbiechar.getProperty("AllowNewCharTitle", "false"));
		NEW_CHAR_TITLE = newbiechar.getProperty("NewCharTitle", "New Char");
		
		CUSTOM_STARTER_ITEMS_ENABLED = Boolean.parseBoolean(newbiechar.getProperty("CustomStarterItemsEnabled", "False"));
		if (Config.CUSTOM_STARTER_ITEMS_ENABLED)
		{
			String[] propertySplit = newbiechar.getProperty("StartingCustomItemsMage", "57,0").split(";");
			STARTING_CUSTOM_ITEMS_M.clear();
			for (final String reward : propertySplit)
			{
				final String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2)
					_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
				else
				{
					try
					{
						STARTING_CUSTOM_ITEMS_M.add(new int[]
						{
							Integer.parseInt(rewardSplit[0]),
							Integer.parseInt(rewardSplit[1])
						});
					}
					catch (final NumberFormatException nfe)
					{
						if (Config.DEBUG_PATH)
							nfe.printStackTrace();
						if (!reward.isEmpty())
							_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
					}
				}
			}
			
			propertySplit = newbiechar.getProperty("StartingCustomItemsFighter", "57,0").split(";");
			STARTING_CUSTOM_ITEMS_F.clear();
			for (final String reward : propertySplit)
			{
				final String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2)
					_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
				else
				{
					try
					{
						STARTING_CUSTOM_ITEMS_F.add(new int[]
						{
							Integer.parseInt(rewardSplit[0]),
							Integer.parseInt(rewardSplit[1])
						});
					}
					catch (final NumberFormatException nfe)
					{
						if (Config.DEBUG_PATH)
							nfe.printStackTrace();
						
						if (!reward.isEmpty())
							_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
					}
				}
			}
		}
		
		STARTING_LEVEL = Integer.parseInt(newbiechar.getProperty("StartingLevelCharacter", "0"));
		STARTING_BUFFS = Boolean.parseBoolean(newbiechar.getProperty("StartingBuffs", "True"));
		String[] startingBuffsSplit = newbiechar.getProperty("StartingBuffsMage", "1204,2").split(";");
		STARTING_BUFFS_M.clear();
		for (String buff : startingBuffsSplit)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("StartingBuffsMage[Config.load()]: invalid config property -> StartingBuffsMage \"" + buff + "\"");
			else
			{
				try
				{
					STARTING_BUFFS_M.add(new int[]
					{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (STARTING_BUFFS_M == null || STARTING_BUFFS_M.isEmpty())
						System.out.println("ERROOOOOOOOOR WITH STARTING BUFS");
				}
				
			}
		}
		startingBuffsSplit = newbiechar.getProperty("StartingBuffsFighter", "1204,2").split(";");
		STARTING_BUFFS_F.clear();
		for (String buff : startingBuffsSplit)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("StartingBuffsFighter[Config.load()]: invalid config property -> StartingBuffsFighter \"" + buff + "\"");
			else
			{
				try
				{
					STARTING_BUFFS_F.add(new int[]
					{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (STARTING_BUFFS_F == null || STARTING_BUFFS_F.isEmpty())
						System.out.println("ERROOOOOOOOOR WITH STARTING BUFFS");
				}
				
			}
		}
		
	}
	
	public static boolean ALLOW_TOURNAMENT_DIE_LEAVE_BUFF;
	public static boolean TVT_DIE_LEAVE_BUFF;
	public static boolean CTF_DIE_LEAVE_BUFF;
	public static boolean DM_DIE_LEAVE_BUFF;
	public static boolean LM_DIE_LEAVE_BUFF;
	public static boolean FOS_DIE_LEAVE_BUFF;
	public static boolean PVPZONE_DIE_LEAVE_BUFF;
	public static boolean RAIDZONE_DIE_LEAVE_BUFF;
	public static boolean BOSSZONE_DIE_LEAVE_BUFF;
	public static boolean KTB_DIE_LEAVE_BUFF;
	public static boolean FLAGZONE_DIE_LEAVE_BUFF;
	public static boolean LEAVE_ALL_BUFFS_WORLD_DIE;
	
	/**
	 * Loads Death Buffs Settings.<br>
	 */
	private static final void loadDeathLocation()
	{
		final ExProperties deathbufflocation = initProperties(DEATH_BUFFS_FILE);
		ALLOW_TOURNAMENT_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffInTournament", "False"));
		TVT_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffTvTEvent", "False"));
		CTF_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffCTFEvent", "False"));
		DM_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffDMEvent", "False"));
		LM_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffLMEvent", "False"));
		FOS_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffFOSEvent", "False"));
		PVPZONE_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffPvPZone", "False"));
		RAIDZONE_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffRaidZone", "False"));
		BOSSZONE_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffBossZone", "False"));
		KTB_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffKTBEvent", "False"));
		FLAGZONE_DIE_LEAVE_BUFF = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffFlagZone", "False"));
		LEAVE_ALL_BUFFS_WORLD_DIE = Boolean.parseBoolean(deathbufflocation.getProperty("AllowDeathBuffAllWorld", "False"));
	}
	
	/**
	 * Loads Diversos Settings.<br>
	 */
	private static final void loadDiversos()
	{
		final ExProperties GeneralMods = initProperties(DIVERSOS_FILE);
		ALLOW_QUAKE_SYSTEM = Boolean.parseBoolean(GeneralMods.getProperty("AllowQuakeSystem", "False"));
		String[] AgathionSkillsSplit = GeneralMods.getProperty("AgathionSkills", "").split(";");
		AGATHION_SKILLS = new HashMap<>(AgathionSkillsSplit.length);
		for (String skill : AgathionSkillsSplit)
		{
			String[] skillSplit = skill.split(",");
			if (skillSplit.length != 2)
			{
				System.out.println("[Agathion System]: invalid config property in diversos.properties -> AgathionSkills \"" + skill + "\"");
			}
			else
			{
				try
				{
					AGATHION_SKILLS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (!skill.equals(""))
					{
						System.out.println("[Agathion System]: invalid config property in Diversos.props -> AgathionSkills \"" + skillSplit[0] + "\"" + skillSplit[1]);
					}
				}
			}
		}
		SKILL_BUFF_ARGUMENT_1 = GeneralMods.getProperty("BuffArgument_ItemID_1", 0);
		SKILL_BUFF_ARGUMENT_2 = GeneralMods.getProperty("BuffArgument_ItemID_2", 0);
		SKILL_BUFF_ARGUMENT_3 = GeneralMods.getProperty("BuffArgument_ItemID_3", 0);
		SKILL_BUFF_ARGUMENT_4 = GeneralMods.getProperty("BuffArgument_ItemID_4", 0);
		SKILL_BUFF_ARGUMENT_5 = GeneralMods.getProperty("BuffArgument_ItemID_5", 0);
		SKILL_BUFF_ARGUMENT_6 = GeneralMods.getProperty("BuffArgument_ItemID_6", 0);
		SKILL_BUFF_ARGUMENT_7 = GeneralMods.getProperty("BuffArgument_ItemID_7", 0);
		SKILL_BUFF_ARGUMENT_8 = GeneralMods.getProperty("BuffArgument_ItemID_8", 0);
		SKILL_BUFF_ARGUMENT_9 = GeneralMods.getProperty("BuffArgument_ItemID_9", 0);
		SKILL_BUFF_ARGUMENT_10 = GeneralMods.getProperty("BuffArgument_ItemID_10", 0);
		SKILL_BUFF_ARGUMENT_11 = GeneralMods.getProperty("BuffArgument_ItemID_11", 0);
		SKILL_BUFF_ARGUMENT_12 = GeneralMods.getProperty("BuffArgument_ItemID_12", 0);
		
		SKILL_BUFF_ARGUMENT_1_ID = GeneralMods.getProperty("BuffArgument_SkillID_1", 0);
		SKILL_BUFF_ARGUMENT_2_ID = GeneralMods.getProperty("BuffArgument_SkillID_2", 0);
		SKILL_BUFF_ARGUMENT_3_ID = GeneralMods.getProperty("BuffArgument_SkillID_3", 0);
		SKILL_BUFF_ARGUMENT_4_ID = GeneralMods.getProperty("BuffArgument_SkillID_4", 0);
		SKILL_BUFF_ARGUMENT_5_ID = GeneralMods.getProperty("BuffArgument_SkillID_5", 0);
		SKILL_BUFF_ARGUMENT_6_ID = GeneralMods.getProperty("BuffArgument_SkillID_6", 0);
		SKILL_BUFF_ARGUMENT_7_ID = GeneralMods.getProperty("BuffArgument_SkillID_7", 0);
		SKILL_BUFF_ARGUMENT_8_ID = GeneralMods.getProperty("BuffArgument_SkillID_8", 0);
		SKILL_BUFF_ARGUMENT_9_ID = GeneralMods.getProperty("BuffArgument_SkillID_9", 0);
		SKILL_BUFF_ARGUMENT_10_ID = GeneralMods.getProperty("BuffArgument_SkillID_10", 0);
		SKILL_BUFF_ARGUMENT_11_ID = GeneralMods.getProperty("BuffArgument_SkillID_11", 0);
		SKILL_BUFF_ARGUMENT_12_ID = GeneralMods.getProperty("BuffArgument_SkillID_12", 0);
		
		SKILL_BUFF_ARGUMENT_1_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_1", 0);
		SKILL_BUFF_ARGUMENT_2_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_2", 0);
		SKILL_BUFF_ARGUMENT_3_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_3", 0);
		SKILL_BUFF_ARGUMENT_4_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_4", 0);
		SKILL_BUFF_ARGUMENT_5_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_5", 0);
		SKILL_BUFF_ARGUMENT_6_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_6", 0);
		SKILL_BUFF_ARGUMENT_7_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_7", 0);
		SKILL_BUFF_ARGUMENT_8_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_8", 0);
		SKILL_BUFF_ARGUMENT_9_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_9", 0);
		SKILL_BUFF_ARGUMENT_10_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_10", 0);
		SKILL_BUFF_ARGUMENT_11_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_11", 0);
		SKILL_BUFF_ARGUMENT_12_LEVEL = GeneralMods.getProperty("BuffArgument_Level_ID_12", 0);
		
		ITEM_COUNT_ROLETA = GeneralMods.getProperty("CountItemRoulette", 0);
		CUSTOM_ITEM_ROULETTE = GeneralMods.getProperty("CustomItemRoulette", 0);
		GLOBAL_VIP_TIME = GeneralMods.getProperty("ChatVipTime", 0);
		COR_CHAT_VIP = GeneralMods.getProperty("ChatColorVIP", 0);
		ENABLE_REWARD_DAILY = Boolean.parseBoolean(GeneralMods.getProperty("EnableRewardDaily", "false"));
		DAILY_REWARD_RESET_TIME = GeneralMods.getProperty("DailyRewardResetTime", "20:00").split(",");
		// ENABLE_BLOCK_SUPPORTZONE = Boolean.valueOf(GeneralMods.getProperty("EnableBlockSupportZone", "false"));
		ENABLE_WARNING = GeneralMods.getProperty("AllowPrivateWarning", false);
		STORE_BUY_CURRENCY = GeneralMods.getProperty("PrivateStoreBuyCoin", 57);
		STORE_SELL_CURRENCY = GeneralMods.getProperty("PrivateStoreSellCoin", 57);
		ANNOUNCE_RAIDBOS_KILL = Boolean.parseBoolean(GeneralMods.getProperty("AnnounceRaidBossKill", "false"));
		ANNOUNCE_GRANDBOS_KILL = Boolean.parseBoolean(GeneralMods.getProperty("AnnounceGranBossKill", "false"));
		ANNOUNCE_BOSS_ALIVE = Boolean.parseBoolean(GeneralMods.getProperty("AnnounceSpawnAllBoss", "false"));
		ALLOW_PK_TITLE_COLOR_SYSTEM = Boolean.parseBoolean(GeneralMods.getProperty("AllowPkTitleColorSystem", "false"));
		String pk_colors = GeneralMods.getProperty("PkColorsTitle", "100,FFFF00");
		String pk_colors_splitted_1[] = pk_colors.split(";");
		for (String s : pk_colors_splitted_1)
		{
			String pk_colors_splitted_2[] = s.split(",");
			PK_COLORS.put(Integer.parseInt(pk_colors_splitted_2[0]), Integer.decode("0x" + pk_colors_splitted_2[1]));
		}
		ALLOW_PVP_NAME_COLOR_SYSTEM = Boolean.parseBoolean(GeneralMods.getProperty("AllowPvpNameColorSystem", "false"));
		String pvp_colors = GeneralMods.getProperty("PvpColorsName", "100,FFFF00");
		String pvp_colors_splitted_1[] = pvp_colors.split(";");
		for (String s : pvp_colors_splitted_1)
		{
			String pvp_colors_splitted_2[] = s.split(",");
			PVP_COLORS.put(Integer.parseInt(pvp_colors_splitted_2[0]), Integer.decode("0x" + pvp_colors_splitted_2[1]));
		}
		ITEM_TICKET_SPAWNSHOP = Integer.parseInt(GeneralMods.getProperty("TicketPawnShopID", "1"));
		ENABLE_GRADE_ITEMS_SPAWNSHOP = Boolean.parseBoolean(GeneralMods.getProperty("EnableGradeItems", "False"));
		for (String list : GeneralMods.getProperty("GradeItemsPawnShop", "A;S").split(";"))
			GRADE_ITEMS_PAWNSHOP.add(CrystalType.valueOf(list));
		ITEMS_NO_RULES_PAWNSHOP_ID = GeneralMods.getProperty("ItemNoBlockPawnShop", "0,0");
		ITEMS_NO_RULES_PAWNSHOP = new ArrayList<>();
		for (String val : ITEMS_NO_RULES_PAWNSHOP_ID.split(","))
		{
			int ItemsIDS = Integer.parseInt(val);
			ITEMS_NO_RULES_PAWNSHOP.add(ItemsIDS);
		}
		STRING_REMOVE_ITEM_COIN = GeneralMods.getProperty("StringTextRemoveItem", "Servidor em Manutencao!");
		STRING_ADD_ITEM_COIN = GeneralMods.getProperty("StringTextAddItem", "Servidor em Manutencao!");
		AMOUNT_ADENA_ADD_PAWNSHOP = Integer.parseInt(GeneralMods.getProperty("PriceAddItemPawnShop", "1"));
		AMOUNT_ADENA_REMOVE_PAWNSHOP = Integer.parseInt(GeneralMods.getProperty("PriceRemoveItemPawnShop", "1"));
		PROTECTED_SKILLS = GeneralMods.getProperty("NotCanceledSkills");
		ALLOW_CUSTOM_CANCEL = new ArrayList<>();
		for (String id : PROTECTED_SKILLS.split(","))
			ALLOW_CUSTOM_CANCEL.add(Integer.parseInt(id));
		CUSTOM_CANCEL_SECONDS = Integer.parseInt(GeneralMods.getProperty("TimeReturnBuffs", "10"));
		ANNOUNCE_PVP_KILL = Boolean.parseBoolean(GeneralMods.getProperty("AnnouncePvPKill", "false"));
		ANNOUNCE_PK_MSG = GeneralMods.getProperty("AnnouncePkMsg", "$killer has matou $target");
		ANNOUNCE_PVP_MSG = GeneralMods.getProperty("AnnouncePvpMsg", "$killer derrotou $target");
		ANNOUNCE_PK_KILL = Boolean.parseBoolean(GeneralMods.getProperty("AnnouncePkKill", "false"));
		PK_ACTIVAR = Boolean.parseBoolean(GeneralMods.getProperty("PkRenawrdEnable", "False"));
		PK_REWARD = Integer.parseInt(GeneralMods.getProperty("PkReward", "6673"));
		PK_CANTIDAD = Integer.parseInt(GeneralMods.getProperty("Pkamount", "1"));
		PVP_ACTIVAR = Boolean.parseBoolean(GeneralMods.getProperty("PvPRenawrdEnable", "False"));
		PVP_REWARD = Integer.parseInt(GeneralMods.getProperty("PvPReward", "6673"));
		PVP_CANTIDAD = Integer.parseInt(GeneralMods.getProperty("PvPamount", "1"));
		RESTART_BY_TIME_OF_DAY = Boolean.parseBoolean(GeneralMods.getProperty("EnableRestartSystem", "false"));
		RESTART_SECONDS = Integer.parseInt(GeneralMods.getProperty("RestartSeconds", "360"));
		RESTART_INTERVAL_BY_TIME_OF_DAY = GeneralMods.getProperty("RestartByTimeOfDay", "20:00").split(",");
		ANNOUNCE_DROP_ITEM = Boolean.parseBoolean(GeneralMods.getProperty("AllowAnuncieDropBoss", "false"));
		INFINITY_SS = GeneralMods.getProperty("InfinitySS", false);
		INFINITY_ARROWS = GeneralMods.getProperty("InfinityArrows", false);
		ANNOUNCE_HERO = Boolean.parseBoolean(GeneralMods.getProperty("AnnounceHeroOn", "false"));
		HERO_SKILL_SUB = Boolean.parseBoolean(GeneralMods.getProperty("AllowHeroSkillsSubClass", "false"));
		ANNOUNCE_CASTLE_LORDS = GeneralMods.getProperty("AnnounceCastleLords", false);
		ANNOUNCE_VIP = Boolean.parseBoolean(GeneralMods.getProperty("AnnounceVipOn", "false"));
		ENABLE_CLASS_DARK_Y_ELF = Boolean.parseBoolean(GeneralMods.getProperty("EnableSubClassElfYDark", "False"));
		ENABLE_ALL_CLASS_SUBCLASS = Boolean.parseBoolean(GeneralMods.getProperty("EnableAllSubClass", "False"));
		ENABLE_CLASS_OVERLORD_Y_WARSMITH = Boolean.parseBoolean(GeneralMods.getProperty("EnableSubClassOverlordYWarmith", "False"));
		GM_SUPER_HASTE = Boolean.parseBoolean(GeneralMods.getProperty("GMStartupSuperHaste", "true"));
		ALT_GAME_SUBCLASS_EVERYWHERE = GeneralMods.getProperty("AltSubclassAllNpcs", false);
		ALT_GAME_FREE_TELEPORT = GeneralMods.getProperty("AltFreeTeleporting", false);
		FREE_TELEPORT_UNTIL = Integer.parseInt(GeneralMods.getProperty("FreeTeleportUntil", "1"));
		ENABLE_ALTERNATIVE_SKILL_DURATION = Boolean.parseBoolean(GeneralMods.getProperty("EnableAlternativeSkillDuration", "false"));
		if (ENABLE_ALTERNATIVE_SKILL_DURATION)
		{
			SKILL_DURATION_LIST = new HashMap<>();
			
			String[] propertySplit;
			propertySplit = GeneralMods.getProperty("SkillDurationList", "").split(";");
			
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						
						if (!skill.equals(""))
						{
							System.out.println("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
	}
	
	private static final void SiegeManager()
	{
		final ExProperties sieges = Config.initProperties(Config.SIEGE_OTHERS_FILE);
		DISABLED_CHAT_NPCS_SIEGE = Boolean.parseBoolean(sieges.getProperty("ChatNpcSiegeDisabled", "true"));
		ENABLE_SIEGE_MANAGER = sieges.getProperty("EnableSiegeManager", false);
		DAY_TO_SIEGE = Integer.parseInt(sieges.getProperty("DayToSiege", "2"));
		SIEGE_HOUR_GLUDIO = Integer.parseInt(sieges.getProperty("HourToSiege_Gludio", "16"));
		SIEGE_HOUR_DION = Integer.parseInt(sieges.getProperty("HourToSiege_Dion", "16"));
		SIEGE_HOUR_GIRAN = Integer.parseInt(sieges.getProperty("HourToSiege_Giran", "16"));
		SIEGE_HOUR_OREN = Integer.parseInt(sieges.getProperty("HourToSiege_Oren", "16"));
		SIEGE_HOUR_ADEN = Integer.parseInt(sieges.getProperty("HourToSiege_Aden", "16"));
		SIEGE_HOUR_INNADRIL = Integer.parseInt(sieges.getProperty("HourToSiege_Innadril", "16"));
		SIEGE_HOUR_GODDARD = Integer.parseInt(sieges.getProperty("HourToSiege_Goddard", "16"));
		SIEGE_HOUR_RUNE = Integer.parseInt(sieges.getProperty("HourToSiege_Rune", "16"));
		SIEGE_HOUR_SCHUT = Integer.parseInt(sieges.getProperty("HourToSiege_Schuttgart", "16"));
		
		SIEGE_DAY_GLUDIO = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Gludio", "1"));
		SIEGE_DAY_DION = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Dion", "7"));
		SIEGE_DAY_GIRAN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Giran", "1"));
		SIEGE_DAY_OREN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Oren", "7"));
		SIEGE_DAY_ADEN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Aden", "1"));
		SIEGE_DAY_INNADRIL = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Innadril", "7"));
		SIEGE_DAY_GODDARD = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Goddard", "1"));
		SIEGE_DAY_RUNE = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Rune", "7"));
		SIEGE_DAY_SCHUT = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Schuttgart", "1"));
		
		SIEGE_GLUDIO = sieges.getProperty("Siege_Gludio", false);
		SIEGE_DION = sieges.getProperty("Siege_Dion", false);
		SIEGE_GIRAN = sieges.getProperty("Siege_Giran", false);
		SIEGE_OREN = sieges.getProperty("Siege_Oren", false);
		SIEGE_ADEN = sieges.getProperty("Siege_Aden", false);
		SIEGE_INNADRIL = sieges.getProperty("Siege_Innadril", false);
		SIEGE_GODDARD = sieges.getProperty("Siege_Goddard", false);
		SIEGE_RUNE = sieges.getProperty("Siege_Rune", false);
		SIEGE_SCHUT = sieges.getProperty("Siege_Schuttgart", false);
		ENABLE_CLAN_REGS_SIEGE = sieges.getProperty("EnableClansRegistrationSiege", false);
		MESSEGE_CLAN_REGISTRADOS = sieges.getProperty("MessengeClanNoRegistration", "");
		ENABLE_WINNNER_REWARD_SIEGE_CLAN = Boolean.parseBoolean(sieges.getProperty("EnableRewardWinnerClan", "true"));
		SIEGE_REWARD_ITEM = Integer.parseInt(sieges.getProperty("SiegeRewardItemId", "57"));
		SIEGE_AMOUNT_REWARD = Integer.parseInt(sieges.getProperty("SiegeRewardItemAmount", "1000000"));
		ENABLE_DOOR_KILL_MAGICS = sieges.getProperty("EnableKillMagicDoors", true);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		FLAGS_MAX_COUNT = sieges.getProperty("MaxFlags", 1);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
	}

	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, rates, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(SERVER_FILE);
		
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		PORT_GAME = server.getProperty("GameserverPort", 7777);
		
		EXTERNAL_HOSTNAME = server.getProperty("ExternalHostname", "*");
		INTERNAL_HOSTNAME = server.getProperty("InternalHostname", "*");
		
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHost", "127.0.0.1");
		
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");

		
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		BLOCK_SERVER_PLAYERS = server.getProperty("ShutDownPlayers", false);
		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		
		DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		MIN_PROTOCOL_REVISION = server.getProperty("MinProtocolRevision", 730);
		MAX_PROTOCOL_REVISION = server.getProperty("MaxProtocolRevision", 746);
		if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
			throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server.properties.");
		
		DEFAULT_PUNISH = server.getProperty("DefaultPunish", 2);
		DEFAULT_PUNISH_PARAM = server.getProperty("DefaultPunishParam", 0);
		
		AUTO_LOOT = server.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);
		
		ALLOW_DISCARDITEM = server.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);
		SAVE_DROPPED_ITEM = server.getProperty("SaveDroppedItem", false);
		
		RATE_XP = server.getProperty("RateXp", 1.);
		RATE_SP = server.getProperty("RateSp", 1.);
		RATE_PARTY_XP = server.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = server.getProperty("RatePartySp", 1.);
		RATE_DROP_ADENA = server.getProperty("RateDropAdena", 1.);
		RATE_DROP_SEAL_STONES = Float.parseFloat(server.getProperty("RateDropSealStones", "1.00"));
		/** VIP System */
		VIP_XP_SP_RATE = Float.parseFloat(server.getProperty("VIPXpSpRate", "1.5"));
		VIP_ADENA_RATE = Float.parseFloat(server.getProperty("VIPAdenaRate", "1.5"));
		VIP_DROP_RATE = Float.parseFloat(server.getProperty("VIPDropRate", "1.5"));
		VIP_SPOIL_RATE = Float.parseFloat(server.getProperty("VIPSpoilRate", "1.5"));
		RATE_CONSUMABLE_COST = server.getProperty("RateConsumableCost", 1.);
		RATE_DROP_ITEMS = server.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = server.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_SPOIL = server.getProperty("RateDropSpoil", 1.);
		RATE_DROP_MANOR = server.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = server.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_REWARD = server.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = server.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = server.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = server.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = server.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = server.getProperty("RateSiegeGuardsPrice", 1.);
		RATE_DROP_COMMON_HERBS = server.getProperty("RateCommonHerbs", 1.);
		RATE_DROP_HP_HERBS = server.getProperty("RateHpHerbs", 1.);
		RATE_DROP_MP_HERBS = server.getProperty("RateMpHerbs", 1.);
		RATE_DROP_SPECIAL_HERBS = server.getProperty("RateSpecialHerbs", 1.);
		PLAYER_DROP_LIMIT = server.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = server.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = server.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = server.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = server.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = server.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = server.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = server.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = server.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = server.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = server.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = server.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = server.getProperty("KarmaRateDropEquipWeapon", 10);
		
		ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = server.getProperty("AllowWear", true);
		WEAR_DELAY = server.getProperty("WearDelay", 5);
		WEAR_PRICE = server.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = server.getProperty("AllowLottery", true);
		ALLOW_WATER = server.getProperty("AllowWater", true);
		ALLOW_MANOR = server.getProperty("AllowManor", true);
		ALLOW_BOAT = server.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);
		
		ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);
		
		ALT_DEV_NO_SPAWNS = server.getProperty("NoSpawns", false);
		DEBUG = server.getProperty("Debug", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		DEADLOCK_DETECTOR = server.getProperty("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = server.getProperty("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = server.getProperty("RestartOnDeadlock", false);
		
		LOG_CHAT = server.getProperty("LogChat", false);
		LOG_ITEMS = server.getProperty("LogItems", false);
		GMAUDIT = server.getProperty("GMAudit", false);
		
		ENABLE_COMMUNITY_BOARD = server.getProperty("EnableCommunityBoard", false);
		BBS_DEFAULT = server.getProperty("BBSDefault", "_bbshome");
		
		ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 500);
		MULTISELL_TIME = server.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
		MANOR_TIME = server.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = server.getProperty("SocialTime", 2000);
		USER_ITEM_TIME = server.getProperty("UserItemTime", 4200);
		USER_ITEM_TIME_ENCHANT = server.getProperty("UserItemTimeEnchant", 4200);
		L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
		AUTODELETE_INVALID_QUEST_DATA = server.getProperty("AutoDeleteInvalidQuestData", false);
		ZONE_TOWN = server.getProperty("ZoneTown", 0);
		SERVER_NEWS = server.getProperty("ShowServerNews", false);
		DISABLE_TUTORIAL = server.getProperty("DisableTutorial", false);
		
		SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = server.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = server.getProperty("ThreadsPerInstantThreadPool", 2);
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	public static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGIN_CONFIGURATION_FILE);
		
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHostname", "*");
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9013);
		
		LOGIN_BIND_ADDRESS = server.getProperty("LoginserverHostname", "*");
		PORT_LOGIN = server.getProperty("LoginserverPort", 2106);
		
		DEBUG = server.getProperty("Debug", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", true);
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		
		LOG_LOGIN_CONTROLLER = server.getProperty("LogLoginController", false);
		
		INTERNAL_HOSTNAME = server.getProperty("InternalHostname", "localhost");
		EXTERNAL_HOSTNAME = server.getProperty("ExternalHostname", "localhost");
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");

		SHOW_LICENCE = server.getProperty("ShowLicence", true);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);
		
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
	}
	
	static boolean _logger;
	
	public static final void loadGameServer()
	{
		if (!_logger)
		{
			_log.info("Loading gameserver configuration files.");
			_logger = true;
		}
		// clans settings
		loadClans();
		
		// events settings
		loadEvents();
		
		// geoengine settings
		loadGeoengine();
		
		// hexID
		loadHexID();
		
		// NPCs/monsters settings
		loadNpcs();
		
		// players settings
		loadPlayers();
		
		// server settings
		loadServer();
		
		// Siege setttings
		SiegeManager();
		
		// Diversos settings
		loadDiversos();
		
		// DeathBuff Location
		loadDeathLocation();
		
		// Personagem settings
		loadPlayerConfig();
		
		// Pet Manager
		loadPetManagerConfig();
		
		// Custom settings
		loadCustomConfig();
		
		// Protege settings
		loadProtection();
		
		// Donate settings
		loadDonate();
		
		// Boss settings
		loadRaid();
		
		// Commandos settins
		loadCommands();
		
		// damges classes status
		loadClassDamage();
		
		// Sell Buffs
		loadSellBuffs();
		
		// Party Farm Evento
		loadPartyFarm();
		
		// Solo Farm Evento
		loadSoloEvent();
		
		// Event Spoil Evento
		loadSpoilEvent();
		
		// PvP kill Evento
		loadPvpEvent();
		
		// Mission Evento
		loadMission();
		
		// Solo Boss
		loadSoloBoss();
		
		// Champion Invade
		loadChampionInvade();
		
	}
	
	public static final void loadLoginServer()
	{
		_log.info("Loading loginserver configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		_log.info("Loading account manager configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		_log.info("Loading gameserver registration configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGeodataConverter()
	{
		_log.info("Loading geodata converter configuration files.");
		
		// geoengine settings
		loadGeoengine();
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		public ClassMasterSettings(String configLine)
		{
			_allowedClassChange = new HashMap<>(3);
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				// Get allowed class change.
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				// Parse items needed for class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				// Feed the map, and clean the list.
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				// Parse gifts after class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
}