package net.cancheta.mixin;

//import static net.cancheta.EuphoniumClient.*;

import net.minecraft.client.MinecraftClient;
//import net.minecraft.text.LiteralText;
//
import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.lang.Exception;

@Mixin(MinecraftClient.class)
public abstract class EuphoniumMixin{
	
//	private static MinecraftClient mc;
//	
//	@Inject(at = @At("HEAD"), method = "tick")
//	private void tick(CallbackInfo info) {
//		mc = MinecraftClient.getInstance();
//		
//		lastPressedToggleKey--;
//		if (lastPressedToggleKey < 0) {
//			lastPressedToggleKey = 0;
//		}
//
//		if (running) { //  && this.itemUseCooldown <= 0
//			try {
//				mc.player.addVelocity(0, 0, (float).13);
//			} catch (Exception e) {
//				mc.player.addVelocity(0, 0, 0);
//				LOGGER.info("Unknown error.");
//				running = false;
//			}
//		} else if (!running) {
//			try {
//				
//			} catch (Exception e) {
//				LOGGER.info("Unkown error.");
//			}
//		}
//	}
//
//	@Inject(at = @At("HEAD"), method = "handleInputEvents")
//	private void handleInputEvents(CallbackInfo info) {
//		mc = MinecraftClient.getInstance();
//		
//		if (lastPressedToggleKey <= 0) {
//			if (start.isPressed()) {
//				mc.player.sendMessage(new LiteralText("NU Bot running."), false);
//				running = true;
//				lastPressedToggleKey += 30; //Allow press once every 30 ticks
//			} else if (stop.isPressed()) {
//				mc.player.sendMessage(new LiteralText("NU Bot stopped."), false);
//				running = false;
//				lastPressedToggleKey += 30;
//			}
//		}
//	}
}