package net.cancheta.util.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.GameOptions;

public class InputController {
	private final InputUtil.Key fakeKey;
	
	private static abstract class KeyAdapter {

		public abstract KeyBinding get(GameOptions gameOptions);
	}
	
	public enum KeyType {
		ATTACK(501, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameOptions) {
				return gameOptions.keyAttack;
			}}),
		USE(502, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameOptions) {
				return gameOptions.keyUse;
			}}),
		SNEAK(503, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameOptions) {
				return gameOptions.keySneak;
			}}),
		SPRINT(504, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameOptions) {
				return gameOptions.keySprint;
			}}),
		JUMP(504, new KeyAdapter() {
			@Override
			public KeyBinding get(GameOptions gameOptions) {
				return gameOptions.keyJump;
			}});
		
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
	private boolean isOverride;
	private boolean wasOverride;
	private KeyType key;
	private InputUtil.Key oldKey;
	
	public InputController(MinecraftClient mc, KeyType key) {
		this.mc = mc;
		this.key = key;
		this.fakeKey = InputUtil.fromKeyCode(key.keyCode, 0);
		oldKey = key.getBinding(mc).getDefaultKey();
	}
	
	public void stopPressed() {
		if (isOverride) {
			//
		}
		isOverride = true;	
	}
	
	public void doTick() {
		KeyBinding binding = key.getBinding(mc);
		if (isOverride) {
			if (!wasOverride) {
				// Map to a temporary key
				binding.setBoundKey(fakeKey);
				KeyBinding.updateKeysByCode();
				// Simulate press of that key.
				KeyBinding.setKeyPressed(fakeKey, true);
				KeyBinding.onKeyPressed(fakeKey);
			}

		} else {
			if (wasOverride) {
				KeyBinding.setKeyPressed(fakeKey, false);
				binding.setBoundKey(oldKey);
				KeyBinding.updateKeysByCode();
			}
		}
		wasOverride = isOverride;
		isOverride = false;
	}
}
