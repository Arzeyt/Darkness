package com.arzeyt.darkness;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.towerObject.TowerBlock;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

public class DarkEventHandler {

	@SubscribeEvent
	public void addOrbsFromInventories(EntityJoinWorldEvent e){
		if(e.entity instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entity;
			for(ItemStack stack : p.inventory.mainInventory){
				if(stack!=null 
						&& stack.hasTagCompound()
						&& stack.getItem() instanceof LightOrb){
					Darkness.darkLists.addLightOrb(stack);
					System.out.println("added light orb to darkList");
				}
			}
		}
	}
	@SubscribeEvent
	public void onBlockBreak(BreakEvent e){
		if(e.world.getTileEntity(e.pos) instanceof TowerTileEntity){
			TowerTileEntity te = (TowerTileEntity) e.world.getTileEntity(e.pos);
			if(Darkness.darkLists.getPoweredTowers().contains(te)){
				Darkness.darkLists.removePoweredTower(te);
				System.out.println("removed tower");
			}
		}
	}
	
	@SubscribeEvent
	public void onOrbDrop(ItemTossEvent e){
		if(e.entityItem.getEntityItem().getItem() instanceof LightOrb){
			Darkness.darkLists.removePlayerWithOrb(e.player);
			e.entityItem.lifespan=20*1;
		}
	}
	
