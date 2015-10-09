package com.arzeyt.darkness;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * somehow being registered server side...
 *
 */
public class ClientEffectTick {

	private final int DETONATION_TICK_RATE = Reference.DETONATION_TICK_RATE;
	private int counter=0;
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if(e.side==Side.SERVER)return;
		counter++;
		
		//detonation effect
		if(Darkness.clientLists.getDetonations().isEmpty()==false
				&&counter%DETONATION_TICK_RATE==0){
			for(BlockPos pos : Darkness.clientLists.getDetonations()){
				Random rand = new Random();
				double vx = ThreadLocalRandom.current().nextDouble(-1, 1);
				double vy = ThreadLocalRandom.current().nextDouble(-2,2);
				double vz = ThreadLocalRandom.current().nextDouble(-1,1);
	
				Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX(), pos.getY(), pos.getZ(), vx, vy, vz);
			}
		}
	}
}
