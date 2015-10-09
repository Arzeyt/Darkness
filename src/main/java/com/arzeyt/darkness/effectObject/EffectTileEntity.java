package com.arzeyt.darkness.effectObject;

import com.arzeyt.darkness.Darkness;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectTileEntity extends TileEntity implements IUpdatePlayerListBox{

	private int effectID;
	private int particleCounter = 100;
	
	public final int SYNC_DISTANCE = 50;
	
	private int syncCounter = 0;
	private int syncRate = 20*5;
	
	/**
	 * on every variable change, make sure to increment this value.
	 */
	private int syncState = 0;
	private int syncStateOld = 0;
	
	
	public void addEffectEntry(ItemStack effectItem){
		
		NBTTagCompound nbt = (NBTTagCompound) effectItem.getTagCompound().getTag("darkness");
		int i = nbt.getInteger("effectID");
		setEffectID(i);
	}

	@Override
	public void update() {
		if(particleCounter>0){
			this.particleCounter--;
			this.getWorld().spawnParticle(EnumParticleTypes.CLOUD, pos.getX()+.5, pos.getY(), pos.getZ()+.5, 0.0D, 0.0D, 0.0D);
		}
		if(syncState==0&&worldObj.isRemote==false){
			updateClient();
			syncState=1;
			syncStateOld=1;
		}
		//updates all clients any time the sync state doesn't equal the old sync state
		if(syncState!=syncStateOld && worldObj.isRemote==false){
			updateClient();
			syncState++;
			syncStateOld=syncState;
		}
		//if there is a player within sync distance, always sync according to the sync rate
		if(worldObj.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), SYNC_DISTANCE)!=null 
				&& worldObj.isRemote==false
				&& syncCounter%syncRate==0)
		{
			updateClient();
		}
		/**if(syncCounter%syncRate==0 && worldObj.isRemote==false){
			Darkness.simpleNetworkWrapper.sendToAll(new EffectMessageToClient(effectID, pos.getX(), pos.getY(), pos.getZ()));
			System.out.println("sent message to client");
		}**/
		
		syncCounter++;
		
		if(syncCounter>123456){
			syncCounter=0;
		}
		
	}
	
	public void updateClient() {
		if(worldObj.isRemote==false){
			Darkness.simpleNetworkWrapper.sendToAll(new EffectMessageToClient(effectID, pos.getX(), pos.getY(), pos.getZ()));
			System.out.println("sent message to client");
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
		this.effectID = nbt.getInteger("effectID");
		System.out.println("nbt tag = "+compound.toString());
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("effectID", effectID);
		compound.setTag("darkness", nbt);
		super.writeToNBT(compound);
		System.out.println("nbt tag = "+compound.toString());
	}

	public void setEffectID(int effectID) {
		this.effectID = effectID;
		syncState++;
	}

	public int getEffectID() {
		return effectID;
	}
	
}
