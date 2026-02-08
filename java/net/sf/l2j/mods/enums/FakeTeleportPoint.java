package net.sf.l2j.mods.enums;

import java.util.Arrays;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.Location;

public enum FakeTeleportPoint
{
	// =========================
	// CIDADES
	// =========================
	GLUDIN("Gludin", -81057, 150294, -3046, Type.CITY),
	GLUDIO("Gludio", -12776, 122664, -3112, Type.CITY),
	DION("Dion", 18760, 145192, -3128, Type.CITY),
	HEINE("Heine", 111336, 219032, -3536, Type.CITY),
	GIRAN("Giran", 83374, 148081, -3407, Type.CITY),
	ADEN("Aden", 146904, 25784, -2008, Type.CITY),
	OREN("Oren", 82808, 53400, -1488, Type.CITY),
	GODDARD("Goddard", 147864, -55432, -2728, Type.CITY),
	SCHUTTGART("Schuttgart", 87192, -143304, -1288, Type.CITY),
	RUNE("Rune", 43849, -47837, -799, Type.CITY),
	TALKING_ISLAND("Talking Island", -84254, 244585, -3731, Type.CITY),
	ELVEN_VILLAGE("Elven Village", 46919, 51207, -2998, Type.CITY),
	DARK_ELVEN_VILLAGE("Dark Elven Village", 9794, 15599, -4576, Type.CITY),
	DWARVEN_VILLAGE("Dwarven Village", 115320, -178241, -930, Type.CITY),
	ORC_VILLAGE("Orc Village", -45057, -112615, -241, Type.CITY),
	HUNTERS_VILLAGE("Hunters Village", 116993, 76894, -2707, Type.CITY),
	FLORAN("Floran Village", 17278, 170039, -3502, Type.CITY),
	
	// =========================
	// ARENAS
	// =========================
	GIRAN_ARENA("Giran Arena", 73625, 142632, -3774, Type.ARENA),
	COLISEUM("Coliseum", 147862, 46717, -3410, Type.ARENA),
	GLUDIN_ARENA("Gludin Arena", -87279, 142358, -3648, Type.ARENA),
	
	// =========================
	// HIGH LEVEL / FARM
	// =========================
	BARAKIEL_I("Barakiel I", 89288, -85744, -2760, Type.FIELD),
	BARAKIEL_II("Barakiel II", 85690, -85408, -3560, Type.FIELD),
	
	HEKATON_I("Hekaton I", 147624, -77128, -4928, Type.FIELD),
	HEKATON_II("Hekaton II", 150824, -77912, -4960, Type.FIELD),
	
	SHADIT_I("Shadit I", 114056, -43112, -2709, Type.FIELD),
	SHADIT_II("Shadit II", 114776, -45624, -2616, Type.FIELD),
	
	TAYR_I("Tayr I", 149016, -82744, -5600, Type.FIELD),
	TAYR_II("Tayr II", 149864, -80888, -5624, Type.FIELD),
	
	MOS("Mos", 109960, -38488, -1482, Type.FIELD),
	
	BRAKKI_I("Brakki I", 142056, -82792, -6480, Type.FIELD),
	BRAKKI_II("Brakki II", 142680, -82104, -6480, Type.FIELD),
	
	HORUS_I("Horus I", 102488, -38488, -1502, Type.FIELD),
	HORUS_II("Horus II", 109928, -41464, -2320, Type.FIELD),
 
	
	VARKA_SILENOS_I("Varka Silenos I", 125976, -40760, -3752, Type.FIELD),
	VARKA_SILENOS_II("Varka Silenos II", 122888, -45736, -3032, Type.FIELD),
	VARKA_SILENOS_III("Varka Silenos III", 119908, -46590, -2806, Type.FIELD),
	
	KETRA_SPORT_I("Ketra Sport I", 149616, -112428, -2060, Type.FIELD),
	KETRA_SPORT_II("Ketra Sport II", 145487, -109049, -3427, Type.FIELD),
	KETRA_SPORT_III("Ketra Sport III", 149258, -116234, -1338, Type.FIELD),
	
	HOT_SPRINGS_I("Hot Springs I", 149616, -112428, -2060, Type.FIELD),
	HOT_SPRINGS_II("Hot Springs II", 145487, -109049, -3427, Type.FIELD),
	HOT_SPRINGS_III("Hot Springs III", 149258, -116234, -1338, Type.FIELD),
	
	MOMASTERY_I("Monastery I", 108081, -87773, -2907, Type.FIELD),
	MOMASTERY_II("Monastery II", 118775, -80555, -2693, Type.FIELD),
	MOMASTERY_III("Monastery III", 123743, -75032, -2897, Type.FIELD);
	
	// =========================
	// INNER
	// =========================
	public enum Type
	{
		CITY,
		ARENA,
		FIELD
	}
	
	private final String displayName;
	private final Location location;
	private final Type type;
	
	FakeTeleportPoint(String displayName, int x, int y, int z, Type type)
	{
		this.displayName = displayName;
		this.location = new Location(x, y, z);
		this.type = type;
	}
	
	public String getName()
	{
		return displayName;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public Type getType()
	{
		return type;
	}
	
	// =========================
	// UTILS
	// =========================
	public static FakeTeleportPoint getRandomCity()
	{
		return getRandomByType(Type.CITY);
	}
	
	public static FakeTeleportPoint getRandomArena()
	{
		return getRandomByType(Type.ARENA);
	}
	
	public static FakeTeleportPoint getRandomField()
	{
		return getRandomByType(Type.FIELD);
	}
	
	public static FakeTeleportPoint[] getCities()
	{
		return Arrays.stream(values()).filter(p -> p.type == Type.CITY).toArray(FakeTeleportPoint[]::new);
	}
	
	public static FakeTeleportPoint[] getField()
	{
		return Arrays.stream(values()).filter(p -> p.type == Type.FIELD).toArray(FakeTeleportPoint[]::new);
	}
	
	private static FakeTeleportPoint getRandomByType(Type type)
	{
		FakeTeleportPoint[] filtered = Arrays.stream(values()).filter(p -> p.type == type).toArray(FakeTeleportPoint[]::new);
		
		return filtered[Rnd.get(filtered.length)];
	}
}
