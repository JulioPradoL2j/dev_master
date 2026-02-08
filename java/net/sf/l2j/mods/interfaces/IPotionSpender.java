package net.sf.l2j.mods.interfaces;

import net.sf.l2j.mods.actor.FakePlayer;

public interface IPotionSpender
{
	default void handlePotions(FakePlayer fakePlayer)
	{
		fakePlayer.tryUsePotion();
	}
}