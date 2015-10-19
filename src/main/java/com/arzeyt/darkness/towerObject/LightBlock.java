package com.arzeyt.darkness.towerObject;

import java.util.Random;

import com.arzeyt.darkness.Darkness;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LightBlock extends Block{
	
	private final String name = "lightBlock";

	public LightBlock() {
		super(Material.air);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(Darkness.MODID+"_"+name);
		this.setBlockBounds(0F, 0F, 0F, 0F, 0F, 0F);
		
		setLightLevel(0.5F);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		//worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}
	
	/**
	public int tickRate(World worldIn) {
		return 20;
	}
	**/
	
	@Override
	public void randomDisplayTick(World worldIn, BlockPos pos,
			IBlockState state, Random rand) {
		/**
		if(rand.nextInt(3)==1){
			worldIn.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 1.0D, 0.0D);
		}
		super.randomDisplayTick(worldIn, pos, state, rand);
		**/
	}
	

	@Override
	public boolean isTranslucent() {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos,
			IBlockState state) {
		return null;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
