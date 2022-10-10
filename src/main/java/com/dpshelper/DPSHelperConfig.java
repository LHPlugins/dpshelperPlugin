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
			name = "Enable Total Tick Lost",
			description = "This tells you how many ticks you have lost in the amount of attacks, you enter",
			section = generalSettings,
			position = 0
	)
	default boolean getSummary(){return false;}

	@ConfigItem(
			keyName = "maxIdleTick",
			name = "Max idle ticks",
			description = "Enter how many ticks before it will reset itself, to prevent false ticks",
			section = generalSettings,
			position = 4
	)
	default int getMaxIdle(){return 30;}

	@ConfigItem(
		keyName = "attackToReset",
		name = "Attacks to reset",
		description = "Amount of attacks before it sends the total lost ticks in chat",
			section = generalSettings,
			position = 1
	)
	default int getAttackReset(){return 4;}

	@ConfigItem(
			keyName = "eachAttack",
			name = "Enable ticks with each attack",
			description = "Enables ticks each attack, that you do and output if you are on tick or not",
			section = generalSettings,
			position = 2
	)
	default boolean getEachAttack(){return false;}

	@ConfigItem(
			keyName = "enableUI",
			name = "Enable UI",
			description = "This enables UI for each attack, instead of chat messages",
			section = generalSettings,
			position = 3
	)
	default boolean getUI(){return false;}

	@ConfigItem(
			keyName = "skip1",
			name = "Skip AnimationID 1",
			description = "You enter an animation id here to skip it when attacking",
			section = extraSettings
	)
	default int getSkip1(){return 0;}

	@ConfigItem(
			keyName = "skip2",
			name = "Skip AnimationID 2",
			description = "You enter an animation id here to skip it when attacking",
			section = extraSettings
	)
	default int getSkip2(){return 0;}

}

