package com.arzeyt.darkness.lightOrb;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class OrbUpdateMessageToClient implements IMessage{

	private int orbID, power, dissipationPercent;
	private boolean messageValid;
	
	public OrbUpdateMessageToClient(int orbID, int power, int dissipationPercent) {
		this.orbID=orbID;
		this.power=power;
		this.dissipationPercent=dissipationPercent;
		messageValid=true;
		
	}
	
	public OrbUpdateMessageToClient() {
		messageValid=false;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			orbID=buf.readInt();
			power=buf.readInt();
			dissipationPercent=buf.readInt();
		}catch(IndexOutOfBoundsException e){
			System.err.println("OrbUpdateMessageToClient ioe "+e);
		}
		
		messageValid=true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!messageValid)return;
		buf.writeInt(orbID);
		buf.writeInt(power);
		buf.writeInt(dissipationPercent);
	}
	
	public boolean isMessageValid() {
		return messageValid;
	}
	
	public int getID(){
		return orbID;
	}
	
	public int getPower(){
		return power;
	}
	
	public int getDissipationPercent(){
		return dissipationPercent;
	}

}