	@SubscribeEvent
	public void onOrbDespawn(ItemExpireEvent e){
		if(e.entityItem.getEntityItem().getItem() instanceof LightOrb){
			ItemStack orb = e.entityItem.getEntityItem();
			BlockPos pos = e.entity.getPosition();
			
			Darkness.darkLists.removeLightOrb(orb);
			Darkness.darkLists.addOrbDetonationDefault(pos);
			Darkness.simpleNetworkWrapper.sendToDimension(new DetonationMessageToClient(true, pos.getX(), pos.getY(), pos.getZ()), e.entityItem.dimension);
			e.entity.worldObj.playSoundAtEntity(e.entity, "darkness:bellLong", 1.0F, 1.0F);

			
			//throw mobs and set them on fire
			World w = e.entity.worldObj;
			Reference r = new Reference();
			List mobs = w.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.fromBounds(pos.getX()-r.ORB_DETONATION_RAIDUS, pos.getY()-r.ORB_DETONATION_RAIDUS, pos.getZ()-r.ORB_DETONATION_RAIDUS, pos.getX()+r.ORB_DETONATION_RAIDUS, pos.getY()+r.ORB_DETONATION_RAIDUS, pos.getZ()+r.ORB_DETONATION_RAIDUS));
			Iterator it = mobs.iterator();
			while(it.hasNext()){
				EntityMob mob = (EntityMob) it.next();
				mob.setVelocity(0.0D, 1.5D, 0.0D);
				mob.setFire(10);
				mob.attackEntityFrom(DamageSource.onFire, 10);
			}
		}
	}
	
	//used this instead of light orb class because it wouldn't let me decrement the item stack.
	@SubscribeEvent
	public void onOrbUse(PlayerInteractEvent e){
		if(e.action.RIGHT_CLICK_BLOCK != null 
				&& e.world.isRemote==false){
			World w = e.entity.worldObj;
			BlockPos pos = e.pos;
			ItemStack stack = e.entityPlayer.getHeldItem();
			System.out.println("--------------------------------------------------------------------------");
			
			if(w.isRemote==false
					&& stack!= null 
					&& stack.getItem() instanceof LightOrb){
				//debug
				System.out.println("Towers: "+Darkness.darkLists.getPoweredTowers().size());
				for(TowerTileEntity t : Darkness.darkLists.getPoweredTowers()){
					System.out.println("tower pos: "+t.getPos());
				}
				System.out.println("--------------------------------------------------------------------------");
				
				int distance = Darkness.darkLists.getDistanceToNearestTower(w.provider.getDimensionId(), e.pos);
				System.out.println("distance: "+distance+"  BlockPos: "+pos.toString());
				if(Darkness.darkLists.getDistanceToNearestTower(w.provider.getDimensionId(), pos)>(Reference.TOWER_RADIUS*2)){
					if(w.getChunkFromBlockCoords(pos).getBlock(pos) instanceof TowerBlock){
						//handled in towerblock class
					}else if(w.getChunkFromBlockCoords(pos).getBlock(pos.getX(),pos.getY()+1,pos.getZ()) instanceof BlockAir
							&& w.getChunkFromBlockCoords(pos).getBlock(pos.getX(),pos.getY()+2,pos.getZ()) instanceof BlockAir){
						IBlockState state = Darkness.towerBlock.getDefaultState();
						w.setBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()), state);
						Darkness.darkLists.removeLightOrb(stack);
						stack.stackSize--;
						w.playSoundAtEntity(e.entityPlayer, "darkness:bell", 1.0F, 0.9F);
					}
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onEntityDamage(LivingAttackEvent e){
		if(e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.source.getEntity();
			if(e.entityLiving instanceof EntityMob){
				EntityMob mob = (EntityMob) e.entityLiving;
				//mob darkness check
				if(inDarkness(mob.worldObj, mob.getPosition())){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_VANISH, mob.getPosition().getX(), mob.getPosition().getY(), mob.getPosition().getZ()));
					e.setCanceled(true);
				}
			}else if(e.entityLiving instanceof EntityAnimal){
				EntityAnimal ani = (EntityAnimal) e.entityLiving;
				
				if(inDarkness(ani.worldObj, ani.getPosition())){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_VANISH, ani.getPosition().getX(), ani.getPosition().getY(), ani.getPosition().getZ()));
					teleportRandomly(ani, 15);
					e.setCanceled(true);
				}
			}
		}
	}
	
	public boolean inDarkness(World w, BlockPos pos){
		//players with orb
		if(Darkness.darkLists.getPlayersWithOrb().isEmpty()==false
				&& Darkness.darkLists.getDistanceToNearestPlayerWithOrb(w,pos) <=Reference.HELD_ORB_RADIUS){
			return false;
		}
		//orb detonations
		else if(Darkness.darkLists.getOrbDetonations().isEmpty()==false
				&& Darkness.darkLists.getDistanceToNearestOrbDetonation(w,pos)<=Reference.ORB_DETONATION_RAIDUS){
			return false;
			
		}
		//tower
		else if(Darkness.darkLists.getPoweredTowers().isEmpty()==false
				&& Darkness.darkLists.getDistanceToNearestTower(w.provider.getDimensionId(), pos) <= Reference.TOWER_RADIUS){
			return false;
		}
		return true;
		
	}
	
	public void teleportRandomly(EntityLiving e, int range){
		BlockPos pos = getRandomGroundPos(e.worldObj, e.getPosition(), range);
		e.setPosition(pos.getX(), pos.getY(), pos.getZ());
		System.out.println("teleport to: "+pos.toString());
		
	}
	
	/**
	 * 
	 * @param w world
	 * @param p start position
	 * @param range
	 * @return a random ground block in the range nearest the original y position
	 * @Warning can result in infinite loop if there are no ground blocks in range!
	 */
	public BlockPos getRandomGroundPos(World w, BlockPos p, int range){
		Random rand = new Random();
		int x = (rand.nextInt(range*2)-range)+p.getX();
		int y = p.getY();
		int z = (rand.nextInt(range*2)-range)+p.getZ();
		BlockPos mpos = new BlockPos(x,y,z);
		mpos=findGroundY(w, mpos);
		
		while(mpos==null){
			x = (rand.nextInt(range*2)-range)+p.getX();
			y = p.getY();
			z = (rand.nextInt(range*2)-range)+p.getZ();
			BlockPos pos = new BlockPos(x,y,z);
			mpos=findGroundY(w, pos);
		}
		return mpos;
	}
	/**
	 * 
	 * @param w
	 * @param p
	 * @return null if no ground can be found
	 */
	public BlockPos findGroundY(World w, BlockPos p){
		HashSet<Integer> grounds = new HashSet<Integer>();
		for(int i = -128; i <128; i++){
			BlockPos pos=new BlockPos(p.getX(), i, p.getZ());
			if(w.getChunkFromBlockCoords(pos).getBlock(pos) instanceof BlockAir ==false){
				BlockPos posUp1 = new BlockPos(pos.getX(), pos.getY()+1,pos.getZ());	
				if(w.getChunkFromBlockCoords(posUp1).getBlock(posUp1) instanceof BlockAir){
					BlockPos posUp2 = new BlockPos(pos.getX(), posUp1.getY()+1, pos.getZ());
					if(w.getChunkFromBlockCoords(posUp2).getBlock(posUp2) instanceof BlockAir){
						grounds.add(i);
					}
				}
			}
		}
	
		if(grounds.isEmpty()){
			return null;
		}
		
		int distance = 300;
		int closestY = 300;
		for(Integer ground : grounds){
			//calculate distances
			int dis = Math.abs(p.getY()-ground);
			if(dis<distance){
				distance=dis;
				closestY=ground;
			}
		}
		
		BlockPos groundPos = new BlockPos(p.getX(), closestY, p.getZ());
		return groundPos;
	}
}
