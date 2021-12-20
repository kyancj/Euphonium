package net.cancheta;

//import net.cancheta.ai.AIController;
import net.fabricmc.api.ClientModInitializer;

import net.cancheta.util.ModCommandRegister;
import net.cancheta.util.KeyRegister;


public class EuphoniumClient implements ClientModInitializer {
//	public static boolean running = true;
//	public static int lastPressedToggleKey = 0;
//	public static Logger LOGGER = LogManager.getLogger("NU Bot");
//	
//	public static KeyBinding start = new KeyBinding("Start Bot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F12, "NU Bot");
//	public static KeyBinding stop = new KeyBinding("Stop Bot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "NU Bot");
//	
	@Override
	public void onInitializeClient() {
//		LOGGER.info("Loading NU Bot.");
//		start = KeyBindingHelper.registerKeyBinding(start);
//		stop = KeyBindingHelper.registerKeyBinding(stop);
//		
		ModCommandRegister.registerCommands();
		KeyRegister.registerKeys();
	}

}
