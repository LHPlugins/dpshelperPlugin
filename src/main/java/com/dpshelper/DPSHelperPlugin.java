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

	@Override
	protected void shutDown(){resetPlugin();}

	private void resetPlugin(){
		gameTicks = 0;
		lastAttackInTicks = 0;
		attackToReset = config.getAttackReset();
		avgTicks = new ArrayList<>();
	}

	@Subscribe
	public void onGameTick(final GameTick gameTick)
	{
		if (attacks >= attackToReset)
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
			if (animationId == config.getSkip1() || animationId == config.getSkip2())
				return;

			switch (animationId){
				case -1: //IDLE
					break;
				case 422: //PUNCH
				case 423: //KICK
				case 426: //BOW
				case 390: //BoS slash
				case 386: //BoS lunge
				case 7045: //bgs chop
				case 7054: //bgs smash
				case 7055: //bgs block
				case 1167: // trident attack
				case 381: //ZHasta stab
				case 440: //ZHasta swipe
				case 419: //ZHasta pound
				case 414: //Staff pummel
				case 7617: //Knife throw
				case 401: //Pickaxe spike
				case 400: //Pickaxe smash
				case 428: //Halberd jab
				case 407: //2h block
				case 406: //2h smash
				case 1665: //Granite maul pound
				case 377: //dds slash
				case 376: //dds stab
				case 7552: //crossbow rapid
				case 2068: //torag hammers
				case 1203: //halberd spec
				case 7642: //bgs spec
				case 1062: //dds spec
				case 7515: //dragon sword spec
				case 1658: //whip spec
				case 1060: //dragon mace spec
				case 2890: //arclight spec
				case 1872: //d scim spec
				case 1667: //granite maul spec
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
