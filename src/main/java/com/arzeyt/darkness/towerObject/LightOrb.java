package com.arzeyt.darkness.towerObject;

import java.util.List;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LightOrb extends Item {

	private final String itemName="lightOrb";
	private final double DISSIPATION_TICKS = 16000;
	private final int UPDATE_RATE = 20;
	
	private int dissipationCounter;
	private int counter=0;

	
	public LightOrb(){
		GameRegistry.registerItem(this, itemName);
		setUnlocalizedName(Darkness.MODID+"_"+itemName);
		setCreativeTab(Darkness.darknessTab);
		setMaxStackSize(1);
		this.dissipationCounter=(int) (DISSIPATION_TICKS/UPDATE_RATE);
	}
	
	public String getName(){
		return itemName;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {
			
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List tooltip, boolean advanced) {
	
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().hasKey("darkness")){
					NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("darkness");
					tooltip.add("id: "+nbt.getInteger(Reference.ID));
					tooltip.add("power: "+nbt.getInteger(Reference.POWER));
					tooltip.add("dissipation percent: "+nbt.getInteger(Reference.DISSIPATION_PERCENT));
					stack.setStackDisplayName(EnumChatFormatting.GOLD+"lightOrb");
				}
			}
			
			super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		
		if(stack.getTagCompound() != null){
			return stack.getTagCompound().hasKey("darkness");
		}
		return false;
	}

	public int getPowerFromNBT(ItemStack stack){
		return stack.getTagCompound().getCompoundTag("darkness").getInteger("power");
	}
	
	/**
	 * Overrides all nbt info! use with caution
	 * @param stack - a lightOrb item stack (no checks to ensure this)
	 * @param power
	 */
	public void setPowerNBT(ItemStack stack, int power){
		if(stack.hasTagCompound() && stack.getTagCompound().getTag("darkness")!=null){
			System.out.println("darkness tag exists");
			NBTTagCompound compound = stack.getTagCompound();
			NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
			nbt.setInteger("power", power);
			compound.setTag("darkness", nbt);
			stack.setTagCompound(compound);
			
		}
		
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("power", power);
		compound.setTag("darkness", nbt);
		stack.setTagCompound(compound);
	}	
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn) {
		Reference r = new Reference();
		NBTTagCompound nbt = (NBTTagCompound) itemStackIn.getTagCompound().getCompoundTag("darkness");
		System.out.println("ID: "+nbt.getInteger(r.ID)+" Power: "+nbt.getInteger(r.POWER)+" DissipationP: "+nbt.getInteger(r.DISSIPATION_PERCENT));
		System.out.println("orbs in list (lightOrb): "+Darkness.darkLists.getLightOrbs().size());
		
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
}
