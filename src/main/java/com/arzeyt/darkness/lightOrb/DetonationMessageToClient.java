package com.arzeyt.darkness.lightOrb;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DetonationMessageToClient implements IMessage{

	private int x, y, z;
	private boolean messageValid, detonate;
	
	public DetonationMessageToClient(boolean detonate, int x, int y, int z) {
		this.detonate=detonate;
		this.x=x;
		this.y=y;
		this.z=z;
		messageValid=true;
		
	}
	
	public DetonationMessageToClient() {
		messageValid=false;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			detonate=buf.readBoolean();
			x=buf.readInt();
			y=buf.readInt();
			z=buf.readInt();
		}catch(IndexOutOfBoundsException e){
			System.err.println("DetonationMessageToClient ioe "+e);
		}
		
		messageValid=true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!messageValid)return;
		buf.writeBoolean(detonate);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	public boolean isMessageValid() {
		return messageValid;
	}
	
	public BlockPos getPos(){
		return new BlockPos(x, y, z);
	}

	public boolean shouldDetonate(){
		return detonate;
	}
}
