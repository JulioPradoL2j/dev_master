package net.sf.l2j.mods.ai;

import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.l2j.mods.actor.FakePlayer;

 
public abstract class AbstractFakePlayerAI
{
	protected final FakePlayer _fakePlayer;
	
	protected final int boneId = 2508;
	protected final int energyStone = 5589;
	protected int _spiritOre = 3031;
	protected int _einhassad = 8874;
	protected final int crystalC = 1459;
	protected final int crystalA = 1461;
	
	private final AtomicBoolean thinking = new AtomicBoolean(false);
	
	public AbstractFakePlayerAI(FakePlayer character)
	{
		_fakePlayer = character;
		setup();
	}
	
	protected void setup()
	{
		_fakePlayer.setIsRunning(true);
	}
	
	public boolean canThink()
	{
		return _fakePlayer != null && _fakePlayer.isOnline();
	}
	
	public boolean tryThink()
	{
		return thinking.compareAndSet(false, true);
	}
	
	public void doneThinking()
	{
		thinking.set(false);
	}
	
	public int getPriority()
	{
		return _fakePlayer.isInCombat() ? 10 : 1;
	}
	
	public FakePlayer getFakePlayer()
	{
		return _fakePlayer;
	}
	
	/* ---------------- AI LOGIC ---------------- */
	
	public abstract void onAiTick();
}
