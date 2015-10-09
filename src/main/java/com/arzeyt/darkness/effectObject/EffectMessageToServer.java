package com.arzeyt.darkness.effectObject;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class EffectMessageToServer implements IMessage{

	private int effectID;
	private boolean messageIsValid;
	

	public EffectMessageToServer(int effectID) {
		this.effectID=effectID;
		this.messageIsValid=true;
	}
	
	public EffectMessageToServer(){
		this.messageIsValid=false;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			effectID=buf.readInt();
		}catch(IndexOutOfBoundsException ioe){
			System.err.println("EffectMessageToServer error: " + ioe);
			return;
		}
		messageIsValid=true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!messageIsValid)return;
		
		buf.writeInt(effectID);
	}

	public int getEffectID() {
		return effectID;
	}

	public boolean isMessageIsValid() {
		return messageIsValid;
	}
	

}
