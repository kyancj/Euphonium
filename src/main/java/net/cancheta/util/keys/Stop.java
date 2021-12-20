package net.cancheta.util.keys;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Stop {
	public static KeyBinding stop = new KeyBinding("Stop", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "Euphonium");
}
