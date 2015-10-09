package com.arzeyt.darkness.effectObject;

import java.util.List;

import com.arzeyt.darkness.Darkness;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EffectItem extends Item {

	private final String itemName = "effectItem";
	
	public EffectItem(){
		GameRegistry.registerItem(this, itemName);
		setUnlocalizedName(Darkness.MODID+"_"+itemName);
		setCreativeTab(Darkness.darknessTab);
		
	}
	
	public String getName(){
		return itemName;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		
		if(!playerIn.isSneaking()){
			if(stack.getTagCompound()==null){
				stack.setTagCompound(new NBTTagCompound());
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("effectID", 0);
				stack.getTagCompound().setTag("darkness", nbt);
				stack.setStackDisplayName(EnumChatFormatting.AQUA + "effectItem");
			}
			//increment by 1 on every use
			else if(stack.hasTagCompound()){
				NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("darkness");
				System.out.println("nbt tag name ="+nbt.toString());
				nbt.setInteger("effectID", nbt.getInteger("effectID")+1);				
			}
		}
		
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn) {
		
		if(playerIn.isSneaking()){
			if(itemStackIn.getTagCompound() != null){
				itemStackIn.getTagCompound().removeTag("darkness");
				itemStackIn.clearCustomName();
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn,
			List tooltip, boolean advanced) {
	
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().hasKey("darkness")){
					NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("darkness");
					tooltip.add("effectID: "+nbt.getInteger("effectID"));
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
}
