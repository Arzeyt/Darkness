package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.LightOrb;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import scala.collection.parallel.ParIterableLike;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * somehow being registered server side...
 *
 */
public class ClientEffectTick {

	private final int DETONATION_TICK_RATE = Reference.DETONATION_EFFECT_TICK_RATE;
	private int counter=0;
	Random rand = new Random();
	
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

	//cannot simply keep setting time to night client side
	public void nightRender(ClientTickEvent e){
		if(e.side==Side.CLIENT && Minecraft.getMinecraft().theWorld!=null){
			if(Darkness.darkLists.isPlayerInTowerRadius(Minecraft.getMinecraft().thePlayer)==false) {

			}
		}
	}



	@SubscribeEvent
	public void atmosphereTick(ClientTickEvent e){
		if(counter%Reference.ATMOSPHERE_TICK==0) {
			if (Minecraft.getMinecraft().theWorld == null) {
				return;
			}
			BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();
			int radius = 7;
			int height = 3;
			int spawnChance = 5;


			for (int i = -radius; i < radius; i++) {
				for (int j = -height; j < height; j++) {
					for (int k = -radius; k < radius; k++) {
						if (rand.nextInt(100) <= spawnChance) {
							double x=pos.getX()+i+rand.nextDouble();
							double y=pos.getY()+j+rand.nextDouble();
							double z=pos.getZ()+k+rand.nextDouble();
							if (Darkness.clientLists.isPosInTowerRadius(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))) {
								//Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x,y,z, 0.0D, 0.1D, 0.0D);
							}else if(Minecraft.getMinecraft().thePlayer.getHeldItem()!=null
									&& Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof LightOrb
									&& Darkness.clientLists.isPosInTowerRadiusX2minus1(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))==false){
								Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, x,y,z, 0.0D, 0.0D, 0.0D);
							}else if(Darkness.clientLists.isPosInTowerRadiusPlus1(Minecraft.getMinecraft().theWorld, new BlockPos(x,y,z))){
								Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, 0.0D, 0.5D, 0.0D);
							}else {
								Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.03D, 0.0D);
							}
						}
					}
				}
			}

		}
	}


}
