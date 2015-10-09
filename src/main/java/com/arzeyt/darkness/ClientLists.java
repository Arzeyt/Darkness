package com.arzeyt.darkness;

import java.util.HashSet;

import net.minecraft.util.BlockPos;
import scala.collection.mutable.HashMap;

public class ClientLists {

	//accessed every tick to see where to render detonations. The server should modify this list via packets.
	private HashSet<BlockPos> detonations = new HashSet<BlockPos>();

	public HashSet<BlockPos> getDetonations() {
		return detonations;
	}
	
	public void addDetonation(BlockPos pos){
		detonations.add(pos);
	}

	public void removeDetonation(BlockPos pos) {
		detonations.remove(pos);
	}
}
