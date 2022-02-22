package net.cancheta.ai.path;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.math.BlockPos;

//import net.minecraft.util.math.BlockPos;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class Pathfind {
	private static PathNode start;
	private static PathNode destination;
	private static LinkedList<PathNode> open = new LinkedList<PathNode>();
	private static LinkedList<PathNode> closed = new LinkedList<PathNode>();
	private static LinkedList<BlockPos> path = new LinkedList<BlockPos>();
	private static boolean pathFound = false;
	private static PathNode currentPos;
	
	
	public Pathfind(BlockPos startPos, BlockPos destinationPos) {
		start = new PathNode(startPos);
		destination = new PathNode(destinationPos);
	}
	
	public static void findPath() {
		open.add(start);
		currentPos = open.get(0);
		while (!pathFound) {
			for(int i = 0; i < open.size(); i++) {
				if(open.get(i).getFinalCost() < currentPos.getFinalCost()) {
					currentPos = open.get(i);
				}
			}
			
			open.remove(currentPos);
			
			closed.add(currentPos);
			
			if(currentPos.getBlock() == destination.getBlock()) {
				pathFound = true;
				constructPath();
			}
			
			consider(new PathNode(new BlockPos(currentPos.getBlock().getX() + 1, currentPos.getBlock().getY(), currentPos.getBlock().getZ())));
			consider(new PathNode(new BlockPos(currentPos.getBlock().getX() - 1, currentPos.getBlock().getY(), currentPos.getBlock().getZ())));
			consider(new PathNode(new BlockPos(currentPos.getBlock().getX(), currentPos.getBlock().getY(), currentPos.getBlock().getZ() + 1)));
			consider(new PathNode(new BlockPos(currentPos.getBlock().getX(), currentPos.getBlock().getY(), currentPos.getBlock().getZ() - 1)));
		}
	}
	
	private static void consider(PathNode passedNode) {
		if (!passedNode.getIsPassable() || closed.contains(passedNode)) {
			return;
		}
		
		open.add(passedNode);
	}
	
	private static void constructPath() {
		for(int i = 0; i < closed.size(); i++) {
			path.add(closed.get(i).getBlock());
		}
		path = (LinkedList<BlockPos>) reverseList(path);
	}
	
	public static<T> List<T> reverseList(List<T> list){
		return new ArrayList<>(Lists.reverse(list));
	}
	
	public LinkedList<BlockPos> getPath(){
		return path;
	}
}
