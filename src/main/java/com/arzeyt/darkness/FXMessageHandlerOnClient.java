package com.arzeyt.darkness;

import java.util.Random;

import com.arzeyt.darkness.effectObject.EffectMessageToServer;
import com.arzeyt.darkness.effectObject.EffectTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class FXMessageHandlerOnClient implements IMessageHandler<FXMessageToClient, IMessage>{

	@Override
	public IMessage onMessage(final FXMessageToClient message, MessageContext ctx) {
		if(ctx.side!=Side.CLIENT){
			System.err.println("FXMessageToClient sent to wrong side!");
			return null;
		}
		if(message.isMessageValid()==false){
			System.err.println("FXMessageToClient is not valid");
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
			FXMessageToClient message) {

		System.out.println("processing FXMessageToClient client side");
		BlockPos pos = message.getPos();
		switch(message.getEffectID()){
			case Reference.FX_VANISH:
				vanishSmoke(worldClient, pos);
				break;
			case Reference.FX_BLOCK:
				blockSmoke(worldClient, pos);
				break;
			case Reference.FX_OUTWARDS_SPARKLE:
				outwardsSparkle(worldClient, pos);

		}
		
	}
	
	public void vanishSmoke(WorldClient w, BlockPos pos){
		for(float x=0; x<=2 ; x=x+0.5F){
			for(float y=0; y<=2 ; y=y+0.5F){
				for(float z=0; z<=2 ; z=z+0.5F){
					w.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(), -1.0D+x, -1.0D+y, -1.0D+z);
				}
			}
		}

	}
	
	public void blockSmoke(WorldClient w, BlockPos pos){
		Random rand = new Random();
		for(int i=0; i<10; i++){
			w.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(), -0.5D+rand.nextFloat(),-0.5D+rand.nextFloat(), -0.5D+rand.nextFloat());
		}
	}

	public void outwardsSparkle(WorldClient w, BlockPos pos){
		for(float x=0; x<=2 ; x=x+0.5F){
			for(float y=0; y<=2 ; y=y+0.5F){
				for(float z=0; z<=2 ; z=z+0.5F){
					w.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX(), pos.getY(), pos.getZ(), -1.0D+x, -1.0D+y, -1.0D+z);
				}
			}
		}

	}

}
