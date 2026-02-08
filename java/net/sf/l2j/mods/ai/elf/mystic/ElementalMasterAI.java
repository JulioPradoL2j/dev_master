package net.sf.l2j.mods.ai.elf.mystic;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.ai.combat.CombatBehaviorAI;
import net.sf.l2j.mods.enums.CombatKit;
import net.sf.l2j.mods.skills.SkillAction;
import net.sf.l2j.mods.skills.SkillCombo;

 

public class ElementalMasterAI extends CombatBehaviorAI
{
	
	
	private static final int SUMMON_SKILL_ID = 1332;
	private static final long SUMMON_CHECK_DELAY = 15000L;
	private static final String VAR_LAST_SUMMON_CHECK = "fake_last_summon_check";
	private int ARCANE_POWER = 337;
	
	public ElementalMasterAI(FakePlayer character)
	{
		super(character);
		LoadSkills();
	}
	
	@Override
	public void onAiTick()
	{
		super.onAiTick();
		
		if (handlePickUp(_fakePlayer))
			return;
		
		handleConsumable(_fakePlayer, crystalC);
		
		handlePetToggleBuff(ARCANE_POWER, false);
		handleEquipes(_fakePlayer);
		
		handleLevel(_fakePlayer);
		handlePotions(_fakePlayer);
		handleShots(_fakePlayer);
		handleBuffers(_fakePlayer);
		HandlerSummon();
		
	}
	
	@Override
	public SkillCombo getCombatCombo()
	{
		return combo;
	}
	
	@Override
	public ShotType getShotType()
	{
		return ShotType.BLESSED_SPIRITSHOT;
	}
	
	@Override
	public boolean isMage()
	{
		return true;
	}
	
	@Override
	public CombatKit getCombatKit()
	{
		return CombatKit.MAGE_NUKE;
	}
	
	public void HandlerSummon()
	{
		if (_fakePlayer.isPrivateBuying() || _fakePlayer.isPrivateSelling() || _fakePlayer.isPrivateManufactureing())
			return;
		
		if (_fakePlayer.getPet() != null && !_fakePlayer.getPet().isDead())
		{
			
			handleTargetSelection(_fakePlayer);
			if (_target != null)
				handleAttackTarget(_fakePlayer, _target);
			return;
		}
		
		long now = System.currentTimeMillis();
		long lastCheck = _fakePlayer.getMemos().getLong(VAR_LAST_SUMMON_CHECK, 0L);
		

		if ((now - lastCheck) < SUMMON_CHECK_DELAY)
			return;
		
		// Atualiza controle
		_fakePlayer.getMemos().set(VAR_LAST_SUMMON_CHECK, now);
		_fakePlayer.getMemos().hasChanges();
		

		if (_fakePlayer.isCastingNow())
			return;
		
		L2Skill summonSkill = SkillTable.getInstance().getInfo(SUMMON_SKILL_ID, _fakePlayer.getSkillLevel(SUMMON_SKILL_ID));
		
		if (summonSkill == null)
			return;

		_fakePlayer.useMagic(summonSkill, false, false);
	}
	
	protected void LoadSkills()
	{
		combo = new SkillCombo(new SkillAction(getBestSkill(1235)));
	}
}
