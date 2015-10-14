package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

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
			System.out.println("removed orb from light orbs, added orb detonation, and set message to client");
			
		}
	}
}
