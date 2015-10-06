package com.arzeyt.darkness;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectTileEntity extends TileEntity implements IUpdatePlayerListBox{

	int effectID;
	int particleCounter = 100;
	
	public EffectTileEntity(){
		
	}
	
	public void addEffectEntry(ItemStack effectItem){
		
		NBTTagCompound nbt = (NBTTagCompound) effectItem.getTagCompound().getTag("darkness");
		int i = nbt.getInteger("effectID");
		this.effectID=i;
		
			
	}

	@Override
	public void update() {
		if(particleCounter>0){
			this.particleCounter--;
			this.getWorld().spawnParticle(EnumParticleTypes.CLOUD, pos.getX()+.5, pos.getY(), pos.getZ()+.5, 0.0D, 0.0D, 0.0D);
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
}
