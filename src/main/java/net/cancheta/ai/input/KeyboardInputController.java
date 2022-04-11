package net.cancheta.ai.input;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class KeyboardInputController {
	private static final Marker MARKER_KEY = MarkerManager.getMarker("key");
	private static final Logger LOGGER = LogManager
			.getLogger(KeyboardInputController.class);
	private final InputUtil.Key fakeKey;

	private static abstract class KeyAdapter {

		public abstract KeyBinding get(GameOptions gameSettings);
	}

	public enum KeyType {
		ATTACK(501, new KeyAdapter() { //501?? Occasionally breaks everything and client can no longer can left click
			@Override
			public KeyBinding get(GameOptions gameSettings) {
				return gameSettings.keyAttack;
			}
		}), USE(502, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameSettings) {
				return gameSettings.keyUse;
			}
		}), SNEAK(503, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameSettings) {
				return gameSettings.keySneak;
			}
		}), SPRINT(504, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameSettings) {
				return gameSettings.keySprint;
			}
		}), JUMP(504, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameSettings) {
				return gameSettings.keyJump;
			}
		});

		private final KeyAdapter keyAdapter;
		private int keyCode;

		private KeyType(int keyCode, KeyAdapter keyAdapter) {
			this.keyCode = keyCode;
			this.keyAdapter = keyAdapter;
		}

		public KeyBinding getBinding(MinecraftClient mc) {
			return keyAdapter.get(mc.options);
		}
	}

	private MinecraftClient mc;
	private KeyType key;
	private boolean isOverride;
	private InputUtil.Key oldKeyCode;
	private boolean wasOverride;

	public KeyboardInputController(MinecraftClient mc, KeyType key) {
		this.mc = mc;
		this.key = key;
		this.fakeKey = InputUtil.fromKeyCode(key.keyCode, 0);
		oldKeyCode = key.getBinding(mc).getDefaultKey();
	}

	/**
	 * Called if the button press should be simulated on the next tick.
	 */
	public void overridePressed() {
		LOGGER.info(MARKER_KEY, "Requested key press for " + key);
		if (isOverride) {
			LOGGER.warn(MARKER_KEY, "A key press was requested twice for " + key);
		}
		isOverride = true;
	}

	public void doTick() {
		KeyBinding binding = key.getBinding(mc);
		if (isOverride) {
			if (!wasOverride) {
				LOGGER.trace(MARKER_KEY, "Simulate a key press down for " + key);
				// Map to a temporary key
				binding.setBoundKey(fakeKey);
				KeyBinding.updateKeysByCode();
				// Simulate press of that key.
				KeyBinding.setKeyPressed(fakeKey, true);
				KeyBinding.onKeyPressed(fakeKey);
			} else {
				LOGGER.trace(MARKER_KEY, "Key should still be down. " + key);
			}
			if (!binding.isPressed()) {
				LOGGER.error(MARKER_KEY, "Key press simulated but key is not pressed: " + key);
			}
		} else {
			if (wasOverride) {
				LOGGER.trace(MARKER_KEY, "Key override deactivated: " + key);
				KeyBinding.setKeyPressed(fakeKey, false);
				binding.setBoundKey(oldKeyCode);
				KeyBinding.updateKeysByCode();
			}
		}
		wasOverride = isOverride;
		isOverride = false;
	}

	public boolean wasPressedByUser() {
		return !isOverride && !wasOverride && key.getBinding(mc).isPressed();
	}
}
