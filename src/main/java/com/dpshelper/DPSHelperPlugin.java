package com.dpshelper;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "DPS Helper",
		description = "This plugin helps you to lose less ticks when attacking npcs, by telling you the ticks between attacks, so you can optimize your gameplay"
)
public class DPSHelperPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private DPSHelperConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private DPSHelperOverlay overlay;
	@Inject
	private ItemManager itemManager;

	private int attackToReset; //Used for summary
	private int attacks; //Users attack count

	@Getter
	private int gameTicks; //GameTicks, used for logic in summary & last attack
	@Getter
	private int cooldown;
	@Getter
	private int lastAttackTick; //This holds gametick from the method handleLastAttack to be displayed on the overlay or in chat
	private int totalTickLost;
	private int aSpeed1;
	@Getter
	private int aSpeed2;
	@Getter
	private int currentAS; //This is a variable to hold the users current weapon the player has equipped
	private boolean inCombat = false;


	@Provides
	DPSHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DPSHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		resetPlugin(); //Reset on startup
	}

	@Override
	protected void shutDown()
	{
		resetPlugin();
		overlayManager.remove(overlay);
		lastAttackTick = 0;
		currentAS = 0;
		cooldown = 0;
		inCombat = false;
	}

	private void resetPlugin()
	{
		gameTicks = 0;
		attacks = 0;
		attackToReset = config.getAttackReset();
		totalTickLost = 0;
	}

	private void resetGameTick()
	{
		gameTicks = 0;
	}

	private void idleReset(){
		gameTicks = 0;
		inCombat = false;
	}

	@Subscribe
	public void onGameTick(final GameTick gameTick)
	{
		if (gameTicks > config.getMaxIdle())
			idleReset();

		if (inCombat) {
			gameTicks++;
			if (--cooldown <= 0) {
				//nothing
			}
		}
	}

	@Subscribe
	public void onAnimationChanged(final AnimationChanged event)
	{
		final Actor actor = event.getActor();
		final int animationId = actor.getAnimation();

		if (actor instanceof Player){
			if (animationId == config.getSkip1() || animationId == config.getSkip2())
				return;
			if (!actor.equals(client.getLocalPlayer()))
				return;
			
			switch (animationId){
				case -1: //IDLE
					break;
				case 422: //PUNCH
				case 423: //KICK
				case 426: //BOW
				case 390: //BoS slash
				case 386: //BoS stab
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
				case 1658: //whip slash
				case 1060: //dragon mace spec
				case 2890: //arclight spec
				case 1872: //d scim spec
				case 1667: //granite maul spec
				case 8056: //SCYTHE_OF_VITUR_ANIMATION
				case 9493: //Tumeken's Shadow
				case 7516: //Colossal Blade
				case 7004: //leaf bladed battleaxe
				case 3852: //leaf bladed battleaxe
				case 1712: //zspear slash
				case 1710: //zspear crush
				case 1711: //zspear stab
				case 1064: //zspear spec
				case 3294: //abyssal dagger slash
				case 3297: //abyssal dagger stab
				case 3300: //abyssal dagger spec
				case 2075: //karil xbow
				case 3298: //abyssal bludgeon
				case 3299: //abyssal bludgeon spec
				case 2062: //verac fail
				case 393: //claws slash
				case 1067: //claws stab
				case 7514: //claws spec
				case 395: //dragon battleaxe slash
				case 1378: //dwh spec
				case 6147: //ancient mace spec
				case 2081: //guthan spear slash
				case 2082: //guthan spear crush
				case 2080: //guthan spear stab
				case 8195: //dragon knife
				case 8194: //dragon knife
				case 8291: //dragon knife spec
				case 8292: //dragon knife spec
				case 7521: //throwaxe spec
				case 5061: //bp
				case 7554: //darts
				case 9168: //zaryte crossbow
				case 1074: //msb spec
				case 245: //chainmace
				case 7555: //Ballista
				case 7556: //Ballista spec
				case 8010: //Ivandis flail spec
				case 5865: //Barrelchest anchor
				case 5870: //Barrelchest anchor spec
				case 8145: //Ghrazi rapier stab
				case 1058: //Dragon longsword/Saeldor spec
				case 2067: //Dharok's greataxe slash
				case 2066: //Dharok's greataxe crush
				case 3157: //2h spec
				case 7328: //Prop sword/candy cane crush
				case 9173: //ancient godsword spec
				case 9171: //ancient godsword spec
				case 7638: //z godsword spec
				case 7644: //ags godsword spec
				case 7640: //sgs godsword spec
				case 8289: //dhl slash
				case 8290: //dhl crush
				case 8288: //dhl stab
				case 7511: //Dinh's bulwhark crush
				case 7512: //Dinh's bulwhark spec
				case 4503: //Inquisitor's mace
				case 4505: //Nightmare Staff
				case 6118: //fang spec
				case 9471: //fang stab
					totalTickLost += getTickLostOnAttack();
					handleAttack();
					inCombat = true;
					if (cooldown <= 0){
						getAttackSpeed();
						cooldown = currentAS; //Update currentAS for the current equipped weapon
					}
					attacks++;
					checkSummary();
					resetGameTick();
					break;
			}
		}
	}

	private int getTickLostOnAttack()
	{
		//Calc tick lost on attack
		int sum = 0;
		if (!inCombat){
			if (attacks == 0)
				sum = 0;
		}else
			sum = gameTicks - currentAS;
		return sum;
	}

	private void checkSummary()
	{
		if (!config.getSummary())
			return;

		if (attacks >= attackToReset)
			postSummary();
	}

	private void postSummary()
	{
		String s = "You lost " + totalTickLost + " ticks in the last " + config.getAttackReset() + " attacks";
		if (client.getGameState() == GameState.LOGGED_IN){
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", s, null);
		}
		resetPlugin();
	}

	private void handleAttack()
	{
		if(!config.getEachAttack())
			return;

		if (!inCombat){
			if (attacks == 0) {
				getAttackSpeed();
				lastAttackTick = currentAS;
			}
		}else{
			 lastAttackTick = gameTicks;
		}

		if (config.getUI())
			return;
		String s = "Ticks between last attack: " + lastAttackTick + " ticks";
		if (client.getGameState() == GameState.LOGGED_IN){
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", s, null);
		}
	}

	private void getAttackSpeed()
	{
		//This method is used to get the attackspeed of the players equipped weapon
		int itemId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
		final ItemStats stats = itemManager.getItemStats(itemId, false);
		if (stats == null)
			return;
		final ItemEquipmentStats currentEquipment = stats.getEquipment();
		handleAS(currentEquipment.getAspeed());
	}

	private void handleAS(int aSpeed)
	{
		aSpeed1 = currentAS;
		aSpeed2 = aSpeed1;
		currentAS = aSpeed;

	}
}
