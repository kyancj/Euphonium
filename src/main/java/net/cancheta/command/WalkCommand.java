package net.cancheta.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.CommandManager;

import net.minecraft.text.TranslatableText;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraft.world.World;

import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.text.LiteralText;


public class WalkCommand {
	private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION =
			new SimpleCommandExceptionType(new TranslatableText("commands.walk.invalidPosition"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandManager.literal("walk")
			.then(CommandManager.argument("location", Vec3ArgumentType.vec3())
			.executes((context) -> {
					return run((ServerCommandSource)context.getSource(),
					((ServerCommandSource)context.getSource()).getWorld(),
					Vec3ArgumentType.getPosArgument(context, "location"));
		})));
	}
	
	private static int run(ServerCommandSource source, ServerWorld world, PosArgument location) throws CommandSyntaxException {
		Vec3d vec3d = location.toAbsolutePos(source);
		Entity player = source.getPlayer();
		walk(source, player, world, vec3d.x, vec3d.y, vec3d.z);
		return 1;
	}
	
	private static void walk(ServerCommandSource source, Entity player, ServerWorld world, double x, double y, double z) throws CommandSyntaxException  {
		BlockPos blockPos = new BlockPos(x, y, z);
		if (!World.isValid(blockPos)) {
			throw INVALID_POSITION_EXCEPTION.create();
		}
		if (player instanceof ServerPlayerEntity) {
			source.getPlayer().sendMessage(new LiteralText("Player set to walk to <" + x + ", " + y + ", " + z + ">."), false);
			if (((ServerPlayerEntity)player).isSleeping()) {
				((ServerPlayerEntity)player).wakeUp(true, true);
			}
			source.getPlayer().sendMessage(new LiteralText("Player set to walk to <" + x + ", " + y + ", " + z + ">."), false);
			int count = 0;
			while (true) {
				count++;
				((ServerPlayerEntity)player).move(MovementType.PLAYER, new Vec3d(1000, 1000, 1000));
				if (count > 1000) {
					break;
				}
			}
			source.getPlayer().sendMessage(new LiteralText("Player set to walk to <" + x + ", " + y + ", " + z + ">."), false);
		}
//		Pathfind find = new Pathfind(source, world, x, y, z);
//		find.walk();
		
	}
}
