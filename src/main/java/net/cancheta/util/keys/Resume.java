package net.cancheta.util.keys;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Resume {
	public static KeyBinding resume = new KeyBinding("Resume Strategy", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F12, "Euphonium");
}
