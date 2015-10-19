package com.arzeyt.darkness.lightOrb;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Detonation {
	
	public World w;
	public BlockPos pos;
	public int lifeRemaining;

	public Detonation(World w, BlockPos pos, int lifeRemaining){
		this.w=w;
		this.pos=pos;
		this.lifeRemaining=lifeRemaining;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Detonation){
			Detonation det = (Detonation)obj;
			if(det.w.provider.getDimensionId()==w.provider.getDimensionId()
				&& det.pos==pos){
					return true;
				}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int h = 1;
		h = 31*h+w.provider.getDimensionId();
		h = 31*h+pos.hashCode();
		return h;
	}
}
