package net.cancheta.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.cancheta.ai.AIController;

public class PlayerEvents implements ClientTickEvents.EndTick {
	private AIController controller;
	
	public PlayerEvents(AIController controller) {
		this.controller = controller;
	}
	
	@Override
	public void onEndTick(MinecraftClient client) {
		controller.initialize(client);
	}
}
