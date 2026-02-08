package net.sf.l2j.mods.skills;

public final class SkillCombo
{
	private final SkillAction[] steps;
	private int index = 0;
	
	public static final SkillCombo EMPTY = new SkillCombo();
	
	private SkillCombo()
	{
		this.steps = new SkillAction[0];
	}
	
	public SkillCombo(SkillAction... steps)
	{
		this.steps = steps;
	}
	
	public boolean isEmpty()
	{
		return steps.length == 0;
	}
	
	public SkillAction current()
	{
		if (isEmpty())
			return null;
		return steps[index];
	}
	
	public void advance()
	{
		if (isEmpty())
			return;
		
		index++;
		if (index >= steps.length)
			index = 0;
	}
	
	public void reset()
	{
		index = 0;
	}
}
