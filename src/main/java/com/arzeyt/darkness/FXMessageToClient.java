package com.arzeyt.darkness;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class FXMessageToClient implements IMessage{

	private int x, y, z;
	private boolean messageValid;
	private int effectID;
	
	public FXMessageToClient(int effectID, int x, int y, int z) {
		this.effectID=effectID;
		this.x=x;
		this.y=y;
		this.z=z;
		messageValid=true;
		
	}
	
	public FXMessageToClient() {
		messageValid=false;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			effectID=buf.readInt();
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
		buf.writeInt(effectID);
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

	public int getEffectID(){
		return effectID;
	}
}
