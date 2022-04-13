package net.cancheta.util;

import net.cancheta.ai.AIController;
import net.cancheta.events.PlayerEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModEventsRegister {
	public static void registerEvents(AIController controller) {
		ClientTickEvents.END_CLIENT_TICK.register(new PlayerEvents(controller));
	}
}
