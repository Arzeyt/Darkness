package com.arzeyt.darkness.effectObject;

import com.arzeyt.darkness.Darkness;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectBlock extends Block implements ITileEntityProvider{

	private final String name = "effectBlock";
	
	public EffectBlock() {
		super(Material.ground);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(Darkness.MODID+"_"+name);
		
		setCreativeTab(Darkness.darknessTab);
		setLightLevel(0.8f);
		setBlockBounds(0f, 0f, 0f, 1f, 1.3f, 1f);
		isBlockContainer=true;
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
	
	public String getName(){
		return name;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new EffectTileEntity();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		
		ItemStack stack = playerIn.getHeldItem();
		if(stack != null){
			if(stack.getItem() instanceof EffectItem){
				if(stack.hasEffect()){
					EffectTileEntity te = (EffectTileEntity) worldIn.getTileEntity(pos);
					te.addEffectEntry(stack);
					worldIn.spawnParticle(EnumParticleTypes.CLOUD, pos.getX(), pos.getY()+1, pos.getZ(), 0.0D, 0.0D, 0.0D);
					System.out.println("id is "+te.getEffectID());
				}
			}
		}else{
			EffectTileEntity te=(EffectTileEntity) worldIn.getTileEntity(pos);
			if(te.isInvalid()==false){
				System.out.println("effectID = "+te.getEffectID());
				if(te.getEffectID()>=3){
					playerIn.setVelocity(0.0D, 2.0D, 0.0D);
				}
			}
		}
		return true;
	}
	
}
