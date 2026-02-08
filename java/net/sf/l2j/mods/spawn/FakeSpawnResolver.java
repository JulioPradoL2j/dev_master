package net.sf.l2j.mods.spawn;

import net.sf.l2j.mods.enums.FakeSpawnCity;

public final class FakeSpawnResolver
{
	public static FakeSpawnCity resolve(int classId)
	{
		// ==========================
		// CLASSES INICIAIS
		// ==========================
		switch (classId)
		{
			// HUMAN
			case 0:
			case 10:
				return FakeSpawnCity.TALKING_ISLAND;
			
			// ELF
			case 18:
			case 25:
				return FakeSpawnCity.ELVEN_VILLAGE;
			
			// DARK ELF
			case 31:
			case 38:
				return FakeSpawnCity.DARK_ELVEN_VILLAGE;
			
			// ORC
			case 44:
			case 49:
				return FakeSpawnCity.ORC_VILLAGE;
			
			// DWARF
			case 53:
				return FakeSpawnCity.DWARVEN_VILLAGE;
				
		}
		
		// Fallback seguro
		return FakeSpawnCity.TALKING_ISLAND;
	}
	
}
