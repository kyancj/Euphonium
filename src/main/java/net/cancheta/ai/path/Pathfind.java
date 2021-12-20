package net.cancheta.ai.path;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import java.lang.Math;

public class Pathfind {
	private static BlockPos destination;
	private static BlockPos currently;
	private static ServerCommandSource psource;
	private static PlayerEntity pplayer;
	private static ServerWorld pworld;
	
	public Pathfind(ServerCommandSource source, ServerWorld world, double x, double y, double z) throws CommandSyntaxException {
		destination = new BlockPos(x, y, z);
		currently = source.getPlayer().getBlockPos();
		psource = source;
		pplayer = source.getPlayer();
		pworld = world;
	}
	
	public void walk() throws CommandSyntaxException{
		int count = 0;
		pplayer.sendMessage(new LiteralText("" + dx() + dz()), false);
		while((Math.abs(dx()) > 0.5) || (Math.abs(dz()) > 0.5)){
//			psource.getPlayer().jump();
//			pplayer.sendMessage(new LiteralText("" + dx() + dz()), false);
			((ServerPlayerEntity)pplayer).jump();
			count++;
			if (count > 300) {
				break;
			}
		}
	}
	
	private static double dx(){
		return destination.getX() - currently.getX();
	}
	
	private static double dz() {
		return destination.getZ() - currently.getZ();
	}
}
