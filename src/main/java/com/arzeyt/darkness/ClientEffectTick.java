package com.arzeyt.darkness;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.eventbus.Subscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
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

	private final int DETONATION_TICK_RATE = Reference.DETONATION_EFFECT_TICK_RATE;
	private int counter=0;
	
	@SubscribeEvent
	public void detonationEffect(ClientTickEvent e){
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
	
	@SubscribeEvent
	public void sphereDetonationEffect(ClientTickEvent e){
		if(e.side==Side.SERVER)return;
		
		if(Darkness.clientLists.getDetonations().isEmpty()==false
				&&counter%(DETONATION_TICK_RATE*5)==0){
			for(BlockPos pos : Darkness.clientLists.getDetonations()){
				double i = pos.getX();
				double j = pos.getY();
				double k = pos.getZ();
				Random rand = new Random();
				int r = Reference.ORB_DETONATION_RAIDUS;
				int density = 20;
				
				for(double x = -r; x < r; x++){
					for(double y = -r; y < r; y++){ 
						for(double z = -r; z < r; z++){					
							double dist = MathHelper.sqrt_double((x*x + y*y + z*z)); //Calculates the distance
							if((dist >= r-1 && dist <= r+1) && rand.nextInt(100)<density){
								Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, i+x+rand.nextDouble(), j+y+rand.nextDouble(), k+z+rand.nextDouble(), 0.0, 0.0, 0.0);
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void towerRadiusEffect(ClientTickEvent e){
		
	}
}
