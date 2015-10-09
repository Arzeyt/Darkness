package com.arzeyt.darkness.towerObject;

import java.util.Random;

import com.arzeyt.darkness.Darkness;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;

public class TowerTileEntity extends TileEntity implements IUpdatePlayerListBox{

	//sync variables
	public final int SYNC_DISTANCE = 50;
	private int counter = 0;
	private int syncRate = 20*3;
	private int nearbyPlayerSyncRate = 20*200;
	private int particleProductionRate = 3;
	private final int TAKE_ORB_COOLDOWN=200; 
		private int noonLowerEnd=6000-(TAKE_ORB_COOLDOWN/2);
		private int noonHigherEnd=6000+(TAKE_ORB_COOLDOWN/2);
		private int takeOrbAtNoonCooldownCounter=0;

	/**
	 * on every variable change, make sure to increment this value.
	 */
	private int syncState = 0;
	private int syncStateOld = 0;
	
	//variables
	private boolean powered = false;
	private boolean loaded = false;
	private int orbPower = 0;
	private long minecraftTime =0;
	private boolean takingOrbAtNoon=false;

	
	
	public void update() {
		
		if(worldObj.isRemote==false){ //serverside
			//sync data
			if(counter%syncRate==0){
				//updates all clients any time the sync state doesn't equal the old sync state
				if(syncState!=syncStateOld && worldObj.isRemote==false){
					updateClient();
					syncState++;
					syncStateOld=syncState;
					System.out.println("syncing to match syncstate");
				}
			}
			//if there is a player within sync distance, always sync according to the sync rate
			if(counter%nearbyPlayerSyncRate==0
					&&worldObj.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), SYNC_DISTANCE)!=null 
					&& worldObj.isRemote==false){
				
				System.out.println("player is nearby");
				updateClient();
			}
			
			//for initial loading
			if(loaded==false){
				System.out.println("loading tower");
				if(isPowered() && Darkness.darkLists.getPoweredTowers().contains(this)==false){
					Darkness.darkLists.addPoweredTowers(this);
					loaded=true;
				}else{
					loaded=true;
				}
				updateClient();
			}
			//orbPower time cycle, limited by sync rate. Also sets server time
			if(counter%syncRate==0){	
				minecraftTime=worldObj.getWorldTime();
				long moduloTime = minecraftTime%24000;
				
				if(noonLowerEnd<moduloTime && moduloTime < noonHigherEnd
						&& takingOrbAtNoon==false){
					System.out.println("minecraft time: "+minecraftTime);
					setPower(100);
				}
			}
			//take orb cooldown increment. stop when reach 0
			if(takeOrbAtNoonCooldownCounter>0){
				takeOrbAtNoonCooldownCounter--;
			}else if(takeOrbAtNoonCooldownCounter==0){
				takingOrbAtNoon=false;
			}
			
		}
		if(worldObj.isRemote==true){//clientside
			//effect
			if(powered && counter%particleProductionRate==0){
				Random rand = new Random();
				this.getWorld().spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX()+.5, pos.getY(), pos.getZ()+.5, 0.0D, 1.0D, 0.0D);
			}
		}
		
		
		//increment counters
		minecraftTime=worldObj.getWorldTime();
		counter++;
		if(counter>123456){ //just so we dont get huge numbers
			counter=0;
		}
	}

	private void updateClient() {
		if(worldObj.isRemote==false){
			System.out.println("sending message: Power= "+getPower()+"Powered= "+powered);
			Darkness.simpleNetworkWrapper.sendToAll(new TowerMessageToClient(getPower(), pos.getX(), pos.getY(), pos.getZ()));
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
		this.powered = nbt.getBoolean("powered");
		setPower(nbt.getInteger("orbPower"));
		System.out.println("nbt tag = "+compound.toString());
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		generateCompound(compound);

		System.out.println("nbt tag = "+compound.toString());
	}
	
	public NBTTagCompound generateCompound(NBTTagCompound compound){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("powered", powered);
		nbt.setInteger("orbPower", getPower());
		
		compound.setTag("darkness", nbt);
		System.out.println("compound tag generated is: "+compound.toString());
		return compound;
	}
	
	public int getPower(){
		return orbPower;
	}
	
	/**
	 * 
	 * @param orbPower Values between 0 to 100 ONLY
	 * @Description sets tower power and adjusts all varaibles accordingly. 
	 */
	public void setPower(int orbPower){
		this.orbPower=orbPower;
		if(orbPower>0){
			this.powered=true;
			if(Darkness.darkLists.getPoweredTowers().isEmpty()){
				Darkness.darkLists.addPoweredTowers(this);
			}
			if(Darkness.darkLists.getPoweredTowers().isEmpty()==false
					&& Darkness.darkLists.getPoweredTowers().contains(this)==false){
				Darkness.darkLists.addPoweredTowers(this);
			}
		}else{
			this.powered=false;
			if(Darkness.darkLists.getPoweredTowers().isEmpty()==false || Darkness.darkLists.getPoweredTowers().contains(this)==true){
				Darkness.darkLists.removePoweredTower(this);
			}
		}
		syncState++;
	}
	
	/**
	 * 
	 * @param p player to give the orb to
	 * @return true if worked, false if didn't
	 */
	public boolean takeOrb(EntityPlayer p){
		if(noonLowerEnd<minecraftTime && minecraftTime<noonHigherEnd){
			if(takingOrbAtNoon==false){
				this.takingOrbAtNoon=true;//activate taking orb. automatically deactivated in update()
				this.takeOrbAtNoonCooldownCounter=TAKE_ORB_COOLDOWN;//reset the cooldown
				
				p.inventory.addItemStackToInventory(generateLightOrb());
				setPower(0);
				System.out.println("took orb");
				return true;
			}else{
				System.out.println("wait for cooldown in: "+takeOrbAtNoonCooldownCounter+" ticks");
				return false;
			}
		}else if(getPower()==0){
			System.out.println("power too low to take orb");
			return false;
		}else{
			p.inventory.addItemStackToInventory(generateLightOrb());
			setPower(0);
			System.out.println("took orb");
			return true;
		}
	}
	
	/**
	 * @Warning Does not handle decrementing tower power
	 * @return An itemStack of 1 light orb with appropriate nbt data. Also adds light orb to light orb list
	 */
	public ItemStack generateLightOrb(){
		Random rand = new Random();
		ItemStack lightOrb = new ItemStack(Darkness.lightOrb);
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("power", this.getPower());
			nbt.setInteger("initalPower", this.getPower());
			nbt.setInteger("id", rand.nextInt(Integer.MAX_VALUE));
		compound.setTag("darkness", nbt);
		lightOrb.setTagCompound(compound);
		Darkness.darkLists.addLightOrb(lightOrb);
		return lightOrb;
	}
	
	public boolean isPowered(){
		return powered;
	}
	
	
	
	
}
