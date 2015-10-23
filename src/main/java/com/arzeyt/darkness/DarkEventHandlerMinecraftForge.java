package com.arzeyt.darkness;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.towerObject.TowerBlock;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import static com.arzeyt.darkness.MobSpawnerData.*;
import static net.minecraftforge.fml.common.eventhandler.Event.*;

public class DarkEventHandlerMinecraftForge {

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
	public void onTowerBreak(BreakEvent e){
		if(e.world.isRemote)return;
		if(e.world.getTileEntity(e.pos) instanceof TowerTileEntity){
			TowerTileEntity te = (TowerTileEntity) e.world.getTileEntity(e.pos);
			if(Darkness.darkLists.towerExists(te)){
				Darkness.darkLists.removePoweredTower(te);
				System.out.println("removed tower");
			}
		}
	}
	
	@SubscribeEvent
	public void onDarkBlockBreak(BreakEvent e){
		if(e.world.isRemote) return;
		EntityPlayer p = e.getPlayer();
		BlockPos pos = e.pos;
		if(Darkness.darkLists.inDarkness(e.world, e.pos)){
			e.setCanceled(true);
			e.world.playSoundAtEntity(p, "darkness:whooshPuff", 1.2F, 1.0F);
			Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_BLOCK, pos.getX(), pos.getY(), pos.getZ()));

		}
	}
	
	@SubscribeEvent
	public void onBlockPlaceInDarkness(PlayerInteractEvent e){
		if(e.entity.worldObj.isRemote)return;
		if(Darkness.darkLists.inDarkness(e.world, e.pos)){
			if(e.entityPlayer.inventory.getCurrentItem()!=null){
				ItemStack stack = e.entityPlayer.inventory.getCurrentItem();
				//item is block
				if(Block.getBlockFromItem(stack.getItem())!=null){
					e.useItem= Result.DENY;
					Darkness.simpleNetworkWrapper.sendTo(new FXMessageToClient(Reference.FX_BLOCK, e.pos.getX(),e.pos.getY()+1,e.pos.getZ()),(EntityPlayerMP) e.entityPlayer);
					e.world.playSoundAtEntity(e.entityPlayer, "darkness:whooshPuff", 1.2F, 1.0F);
				}
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
	public void onOrbDespawnDetonate(ItemExpireEvent e){
		if(e.entityItem.getEntityItem().getItem() instanceof LightOrb){
			ItemStack orb = e.entityItem.getEntityItem();
			BlockPos pos = e.entity.getPosition();
			
			Darkness.darkLists.removeLightOrb(orb);
			Darkness.darkLists.addNewOrbDetonation(e.entity.worldObj, pos);
			Darkness.simpleNetworkWrapper.sendToDimension(new DetonationMessageToClient(true, pos.getX(), pos.getY(), pos.getZ()), e.entityItem.dimension);
			e.entity.worldObj.playSoundAtEntity(e.entity, "darkness:sustainedBell", 1.0F, 1.0F);


			//throw mobs and set them on fire. constant fire is handled in DarkEventHandlerFML
			Random rand = new Random();
			World w = e.entity.worldObj;
			Reference r = new Reference();
			List mobs = w.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.fromBounds(pos.getX()-r.ORB_DETONATION_RAIDUS, pos.getY()-r.ORB_DETONATION_RAIDUS, pos.getZ()-r.ORB_DETONATION_RAIDUS, pos.getX()+r.ORB_DETONATION_RAIDUS, pos.getY()+r.ORB_DETONATION_RAIDUS, pos.getZ()+r.ORB_DETONATION_RAIDUS));
			Iterator it = mobs.iterator();
			while(it.hasNext()){
				EntityMob mob = (EntityMob) it.next();
				mob.setVelocity(-0.5D+rand.nextDouble(), 1.5D, -0.5D+rand.nextDouble());
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
			
			if(w.isRemote==false
					&& stack!= null 
					&& stack.getItem() instanceof LightOrb){
				//debug
				System.out.println("Towers: "+Darkness.darkLists.getPoweredTowers().size());
				for(TowerTileEntity t : Darkness.darkLists.getPoweredTowers()){
					System.out.println("tower pos: "+t.getPos());
				}
				
				
				if(Darkness.darkLists.isPosInTowerRadiusX2minus1(w, pos)==false){
					if(w.getChunkFromBlockCoords(pos).getBlock(pos) instanceof TowerBlock){
						//handled in towerblock class
					}else if(w.getChunkFromBlockCoords(pos).getBlock(pos.getX(),pos.getY()+1,pos.getZ()) instanceof BlockAir
							&& w.getChunkFromBlockCoords(pos).getBlock(pos.getX(),pos.getY()+2,pos.getZ()) instanceof BlockAir){
						IBlockState state = Darkness.towerBlock.getDefaultState();
						w.setBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()), state);
						Darkness.darkLists.removeLightOrb(stack);
						stack.stackSize--;
						Random rand = new Random();
						w.playSoundAtEntity(e.entityPlayer, "darkness:bell", 1.0F, 0.5F+rand.nextFloat());
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onMobDamage(LivingAttackEvent e){
		//player attack
		if(e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.source.getEntity();
			//invincimob
			if(e.entityLiving instanceof EntityMob){
				EntityMob mob = (EntityMob) e.entityLiving;
				//mob darkness check
				if(Darkness.darkLists.inDarkness(mob.worldObj, mob.getPosition())){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_BLOCK, mob.getPosition().getX(), mob.getPosition().getY(), mob.getPosition().getZ()));
					mob.worldObj.playSoundAtEntity(mob, "darkness:whoosh", 1.0F, 1.0F);
					e.setCanceled(true);
				}
			//teleport animal
			}else if(e.entityLiving instanceof EntityAnimal){
				EntityAnimal ani = (EntityAnimal) e.entityLiving;
				
				if(Darkness.darkLists.inDarkness(ani.worldObj, ani.getPosition())){
					Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_VANISH, ani.getPosition().getX(), ani.getPosition().getY(), ani.getPosition().getZ()));
					EffectHelper.teleportRandomly(ani, Reference.EVASION_RADIUS);
					ani.worldObj.playSoundAtEntity(ani, "darkness:teleWhoosh", 1.5F, 1.0F);
					e.setCanceled(true);
				}
			}
			//player is attacked
		}else if(e.entityLiving instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			//all mobs
			if(e.source.getEntity() instanceof EntityMob){
				p.attackEntityFrom(DamageSource.wither, e.ammount/2);
			}
			//spiders
			if(e.source.getEntity() instanceof EntitySpider){
				Random rand = new Random();
				int value = rand.nextInt(2);
				if(value==0)
					//poison
					p.addPotionEffect(new PotionEffect(19, 20*5, 0, false, true));
				else if(value==1){
					//stronger poison
					p.addPotionEffect(new PotionEffect(19, 20*5, 1, false, true));
					//nausea
				}else if(value==2){
					p.addPotionEffect(new PotionEffect(9, 20*15, 0, false, true));
				}
			}
		}else if (e.entityLiving instanceof EntityMob) {
		}
	}

	@SubscribeEvent
	public void meow(PlayerInteractEvent e){
		Random rand = new Random();
		//e.world.playSoundAtEntity(e.entityPlayer, "darkness:meow", 1.0F, 0.5F+rand.nextFloat());
	}

	@SubscribeEvent
	public void playerDeath(LivingDeathEvent e){
		if(e.entityLiving.worldObj.isRemote)return;
		if(e.entityLiving instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			p.setSpawnPoint(p.getPosition(),true);
			Darkness.darkLists.addDarkPlayer(p);
		}
	}

	//cancels interactions with everything. Also resurrects on tower absorb
	@SubscribeEvent
	public void deadPlayerInteract(PlayerInteractEvent e){
		System.out.println("interact = "+e.action.toString());
		if(e.entityPlayer.worldObj.isRemote)return;
		EntityPlayer p = e.entityPlayer;
		if(Darkness.darkLists.isGhost(p)) {
			if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
				if (p.getEntityWorld().getTileEntity(e.pos) != null
						&& p.getEntityWorld().getTileEntity(e.pos) instanceof TowerTileEntity) {
					TowerTileEntity t = (TowerTileEntity) p.getEntityWorld().getTileEntity(e.pos);
					if (t.getPower() > 99) {
						t.setPower(1);
						p.setInvisible(false);
						DPlayer.nbtSetGhost(p,false);
						Darkness.darkLists.removeDarkPlayer(p);
						Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(Reference.FX_OUTWARDS_SPARKLE, p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ()));
						p.worldObj.playSoundAtEntity(p,"darkness:bell",1.0F,1.2F);
					}
				}
			}else if(e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)){
				System.out.println("right clicked air");
				if(p.getFoodStats().getFoodLevel()>=6){
					System.out.println("blink passed");
					EffectHelper.blink(p);
					p.getFoodStats().setFoodLevel(p.getFoodStats().getFoodLevel()-6);
				}
			}
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void deadPlayerAttack(LivingHurtEvent e){
		//player is attacking
		if(e.source.getEntity() instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.source.getEntity();
			if(Darkness.darkLists.isGhost(p)){//ghost
				if(Darkness.darkLists.isPlayerInDarkness(p)) {//in darkness
					EntityLiving entity = (EntityLiving) e.entityLiving;
					entity.attackEntityFrom(DamageSource.magic, 0.5F);
					e.setCanceled(true);

				}else{//in light
					if(e.entityLiving instanceof EntityPlayer==false){//not a player
						e.entityLiving.attackEntityFrom(DamageSource.magic, 0.5F);
						e.entityLiving.setFire(2);
						e.setCanceled(true);
					}else{//a player
						e.entityLiving.attackEntityFrom(DamageSource.magic, 0.5F);
						e.setCanceled(true);
					}
				}
			}

			//something attacking player
		}else if(e.entityLiving instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer) e.entityLiving;
			if(Darkness.darkLists.isGhost(p)){
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void deadPlayerPickup(EntityItemPickupEvent e){
		if(Darkness.darkLists.isGhost(e.entityPlayer)){
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void deadTargetEvent(LivingSetAttackTargetEvent e){
		if(e.entityLiving.worldObj.isRemote==true)return;
		if(e.target instanceof  EntityPlayer){
			if (Darkness.darkLists.isGhost((EntityPlayer) e.target)){
				e.entityLiving.setRevengeTarget(null);
			}
		}
	}

	@SubscribeEvent
	public void darkMobDeath(LivingDeathEvent e){
		if(e.entity.getEntityData().hasKey("darkness")){
			mobs.remove(e.entityLiving);
		}
	}

}
