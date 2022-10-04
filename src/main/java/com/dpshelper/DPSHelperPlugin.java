package com.dpshelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "DPS Helper",
		description = "This plugin you to lose less ticks when attacking npcs, by telling you the ticks between attacks, so you can optimize your gameplay"
)
public class DPSHelperPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private DPSHelperConfig config;

	private int attackToReset;
	private int attacks;
	private int gameTicks;
	private int lastAttackInTicks;
	private List<Integer> avgTicks;

	@Provides
	DPSHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DPSHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		resetPlugin();
	}

	private void resetPlugin(){
		gameTicks = 0;
		lastAttackInTicks = 0;
		attackToReset = config.getAttackReset();
		avgTicks = new ArrayList<>();
	}

	@Subscribe
	public void onGameTick(final GameTick gameTick)
	{
		if (attacks == attackToReset)
			postSummary();
		gameTicks++;
	}

	private int getAVG()
	{
		int avg = 0;

		for (int i = 0; i < avgTicks.size(); i++) {
			avg += avgTicks.get(i);
		}
		avg /= avgTicks.size();

		return avg;
	}

	private void postSummary()
	{
		String s = "Ticks between attacks avg: " + getAVG() + " ticks";
		if (client.getGameState() == GameState.LOGGED_IN){
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", s, null);
		}
		resetPlugin();
	}

	@Subscribe
	public void onAnimationChanged(final AnimationChanged event)
	{
		final Actor actor = event.getActor();
		final int animationId = actor.getAnimation();
		if (actor instanceof Player){
			switch (animationId){
				case -1: //IDLE
					break;
				case 422: //PUNCH
				case 423: //KICK
				case 426: //BOW
				case 11989: //AGS SPEC
				case 11993: //SS SPEC
				case 11995: //SGS SPEC
				case 12169: //Mining with Dragon Pickaxe
				case 1658: //whip attack
				case 1665: //Granite maul attack
				case 1667: //Granite Maul special
				case 2066: //dharoks crush
				case 2067: //dharoks slash
				case 2068: //torag attack
				case 2080: //guthan poke
				case 2081: //guthan slash
				case 2082: //guthan crush
					avgTicks.add(gameTicks);
					handleLastAttack();
					attacks++;
					resetTick();
					break;
			}
		}
	}

	private void handleLastAttack(){
		if (!config.getEachAttack())
			return;
		String s = "Ticks between last attack: " + gameTicks + " ticks";
		if (client.getGameState() == GameState.LOGGED_IN){
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", s, null);
		}
	}

	private void resetTick(){
		gameTicks = 0;
	}
}
