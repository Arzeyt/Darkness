package com.arzeyt.darkness;

import java.util.*;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import com.arzeyt.darkness.lightOrb.Detonation;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.*;

public class DarkEventHandlerFML {

	private int counter = 1;
	
	Reference r = new Reference();
	
	//40 since it's called twice. Counter increments 2 times faster than normal
	private final int DARKNESS_CHECK_RATE = Reference.DARKNESS_CHECK_RATE;
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
					//System.out.println(player.getName()+" is holding orb");
				}
			//tower
				else if(Darkness.darkLists.getPoweredTowers().isEmpty()==false
						&& Darkness.darkLists.isPlayerInTowerRadius(player)){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getName()+" is near powered tower");
				}
			//player
				else if(Darkness.darkLists.getPlayersWithOrb().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestPlayerWithOrb(player)<=HELD_ORB_RADIUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getName()+" is near player with orb");
				}
			//detonations
				else if(Darkness.darkLists.getOrbDetonations().isEmpty()==false
						&& Darkness.darkLists.getDistanceToNearestOrbDetonation(player)<=ORB_DETONATION_RAIDUS){
					Darkness.darkLists.removePlayerInDarkness(player);
					//System.out.println(player.getName()+" is near orb detonation");
				}
			//player is in darkness if none of the above are true
				else if(Darkness.darkLists.getPlayersInDarkness().isEmpty()==true
							|| Darkness.darkLists.getPlayersInDarkness().contains(player)==false){
						Darkness.darkLists.addPlayersInDarkness(player);
						//System.out.println(player.getName()+" is in darkness");
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
			HashSet<ItemStack> removalOrbs = new HashSet<ItemStack>();
			
			for(ItemStack orb : Darkness.darkLists.getLightOrbs()){
				if(orb.hasTagCompound()==false){
					System.out.println("Orb doesn't have TAG! D:");
				}else{
					NBTTagCompound nbt = (NBTTagCompound) orb.getTagCompound().getTag("darkness");
					
					int id = nbt.getInteger(r.ID);
					int dissipationPercent = nbt.getInteger(r.DISSIPATION_PERCENT);
					int initialPower = nbt.getInteger(r.INITAL_POWER);
					int power = nbt.getInteger(r.POWER);
					

					dissipationPercent++;
					power = initialPower-dissipationPercent;
					
					if(power<1){
						ItemStack pOrb = Darkness.darkLists.getActualOrbFromID(id);
						if(pOrb==null){
							System.out.println("player orb cannot be found!");
						}else{
							pOrb.stackSize--;
							removalOrbs.add(orb);
							System.out.println("removed orb");
						}
					}
					
					nbt.setInteger(r.POWER, power);
					nbt.setInteger(r.DISSIPATION_PERCENT, dissipationPercent);
					
					System.out.println("Power: "+power+" dissipationPercent: "+dissipationPercent);
					System.out.println("nbt data says id: "+nbt.getInteger(r.ID)+" Power: "+nbt.getInteger(r.POWER)+" dissipationP: "+nbt.getInteger(r.DISSIPATION_PERCENT));
					
					//per player basis... map needs to include orb owner
					System.out.println("sending orb update message");
					Darkness.simpleNetworkWrapper.sendToAll(new OrbUpdateMessageToClient(id, power, dissipationPercent));
				}
			}
			for(ItemStack deadOrb : removalOrbs){
				Darkness.darkLists.removeLightOrb(deadOrb);
			}
		}
	}
	
	@SubscribeEvent
	public void detonationDepletion(ServerTickEvent e){
		if(Darkness.darkLists.getOrbDetonations().isEmpty()==false){
			HashSet<Detonation> toRemove = new HashSet<Detonation>();

			//
			for(Detonation d : Darkness.darkLists.getOrbDetonations()){
				if(d.lifeRemaining<=0){
					toRemove.add(d);
					Darkness.simpleNetworkWrapper.sendToAll(new DetonationMessageToClient(false, d.pos.getX(), d.pos.getY(), d.pos.getZ()));
					System.out.println("sent orb detonate message");
				}else{
					d.lifeRemaining--;
				}
			}
			
			if(toRemove.isEmpty()==false){
				for(Detonation d : toRemove){
					Darkness.darkLists.removeOrbDetonation(d.w, d.pos);
				}
			}
			//apply firey swag (too many ticks for this?) efficiency improvement possible
			int r = Reference.ORB_DETONATION_RAIDUS;
			for(Detonation d : Darkness.darkLists.getOrbDetonations()){
				List mobList = d.w.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(d.pos.getX()-r, d.pos.getY()-r, d.pos.getZ()-r, d.pos.getX()+r, d.pos.getY()+r, d.pos.getZ()+r));
				Iterator it = mobList.iterator();
				while(it.hasNext()){
					EntityMob mob = (EntityMob) it.next();
					if(Darkness.darkLists.getDistanceToNearestOrbDetonation(mob.worldObj, mob.getPosition())<=r){
						mob.fireResistance=0;
						mob.setFire(1);
					}
				}
			}
			
		}
	}
	
	//player tick event wasn't working so went with player loop through server tick 
	@SubscribeEvent
	public void playerEffects(ServerTickEvent e){
		if(counter%(DARKNESS_CHECK_RATE)==3){//offset from darkness check tick a bit...
			int icounter = 0;//unused for nao
			icounter++;
			
			ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator iterator = list.iterator();
			while(iterator.hasNext())
			{
				EntityPlayer player = (EntityPlayer) iterator.next();
				WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);
				BlockPos ppos = player.playerLocation;
				
				//potion effect
				if(Darkness.darkLists.isPlayerInTowerRadius(player)==false){
					if(Darkness.darkLists.isGhost(player)) {
						player.removePotionEffect(16);
						player.removePotionEffect(23);
						player.removePotionEffect(1);
						player.removePotionEffect(8);
					}else if(player.getHeldItem() != null
						&& player.getHeldItem().getItem() instanceof LightOrb){
						player.addPotionEffect(new PotionEffect(2, DARKNESS_CHECK_RATE, 2, false, false));
					}else{
						player.addPotionEffect(new PotionEffect(2, DARKNESS_CHECK_RATE, 1, false, false));

					}
				}else {//in light
					if (Darkness.darkLists.isGhost(player)) {
						player.addPotionEffect(new PotionEffect(16, DARKNESS_CHECK_RATE, 0, false, false));
						player.addPotionEffect(new PotionEffect(23, DARKNESS_CHECK_RATE, 0, false, false));
						player.addPotionEffect(new PotionEffect(1, DARKNESS_CHECK_RATE, 0, false, false));
						player.addPotionEffect(new PotionEffect(8, DARKNESS_CHECK_RATE, 1, false, false));//jump
					} else {
						player.removePotionEffect(2);
					}
				}
				//update held orb list (doesn't really belong here...)
				ItemStack stack = player.getHeldItem();
				if(stack !=null 
						&& stack.getItem() instanceof LightOrb==false
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)){
					Darkness.darkLists.removePlayerWithOrb(player);
				}else if(stack==null
						&& Darkness.darkLists.getPlayersWithOrb().contains(player)){
					Darkness.darkLists.removePlayerWithOrb(player);
				}

				//dark player invisible
				if(Darkness.darkLists.isGhost(player)){
					player.setInvisible(true);
					player.addPotionEffect(new PotionEffect(14, DARKNESS_CHECK_RATE, 0, false, false));
				}

			}
		}
	}




	@SubscribeEvent
	public void deadRespawn(PlayerRespawnEvent e){
		if(e.player.worldObj.isRemote==true)return;
		Darkness.darkLists.addDarkPlayer(e.player);
		e.player.setInvisible(true);
		DPlayer.nbtSetGhost(e.player, true);
		BlockPos pos = e.player.getPosition();
		int range = 10;
		List mobs = e.player.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.fromBounds(pos.getX()-range, pos.getY()-range, pos.getZ()-range, pos.getX()+range, pos.getY()+range,pos.getZ()+range));
		{
			Iterator it = mobs.iterator();
			while (it.hasNext()) {
				EntityMob mob = (EntityMob) it.next();
				mob.setAttackTarget(null);
				mob.setRevengeTarget(null);
			}
		}
	}

	@SubscribeEvent
	public void playerLogIn(PlayerLoggedInEvent e){
		if(DPlayer.isGhost(e.player)){
			Darkness.darkLists.addDarkPlayer(e.player);
		}
	}
}

