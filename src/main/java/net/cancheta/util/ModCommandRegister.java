package net.cancheta.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.cancheta.ai.command.IAIControllable;
import net.cancheta.ai.commands.WalkCommand;
//import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ModCommandRegister {                
    public static void registerCommands(LiteralArgumentBuilder<IAIControllable> euphonium,
                        LiteralArgumentBuilder<IAIControllable> minebuild) {
	WalkCommand.register(euphonium);
	}
}
