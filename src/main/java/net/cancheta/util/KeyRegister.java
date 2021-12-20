package net.cancheta.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.cancheta.util.keys.Stop;
import net.cancheta.util.keys.Resume;

public class KeyRegister {
	public static KeyBinding stop;
	public static KeyBinding resume;
	
	public static void registerKeys() {
		stop = KeyBindingHelper.registerKeyBinding(Stop.stop);
		resume = KeyBindingHelper.registerKeyBinding(Resume.resume);
	}
}
