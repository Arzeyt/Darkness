package com.arzeyt.darkness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *This class should only run on the server, and only have one instance
 */
public class DarkLists {

	//for dark determination
	private HashSet<EntityPlayer> playersWithOrb = new HashSet<EntityPlayer>();
	private HashMap<BlockPos, Integer> orbDetonations = new HashMap<BlockPos, Integer>(); //position of detonation and lifetime
	private HashSet<EntityPlayer> playersInDarkness = new HashSet<EntityPlayer>();
	private HashSet<TowerTileEntity> poweredTowers = new HashSet<TowerTileEntity>();
	
	//stores all player-held light orbs
	
	private HashSet<ItemStack> lightOrbs = new HashSet<ItemStack>();
	
	public HashSet<ItemStack> getLightOrbs(){
		return lightOrbs;
	}
	
	/**
	 * @add: on new orb creation, player orb handoff
	 * @remove: on drop, dissipation, detonation
	 */
	public void addLightOrb(ItemStack lightOrb){
		if(orbExists(lightOrb))return;
		System.out.println("light orbs in list: "+getLightOrbs().size());
		lightOrbs.add(lightOrb);
	}
	
	public void removeLightOrb(ItemStack lightOrb){
		System.out.println("attempting to remove light orb");
		
		if(orbExists(lightOrb)){
			for(ItemStack listOrb : getLightOrbs()){
				if(listOrb.hasTagCompound()&&lightOrb.hasTagCompound()){
					NBTTagCompound nbtLightOrb = (NBTTagCompound) lightOrb.getTagCompound().getTag("darkness");
					NBTTagCompound nbtOrb = (NBTTagCompound) listOrb.getTagCompound().getTag("darkness");
					if(nbtLightOrb.getInteger("id")==nbtOrb.getInteger("id")){
						lightOrbs.remove(listOrb);
						System.out.println("removed orb");
						return;
					}
				}else{
					System.out.println("orb does not have NBT! AHHH");
				}
			}
		}
		System.out.println("light orb doesn't exist in list!");
	}
	public boolean orbExists(ItemStack orb1){
		if(lightOrbs.size()==0)return false;
		for(ItemStack orb2 : getLightOrbs()){
			if(orb2.hasTagCompound()&&orb1.hasTagCompound()){
				NBTTagCompound nbtLightOrb = (NBTTagCompound) orb1.getTagCompound().getTag("darkness");
				NBTTagCompound nbtOrb = (NBTTagCompound) orb2.getTagCompound().getTag("darkness");
				if(nbtLightOrb.getInteger("id")==nbtOrb.getInteger("id")){
					return true;
				}
			}else{
				System.out.println("orb does not have NBT! AHHH");
			}
		}
		return false;
	}
	

	public HashSet<EntityPlayer> getPlayersWithOrb() {
		return playersWithOrb;
	}
	
	public int getDistanceToNearestPlayerWithOrb(EntityPlayer p){
		int distance = 1000;
		for(EntityPlayer pOrb : getPlayersWithOrb()){
			BlockPos pos = pOrb.getPosition();
			int dis = (int) p.getDistance(pos.getX(), pos.getY(), pos.getZ());
			if(dis<distance){
				distance=dis;
			}
		}
		return distance;
	}
	
	public int getDistanceToNearestPlayerWithOrb(World w, BlockPos pos){
		int distance = 1000;
		for(EntityPlayer pOrb : getPlayersWithOrb()){
			if(w.provider.getDimensionId()==pOrb.worldObj.provider.getDimensionId()){
				double dis = pOrb.getDistance(pos.getX(), pos.getY(), pos.getZ());
				if(dis<distance){
					distance=(int) dis;
				}
			}
		}
		return distance;
	}
	
	public void addPlayerWithOrb(EntityPlayer p){
		playersWithOrb.add(p);
	}
	
	public void removePlayerWithOrb(EntityPlayer p){
			playersWithOrb.remove(p);
		
	}
	
	public HashMap<BlockPos, Integer> getOrbDetonations(){
		return orbDetonations;
	}
	
	public void addOrbDetonationDefault(BlockPos orbPos){
		orbDetonations.put(orbPos, Reference.DETONATION_LIFETIME);
	}
	
	public void addOrbDetonation(BlockPos orbPos, int lifetime){
		orbDetonations.put(orbPos, lifetime);
	}
	public void removeOrbDetonation(BlockPos pos){
		if(orbDetonations.containsKey(pos)){
			orbDetonations.remove(pos);
		}
	}
	
