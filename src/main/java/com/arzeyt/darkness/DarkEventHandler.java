package com.arzeyt.darkness;

import com.arzeyt.darkness.towerObject.DetonationMessageToClient;
import com.arzeyt.darkness.towerObject.LightOrb;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DarkEventHandler {

	
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
