package com.arzeyt.darkness;

import java.util.HashSet;

import com.arzeyt.darkness.towerObject.TowerTileEntity;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import scala.collection.mutable.HashMap;

import static com.arzeyt.darkness.Reference.TOWER_RADIUS;

public class ClientLists {

	//accessed every tick to see where to render detonations. The server should modify this list via packets.
	private HashSet<BlockPos> detonations = new HashSet<BlockPos>();
	private HashSet<TowerTileEntity> poweredTowers = new HashSet<TowerTileEntity>();
	public boolean renderDarkness = false;


	
	public HashSet<BlockPos> getDetonations() {
		return detonations;
	}
	
	public void addDetonation(BlockPos pos){
		detonations.add(pos);
	}

	public void removeDetonation(BlockPos pos) {
		detonations.remove(pos);
	}


	public HashSet<TowerTileEntity> getPoweredTowers() {
		return poweredTowers;
	}

	public void addPoweredTower(TowerTileEntity t){
		if(towerExists(t)==false){
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

	public int getDistanceToNearestTower(EntityPlayer p){
		//System.out.println("towers: "+getPoweredTowers().size());
		return getDistanceToNearestTower(p.worldObj.provider.getDimensionId(), p.getPosition());

	}

	/**
	 *
	 * @param dimID
	 * @param pos
	 * @return a realistic distance value. Change to manhattan distance for performances.
	 */
	public int getDistanceToNearestTower(int dimID, BlockPos pos){
		int distance = Integer.MAX_VALUE;
		for(TowerTileEntity t : getPoweredTowers()){
			if(dimID==t.getWorld().provider.getDimensionId()){
				BlockPos tpos = t.getPos();
				int dis = (int) Math.hypot(pos.getX() - tpos.getX(), pos.getZ() - tpos.getZ());
				distance = dis<distance ? dis : distance;
			}
		}
		//System.out.println("distance is: "+distance);
		return distance;
	}

	public double getDistanceToNearestTowerDouble(int dimID, BlockPos pos){
		double distance = Integer.MAX_VALUE;
		for(TowerTileEntity t : getPoweredTowers()){
			if(dimID==t.getWorld().provider.getDimensionId()){
				BlockPos tpos = t.getPos();
				double dis =  Math.hypot(pos.getX()-tpos.getX(), pos.getZ()-tpos.getZ());
				distance = (int) (dis<distance ? dis : distance);
			}
		}
		//System.out.println("distance is: "+distance);
		return distance;
	}


	public boolean isPosInTowerRadius(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.getDimensionId()==t.getWorld().provider.getDimensionId()) {
				int xmax = t.getPos().getX() + TOWER_RADIUS;
				int xmin = t.getPos().getX() - TOWER_RADIUS;

				int zmax = t.getPos().getZ() + TOWER_RADIUS;
				int zmin = t.getPos().getZ() - TOWER_RADIUS;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}


		return false;

	}

	public boolean isPosInTowerRadiusPlus1(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.getDimensionId()==t.getWorld().provider.getDimensionId()) {
				int xmax = t.getPos().getX() + TOWER_RADIUS+1;
				int xmin = t.getPos().getX() - TOWER_RADIUS-1;

				int zmax = t.getPos().getZ() + TOWER_RADIUS+1;
				int zmin = t.getPos().getZ() - TOWER_RADIUS-1;

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}


		return false;

	}


	public boolean isPosInTowerRadiusX2minus1(World w, BlockPos pos){
		for(TowerTileEntity t : getPoweredTowers()){
			if(w.provider.getDimensionId()==t.getWorld().provider.getDimensionId()) {
				int xmax = t.getPos().getX() + (TOWER_RADIUS*2-1);
				int xmin = t.getPos().getX() - (TOWER_RADIUS*2-1);

				int zmax = t.getPos().getZ() + (TOWER_RADIUS*2-1);
				int zmin = t.getPos().getZ() - (TOWER_RADIUS*2-1);

				int px = pos.getX();
				int pz = pos.getZ();

				if (xmin < px && px < xmax) {
					if (zmin < pz && pz < zmax) {
						return true;
					}
				}
			}
		}


		return false;

	}

	public boolean isPlayerInTowerRadius(EntityPlayer p){
		return isPosInTowerRadius(p.worldObj, p.getPosition());
	}

	public void clearTowerList(){
		this.poweredTowers= new HashSet<TowerTileEntity>();
	}

}
