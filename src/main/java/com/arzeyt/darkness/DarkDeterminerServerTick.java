package com.arzeyt.darkness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.arzeyt.darkness.towerObject.DetonationMessageToClient;
import com.arzeyt.darkness.towerObject.LightOrb;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DarkDeterminerServerTick {

	private int counter = 1;
	
	Reference r = new Reference();
	
	//40 since it's called twice. Counter increments 2 times faster than normal
	private final int DARKNESS_CHECK_RATE = r.DARKNESS_CHECK_RATE;
	private final int ORB_DEPLETION_RATE = r.ORB_DEPLETETION_RATE;
	private final int TOWER_RADIUS=r.TOWER_RADIUS;
	private final int HELD_ORB_RADIUS=r.HELD_ORB_RADIUS;
	private final int ORB_DETONATION_RAIDUS=r.ORB_DETONATION_RAIDUS;

	
	@SubscribeEvent
	public void darknessCheck(ServerTickEvent e){
		
		if(counter%DARKNESS_CHECK_RATE==0){

			ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator iterator = list.iterator();
			while(iterator.hasNext())
			{
				EntityPlayerMP player = (EntityPlayerMP) iterator.next();
				WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);
				
			//held orb
				if(player.getHeldItem()!=null
						&& player.getHeldItem().getItem() instanceof LightOrb
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)==false){					
					
					Darkness.darkLists.addPlayerWithOrb(player);
					Darkness.darkLists.removePlayerInDarkness(player);
					System.out.println("holding orb");
				}
			//tower
				else if(Darkness.darkLists.getPoweredTowers().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestTower(player) < TOWER_RADIUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					System.out.println("near powered tower");
				}
			//player
				else if(Darkness.darkLists.getPlayersWithOrb().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestPlayerWithOrb(player)<HELD_ORB_RADIUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					System.out.println("near player with orb");
				}
			//detonations
				else if(Darkness.darkLists.getOrbDetonations().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestOrbDetonation(player)<ORB_DETONATION_RAIDUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					System.out.println("near orb detonation");
				}
			//player is in darkness if none of the above are true
				else if(Darkness.darkLists.getPlayersInDarkness().isEmpty()==true
							|| Darkness.darkLists.getPlayersInDarkness().contains(player)==false){
						Darkness.darkLists.addPlayersInDarkness(player);
						System.out.println("player is in darkness");
				}
				
				System.out.println("Player in darkness: "+Darkness.darkLists.isPlayerInDarkness(player));
			}
		
		}
		counter++;
		if(counter>123456){
			counter=1;
		}
	}
	
	@SubscribeEvent
	public void orbDepletion(ServerTickEvent e){
		if(counter%ORB_DEPLETION_RATE==0
				&&Darkness.darkLists.getLightOrbs().isEmpty()==false){
			System.out.println("orbs in list: "+Darkness.darkLists.getLightOrbs());
			for(ItemStack orb : Darkness.darkLists.getLightOrbs()){
				if(orb.hasTagCompound()==false){
					System.out.println("Orb doesn't have TAG! D:");
				}else{
					NBTTagCompound nbt = (NBTTagCompound) orb.getTagCompound().getTag("darkness");
					
					int dissipationPercent = nbt.getInteger(r.DISSIPATION_PERCENT);
					int initialPower = nbt.getInteger(r.INITAL_POWER);
					int power = nbt.getInteger(r.POWER);
					

					dissipationPercent++;
					power = initialPower-dissipationPercent;
					
					nbt.setInteger(r.POWER, power);
					nbt.setInteger(r.DISSIPATION_PERCENT, dissipationPercent);
					
					System.out.println("Power: "+power+" dissipationPercent: "+dissipationPercent);
					System.out.println("nbt data says id: "+nbt.getInteger(r.ID)+" Power: "+nbt.getInteger(r.POWER)+" dissipationP: "+nbt.getInteger(r.DISSIPATION_PERCENT));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void detonationDepletion(ServerTickEvent e){
		if(Darkness.darkLists.getOrbDetonations().isEmpty()==false){
			HashMap <BlockPos, Integer> map = Darkness.darkLists.getOrbDetonations();
			for(BlockPos p : map.keySet()){
				System.out.println("lifetime: "+map.get(p));
				if(map.get(p)<=0){//lifetime expired, so remove position, and send remove to client
					Darkness.darkLists.removeOrbDetonation(p);
					Darkness.simpleNetworkWrapper.sendToAll(new DetonationMessageToClient(false, p.getX(), p.getY(), p.getZ()));
					System.out.println("sent orb remove message");
				}else{
					int lifetime=map.get(p);
					Darkness.darkLists.removeOrbDetonation(p);
					Darkness.darkLists.addOrbDetonation(p,lifetime--);
				}
			}
		}
	}
	
}
