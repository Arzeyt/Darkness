package com.arzeyt.darkness.towerObject;

import java.awt.image.TileObserver;
import java.util.Random;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.Reference;
import com.arzeyt.darkness.lightOrb.LightOrb;

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
		setHardness(3F);
		
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
					
					//add orb to tower, and replace tower power with orb power
					if(playerIn.getHeldItem().getItem() instanceof LightOrb){ //player is holding a light orb
						ItemStack orb = playerIn.getHeldItem();
						if(orb.getTagCompound().hasKey("darkness")){ 
							NBTTagCompound nbt = orb.getTagCompound().getCompoundTag("darkness");
							int orbPower = nbt.getInteger(Reference.POWER);
							te.setPower(orbPower);							
							orb.stackSize--;
							Darkness.darkLists.removeLightOrb(orb);
							worldIn.playSoundAtEntity(playerIn, "darkness:bell", 1.0F, 1.1F);
							System.out.println("set tower power to: "+te.getPower());
						}else{ //this should never happen
							System.out.println("no data in orb");
						}
					}else{
						//player is holding something else
					}
				}else{//player is holding nothing
					System.out.println("power = "+te.getPower()+" time is: "+worldIn.getWorldTime());
					te.takeOrb(playerIn);
					Random rand = new Random();
					worldIn.playSoundAtEntity(playerIn, "darkness:bell", 1.0F, 0.5F+rand.nextFloat());				}
			}
			
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
		if(Darkness.clientLists.getPoweredTowers().contains((TowerTileEntity)worldIn.getTileEntity(pos))){
			Darkness.clientLists.removePoweredTower((TowerTileEntity) worldIn.getTileEntity(pos));
		}
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}
	
	
}
