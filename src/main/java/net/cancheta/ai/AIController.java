package net.cancheta.ai;

import net.cancheta.ai.command.AIChatController;
import net.cancheta.ai.command.IAIControllable;
import net.cancheta.ai.command.SafeStrategyRule;
import net.cancheta.ai.command.StackBuilder;
import net.cancheta.ai.strategy.AIStrategy;
//import net.cancheta.ai.strategy.AIStrategy.TickResult;
//
//import java.util.Hashtable;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.Marker;
//import org.apache.logging.log4j.MarkerManager;
//import java.util.Map.Entry;
//
//
//import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.option.KeyBinding;
//import net.minecraft.client.util.InputUtil;



//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
//import net.minecraft.entity.player.PlayerEntity;

public class AIController {// extends AIHelper implements IAIControllable{
	private final StackBuilder stackBuilder = new StackBuilder();
//	
//	private static final Marker MARKER_EVENT = MarkerManager.getMarker("event");
//	private static final Marker MARKER_STRATEGY = MarkerManager
//			.getMarker("strategy");
//	private static final Logger LOGGER = LogManager
//			.getLogger(AIController.class);
//
//	private final static Hashtable<KeyBinding, AIStrategyFactory> uses = new Hashtable<KeyBinding, AIStrategyFactory>();
//
//	protected static final KeyBinding stop = new KeyBinding("Stop",
//			InputUtil.fromTranslationKey("key.keyboard.n").getCode(), "Command Mod");
//	protected static final KeyBinding ungrab = new KeyBinding("Ungrab",
//			InputUtil.fromTranslationKey("key.keyboard.u").getCode(), "Command Mod");
//
//	static {
//		KeyBindingHelper.registerKeyBinding(stop);
//		KeyBindingHelper.registerKeyBinding(ungrab);
//	}
//	
//	public AIController() {
//		
//	}
//	
//	private boolean dead;
//	private volatile AIStrategy currentStrategy;
//	
	private AIStrategy deactivatedStrategy;
//
//	private AIStrategy requestedStrategy;
//	
	public void connect() {
//		AIChatController.getRegistry().setControlled(this);
	}
//	
	public void onPlayerTick(MinecraftClient client) {
		if (client.player == null ||
			client.world == null) {
			// Player ticks, but player is not in world.
			return;
		}
//		System.out.println("text");
		AIStrategy newStrategy;
//		if (dead || stop.isPressed()) {
//			if (deactivatedStrategy == null) {
//				LOGGER.trace(MARKER_STRATEGY,
//						"Store strategy to be resumed later: "
//								+ currentStrategy);
//				deactivatedStrategy = currentStrategy;
//			}
//			deactivateCurrentStrategy();
//			dead = false;
//		} else if ((newStrategy = findNewStrategy()) != null) {
//			deactivateCurrentStrategy();
//			currentStrategy = newStrategy;
//			deactivatedStrategy = null;
//			LOGGER.debug(MARKER_STRATEGY, "Using new root strategy: "
//					+ newStrategy);
//			currentStrategy.setActive(true, this);
//		}
//		
//		if (currentStrategy != null) {
//			TickResult result = null;
//			for (int i = 0; i < 100; i++) {
//				result = currentStrategy.gameTick(this);
//				if (result != TickResult.TICK_AGAIN) {
//					break;
//				}
//				LOGGER.trace(MARKER_STRATEGY,
//						"Strategy requests to tick again.");
//			}
//			if (result == TickResult.ABORT || result == TickResult.NO_MORE_WORK) {
//				LOGGER.debug(MARKER_STRATEGY, "Strategy is dead.");
//				dead = true;
//			}
//		} 
//		
//		keyboardPostTick();
//		LOGGER.debug(MARKER_STRATEGY, "Strategy game tick done");
	}
//	
//	private void deactivateCurrentStrategy() {
//		if (currentStrategy != null) {
//			LOGGER.trace(MARKER_STRATEGY, "Deactivating strategy: "
//					+ currentStrategy);
//			currentStrategy.setActive(false, this);
//		}
//		currentStrategy = null;
//	}
//	
//	public void resetOnGameEnd() {
//		LOGGER.trace(MARKER_EVENT, "Unloading world.");
//		dead = true;
//	}
//	
//	private AIStrategy findNewStrategy() {
//		if (requestedStrategy != null) {
//			final AIStrategy strategy = requestedStrategy;
//			requestedStrategy = null;
//			return strategy;
//		}
//
//		for (final Entry<KeyBinding, AIStrategyFactory> e : uses.entrySet()) {
//			if (e.getKey().isPressed()) {
//				final AIStrategy strategy = e.getValue().produceStrategy(this);
//				if (strategy != null) {
//					return strategy;
//				}
//			}
//		}
//		return null;
//	}
//	
//	@Override
//	public AIHelper getAiHelper() {
//		return this;
//	}


//	@Override
//	public int requestUseStrategy(AIStrategy strategy) {
//		return requestUseStrategy(strategy, SafeStrategyRule.NONE);
//	}
	
	public void initialize(MinecraftClient client) {
		onPlayerTick(client);
		connect();
	}
	
//	public int dumb() {
//		return 1;
//	}

//	@Override
//	public StackBuilder getStackBuilder() {
//		return stackBuilder;
//	}
//
//	@Override
//	public AIStrategy getResumeStrategy() {
//		return deactivatedStrategy;
//	}
}
