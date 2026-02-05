package net.sf.l2j.gameserver.scriptings.scripts.ai.individual;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.bosstimerespawn.TimeEpicBossManager;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2CommandChannel;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.zone.type.L2BossZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillCanceld;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scriptings.scripts.ai.L2AttackableAIScript;
import net.sf.l2j.gameserver.skills.AbnormalEffect;
import net.sf.l2j.gameserver.templates.StatsSet;

public class Frintezza extends L2AttackableAIScript
{
	private static final int[][] _invadeLoc =
	{
		{
			174102,
			-76039,
			-5105
		},
		{
			173235,
			-76884,
			-5105
		},
		{
			175003,
			-76933,
			-5105
		},
		{
			174196,
			-76190,
			-5105
		},
		{
			174013,
			-76120,
			-5105
		},
		{
			173263,
			-75161,
			-5105
		}
	};
	
	private static final int[][] _skill =
	{
		{
			5015,
			1,
			5000
		},
		{
			5015,
			4,
			5000
		},
		{
			5015,
			2,
			5000
		},
		{
			5015,
			5,
			5000
		},
		{
			5018,
			1,
			10000
		},
		{
			5016,
			1,
			5000
		},
		{
			5015,
			3,
			5000
		},
		{
			5015,
			6,
			5000
		},
		{
			5018,
			2,
			10000
		},
		{
			5019,
			1,
			10000
		},
		{
			5016,
			1,
			5000
		}
	};
	
	private static final int[][] _mobLoc =
	{
		{
			18328,
			172894,
			-76019,
			-5107,
			243
		},
		{
			18328,
			174095,
			-77279,
			-5107,
			16216
		},
		{
			18328,
			174111,
			-74833,
			-5107,
			49043
		},
		{
			18328,
			175344,
			-76042,
			-5107,
			32847
		},
		{
			18330,
			173489,
			-76227,
			-5134,
			63565
		},
		{
			18330,
			173498,
			-75724,
			-5107,
			58498
		},
		{
			18330,
			174365,
			-76745,
			-5107,
			22424
		},
		{
			18330,
			174570,
			-75584,
			-5107,
			31968
		},
		{
			18330,
			174613,
			-76179,
			-5107,
			31471
		},
		{
			18332,
			173620,
			-75981,
			-5107,
			4588
		},
		{
			18332,
			173630,
			-76340,
			-5107,
			62454
		},
		{
			18332,
			173755,
			-75613,
			-5107,
			57892
		},
		{
			18332,
			173823,
			-76688,
			-5107,
			2411
		},
		{
			18332,
			174000,
			-75411,
			-5107,
			54718
		},
		{
			18332,
			174487,
			-75555,
			-5107,
			33861
		},
		{
			18332,
			174517,
			-76471,
			-5107,
			21893
		},
		{
			18332,
			174576,
			-76122,
			-5107,
			31176
		},
		{
			18332,
			174600,
			-75841,
			-5134,
			35927
		},
		{
			18329,
			173481,
			-76043,
			-5107,
			61312
		},
		{
			18329,
			173539,
			-75678,
			-5107,
			59524
		},
		{
			18329,
			173584,
			-76386,
			-5107,
			3041
		},
		{
			18329,
			173773,
			-75420,
			-5107,
			51115
		},
		{
			18329,
			173777,
			-76650,
			-5107,
			12588
		},
		{
			18329,
			174585,
			-76510,
			-5107,
			21704
		},
		{
			18329,
			174623,
			-75571,
			-5107,
			40141
		},
		{
			18329,
			174744,
			-76240,
			-5107,
			29202
		},
		{
			18329,
			174769,
			-75895,
			-5107,
			29572
		},
		{
			18333,
			173861,
			-76011,
			-5107,
			383
		},
		{
			18333,
			173872,
			-76461,
			-5107,
			8041
		},
		{
			18333,
			173898,
			-75668,
			-5107,
			51856
		},
		{
			18333,
			174422,
			-75689,
			-5107,
			42878
		},
		{
			18333,
			174460,
			-76355,
			-5107,
			27311
		},
		{
			18333,
			174483,
			-76041,
			-5107,
			30947
		},
		{
			18331,
			173515,
			-76184,
			-5107,
			6971
		},
		{
			18331,
			173516,
			-75790,
			-5134,
			3142
		},
		{
			18331,
			173696,
			-76675,
			-5107,
			6757
		},
		{
			18331,
			173766,
			-75502,
			-5134,
			60827
		},
		{
			18331,
			174473,
			-75321,
			-5107,
			37147
		},
		{
			18331,
			174493,
			-76505,
			-5107,
			34503
		},
		{
			18331,
			174568,
			-75654,
			-5134,
			41661
		},
		{
			18331,
			174584,
			-76263,
			-5107,
			31729
		},
		{
			18339,
			173892,
			-81592,
			-5123,
			50849
		},
		{
			18339,
			173958,
			-81820,
			-5123,
			7459
		},
		{
			18339,
			174128,
			-81805,
			-5150,
			21495
		},
		{
			18339,
			174245,
			-81566,
			-5123,
			41760
		},
		{
			18334,
			173264,
			-81529,
			-5072,
			1646
		},
		{
			18334,
			173265,
			-81656,
			-5072,
			441
		},
		{
			18334,
			173267,
			-81889,
			-5072,
			0
		},
		{
			18334,
			173271,
			-82015,
			-5072,
			65382
		},
		{
			18334,
			174867,
			-81655,
			-5073,
			32537
		},
		{
			18334,
			174868,
			-81890,
			-5073,
			32768
		},
		{
			18334,
			174869,
			-81485,
			-5073,
			32315
		},
		{
			18334,
			174871,
			-82017,
			-5073,
			33007
		},
		{
			18335,
			173074,
			-80817,
			-5107,
			8353
		},
		{
			18335,
			173128,
			-82702,
			-5107,
			5345
		},
		{
			18335,
			173181,
			-82544,
			-5107,
			65135
		},
		{
			18335,
			173191,
			-80981,
			-5107,
			6947
		},
		{
			18335,
			174859,
			-80889,
			-5134,
			24103
		},
		{
			18335,
			174924,
			-82666,
			-5107,
			38710
		},
		{
			18335,
			174947,
			-80733,
			-5107,
			22449
		},
		{
			18335,
			175096,
			-82724,
			-5107,
			42205
		},
		{
			18336,
			173435,
			-80512,
			-5107,
			65215
		},
		{
			18336,
			173440,
			-82948,
			-5107,
			417
		},
		{
			18336,
			173443,
			-83120,
			-5107,
			1094
		},
		{
			18336,
			173463,
			-83064,
			-5107,
			286
		},
		{
			18336,
			173465,
			-80453,
			-5107,
			174
		},
		{
			18336,
			173465,
			-83006,
			-5107,
			2604
		},
		{
			18336,
			173468,
			-82889,
			-5107,
			316
		},
		{
			18336,
			173469,
			-80570,
			-5107,
			65353
		},
		{
			18336,
			173469,
			-80628,
			-5107,
			166
		},
		{
			18336,
			173492,
			-83121,
			-5107,
			394
		},
		{
			18336,
			173493,
			-80683,
			-5107,
			0
		},
		{
			18336,
			173497,
			-80510,
			-5134,
			417
		},
		{
			18336,
			173499,
			-82947,
			-5107,
			0
		},
		{
			18336,
			173521,
			-83063,
			-5107,
			316
		},
		{
			18336,
			173523,
			-82889,
			-5107,
			128
		},
		{
			18336,
			173524,
			-80627,
			-5134,
			65027
		},
		{
			18336,
			173524,
			-83007,
			-5107,
			0
		},
		{
			18336,
			173526,
			-80452,
			-5107,
			64735
		},
		{
			18336,
			173527,
			-80569,
			-5134,
			65062
		},
		{
			18336,
			174602,
			-83122,
			-5107,
			33104
		},
		{
			18336,
			174604,
			-82949,
			-5107,
			33184
		},
		{
			18336,
			174609,
			-80514,
			-5107,
			33234
		},
		{
			18336,
			174609,
			-80684,
			-5107,
			32851
		},
		{
			18336,
			174629,
			-80627,
			-5107,
			33346
		},
		{
			18336,
			174632,
			-80570,
			-5107,
			32896
		},
		{
			18336,
			174632,
			-83066,
			-5107,
			32768
		},
		{
			18336,
			174635,
			-82893,
			-5107,
			33594
		},
		{
			18336,
			174636,
			-80456,
			-5107,
			32065
		},
		{
			18336,
			174639,
			-83008,
			-5107,
			33057
		},
		{
			18336,
			174660,
			-80512,
			-5107,
			33057
		},
		{
			18336,
			174661,
			-83121,
			-5107,
			32768
		},
		{
			18336,
			174663,
			-82948,
			-5107,
			32768
		},
		{
			18336,
			174664,
			-80685,
			-5107,
			32676
		},
		{
			18336,
			174687,
			-83008,
			-5107,
			32520
		},
		{
			18336,
			174691,
			-83066,
			-5107,
			32961
		},
		{
			18336,
			174692,
			-80455,
			-5107,
			33202
		},
		{
			18336,
			174692,
			-80571,
			-5107,
			32768
		},
		{
			18336,
			174693,
			-80630,
			-5107,
			32994
		},
		{
			18336,
			174693,
			-82889,
			-5107,
			32622
		},
		{
			18337,
			172837,
			-82382,
			-5107,
			58363
		},
		{
			18337,
			172867,
			-81123,
			-5107,
			64055
		},
		{
			18337,
			172883,
			-82495,
			-5107,
			64764
		},
		{
			18337,
			172916,
			-81033,
			-5107,
			7099
		},
		{
			18337,
			172940,
			-82325,
			-5107,
			58998
		},
		{
			18337,
			172946,
			-82435,
			-5107,
			58038
		},
		{
			18337,
			172971,
			-81198,
			-5107,
			14768
		},
		{
			18337,
			172992,
			-81091,
			-5107,
			9438
		},
		{
			18337,
			173032,
			-82365,
			-5107,
			59041
		},
		{
			18337,
			173064,
			-81125,
			-5107,
			5827
		},
		{
			18337,
			175014,
			-81173,
			-5107,
			26398
		},
		{
			18337,
			175061,
			-82374,
			-5107,
			43290
		},
		{
			18337,
			175096,
			-81080,
			-5107,
			24719
		},
		{
			18337,
			175169,
			-82453,
			-5107,
			37672
		},
		{
			18337,
			175172,
			-80972,
			-5107,
			32315
		},
		{
			18337,
			175174,
			-82328,
			-5107,
			41760
		},
		{
			18337,
			175197,
			-81157,
			-5107,
			27617
		},
		{
			18337,
			175245,
			-82547,
			-5107,
			40275
		},
		{
			18337,
			175249,
			-81075,
			-5107,
			28435
		},
		{
			18337,
			175292,
			-82432,
			-5107,
			42225
		},
		{
			18338,
			173014,
			-82628,
			-5107,
			11874
		},
		{
			18338,
			173033,
			-80920,
			-5107,
			10425
		},
		{
			18338,
			173095,
			-82520,
			-5107,
			49152
		},
		{
			18338,
			173115,
			-80986,
			-5107,
			9611
		},
		{
			18338,
			173144,
			-80894,
			-5107,
			5345
		},
		{
			18338,
			173147,
			-82602,
			-5107,
			51316
		},
		{
			18338,
			174912,
			-80825,
			-5107,
			24270
		},
		{
			18338,
			174935,
			-80899,
			-5107,
			18061
		},
		{
			18338,
			175016,
			-82697,
			-5107,
			39533
		},
		{
			18338,
			175041,
			-80834,
			-5107,
			25420
		},
		{
			18338,
			175071,
			-82549,
			-5107,
			39163
		},
		{
			18338,
			175154,
			-82619,
			-5107,
			36345
		}
	};
	
