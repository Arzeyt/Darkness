package com.arzeyt.darkness.towerObject;

import java.awt.image.TileObserver;

import com.arzeyt.darkness.Darkness;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TowerBlock extends Block implements ITileEntityProvider{
	
	private final String name = "towerBlock";
	
	public TowerBlock(){
		super(Material.rock);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(Darkness.MODID+"_"+name);
		
		setCreativeTab(Darkness.darknessTab);
		setLightLevel(1.0f);
		setBlockBounds(0f, 0f, 0f, 1f, 2f, 1f);
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
		return new TowerTileEntity();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		
		if(worldIn.isRemote==false){
			TowerTileEntity te = (TowerTileEntity) worldIn.getTileEntity(pos);
			
			if(!te.isInvalid()){
				if(playerIn.getHeldItem()!=null){
					String heldItemName = playerIn.getHeldItem().getItem().getUnlocalizedName();
					String lightOrbName = Darkness.lightOrb.getUnlocalizedName();
					
					if(heldItemName.equals(lightOrbName)){ //player is holding a light orb
						ItemStack orb = playerIn.getHeldItem();
						if(orb.getTagCompound().hasKey("darkness")){ //has darkness data
							NBTTagCompound nbt = orb.getTagCompound().getCompoundTag("darkness");
							int orbPower = nbt.getInteger("orbPower");
							te.setPower(orbPower);							
							playerIn.inventory.consumeInventoryItem(Item.getItemFromBlock(Darkness.lightOrbBlock));
						}else{ //this should never happen
							System.out.println("no data in orb");
						}
					}else{
						//player is holding something else
					}
				}else{//player is holding nothing
					System.out.println("power = "+te.getPower()+" time is: "+worldIn.getWorldTime());
					te.takeOrb(playerIn);
				}
			}
			worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, hitX, hitY, hitZ, 0, 1.0D, 0);
			
		}
	return true;
	}
	
	//this needs to be replaced by an actual event handler
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos,
			IBlockState state) {
		if(Darkness.darkLists.getPoweredTowers().contains((TowerTileEntity)worldIn.getTileEntity(pos))){
			Darkness.darkLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(pos));
		}
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}
	
	
}
