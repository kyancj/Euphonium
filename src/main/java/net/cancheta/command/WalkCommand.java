package net.cancheta.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;

import net.minecraft.text.TranslatableText;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.cancheta.ai.strategy.AIStrategy.TickResult;
import net.cancheta.ai.strategy.WalkStrategy;
import net.cancheta.ai.task.WalkTask;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;


public class WalkCommand {
	private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION =
			new SimpleCommandExceptionType(new TranslatableText("commands.walk.invalidPosition"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandManager.literal("walk")
			.then(CommandManager.argument("destination", Vec3ArgumentType.vec3())
			.executes((context) -> {
					return run((ServerCommandSource)context.getSource(),
					Vec3ArgumentType.getPosArgument(context, "destination"));
		})));
	}
	
	private static int run(ServerCommandSource source, PosArgument location) throws CommandSyntaxException {
		WalkTask task = new WalkTask();
		Vec3d vec3d = location.toAbsolutePos(source);
//		ServerPlayerEntity player = source.getPlayer();
		BlockPos target = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
		if (!World.isValid(target)) { //Check if block is valid
			throw INVALID_POSITION_EXCEPTION.create();
		}
		WalkStrategy walk = new WalkStrategy(source, target);
		while (true) { //This line needs to be done on every tick.
			TickResult result = walk.onGameTick(task);
			if (result == TickResult.NO_MORE_WORK || result == TickResult.ABORT) {
				break;
			}
		}
		return 1;
	}
}
