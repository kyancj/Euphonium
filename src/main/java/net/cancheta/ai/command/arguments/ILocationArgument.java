package net.cancheta.ai.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;


public interface ILocationArgument {
	Vec3d getPosition(CommandSource context);
	
	Vec2f getRotation(CommandSource context);
	
	default BlockPos getBlockPos(CommandSource context) { return new BlockPos(this.getPosition(context)); }

	boolean isXRelative();
	
	boolean isYRelative();
	
	boolean isZRelative();
	}