	public int getDistanceToNearestOrbDetonation(EntityPlayer p){
		int distance = 1000;
		for(BlockPos pos : getOrbDetonations().keySet()){
			int dis = (int) p.getDistance(pos.getX(), pos.getY(), pos.getZ());
			if(dis<distance){
				distance=dis;
			}
		}
		return distance;
	}
	
	public int getDistanceToNearestOrbDetonation(World w, BlockPos pos){
		int distance = 1000;
		for(BlockPos detPos : getOrbDetonations().keySet()){
			double dis = Math.sqrt(pos.distanceSq(detPos.getX(), detPos.getY(), detPos.getZ()));
			if(dis<distance){
				distance=(int) dis;
			}
		}
		return distance;
	}
	public HashSet<EntityPlayer> getPlayersInDarkness() {
		return playersInDarkness;
	}
	
	public void addPlayersInDarkness(EntityPlayer p){
		playersInDarkness.add(p);
	}
	
	public void removePlayerInDarkness(EntityPlayer p){
		if(getPlayersInDarkness().contains(p)){
			System.out.println("remove "+p.getDisplayNameString()+" from darkness list");
			playersInDarkness.remove(p);
		}
	}
	
	public HashSet<TowerTileEntity> getPoweredTowers() {
		return poweredTowers;
	}
	
	public void addPoweredTowers(TowerTileEntity t){
		if(t.getWorld().isRemote==false && towerExists(t)==false){
			poweredTowers.add(t);
			System.out.println("added powered tower");
		}
		System.out.println("tried to add powered tower");
	}
	
	public boolean towerExists(TowerTileEntity t){
		for(TowerTileEntity tow : getPoweredTowers()){
			if(tow.getPos()==t.getPos()){
				return true;
			}
		}
		return false;
	}
	public void removePoweredTower(TowerTileEntity t){
		if(getPoweredTowers().contains(t)){
			poweredTowers.remove(t);
			System.out.println("removed powered tower");
		}
		System.out.println("tried to remove powered tower");
	}
	
	public boolean isPlayerInDarkness(EntityPlayer p){
		if(getPlayersInDarkness().contains(p))return true;
		return false;
		
		/**
		if(playersInDarkness.size()>0){
			for(EntityPlayer p : getPlayersInDarkness()){
				if(p.getName()==name){
					return true;
				}
			}
		}
		return false;
		**/
	}
	
	public int getDistanceToNearestTower(EntityPlayer p){
		System.out.println("towers: "+getPoweredTowers().size());
		return getDistanceToNearestTower(p.worldObj.provider.getDimensionId(), p.getPosition());
		
	}
	
	public int getDistanceToNearestTower(int dimID, BlockPos pos){
		int distance = Integer.MAX_VALUE;
		for(TowerTileEntity t : getPoweredTowers()){
			if(dimID==t.getWorld().provider.getDimensionId()){
				BlockPos tpos = t.getPos();
				double dis = Math.hypot(pos.getX()-tpos.getX(), pos.getZ()-tpos.getZ());
				distance = (int) (dis<distance ? dis : distance);
			}
		}
		System.out.println("distance is: "+distance);
		return distance;
	}
	
	public ItemStack getActualOrbFromID(int ID){
		Iterator i = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while(i.hasNext()){
			EntityPlayerMP p = (EntityPlayerMP)i.next();
			for(ItemStack stack : p.inventory.mainInventory){
				if(stack!=null && stack.hasTagCompound() && stack.getItem() instanceof LightOrb){
					NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("darkness");
					if(nbt.getInteger(Reference.ID)==ID){
						return stack;
					}
				}
			}
		}
		return null;
	}
	
	public EntityPlayer getPlayerHoldingOrb(int ID){
		Iterator i = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while(i.hasNext()){
			EntityPlayerMP p = (EntityPlayerMP)i.next();
			for(ItemStack stack : p.inventory.mainInventory){
				if(stack!=null && stack.hasTagCompound() && stack.getItem() instanceof LightOrb){
					NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("darkness");
					if(nbt.getInteger(Reference.ID)==ID){
						return p;
					}
				}
			}
		}
		return null;
	}
	
	public void clearTowerList(){
		this.poweredTowers= new HashSet<TowerTileEntity>();
	}
}