	private static final int SCARLET1 = 29046;
	private static final int SCARLET2 = 29047;
	private static final int FRINTEZZA = 29045;
	private static final int GUIDE = 32011;
	private static final int CUBE = 29061;
	
	// Frintezza Status Tracking :
	private static final byte DORMANT = 0; // Frintezza is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1; // Frintezza is spawend and someone has entered, triggering a 30 minute window for additional people to enter
	// before he unleashes his attack. Entry is unlocked
	private static final byte FIGHTING = 2; // Frintezza is engaged in battle, annihilating his foes. Entry is locked
	private static final byte DEAD = 3; // Frintezza has been killed. Entry is locked
	
	private static long _LastAction = 0;
	private static int _Angle = 0;
	private static int _Heading = 0;
	private static int _LocCycle = 0;
	private static int _Bomber = 0;
	private static int _CheckDie = 0;
	private static int _OnCheck = 0;
	private static int _OnSong = 0;
	private static int _Abnormal = 0;
	private static int _OnMorph = 0;
	private static int _Scarlet_x = 0;
	private static int _Scarlet_y = 0;
	private static int _Scarlet_z = 0;
	private static int _Scarlet_h = 0;
	private static int _SecondMorph = 0;
	private static int _ThirdMorph = 0;
	private static int _KillHallAlarmDevice = 0;
	private static int _KillDarkChoirPlayer = 0;
	private static int _KillDarkChoirCaptain = 0;
	
	private static L2BossZone _Zone;
	@SuppressWarnings("unused")
	private L2GrandBossInstance frintezza, weakScarlet, strongScarlet, activeScarlet;
	private L2MonsterInstance demon1, demon2, demon3, demon4, portrait1, portrait2, portrait3, portrait4;
	private L2Npc _frintezzaDummy, _overheadDummy, _portraitDummy1, _portraitDummy3, _scarletDummy;
	private final List<Player> _PlayersInside = new ArrayList<>();
	private final List<L2Npc> _Room1Mobs = new ArrayList<>();
	private final List<L2Npc> _Room2Mobs = new ArrayList<>();
	private final List<Attackable> Minions = new ArrayList<>();
	
	// Boss: Frintezza
	/*public Frintezza(final int id, final String name, final String descr)
	{
		super(id, name, descr);
		final int[] mob =
		{
			SCARLET1,
			SCARLET2,
			FRINTEZZA,
			18328,
			18329,
			18330,
			18331,
			18332,
			18333,
			18334,
			18335,
			18336,
			18337,
			18338,
			18339,
			29048,
			29049,
			29050,
			29051
		};
		_Zone = GrandBossManager.getInstance().getZone(getXFix(174232), getYFix(-88020), getZFix(-5116));
		registerMobs(mob);
		addStartNpc(GUIDE);
		addTalkId(GUIDE);
		addStartNpc(CUBE);
		addTalkId(CUBE);
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
		
		final Integer status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (status == DEAD)
		{
			final long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
				startQuestTimer("frintezza_unlock", temp, null, null);
			else
				GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		else if (status != DORMANT)
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		
		// tempfix for messed door cords
		for (int i = 0; i < 8; i++)
			DoorTable.getInstance().getDoor(25150051 + i).setRange(0, 0, 0, 0, 0, 0);
	}*/
	public Frintezza()
	{
	    super("ai/individual");
	}

	@Override
	protected void registerNpcs()
	{
	    // Lista de NPCs envolvidos
	    int[] mob =
	    {
	        SCARLET1, SCARLET2, FRINTEZZA,
	        18328, 18329, 18330, 18331, 18332, 18333,
	        18334, 18335, 18336, 18337, 18338, 18339,
	        29048, 29049, 29050, 29051
	    };

	    // Registra a zona do boss
	    _Zone = ZoneManager.getInstance().getZoneById(110011, L2BossZone.class);

	    // Registra os NPCs para eventos de ataque, morte e spawn
	    addAttackId(mob);
	    addKillId(mob);

	    // Registra os NPCs de interação
	    addStartNpc(GUIDE, CUBE);
	    addTalkId(GUIDE, CUBE);

	    // Verifica o status do boss Frintezza
	    StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
	    int status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);

	    // Se o status for DEAD (morto)
	    if (status == DEAD)
	    {
	        long temp = info.getLong("respawn_time") - System.currentTimeMillis();
	        if (temp > 0L)
	        {
	            // Se o tempo de respawn não expirou, inicia o timer
	            startQuestTimer("frintezza_unlock", temp, null, null, false);
	        }
	        else
	        {
	            // Caso o tempo tenha expirado, define o status como DORMANT (dormindo)
	            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
	        }
	    }
	    else if (status != DORMANT)
	    {
	        // Se o status não for DORMANT, define como DORMANT
	        GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
	    }

