package com.arzeyt.darkness.towerObject;

import com.arzeyt.darkness.effectObject.EffectMessageToServer;
import com.arzeyt.darkness.effectObject.EffectTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TowerMessageHandlerOnClient implements IMessageHandler<TowerMessageToClient, IMessage>{

	@Override
	public IMessage onMessage(final TowerMessageToClient message, MessageContext ctx) {
		if(ctx.side!=Side.CLIENT){
			System.err.println("TowerMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("TowerMessageToClient is not valid");
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
			TowerMessageToClient message) {

		int orbPower = message.getOrbPower();
		if(worldClient.getTileEntity(message.getPos()).isInvalid()==false){
			TowerTileEntity te = (TowerTileEntity) worldClient.getTileEntity(message.getPos());
			te.setPower(orbPower);
			System.out.println("Message processed. Set power to: "+orbPower+" and the tile entity now has: "+te.getPower());
		}
	}

}
