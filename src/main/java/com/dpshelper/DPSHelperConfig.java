package com.dpshelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("dpshelper")
public interface DPSHelperConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "menu of the general features",
			position = 0
	)
	String generalSettings = "generalSettings";

	@ConfigSection(
			name = "Extra",
			description = "Menu of extra features",
			position = 1
	)
	String extraSettings = "extraSettings";

	@ConfigItem(
			keyName = "enableSummary",
			name = "Enable AVG Attacks",
			description = "This enables you to pick a number of attacks before it gives you a avg ticks pr attack",
			section = generalSettings
	)
	default boolean getSummary(){return false;}

	@ConfigItem(
		keyName = "attackToReset",
		name = "Attacks to reset",
		description = "How many attacks before it reset and send summary",
			section = generalSettings
	)
	default int getAttackReset(){return 4;}

	@ConfigItem(
			keyName = "eachAttack",
			name = "Post ticks with each attack",
			description = "This posts information about ticks between each attack",
			section = generalSettings
	)
	default boolean getEachAttack(){return false;}

	@ConfigItem(
			keyName = "enableUI",
			name = "Enable UI",
			description = "This enables UI instead of chat messages",
			section = generalSettings
	)
	default boolean getUI(){return false;}

	@ConfigItem(
			keyName = "skip1",
			name = "Skip AnimationID 1",
			description = "You enter an animationid here to skip it when attacking",
			section = extraSettings
	)
	default int getSkip1(){return 0;}

	@ConfigItem(
			keyName = "skip2",
			name = "Skip AnimationID 2",
			description = "You enter an animationid here to skip it when attacking",
			section = extraSettings
	)
	default int getSkip2(){return 0;}

}

