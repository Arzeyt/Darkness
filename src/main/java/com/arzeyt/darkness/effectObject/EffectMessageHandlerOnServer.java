package com.arzeyt.darkness.effectObject;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EffectMessageHandlerOnServer implements IMessageHandler<EffectMessageToServer, IMessage>{

	@Override
	public IMessage onMessage(final EffectMessageToServer message, MessageContext ctx) {
		if(ctx.side != Side.SERVER){
			System.err.println("effect message to server recieved on wrong side" + ctx.side);
			return null;
		}
		if(message.isMessageIsValid()==false){
			System.err.println("effect message to server is invalid");
			return null;
		}
		
		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().playerEntity;
		    if (sendingPlayer == null) {
		      System.err.println("EntityPlayerMP was null when EffectMessageToServer was received");
		      return null;
		    }
		    
	    final WorldServer playerWorldServer = sendingPlayer.getServerForPlayer();
	    playerWorldServer.addScheduledTask(new Runnable() {
		      public void run() {
		        processMessage(message, sendingPlayer);
		      }
		    }
	    );
		return null;
	}

	protected void processMessage(EffectMessageToServer message,
			EntityPlayerMP sendingPlayer) {
		//put code here
	}

}
