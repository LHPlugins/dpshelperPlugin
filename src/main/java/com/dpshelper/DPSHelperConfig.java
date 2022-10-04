package com.dpshelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("dpshelper")
public interface DPSHelperConfig extends Config
{
	@ConfigItem(
		keyName = "attackToReset",
		name = "Attacks to reset",
		description = "How many attacks before it reset and send summary"
	)
	default int getAttackReset(){return 4;}

	@ConfigItem(
			keyName = "eachAttack",
			name = "Post ticks with each attack",
			description = "This posts information about ticks between each attack"
	)
	default boolean getEachAttack(){return false;}
}
