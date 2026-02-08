package net.sf.l2j.mods.skills;

import net.sf.l2j.gameserver.model.L2Skill;

public final class SkillAction
{
    private final L2Skill skill;

    public SkillAction(L2Skill skill)
    {
        this.skill = skill;
    }

    public L2Skill getSkill()
    {
        return skill;
    }
}
