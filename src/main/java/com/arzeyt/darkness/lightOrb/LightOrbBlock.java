package com.arzeyt.darkness.lightOrb;

import com.arzeyt.darkness.Darkness;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LightOrbBlock extends Block {

	private final String name = "lightOrbBlock";
	
	public LightOrbBlock() {
		super(Material.glass);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(Darkness.MODID+"_"+name);
		
		setCreativeTab(Darkness.darknessTab);
		setLightLevel(0.8f);
		setBlockBounds(0.3f, 0.0f, 0.3f, 0.6f, 0.3f, 0.6f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItem()==null){
			int currentSlot = playerIn.inventory.currentItem;
			playerIn.inventory.setInventorySlotContents(currentSlot, new ItemStack(Darkness.lightOrb));
			this.breakBlock(worldIn, pos, state);
		}else{
			System.out.println("Hand must be empty");
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY,
				hitZ);
	}

	public String getName() {
		return name;
	}
}
