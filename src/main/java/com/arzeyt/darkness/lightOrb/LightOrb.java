package com.arzeyt.darkness.lightOrb;

import java.util.List;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;
import com.arzeyt.darkness.towerObject.TowerBlock;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
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
		this.setMaxDamage(100);
	}
	
	public String getName(){
		return itemName;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {
			
		if(stack.stackSize==0){
			EntityPlayer p = (EntityPlayer)entityIn;
			p.inventory.setInventorySlotContents(itemSlot, null);
		}
		int power = getPowerFromNBT(stack);
		if(getPowerFromNBT(stack)!=0){
			this.setDamage(stack, 100-power);
		}
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
					
					Reference r = Darkness.reference;
					int power = nbt.getInteger(r.POWER);
					
					if(power<10){
						stack.setStackDisplayName(EnumChatFormatting.DARK_RED+"Dying Light Orb");
					}else if(power<25){
						stack.setStackDisplayName(EnumChatFormatting.RED+"Faint Light Orb");
					}else if(power<50){
						stack.setStackDisplayName(EnumChatFormatting.GRAY+"Diminished Light Orb");
					}else if(power<=100){
						stack.setStackDisplayName(EnumChatFormatting.GOLD+"Light Orb");
					}
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
		int power = 0;
		if(stack.hasTagCompound()){
			power = stack.getTagCompound().getCompoundTag("darkness").getInteger(Reference.POWER);
		}
		return power;
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
		
		//debug info CLIENT ONLY
		Reference r = new Reference();
		NBTTagCompound nbt = (NBTTagCompound) itemStackIn.getTagCompound().getCompoundTag("darkness");
		System.out.println("------------------------------------------------------------------");
		System.out.println("ID: "+nbt.getInteger(r.ID)+" Power: "+nbt.getInteger(r.POWER)+" DissipationP: "+nbt.getInteger(r.DISSIPATION_PERCENT));
		System.out.println("orbs in list (lightOrb): "+Darkness.darkLists.getLightOrbs().size());
		
		System.out.println("------------------------------------------------------------------");

		
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		
		
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
	}
	
}