	    // Corrige as coordenadas das portas (se necessário)
	//    for (int i = 0; i < 8; i++)
	 //   {
	  //      DoorTable.getInstance().getDoor(25150051 + i).setRange(0, 0, 0, 0, 0, 0);
	   // }
	 // Abrir todas as portas de 25150051 a 25150058
	/*    for (int i = 0; i < 8; i++) 
	    {
	        // Obtenha a porta usando o método getDoor() com o ID correspondente
	        L2DoorInstance door = DoorTable.getInstance().getDoor(25150051 + i);
	        
	        // Verifique se a porta foi encontrada
	        if (door != null)
	        {
	            // Abra a porta
	            door.openMe();
	        }
	    }*/
	}

	
	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final Player player)
	{
		long temp = 0;
		if (event.equalsIgnoreCase("waiting"))
		{
		    // Inicia os timers com o parâmetro 'false' para isSummon
		    startQuestTimer("close", 27000L, npc, null, false);
		    startQuestTimer("camera_1", 30000L, npc, null, false);
		    
		    // Envia o terremoto para a zona do boss
		    _Zone.broadcastPacket(new Earthquake(getXFix(174232), getYFix(-88020), getZFix(-5116), 45, 27));
		}

		else if (event.equalsIgnoreCase("room1_spawn"))
		{
			for (int i = 0; i <= 17; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room1Mobs)
				{
					_Room1Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn2"))
		{
			for (int i = 18; i <= 26; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room1Mobs)
				{
					_Room1Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn3"))
		{
			for (int i = 27; i <= 32; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room1Mobs)
				{
					_Room1Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room1_spawn4"))
		{
			for (int i = 33; i <= 40; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room1Mobs)
				{
					_Room1Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room2_spawn"))
		{
			for (int i = 41; i <= 44; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room2Mobs)
				{
					_Room2Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room2_spawn2"))
		{
			for (int i = 45; i <= 131; i++)
			{
				final L2Npc mob = addSpawn(_mobLoc[i][0], _mobLoc[i][1], _mobLoc[i][2], _mobLoc[i][3], _mobLoc[i][4], false, 0);
				synchronized (_Room2Mobs)
				{
					_Room2Mobs.add(mob);
				}
				
			}
		}
		else if (event.equalsIgnoreCase("room1_del"))
		{
			synchronized (_Room1Mobs)
			{
				for (final L2Npc mob : _Room1Mobs)
				{
					if (mob != null)
						mob.deleteMe();
				}
				_Room1Mobs.clear();
			}
			
		}
		else if (event.equalsIgnoreCase("room2_del"))
		{
			synchronized (_Room2Mobs)
			{
				for (final L2Npc mob : _Room2Mobs)
				{
					if (mob != null)
						mob.deleteMe();
				}
				_Room2Mobs.clear();
			}
			
		}
		else if (event.equalsIgnoreCase("room3_del"))
		{
			if (demon1 != null)
				demon1.deleteMe();
			if (demon2 != null)
				demon2.deleteMe();
			if (demon3 != null)
				demon3.deleteMe();
			if (demon4 != null)
				demon4.deleteMe();
			if (portrait1 != null)
				portrait1.deleteMe();
			if (portrait2 != null)
				portrait2.deleteMe();
			if (portrait3 != null)
				portrait3.deleteMe();
			if (portrait4 != null)
				portrait4.deleteMe();
			if (frintezza != null)
				frintezza.deleteMe();
			if (weakScarlet != null)
				weakScarlet.deleteMe();
			if (strongScarlet != null)
				strongScarlet.deleteMe();
			
			demon1 = null;
			demon2 = null;
			demon3 = null;
			demon4 = null;
			portrait1 = null;
			portrait2 = null;
			portrait3 = null;
			portrait4 = null;
			frintezza = null;
			weakScarlet = null;
			strongScarlet = null;
			activeScarlet = null;
		}
		else if (event.equalsIgnoreCase("clean"))
		{
			_LastAction = 0;
			_LocCycle = 0;
			_CheckDie = 0;
			_OnCheck = 0;
			_Abnormal = 0;
			_OnMorph = 0;
			_SecondMorph = 0;
			_ThirdMorph = 0;
			_KillHallAlarmDevice = 0;
			_KillDarkChoirPlayer = 0;
			_KillDarkChoirCaptain = 0;
			_PlayersInside.clear();
		}
		else if (event.equalsIgnoreCase("close"))
		{
			for (int i = 25150051; i <= 25150058; i++)
				DoorTable.getInstance().getDoor(i).closeMe();
			for (int i = 25150061; i <= 25150070; i++)
				DoorTable.getInstance().getDoor(i).closeMe();
			
			DoorTable.getInstance().getDoor(25150042).closeMe();
			DoorTable.getInstance().getDoor(25150043).closeMe();
			DoorTable.getInstance().getDoor(25150045).closeMe();
			DoorTable.getInstance().getDoor(25150046).closeMe();
		}
		else if (event.equalsIgnoreCase("camera_1"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, FIGHTING);
			
			_frintezzaDummy = addSpawn(29052, 174240, -89805, -5022, 16048, false, 0);
			_frintezzaDummy.setIsInvul(true);
			_frintezzaDummy.setIsImmobilized(true);
			
			_overheadDummy = addSpawn(29052, 174232, -88020, -5110, 16384, false, 0);
			_overheadDummy.setIsInvul(true);
			_overheadDummy.setIsImmobilized(true);
			_overheadDummy.setCollisionHeight(600);
			_Zone.broadcastPacket(new NpcInfo(_overheadDummy, null));
			
			_portraitDummy1 = addSpawn(29052, 172450, -87890, -5100, 16048, false, 0);
			_portraitDummy1.setIsImmobilized(true);
			_portraitDummy1.setIsInvul(true);
			
			_portraitDummy3 = addSpawn(29052, 176012, -87890, -5100, 16048, false, 0);
			_portraitDummy3.setIsImmobilized(true);
			_portraitDummy3.setIsInvul(true);
			
			_scarletDummy = addSpawn(29053, 174232, -88020, -5110, 16384, false, 0);
			_scarletDummy.setIsInvul(true);
			_scarletDummy.setIsImmobilized(true);
			
			startQuestTimer("stop_pc", 0L, npc, null, false);
			startQuestTimer("camera_2", 1000L, _overheadDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_2"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100));
			startQuestTimer("camera_2b", 0L, _overheadDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_2b"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 0, 75, -89, 0, 100));
			startQuestTimer("camera_3", 0L, _overheadDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_3"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 300, 90, -10, 6500, 7000));
			
			frintezza = (L2GrandBossInstance) addSpawn(FRINTEZZA, 174240, -89805, -5022, 16048, false, 0);
			GrandBossManager.getInstance().addBoss(frintezza);
			frintezza.setIsImmobilized(true);
			frintezza.setIsInvul(true);
			frintezza.disableAllSkills();
		//	_Zone.updateKnownList(frintezza);
			
			demon2 = (L2MonsterInstance) addSpawn(29051, 175876, -88713, -5100, 28205, false, 0);
			demon2.setIsImmobilized(true);
			demon2.disableAllSkills();
		//	_Zone.updateKnownList(demon2);
			
			demon3 = (L2MonsterInstance) addSpawn(29051, 172608, -88702, -5100, 64817, false, 0);
			demon3.setIsImmobilized(true);
			demon3.disableAllSkills();
		//	_Zone.updateKnownList(demon3);
			
			demon1 = (L2MonsterInstance) addSpawn(29050, 175833, -87165, -5100, 35048, false, 0);
			demon1.setIsImmobilized(true);
			demon1.disableAllSkills();
		//	_Zone.updateKnownList(demon1);
			
			demon4 = (L2MonsterInstance) addSpawn(29050, 172634, -87165, -5100, 57730, false, 0);
			demon4.setIsImmobilized(true);
			demon4.disableAllSkills();
		//	_Zone.updateKnownList(demon4);
			
			startQuestTimer("camera_4", 6500L, _overheadDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_4"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 1800, 90, 8, 6500, 7000));
			startQuestTimer("camera_5", 900L, _frintezzaDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_5"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_frintezzaDummy.getObjectId(), 140, 90, 10, 2500, 4500));
			startQuestTimer("camera_5b", 4000L, _frintezzaDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_5b"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 1000));
			startQuestTimer("camera_6", 0L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_6"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 40, 75, -10, 0, 12000));
			startQuestTimer("camera_7", 1350L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_7"))
		{
			_Zone.broadcastPacket(new SocialAction(frintezza, 2));


			startQuestTimer("camera_8", 7000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_8"))
		{
			startQuestTimer("camera_9", 1000L, frintezza, null, false);

			_frintezzaDummy.deleteMe();
			_frintezzaDummy = null;
		}
		else if (event.equalsIgnoreCase("camera_9"))
		{
			_Zone.broadcastPacket(new SocialAction(demon2, 1));
			_Zone.broadcastPacket(new SocialAction(demon3, 1));

			startQuestTimer("camera_9b", 400L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_9b"))
		{
			_Zone.broadcastPacket(new SocialAction(demon1, 1));
			_Zone.broadcastPacket(new SocialAction(demon4, 1));

			
			for (final Creature pc : _Zone.getCharactersInside())
			{
				if (pc instanceof Player)
				{
					if (pc.getX() < getXFix(174232))
						pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 1000));
					else
						pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 1000));
				}
			}
			startQuestTimer("camera_9c", 0L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_9c"))
		{
			for (final Creature pc : _Zone.getCharactersInside())
			{
				if (pc instanceof Player)
				{
					if (pc.getX() < getXFix(174232))
						pc.broadcastPacket(new SpecialCamera(_portraitDummy1.getObjectId(), 1000, 118, 0, 0, 10000));
					else
						pc.broadcastPacket(new SpecialCamera(_portraitDummy3.getObjectId(), 1000, 62, 0, 0, 10000));
				}
			}
			startQuestTimer("camera_10", 2000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_10"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 0, 0, 1000));
			startQuestTimer("camera_11", 0L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_11"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 240, 90, 25, 5500, 10000));
			_Zone.broadcastPacket(new SocialAction(frintezza, 3));

			_portraitDummy1.deleteMe();
			_portraitDummy3.deleteMe();
			_portraitDummy1 = null;
			_portraitDummy3 = null;
			
			startQuestTimer("camera_12", 4500L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_12"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000));
			startQuestTimer("camera_13", 700L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_13"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 195, 35, 0, 10000));
			startQuestTimer("camera_14", 1300L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_14"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 120, 180, 45, 1500, 10000));
			_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
			startQuestTimer("camera_16", 1500L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_16"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 520, 135, 45, 8000, 10000));
			startQuestTimer("camera_17", 7500L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_17"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 1500, 110, 25, 10000, 13000));
			startQuestTimer("camera_18", 9500L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("camera_18"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 930, 160, -20, 0, 1000));
			startQuestTimer("camera_18b", 0L, _overheadDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_18b"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_overheadDummy.getObjectId(), 600, 180, -25, 0, 10000));
			_Zone.broadcastPacket(new MagicSkillUse(_scarletDummy, _overheadDummy, 5004, 1, 5800, 0));
			
			weakScarlet = (L2GrandBossInstance) addSpawn(SCARLET1, 174232, -88020, -5110, 16384, false, 0);
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
		//	_Zone.updateKnownList(weakScarlet);
			activeScarlet = weakScarlet;
			
			/*
			 * startQuestTimer("camera_19", 2400, _scarletDummy, null); startQuestTimer("camera_19b", 5000, _scarletDummy, null);
			 */
			startQuestTimer("camera_19", 5500L, _scarletDummy, null, false);
			startQuestTimer("camera_19b", 5400L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("camera_19"))
		{
			weakScarlet.teleToLocation(getXFix(174232), getYFix(-88020), getZFix(-5110), 0);
		}
		else if (event.equalsIgnoreCase("camera_19b"))
		{
			_Zone.broadcastPacket(new SpecialCamera(_scarletDummy.getObjectId(), 800, 180, 10, 1000, 10000));
			startQuestTimer("camera_20", 2100L, _scarletDummy, null, false);

		}
		else if (event.equalsIgnoreCase("camera_20"))
		{
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 300, 60, 8, 0, 10000));
			startQuestTimer("camera_21", 2000L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("camera_21"))
		{
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 500, 90, 10, 3000, 5000));
			startQuestTimer("camera_22", 3000L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("camera_22"))
		{
			portrait2 = (L2MonsterInstance) addSpawn(29049, 175876, -88713, -5000, 28205, false, 0);
			portrait2.setIsImmobilized(true);
			portrait2.disableAllSkills();
		//	_Zone.updateKnownList(portrait2);
			
			portrait3 = (L2MonsterInstance) addSpawn(29049, 172608, -88702, -5000, 64817, false, 0);
			portrait3.setIsImmobilized(true);
			portrait3.disableAllSkills();
		//	_Zone.updateKnownList(portrait3);
			
			portrait1 = (L2MonsterInstance) addSpawn(29048, 175833, -87165, -5000, 35048, false, 0);
			portrait1.setIsImmobilized(true);
			portrait1.disableAllSkills();
		//	_Zone.updateKnownList(portrait1);
			
			portrait4 = (L2MonsterInstance) addSpawn(29048, 172634, -87165, -5000, 57730, false, 0);
			portrait4.setIsImmobilized(true);
			portrait4.disableAllSkills();
		//	_Zone.updateKnownList(portrait4);
			
			_overheadDummy.deleteMe();
			_scarletDummy.deleteMe();
			_overheadDummy = null;
			_scarletDummy = null;
			
			startQuestTimer("camera_23", 2000L, weakScarlet, null, false);
			startQuestTimer("start_pc", 2000L, weakScarlet, null, false);
			// startQuestTimer("loc_check", 60000L, weakScarlet, null, true);
			startQuestTimer("songs_play", 10000L + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill01", 10000L + Rnd.get(10000), weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("camera_23"))
		{
			demon1.setIsImmobilized(false);
			demon2.setIsImmobilized(false);
			demon3.setIsImmobilized(false);
			demon4.setIsImmobilized(false);
			demon1.enableAllSkills();
			demon2.enableAllSkills();
			demon3.enableAllSkills();
			demon4.enableAllSkills();
			portrait1.setIsImmobilized(false);
			portrait2.setIsImmobilized(false);
			portrait3.setIsImmobilized(false);
			portrait4.setIsImmobilized(false);
			portrait1.enableAllSkills();
			portrait2.enableAllSkills();
			portrait3.enableAllSkills();
			portrait4.enableAllSkills();
			weakScarlet.setIsInvul(false);
			weakScarlet.setIsImmobilized(false);
			weakScarlet.enableAllSkills();
			weakScarlet.setRunning();
			
			startQuestTimer("spawn_minion", 20000L, portrait1, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait2, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait3, null, false);
			startQuestTimer("spawn_minion", 20000L, portrait4, null, false);

		}
		else if (event.equalsIgnoreCase("stop_pc"))
		{
			for (final Creature cha : _Zone.getCharactersInside())
			{
				cha.abortAttack();
				cha.abortCast();
				cha.disableAllSkills();
				cha.setTarget(null);
				cha.stopMove(null);
				cha.setIsImmobilized(true);
				cha.getAI().setIntention(CtrlIntention.IDLE);
			}
		}
		else if (event.equalsIgnoreCase("stop_npc"))
		{
			_Heading = npc.getHeading();
			if (_Heading < 32768)
				_Angle = Math.abs(180 - (int) (_Heading / 182.044444444));
			else
				_Angle = Math.abs(540 - (int) (_Heading / 182.044444444));
		}
		else if (event.equalsIgnoreCase("start_pc"))
		{
			for (final Creature cha : _Zone.getCharactersInside())
			{
				if (cha != frintezza)
				{
					cha.enableAllSkills();
					cha.setIsImmobilized(false);
				}
			}
		}
		else if (event.equalsIgnoreCase("start_npc"))
		{
			npc.setRunning();
			npc.setIsInvul(false);
		}
		else if (event.equalsIgnoreCase("morph_end"))
		{
			_OnMorph = 0;
		}
		else if (event.equalsIgnoreCase("morph_01"))
		{
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 2000, 15000));
			startQuestTimer("morph_02", 3000L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_02"))
		{
			_Zone.broadcastPacket(new SocialAction(weakScarlet, 1));

			weakScarlet.setRHandId(7903);
			startQuestTimer("morph_03", 4000L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_03"))
		{
			startQuestTimer("morph_04", 1500L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_04"))
		{
			_Zone.broadcastPacket(new SocialAction(weakScarlet, 4));

			final L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
			if (skill != null)
				skill.getEffects(weakScarlet, weakScarlet);
			
			startQuestTimer("morph_end", 6000L, weakScarlet, null, false);
			startQuestTimer("start_pc", 3000L, weakScarlet, null, false);
			startQuestTimer("start_npc", 3000L, weakScarlet, null, false);
			startQuestTimer("songs_play", 10000 + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill02", 10000 + Rnd.get(10000), weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_05a"))
		{
			_Zone.broadcastPacket(new SocialAction(frintezza, 4));

		}
		else if (event.equalsIgnoreCase("morph_05"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 1000));
			startQuestTimer("morph_06", 0L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_06"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 250, 120, 15, 0, 10000));
			
			// cancelQuestTimer("loc_check");
			
			_Scarlet_x = weakScarlet.getX();
			_Scarlet_y = weakScarlet.getY();
			_Scarlet_z = weakScarlet.getZ();
			_Scarlet_h = weakScarlet.getHeading();
			weakScarlet.deleteMe();
			weakScarlet = null;
			activeScarlet = null;
			weakScarlet = (L2GrandBossInstance) addSpawn(SCARLET1, _Scarlet_x, _Scarlet_y, _Scarlet_z, _Scarlet_h, false, 0);
			weakScarlet.setIsInvul(true);
			weakScarlet.setIsImmobilized(true);
			weakScarlet.disableAllSkills();
			weakScarlet.setRHandId(7903);
		//	_Zone.updateKnownList(weakScarlet);
			
			startQuestTimer("morph_07", 7000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_07"))
		{
			_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 500, 70, 15, 3000, 10000));
			startQuestTimer("morph_08", 3000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_08"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 2500, 90, 12, 6000, 10000));
			startQuestTimer("morph_09", 3000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_09"))
		{
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 1000));
			startQuestTimer("morph_10", 0L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_10"))
		{
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 250, _Angle, 12, 0, 10000));
			startQuestTimer("morph_11", 500L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_11"))
		{
			weakScarlet.doDie(weakScarlet);
			_Zone.broadcastPacket(new SpecialCamera(weakScarlet.getObjectId(), 450, _Angle, 14, 8000, 8000));
			
			startQuestTimer("morph_12", 6250L, weakScarlet, null, false);
			startQuestTimer("morph_13", 7200L, weakScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_12"))
		{
			weakScarlet.deleteMe();
			weakScarlet = null;
		}
		else if (event.equalsIgnoreCase("morph_13"))
		{
			strongScarlet = (L2GrandBossInstance) addSpawn(SCARLET2, reverseXFix(_Scarlet_x), reverseYFix(_Scarlet_y), reverseZFix(_Scarlet_z), _Scarlet_h, false, 0);
			strongScarlet.setIsInvul(true);
			strongScarlet.setIsImmobilized(true);
			strongScarlet.disableAllSkills();
		//	_Zone.updateKnownList(strongScarlet);
			activeScarlet = strongScarlet;
			
			_Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 450, _Angle, 12, 500, 14000));
			
			startQuestTimer("morph_14", 3000L, strongScarlet, null, false);

			// startQuestTimer("loc_check", 60000, strongScarlet, null, true);
		}
		else if (event.equalsIgnoreCase("morph_14"))
		{
			startQuestTimer("morph_15", 5100L, strongScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_15"))
		{
			_Zone.broadcastPacket(new SocialAction(strongScarlet, 2));

			final L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
			if (skill != null)
				skill.getEffects(strongScarlet, strongScarlet);
			
			startQuestTimer("morph_end", 9000L, strongScarlet, null, false);
			startQuestTimer("start_pc", 6000L, strongScarlet, null, false);
			startQuestTimer("start_npc", 6000L, strongScarlet, null, false);
			startQuestTimer("songs_play", 10000L + Rnd.get(10000), frintezza, null, false);
			startQuestTimer("skill03", 10000L + Rnd.get(10000), strongScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_16"))
		{
			_Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 300, _Angle - 180, 5, 0, 7000));
			startQuestTimer("morph_17", 0L, strongScarlet, null, false);

		}
		else if (event.equalsIgnoreCase("morph_17"))
		{
			_Zone.broadcastPacket(new SpecialCamera(strongScarlet.getObjectId(), 200, _Angle, 85, 4000, 10000));
			startQuestTimer("morph_17b", 7400L, frintezza, null, false);
			startQuestTimer("morph_18", 7500L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_17b"))
		{
			frintezza.doDie(frintezza);
		}
		else if (event.equalsIgnoreCase("morph_18"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 120, 5, 0, 7000));
			startQuestTimer("morph_19", 0L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_19"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 100, 90, 5, 5000, 15000));
			startQuestTimer("morph_20", 7000L, frintezza, null, false);
			startQuestTimer("spawn_cubes", 7000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("morph_20"))
		{
			_Zone.broadcastPacket(new SpecialCamera(frintezza.getObjectId(), 900, 90, 25, 7000, 10000));
			startQuestTimer("start_pc", 7000L, frintezza, null, false);

		}
		else if (event.equalsIgnoreCase("songs_play"))
		{
			if (frintezza != null && !frintezza.isDead() && _OnMorph == 0)
			{
				_OnSong = Rnd.get(1, 5);
				if (_OnSong == 3)
				{ // to fix skill exception
					_OnSong = 2;
				}
				
				String SongName = "";
				
				// Name of the songs are custom, named with client side description.
				switch (_OnSong)
				{
					case 1:
						SongName = "Frintezza's Healing Rhapsody";
						break;
					case 2:
						SongName = "Frintezza's Rampaging Opus";
						break;
					case 3:
						SongName = "Frintezza's Power Concerto";
						break;
					case 4:
						SongName = "Frintezza's Plagued Concerto";
						break;
					case 5:
						SongName = "Frintezza's Psycho Symphony";
						break;
					default:
						SongName = "Frintezza's Song";
						break;
				}
				
				// Like L2OFF the skill name is printed on screen
				_Zone.broadcastPacket(new ExShowScreenMessage(SongName, 6000));
				
				if (_OnSong == 1 && _ThirdMorph == 1 && strongScarlet.getCurrentHp() < strongScarlet.getMaxHp() * 0.6 && Rnd.get(100) < 80)
				{
					_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 1, 32000, 0));
					startQuestTimer("songs_effect", 5000L, frintezza, null, false);
					startQuestTimer("songs_play", 32000L + Rnd.get(10000), frintezza, null, false);

				}
				else if (_OnSong == 2 || _OnSong == 3)
				{
					_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, _OnSong, 32000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 32000 + Rnd.get(10000), frintezza, null, false);

				}
				else if (_OnSong == 4 && _SecondMorph == 1)
				{
					_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 4, 31000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 31000 + Rnd.get(10000), frintezza, null, false);

				}
				else if (_OnSong == 5 && _ThirdMorph == 1 && _Abnormal == 0)
				{
					_Abnormal = 1;
					_Zone.broadcastPacket(new MagicSkillUse(frintezza, frintezza, 5007, 5, 35000, 0));
					startQuestTimer("songs_effect", 5000, frintezza, null, false);
					startQuestTimer("songs_play", 35000 + Rnd.get(10000), frintezza, null, false);
					}
					else {
					    startQuestTimer("songs_play", 5000 + Rnd.get(5000), frintezza, null, false);
					}

			}
		}
		else if (event.equalsIgnoreCase("songs_effect"))
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(5008, _OnSong);
			if (skill == null)
				return null;
			
			String SongName = "";
			
			// Name of the songs are custom, named with client side description.
			switch (_OnSong)
			{
				case 1:
					SongName = "Frintezza's Concert Hall Melody";
					break;
				case 2:
					SongName = "Frintezza's Rampaging Opus en masse";
					break;
				case 3:
					SongName = "Frintezza Power Encore";
					break;
				case 4:
					SongName = "Mournful Chorale Prelude";
					break;
				case 5:
					SongName = "Hypnotic Mazurka ";
					break;
				default:
					SongName = "Frintezza's Song";
					break;
			}
			
			// Like L2OFF the skill name is printed on screen
			_Zone.broadcastPacket(new ExShowScreenMessage(SongName, 6000));
			
			if (_OnSong == 1 || _OnSong == 2 || _OnSong == 3)
			{
				/*
				 * TODO: Frintezza songs to be fixed like retail if (frintezza != null && !frintezza.isDead() && activeScarlet != null && !activeScarlet.isDead()) skill.getEffects(frintezza, activeScarlet, false, false, false);
				 */
			}
			else if (_OnSong == 4)
			{
				for (final Creature cha : _Zone.getCharactersInside())
				{
					if (cha instanceof Player && Rnd.get(100) < 80)
					{
						skill.getEffects(frintezza, cha);
						cha.sendPacket(new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 4));
					}
				}
			}
			else if (_OnSong == 5)
			{
				for (final Creature cha : _Zone.getCharactersInside())
				{
					if (cha instanceof Player && Rnd.get(100) < 70)
					{
						cha.abortAttack();
						cha.abortCast();
						cha.disableAllSkills();
						cha.stopMove(null);
						cha.setIsImmobilized(true);
						cha.setIsParalyzed(true);
						cha.getAI().setIntention(CtrlIntention.IDLE);
						skill.getEffects(frintezza, cha);
						cha.startAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
						cha.sendPacket(new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(5008, 5));
					}
				}
				startQuestTimer("stop_effect", 25000, frintezza, null, false);

			}
		}
		else if (event.equalsIgnoreCase("stop_effect"))
		{
			for (final Creature cha : _Zone.getCharactersInside())
			{
				if (cha instanceof Player)
				{
			        cha.stopAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
	                cha.stopAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
					cha.enableAllSkills();
					cha.setIsImmobilized(false);
					cha.setIsParalyzed(false);
				}
			}
			_Abnormal = 0;
		}
		else if (event.equalsIgnoreCase("attack_stop"))
		{
			cancelQuestTimer("skill01", npc, null);
			cancelQuestTimer("skill02", npc, null);
			cancelQuestTimer("skill03", npc, null);
			cancelQuestTimer("songs_play", npc, null);
			cancelQuestTimer("songs_effect", npc, null);
			
			if (frintezza != null)
				_Zone.broadcastPacket(new MagicSkillCanceld(frintezza.getObjectId()));
		}
		else if (event.equalsIgnoreCase("check_hp"))
		{
			if (npc.isDead())
			{
				_OnMorph = 1;
				_Zone.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
				
				startQuestTimer("attack_stop", 0, frintezza, null, false);
				startQuestTimer("stop_pc", 0, npc, null, false);
				startQuestTimer("stop_npc", 0, npc, null, false);
				startQuestTimer("morph_16", 0, npc, null, false);

			}
			else
			{
				_CheckDie = _CheckDie + 10;
				if (_CheckDie < 3000)
					startQuestTimer("check_hp", 10, npc, null, false);

				else
				{
					_OnCheck = 0;
					_CheckDie = 0;
				}
			}
		}
		else if (event.equalsIgnoreCase("skill01"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				final int i = Rnd.get(0, 1);
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					// weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill01", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);

			}
		}
		else if (event.equalsIgnoreCase("skill02"))
		{
			if (weakScarlet != null && !weakScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(2, 5);
				else
					i = Rnd.get(2, 4);
				
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					weakScarlet.stopMove(null);
					// weakScarlet.setIsCastingNow(true);
					weakScarlet.doCast(skill);
				}
				startQuestTimer("skill02", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);

				
				if (i == 5)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 4000, weakScarlet, null, false);

				}
			}
		}
		else if (event.equalsIgnoreCase("skill03"))
		{
			if (strongScarlet != null && !strongScarlet.isDead() && _SecondMorph == 1 && _ThirdMorph == 1 && _OnMorph == 0)
			{
				int i = 0;
				if (_Abnormal == 0)
					i = Rnd.get(6, 10);
				else
					i = Rnd.get(6, 9);
				
				final L2Skill skill = SkillTable.getInstance().getInfo(_skill[i][0], _skill[i][1]);
				if (skill != null)
				{
					strongScarlet.stopMove(null);
					// strongScarlet.setIsCastingNow(true);
					strongScarlet.doCast(skill);
				}
				startQuestTimer("skill03", _skill[i][2] + 5000 + Rnd.get(10000), npc, null, false);

				
				if (i == 10)
				{
					_Abnormal = 1;
					startQuestTimer("float_effect", 3000, npc, null, false);

				}
			}
		}
		else if (event.equalsIgnoreCase("float_effect"))
		{
			if (npc.isCastingNow())
			{
				startQuestTimer("float_effect", 500, npc, null, false);

			}
			else
			{
				for (final Creature cha : _Zone.getCharactersInside())
				{
					if (cha instanceof Player)
					{
						if (cha.getFirstEffect(5016) != null)
						{
							cha.abortAttack();
							cha.abortCast();
							cha.disableAllSkills();
							cha.stopMove(null);
							cha.setIsImmobilized(true);
							cha.setIsParalyzed(true);
							cha.getAI().setIntention(CtrlIntention.IDLE);
							 cha.startAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
						}
					}
				}
				startQuestTimer("stop_effect", 25000, npc, null, false);

			}
		}
		else if (event.equalsIgnoreCase("action"))
		{
			_Zone.broadcastPacket(new SocialAction(npc, 1));

		}
		else if (event.equalsIgnoreCase("bomber"))
		{
			_Bomber = 0;
		}
		else if (event.equalsIgnoreCase("room_final"))
		{
			_Zone.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Exceeded his time limit, challenge failed!"));
			_Zone.oustAllPlayers();
			
			cancelQuestTimer("waiting", npc, null);
			cancelQuestTimer("frintezza_despawn", null, null);
			startQuestTimer("clean", 1000, npc, null, false);
			startQuestTimer("close", 1000, npc, null, false);
			startQuestTimer("room1_del", 1000, npc, null, false);
			startQuestTimer("room2_del", 1000, npc, null, false);

			
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		else if (event.equalsIgnoreCase("frintezza_despawn"))
		{
			temp = (System.currentTimeMillis() - _LastAction);
			if (temp > 900000)
			{
				_Zone.oustAllPlayers();
				
				cancelQuestTimer("waiting", npc, null);
				// cancelQuestTimer("loc_check");
				cancelQuestTimer("room_final", null, null);
				cancelQuestTimer("spawn_minion", npc, null);
				startQuestTimer("clean", 1000, npc, null, false);
				startQuestTimer("close", 1000, npc, null, false);
				startQuestTimer("attack_stop", 1000, npc, null, false);
				startQuestTimer("room1_del", 1000, npc, null, false);
				startQuestTimer("room2_del", 1000, npc, null, false);
				startQuestTimer("room3_del", 1000, npc, null, false);
				startQuestTimer("minions_despawn", 1000, npc, null, false);

				
				GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
				
				cancelQuestTimer("frintezza_despawn", null, null);
			}
		}
		else if (event.equalsIgnoreCase("minions_despawn"))
		{
			synchronized (Minions)
			{
				for (int i = 0; i < Minions.size(); i++)
				{
					final Attackable mob = Minions.get(i);
					if (mob != null)
						mob.decayMe();
				}
				Minions.clear();
			}
			
		}
		else if (event.equalsIgnoreCase("spawn_minion"))
		{
			if (npc != null && !npc.isDead() && frintezza != null && !frintezza.isDead())
			{
				final L2Npc mob = addSpawn(npc.getNpcId() + 2, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
				// mob.setIsRaidMinion(true);
				synchronized (Minions)
				{
					Minions.add((Attackable) mob);
				}
				
				startQuestTimer("action", 200, mob, null, false);
				startQuestTimer("spawn_minion", 18000, npc, null, false);

			}
		}
		else if (event.equalsIgnoreCase("spawn_cubes"))
		{
			addSpawn(CUBE, 174232, -88020, -5114, 16384, false, 900000);
		}
		else if (event.equalsIgnoreCase("frintezza_unlock"))
		{
			GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
		}
		else if (event.equalsIgnoreCase("remove_players"))
		{
			_Zone.oustAllPlayers();
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	/*public String onTalk(final L2Npc npc, final L2PcInstance player)
	{
		if (npc.getNpcId() == CUBE)
		{
			final int x = 150037 + Rnd.get(500);
			final int y = -57720 + Rnd.get(500);
			player.teleToLocation(x, y, -2976, 0);
			return null;
		}
		
		String htmltext = "";
		
		final Integer status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (status == DEAD)
		{
			htmltext = "<html><body>There is nothing beyond the Magic Force Field. Come back later.<br>(You may not enter because Frintezza is not inside the Imperial Tomb.)</body></html>";
		}
		else if (status == DORMANT)
		{
			boolean party_check_success = true;
			
			if (!Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
			{
				
				if ((!player.isInParty() || !player.getParty().isLeader(player)) || (player.getParty().getCommandChannel() == null) || (player.getParty().getCommandChannel().getChannelLeader() != player))
				{
					htmltext = "<html><body>No reaction. Contact must be initiated by the Command Channel Leader.</body></html>";
					party_check_success = false;
				}
				else if (player.getParty().getCommandChannel().getPartys().size() < Config.FRINTEZZA_MIN_PARTIES || player.getParty().getCommandChannel().getPartys().size() > Config.FRINTEZZA_MAX_PARTIES)
				{
					htmltext = "<html><body>Your command channel needs to have at least " + Config.FRINTEZZA_MIN_PARTIES + " parties and a maximum of " + Config.FRINTEZZA_MAX_PARTIES + ".</body></html>";
					party_check_success = false;
				}
				
			}
			
			if (party_check_success)
			{
				
				if (player.getInventory().getItemByItemId(Config.QUEST_FRINTEZZA) == null)
				{
					htmltext = "<html><body>You dont have required item.</body></html>";
				}
				else
				{
					player.destroyItemByItemId("Quest", Config.QUEST_FRINTEZZA, 1, player, true);
					GrandBossManager.getInstance().setBossStatus(FRINTEZZA, WAITING);
					
					startQuestTimer("close", 0, npc, null, false);
					startQuestTimer("room1_spawn", 5000, npc, null, false);
					startQuestTimer("room_final", 2100000, null, null, false);
					startQuestTimer("frintezza_despawn", 60000, null, null, true);

					
					_LastAction = System.currentTimeMillis();
					
					if (Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
					{
						
						if (player.getParty() != null)
						{
							
							final L2CommandChannel CC = player.getParty().getCommandChannel();
							
							if (CC != null)
							{ // teleport all parties into CC
							
								for (final L2Party party : CC.getPartys())
								{
									if (party == null)
										continue;
									
									synchronized (_PlayersInside)
									{
										
										for (final L2PcInstance member : party.getPartyMembers())
										{
											if (member == null || member.getLevel() < 74)
												continue;
											if (!member.isInsideRadius(npc, 700, false, false))
												continue;
											if (_PlayersInside.size() > 45)
											{
												member.sendMessage("The number of challenges have been full, so can not enter.");
												break;
											}
											_PlayersInside.add(member);
											_Zone.allowPlayerEntry(member, 300);
											member.teleToLocation(getXFix(_invadeLoc[_LocCycle][0]) + Rnd.get(50), getYFix(_invadeLoc[_LocCycle][1]) + Rnd.get(50), getZFix(_invadeLoc[_LocCycle][2]), 0);
										}
										if (_PlayersInside.size() > 45)
											break;
										
									}
									
									_LocCycle++;
									if (_LocCycle >= 6)
										_LocCycle = 1;
								}
								
							}
							else
							{ // teleport just actual party
							
								final L2Party party = player.getParty();
								
								for (final L2PcInstance member : party.getPartyMembers())
								{
									if (member == null || member.getLevel() < 74)
										continue;
									if (!member.isInsideRadius(npc, 700, false, false))
										continue;
									
									synchronized (_PlayersInside)
									{
										if (_PlayersInside.size() > 45)
										{
											member.sendMessage("The number of challenges have been full, so can not enter.");
											break;
										}
										_PlayersInside.add(member);
									}
									
									_Zone.allowPlayerEntry(member, 300);
									member.teleToLocation(getXFix(_invadeLoc[_LocCycle][0]) + Rnd.get(50), getYFix(_invadeLoc[_LocCycle][1]) + Rnd.get(50), getZFix(_invadeLoc[_LocCycle][2]), 0);
								}
								
								_LocCycle++;
								if (_LocCycle >= 6)
									_LocCycle = 1;
								
							}
							
						}
						else
						{ // teleport just player
						
							if (player.isInsideRadius(npc, 700, false, false))
							{
								
								synchronized (_PlayersInside)
								{
									_PlayersInside.add(player);
									
								}
								_Zone.allowPlayerEntry(player, 300);
								player.teleToLocation(getXFix(_invadeLoc[_LocCycle][0]) + Rnd.get(50), getYFix(_invadeLoc[_LocCycle][1]) + Rnd.get(50), getZFix(_invadeLoc[_LocCycle][2]), 0);
								
							}
							
						}
						
					}
					else
					{
						
						final L2CommandChannel CC = player.getParty().getCommandChannel();
						
						for (final L2Party party : CC.getPartys())
						{
							if (party == null)
								continue;
							
							synchronized (_PlayersInside)
							{
								for (final L2PcInstance member : party.getPartyMembers())
								{
									if (member == null || member.getLevel() < 74)
										continue;
									if (!member.isInsideRadius(npc, 700, false, false))
										continue;
									if (_PlayersInside.size() > 45)
									{
										member.sendMessage("The number of challenges have been full, so can not enter.");
										break;
									}
									_PlayersInside.add(member);
									_Zone.allowPlayerEntry(member, 300);
									member.teleToLocation(getXFix(_invadeLoc[_LocCycle][0]) + Rnd.get(50), getYFix(_invadeLoc[_LocCycle][1]) + Rnd.get(50), getZFix(_invadeLoc[_LocCycle][2]), 0);
								}
								if (_PlayersInside.size() > 45)
									break;
								
							}
							
							_LocCycle++;
							if (_LocCycle >= 6)
								_LocCycle = 1;
						}
						
					}
					
				}
				
			}
			
		}
		else
			htmltext = "<html><body>Someone else is already inside the Magic Force Field. Try again lL2Npc/html>";
		
		return htmltext;
	}*/
	public String onTalk(L2Npc npc, Player player)
	{
	    if (npc.getNpcId() == CUBE)
	    {
	        int x = 150037 + Rnd.get(500);
	        int y = -57720 + Rnd.get(500);
	        player.teleToLocation(x, y, -2976, 0);
	        return null;
	    }
	    
	    String htmltext = "";
	    
	    // Verifica o status do Frintezza
	    if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DEAD)
	    {
	        htmltext = "<html><body>There is nothing beyond the Magic Force Field. Come back later.<br>(You may not enter because Frintezza is not inside the Imperial Tomb.)</body></html>";
	    }
	    else if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DORMANT || GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == WAITING)
	    {
	        boolean party_check_success = true;
	        
	        if (Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
	        {
	            if ((!player.isInParty() || !player.getParty().isLeader(player))
	                || (player.getParty().getCommandChannel() == null)
	                || (player.getParty().getCommandChannel().getChannelLeader() != player))
	            {
	                htmltext = "<html><body>No reaction. Contact must be initiated by the Command Channel Leader.</body></html>";
	                party_check_success = false;
	            }
	            else if (player.getParty().getCommandChannel().getPartys().size() < Config.FRINTEZZA_MIN_PARTIES || player.getParty().getCommandChannel().getPartys().size() > Config.FRINTEZZA_MAX_PARTIES)
	            {
	                htmltext = "<html><body>Your command channel needs to have at least "+Config.FRINTEZZA_MIN_PARTIES+" parties and a maximum of "+Config.FRINTEZZA_MAX_PARTIES+".</body></html>";
	                party_check_success = false;
	            }
	        }

	        if (party_check_success)
	        {
	            if (player.getInventory().getItemByItemId(Config.QUEST_FRINTEZZA) == null)
	            {
	                htmltext = "<html><body>You don't have the required item.</body></html>";
	            }
	            else
	            {
	                player.destroyItemByItemId("Quest", Config.QUEST_FRINTEZZA, 1, player, true);
	                
	                if (GrandBossManager.getInstance().getBossStatus(FRINTEZZA) == DORMANT)
	                {
	                    startQuestTimer("close", 0, npc, null, false);
	                    startQuestTimer("room1_spawn", 5000, npc, null, false);
	                    startQuestTimer("room_final", 2100000, null, null, false);
	                    startQuestTimer("frintezza_despawn", 60000, null, null, true);
	                    
	                    GrandBossManager.getInstance().setBossStatus(FRINTEZZA, WAITING);
	                }
	                
	                _LastAction = System.currentTimeMillis();
	                
	                // Verifica se o bypass está ativado
	                if (!Config.BYPASS_FRINTEZZA_PARTIES_CHECK)
	                {
	                    if (player.getParty() != null)
	                    {
	                        L2CommandChannel CC = player.getParty().getCommandChannel();
	                        
	                        if (CC != null) // Teleporta todas as parties do Command Channel
	                        {
	                            for (L2Party party : CC.getPartys())
	                            {
	                                if (party == null)
	                                    continue;
	                                
	                                synchronized (_PlayersInside)
	                                {
	                                    for (Player member : party.getPartyMembers())
	                                    {
	                                        if (member == null || member.getLevel() < 74)
	                                            continue;
	                                        if (!member.isInsideRadius(npc, 700, false, false))
	                                            continue;
	                                        if (_PlayersInside.size() > 45)
	                                        {
	                                            member.sendMessage("The number of challenges has been filled, so you cannot enter.");
	                                            break;
	                                        }
	                                        _PlayersInside.add(member);
	                                        _Zone.allowPlayerEntry(member, 300);
	                                        member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2], 0);
	                                    }
	                                    if (_PlayersInside.size() > 45)
	                                        break;
	                                }
	                                
	                                _LocCycle++;
	                                if (_LocCycle >= 6)
	                                    _LocCycle = 1;
	                            }
	                        }
	                        else // Teleporta apenas a party atual
	                        {
	                            L2Party party = player.getParty();
	                            
	                            for (Player member : party.getPartyMembers())
	                            {
	                                if (member == null || member.getLevel() < 74)
	                                    continue;
	                                if (!member.isInsideRadius(npc, 700, false, false))
	                                    continue;
	                                
	                                synchronized (_PlayersInside)
	                                {
	                                    if (_PlayersInside.size() > 45)
	                                    {
	                                        member.sendMessage("The number of challenges has been filled, so you cannot enter.");
	                                        break;
	                                    }
	                                    _PlayersInside.add(member);
	                                }
	                                
	                                _Zone.allowPlayerEntry(member, 300);
	                                member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2], 0);
	                            }
	                            
	                            _LocCycle++;
	                            if (_LocCycle >= 6)
	                                _LocCycle = 1;
	                        }
	                    }
	                    else // Teleporta apenas o jogador
	                    {
	                        if (player.isInsideRadius(npc, 700, false, false))
	                        {
	                            synchronized (_PlayersInside)
	                            {
	                                _PlayersInside.add(player);
	                            }
	                            _Zone.allowPlayerEntry(player, 300);
	                            player.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2], 0);
	                        }
	                    }
	                }
	                else
	                {
	                    L2CommandChannel CC = player.getParty().getCommandChannel();
	                    
	                    for (L2Party party : CC.getPartys())
	                    {
	                        if (party == null)
	                            continue;
	                        
	                        synchronized (_PlayersInside)
	                        {
	                            for (Player member : party.getPartyMembers())
	                            {
	                                if (member == null || member.getLevel() < 74)
	                                    continue;
	                                if (!member.isInsideRadius(npc, 700, false, false))
	                                    continue;
	                                if (_PlayersInside.size() > 45)
	                                {
	                                    member.sendMessage("The number of challenges has been filled, so you cannot enter.");
	                                    break;
	                                }
	                                _PlayersInside.add(member);
	                                _Zone.allowPlayerEntry(member, 300);
	                                member.teleToLocation(_invadeLoc[_LocCycle][0] + Rnd.get(50), _invadeLoc[_LocCycle][1] + Rnd.get(50), _invadeLoc[_LocCycle][2], 0);
	                            }
	                            if (_PlayersInside.size() > 45)
	                                break;
	                        }
	                        
	                        _LocCycle++;
	                        if (_LocCycle >= 6)
	                            _LocCycle = 1;
	                    }
	                }
	            }
	        }
	    }
	    else
	    {
	        htmltext = "<html><body>Someone else is already inside the Magic Force Field. Try again later.</body></html>";
	    }
	    
	    return htmltext;
	}

	
	@Override
	 public String onAttack(L2Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		_LastAction = System.currentTimeMillis();
		if (npc.getNpcId() == FRINTEZZA)
		{
			npc.setCurrentHpMp(npc.getMaxHp(), 0);
			return null;
		}
		
		final Integer status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
		
		if (npc.getNpcId() == SCARLET1 && _SecondMorph == 0 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.75 && status == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);

			
			_SecondMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 1000, npc, null, false);
			startQuestTimer("stop_npc", 1000, npc, null, false);
			startQuestTimer("morph_01", 1100, npc, null, false);

		}
		else if (npc.getNpcId() == SCARLET1 && _SecondMorph == 1 && _ThirdMorph == 0 && _OnMorph == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.5 && status == FIGHTING)
		{
			startQuestTimer("attack_stop", 0, frintezza, null, false);

			
			_ThirdMorph = 1;
			_OnMorph = 1;
			
			startQuestTimer("stop_pc", 2000, npc, null, false);
			startQuestTimer("stop_npc", 2000, npc, null, false);
			startQuestTimer("morph_05a", 2000, npc, null, false);
			startQuestTimer("morph_05", 2100, npc, null, false);

		}
		else if (npc.getNpcId() == SCARLET2 && _SecondMorph == 1 && _ThirdMorph == 1 && _OnCheck == 0 && damage >= npc.getCurrentHp() && status == FIGHTING)
		{
			_OnCheck = 1;
			startQuestTimer("check_hp", 0, npc, null, false);

		}
		else if ((npc.getNpcId() == 29050 || npc.getNpcId() == 29051) && _Bomber == 0)
		{
		    if (npc.getCurrentHp() < npc.getMaxHp() * 0.1)
		    {
		        if (Rnd.get(100) < 30)
		        {
		            _Bomber = 1;
		            startQuestTimer("bomber", 3000, npc, null, false);

		            L2Skill bomberSkill = SkillTable.getInstance().getInfo(5011, 1); // Renamed to bomberSkill to avoid duplicate
		            if (bomberSkill != null)
		            {
		                // npc.setIsCastingNow(true);
		                npc.doCast(bomberSkill);
		            }
		        }
		    }
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isPet)
	{
	    if (npc.getNpcId() == SCARLET2)
	    {
	        // Emite um som quando o alarme é destruído
	        _Zone.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

	        // Dispara timers para eventos subsequentes
	        startQuestTimer("stop_pc", 0, null, null, false);
	        startQuestTimer("stop_npc", 0, npc, null, false);
	        startQuestTimer("morph_16", 0, npc, null, false);

	        // Marca o boss como morto
	        GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DEAD);

	        // 🆕 Respawn fixo via TimeEpicBossManager
	        long respawnTime = TimeEpicBossManager.getInstance().getMillisUntilNextRespawn(FRINTEZZA);

	        if (respawnTime <= 0)
	        {
	            // fallback para o cálculo antigo
	            respawnTime = (long) Config.SPAWN_INTERVAL_FRINTEZZA + Rnd.get(-Config.RANDOM_SPAWN_TIME_FRINTEZZA, Config.RANDOM_SPAWN_TIME_FRINTEZZA);
	            respawnTime *= 3600000; // converte horas para ms
	            _log.warning("TimeEpicBoss: No respawn configured for Frintezza (" + FRINTEZZA + "), using fallback.");
	        }

	        // Cancela e dispara timers necessários
	        cancelQuestTimers("spawn_minion");
	        cancelQuestTimers("frintezza_despawn");
	        startQuestTimer("close", 0, null, null, false);
	        startQuestTimer("rooms_del", 0, npc, null, false);
	        startQuestTimer("minions_despawn", 0, null, null, false);
	        startQuestTimer("remove_players", 900000, null, null, false);
	        startQuestTimer("frintezza_unlock", respawnTime, null, null, false);

	        // Atualiza respawn no GrandBossManager para persistência
	        StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
	        info.set("respawn_time", System.currentTimeMillis() + respawnTime);
	        GrandBossManager.getInstance().setStatsSet(FRINTEZZA, info);
	    }
	    else if (npc.getNpcId() == 18328)
	    {
	        _KillHallAlarmDevice++;
	        if (_KillHallAlarmDevice == 3) // Abre as paredes após 3 alarmes mortos
	        {
	            for (int i = 25150051; i <= 25150058; i++)
	                DoorTable.getInstance().getDoor(i).openMe();
	        }
	        else if (_KillHallAlarmDevice == 4)
	        {
	            startQuestTimer("room1_del", 100, npc, null, false); // Deleta a sala 1
	            startQuestTimer("room2_spawn", 100, npc, null, false); // Spawn dos monstros da sala 2

	            // Abre as portas para a sala 2
	            DoorTable.getInstance().getDoor(25150042).openMe();
	            DoorTable.getInstance().getDoor(25150043).openMe();
	        }
	    }
	    else if (npc.getNpcId() == 18339)
	    {
	        _KillDarkChoirPlayer++;
	        if (_KillDarkChoirPlayer == 2)
	        {
	            DoorTable.getInstance().getDoor(25150042).closeMe();
	            DoorTable.getInstance().getDoor(25150043).closeMe();

	            // Abre as portas para a próxima sala
	            for (int i = 25150061; i <= 25150070; i++)
	                DoorTable.getInstance().getDoor(i).openMe();

	            startQuestTimer("room2_spawn2", 1000, npc, null, false); // Dispara o spawn de monstros
	        }
	    }
	    else if (npc.getNpcId() == 18334)
	    {
	        _KillDarkChoirCaptain++;
	        if (_KillDarkChoirCaptain == 8)
	        {
	            startQuestTimer("room2_del", 100, npc, null, false);

	            // Abre portas da sala 2
	            DoorTable.getInstance().getDoor(25150045).openMe();
	            DoorTable.getInstance().getDoor(25150046).openMe();
	            startQuestTimer("waiting", Config.WAIT_TIME_FRINTEZZA, npc, null, false);
	            cancelQuestTimers("room_final");
	        }
	    }

	    return super.onKill(npc, killer, isPet);
	}

	
	/**
	 * Override spawn method to correct spawn cords after lair move<BR>
	 * TODO: Update cords in script, im just lazy :)
	 * @param npcId 
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param heading 
	 * @param randomOffset 
	 * @param despawnDelay 
	 * @return 
	 */
	//@Override
	//public L2Npc addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffset, final int despawnDelay)
	//{
	//	// cords fix:
	//	return super.addSpawn(npcId, getXFix(x), getYFix(y), getZFix(z), heading, randomOffset, despawnDelay);
	//}

	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay)
	{
	    // Ajuste as coordenadas conforme necessário antes de chamar o método da classe pai
	    return super.addSpawn(npcId, getXFix(x), getYFix(y), getZFix(z), heading, randomOffset, despawnDelay, false);  // 'false' para isSummonSpawn
	}


	public int getXFix(final int x)
	{
		return x/* - 262016 */;
	}
	
	public int getYFix(final int y)
	{
		return y/* - 65278 */;
	}
	
	public int getZFix(final int z)
	{
		return z/* - 4065 */;
	}
	
	public int reverseXFix(final int x)
	{
		return x/* + 262016 */;
	}
	
	public int reverseYFix(final int y)
	{
		return y/* + 65278 */;
	}
	
	public int reverseZFix(final int z)
	{
		return z/* + 4065 */;
	}
}