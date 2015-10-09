package com.arzeyt.darkness.effectObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EffectMessageHandlerOnClient implements IMessageHandler<EffectMessageToClient, IMessage>{


	@Override
	public IMessage onMessage(final EffectMessageToClient message, MessageContext ctx) {
		if(ctx.side!=Side.CLIENT){
			System.err.println("EffectMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("EffectMessageToClient is not valid");
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
			EffectMessageToClient message) {

		int effectID = message.getEffectID();
		if(worldClient.getTileEntity(message.getPos()).isInvalid()==false){
			EffectTileEntity te = (EffectTileEntity) worldClient.getTileEntity(message.getPos());
			te.setEffectID(effectID);
			System.out.println("Message processed");
		}
	}

}
