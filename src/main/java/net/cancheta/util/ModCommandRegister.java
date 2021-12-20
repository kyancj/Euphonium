package net.cancheta.util;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.cancheta.command.WalkCommand;

public class ModCommandRegister {
	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register(WalkCommand::register);
	}
}
