package com.arzeyt.darkness.towerObject;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.effectObject.EffectMessageToServer;
import com.arzeyt.darkness.effectObject.EffectTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OrbUpdateMessageHandlerOnClient implements IMessageHandler<DetonationMessageToClient, IMessage>{

	@Override
	public IMessage onMessage(final DetonationMessageToClient message, MessageContext ctx) {
		if(ctx.side!=Side.CLIENT){
			System.err.println("DetonationMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("DetonationMessageToClient is not valid");
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
			DetonationMessageToClient message) {

		System.out.println("processing detonation client side");
		BlockPos pos = message.getPos();
		Darkness.clientLists.addDetonation(pos);
		
	}

}
