package com.arzeyt.darkness;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class DPlayer{

	private EntityPlayer p;
	
	public DPlayer(EntityPlayer p){
		this.p=p;
	}
	
	public EntityPlayer getEntityPlayer(){
		return p;
	}
	
	public BlockPos getPosition(){
		return p.getPosition();
	}
	
	public String getName(){
		return p.getDisplayNameString();
	}
}
