package com.arzeyt.darkness.towerObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TowerMessageToClient implements IMessage{

	private int x, y, z;
	private int power;
	private boolean messageValid;
	
	public TowerMessageToClient(int orbPower, int x, int y, int z) {
		this.power = orbPower;
		this.x=x;
		this.y=y;
		this.z=z;
		messageValid=true;
		
	}
	
	public TowerMessageToClient() {
		messageValid=false;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			power=buf.readInt();
			x=buf.readInt();
			y=buf.readInt();
			z=buf.readInt();
		}catch(IndexOutOfBoundsException e){
			System.err.println("EffectMessageToClient ioe "+e);
		}
		
		messageValid=true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(!messageValid)return;
		buf.writeInt(power);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	public boolean isPowered() {
		return power > 0;
	}
	
	public boolean isMessageValid() {
		return messageValid;
	}
	
	public BlockPos getPos(){
		return new BlockPos(x, y, z);
	}

	public int power() {
		return power;
	}

}
