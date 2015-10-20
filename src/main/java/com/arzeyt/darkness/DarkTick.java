package com.arzeyt.darkness;

import java.util.*;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.arzeyt.darkness.lightOrb.Detonation;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;
import com.sun.xml.internal.stream.Entity;

public class DarkTick {

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
				if(Darkness.darkLists.isPlayerInDarkness(player)){
					player.addPotionEffect(new PotionEffect(2, DARKNESS_CHECK_RATE+20, 1, false, false));
				}else{
					player.removePotionEffect(2);
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


			}
		}
	}

	//probably need a more advanced mob spawner
	public int darkMobSpawn=0;
	Random rand = new Random();
	HashSet<EntityMob> mobs = new HashSet<EntityMob>();

	@SubscribeEvent
	public void darkMobSpawn(ServerTickEvent e){
		if(counter%Reference.MOB_SPAWN_RATE==0
				&& darkMobSpawn>=0){
			System.out.println("dark mob spawn: "+darkMobSpawn);
			ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator iterator = list.iterator();
			while(iterator.hasNext()) {
				EntityPlayer player = (EntityPlayer) iterator.next();
				WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);
				BlockPos ppos = player.getPosition();
				if(ppos==null)return;

				if(Darkness.darkLists.isPlayerInTowerRadius(player)==false){
					EntityZombie zombie = new EntityZombie(world);
					BlockPos zloc = EffectHelper.getRandomGroundPos(world, ppos, 10);
					if(zloc==null)return;
					zombie.setPosition(zloc.getX(), zloc.getY(), zloc.getZ());
					world.spawnEntityInWorld(zombie);
					zombie.setAttackTarget(player);
					zombie.setCurrentItemOrArmor(4, new ItemStack(Item.getByNameOrId("leather_helmet")));
					zombie.deathTime=20*20;
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_VANISH, zloc.getX(), zloc.getY(), zloc.getZ()));
					world.playSoundAtEntity(zombie, "darkness:teleWhoosh", 1.0F, 1.0F);
					darkMobSpawn--;
				}

			}
		}else if(counter%Reference.MOB_SPAWN_RATE*100==0){
			//darkMobSpawn++;
		}
	}
	//more fail
	public void playerEffectPlayerTick(PlayerTickEvent e){
		if(counter%Reference.EVASION_CHECK_RATE==0) {
			int er = Reference.EVASION_CHECK_RADIUS;
			BlockPos ppos = e.player.getPosition();
			List animals = e.player.worldObj.getEntitiesWithinAABB(EntityAnimal.class, AxisAlignedBB.fromBounds((double) ppos.getX() - er, (double) ppos.getY() - er, (double) ppos.getZ() - er, (double) ppos.getX() + er, (double) ppos.getY() + er, (double) ppos.getZ() + er));
			Iterator pit = animals.iterator();
			while (pit.hasNext()) {
				EntityAnimal ani = (EntityAnimal) pit.next();
				EffectHelper.teleportRandomly(ani, Reference.EVASION_RADIUS);
				System.out.println("teleported animal");
			}
		}
	}
}

