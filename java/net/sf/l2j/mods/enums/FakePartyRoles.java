package net.sf.l2j.mods.enums;

import java.util.List;

import net.sf.l2j.gameserver.model.base.ClassId;

public final class FakePartyRoles
{
	public static final List<ClassId> HEALERS = List.of(ClassId.CARDINAL);
	
	public static final List<ClassId> MELEE = List.of(ClassId.DUELIST, ClassId.TITAN, ClassId.GRAND_KHAVATARI, ClassId.ADVENTURER, ClassId.GHOST_HUNTER, ClassId.PHOENIX_KNIGHT);
	
	public static final List<ClassId> RANGED = List.of(ClassId.SAGGITARIUS, ClassId.GHOST_SENTINEL, ClassId.MOONLIGHT_SENTINEL);
	
	public static final List<ClassId> MAGES = List.of(ClassId.ARCHMAGE, ClassId.MYSTIC_MUSE, ClassId.STORM_SCREAMER, ClassId.SOULTAKER, ClassId.ELEMENTAL_MASTER);
	
	public static final List<ClassId> SUPPORT = List.of(ClassId.DOMINATOR, ClassId.DOOMCRYER);
}
