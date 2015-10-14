package com.arzeyt.darkness.lightOrb;

import com.arzeyt.darkness.ClientLists;
import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;
import com.arzeyt.darkness.effectObject.EffectMessageToServer;
import com.arzeyt.darkness.effectObject.EffectTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OrbUpdateMessageHandlerOnClient implements IMessageHandler<OrbUpdateMessageToClient, IMessage>{

	@Override
	public IMessage onMessage(final OrbUpdateMessageToClient message, MessageContext ctx) {
		if(ctx.side!=Side.CLIENT){
			System.err.println("orbUpdateMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("orbUpdateMessageToClient is not valid");
			return null;
		}
		
		 Minecraft minecraft = Minecraft.getMinecraft();
		    final WorldClient worldClient = minecraft.theWorld;
		    minecraft.addScheduledTask(new Runnable()
		    {
		      public void run() {
		        processMessage(worldClient, message);
		      }
		    });

		    return null;
	}

	protected void processMessage(WorldClient worldClient,
			OrbUpdateMessageToClient message) {

		System.out.println("processing orb update client side");
		int id = message.getID();
		int power = message.getPower();
		int dp = message.getDissipationPercent();
		System.out.println("id: "+id+" power: "+power+" dp: "+dp);
		
		for(ItemStack stack : Minecraft.getMinecraft().thePlayer.inventory.mainInventory){
			if(stack!=null){
				if(stack.getItem() instanceof LightOrb){
					if(stack.hasTagCompound()){
						NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("darkness");
						if(nbt.getInteger(Reference.ID)==id){
							System.out.println("updating player's orb");
							nbt.setInteger(Reference.POWER, power);
							nbt.setInteger(Reference.DISSIPATION_PERCENT, dp);
						}
					}
				}
			}
		}
	}
	
	public boolean playerHasOrb(int ID){
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		for(ItemStack stack : p.getInventory()){
			if(stack.hasTagCompound()){
				NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("darkness");
				if(nbt.getInteger(Reference.ID)==ID){
					return true;
				}
			}
		}
		return false;
	}
	
	public ItemStack getPlayerInventoryOrb(int ID){
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		for(ItemStack stack : p.inventory.mainInventory){
			if(stack!=null){
				if(stack.hasTagCompound()){
					NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("darkness");
					if(nbt.getInteger(Reference.ID)==ID){
						return stack;
					}
				}
			}
		}
		return null;
	}

}
