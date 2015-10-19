package com.arzeyt.darkness.towerObject;

import java.util.Random;

import com.arzeyt.darkness.Darkness;
import com.arzeyt.darkness.EffectHelper;
import com.arzeyt.darkness.Reference;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TowerTileEntity extends TileEntity implements IUpdatePlayerListBox{

	//sync variables
	public final int SYNC_DISTANCE = 50;
	private int counter = 0;
	private int syncRate = 20*3;
	private int nearbyPlayerSyncRate = 20*5;
	private int particleProductionRate = 2;
	private int borderConstructRate = 3;
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
	private int power = 0;
	private long minecraftTime =0;
	private boolean takingOrbAtNoon=false;
	private boolean doBorderEffect=false;
	
	private int token = 0;
	private BlockPos magicBlock=pos;

	Reference r = new Reference();
	
	
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
					&& worldObj.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), SYNC_DISTANCE)!=null 
					&& power>99){
				
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
			minecraftTime=worldObj.getWorldTime();
			long timeOfDay = minecraftTime%24000;
			
			if(counter%r.TOWER_DEPLETION_RATE==0
					&& timeOfDay < r.TOWER_DEPLETE_END_TIME
					&& timeOfDay> r.TOWER_DEPLETE_START_TIME
					&& power>0){
				System.out.println("decrementing power");
					setPower(getPower()-1);
			}
			if(counter%r.TOWER_CHARGE_RATE==0
					&& timeOfDay<r.TOWER_CHARGE_END_TIME
					&& r.TOWER_CHARGE_START_TIME<timeOfDay
					&& power<100){
				System.out.println("incrementing power");
				setPower(getPower()+1);
				System.out.println("power = "+getPower());
			}
		}
		if(worldObj.isRemote==true){//clientside
			
			//effect
			double adjustedParticleProductionRate = getPower() > 0 ? particleProductionRate*100/getPower() : particleProductionRate*100;
			if(getPower()>0 && counter%adjustedParticleProductionRate==0){
				Random rand = new Random();
				this.getWorld().spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX()+.5, pos.getY()+2, pos.getZ()+.5, -0.5D+rand.nextDouble(), 0.5D, -0.5D+rand.nextDouble());
			}
			/**
			if(getPower()>0){
				Reference r = Darkness.reference;
				double adjustedTowerRadius = (double)r.TOWER_RADIUS/100*(double)getPower();
				if(adjustedTowerRadius<1){
					adjustedTowerRadius=1;
				}
				Random rand = new Random();
				double randX = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				double randY = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				double randZ = rand.nextInt((int) (adjustedTowerRadius*2))-adjustedTowerRadius;
				this.getWorld().spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX()+.5+randX, rand.nextInt(256), pos.getZ()+randZ+.5, 0.0D, 0.1D, 0.0D);
			}**/
			
			if(doBorderEffect==false
					&&power>0){
				doBorderEffect=true;
				borderEffectRender();
				System.out.println("border effect on");
			}else if(doBorderEffect==true
					&&power<=0){
				doBorderEffect=false;
				borderEffectOff();
				System.out.println("border effect off");
			}else if(doBorderEffect==true
					&&counter%borderConstructRate==0){
				borderEffectRender();
			}
		
		}
		
		
		//increment counters
		minecraftTime=worldObj.getWorldTime();
		counter++;
		if(counter>Integer.MAX_VALUE-20){ //just so we dont get huge numbers
			counter=0;
		}
	}

	Random rand = new Random();
	private void magicLight() {
		if(token>100 || counter<200){
			return;
		}
		if(magicBlock.getX()==0){
			magicBlock=pos;
		}
		int i = rand.nextInt(3);
		int x=magicBlock.getX();
		int y=magicBlock.getY();
		int z=magicBlock.getZ();
		switch (i) {
		case 0:
			x=x+1;
			break;
		case 1: 
			x=x-1;
			break;
		case 2:
			z=z+1;
			break;
		case 3:
			z=z-1;
		default:
			break;
		}
		System.out.println("magic block: "+magicBlock.toString());
		magicBlock = EffectHelper.findGroundY(worldObj, new BlockPos(x,y+1,z));
		try{
			worldObj.getChunkFromBlockCoords(magicBlock).setBlockState(magicBlock, Blocks.bedrock.getDefaultState());
		}catch(Exception e){
			System.out.println("exception, brah");
		}
		token++;
		System.out.println("token: "+token);
	}

	private void updateClient() {
		if(worldObj.isRemote==false){
			//System.out.println("sending message: Power= "+getPower()+"Powered= "+powered);
			Darkness.simpleNetworkWrapper.sendToAll(new TowerMessageToClient(getPower(), pos.getX(), pos.getY(), pos.getZ()));
		}
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag("darkness");
		this.powered = nbt.getBoolean("powered");
		this.power=(nbt.getInteger("orbPower"));
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
		return this.power;
	}
	
	/**
	 * 
	 * @param orbPower Values between 0 to 100 ONLY
	 * @Description sets tower power and adjusts all variables accordingly. 
	 */
	public void setPower(int power){
		this.power=power;
		if(power>0 && this.powered==false){
			this.powered=true;
			if(this.worldObj.isRemote==false
					&& Darkness.darkLists.getPoweredTowers().isEmpty() 
					|| Darkness.darkLists.towerExists(this)==false){
				Darkness.darkLists.addPoweredTowers(this);
			}
		}else if(power<=0 && this.powered==true){
			this.powered=false;
			if(this.worldObj.isRemote==false
					&& Darkness.darkLists.getPoweredTowers().isEmpty()==false 
					&& Darkness.darkLists.towerExists(this)==true){
				Darkness.darkLists.removePoweredTower(this);
			}
		}
		System.out.println("set power to: "+power+"  powered= "+this.powered);
		syncState++;
	}
	
	public void orbAbove(boolean present){
		if(present){
			if(worldObj.getChunkFromBlockCoords(pos).getBlock(pos.getX(), pos.getY()+2, pos.getZ()) instanceof BlockAir){
				worldObj.getChunkFromBlockCoords(pos).setBlockState(new BlockPos(pos.getX(),pos.getY()+2, pos.getZ()), Darkness.lightOrbBlock.getDefaultState());
			}
		}else{
			worldObj.getChunkFromBlockCoords(pos).setBlockState(new BlockPos(pos.getX(), pos.getY()+2, pos.getZ()), Blocks.air.getDefaultState());
		}
	}
	/**
	 * 
	 * @param p player to give the orb to
	 * @return true if worked, false if didn't
	 */
	public boolean takeOrb(EntityPlayer p){
		/**
		if(noonLowerEnd<minecraftTime && minecraftTime<noonHigherEnd){
			if(takingOrbAtNoon==false){
				this.takingOrbAtNoon=true;//activate taking orb. automatically deactivated in update()
				this.takeOrbAtNoonCooldownCounter=TAKE_ORB_COOLDOWN;//reset the cooldown
				
				ItemStack newOrb = generateLightOrb(p);
				p.inventory.addItemStackToInventory(newOrb);
				setPower(0);
				System.out.println("took orb");
				return true;
			}else{
				System.out.println("wait for cooldown in: "+takeOrbAtNoonCooldownCounter+" ticks");
				return false;
			}
		}**/
		if(getPower()==0){
			System.out.println("power too low to take orb");
			return false;
		}else{
			ItemStack newOrb = generateLightOrb(p);
			p.inventory.addItemStackToInventory(newOrb);
			setPower(0);
			System.out.println("took orb");
			return true;
		}
	}
	
	/**
	 * @Warning Does not handle decrementing tower power
	 * @return An itemStack of 1 light orb with appropriate nbt data. Also adds light orb to light orb list
	 */
	public ItemStack generateLightOrb(EntityPlayer p){
		Random rand = new Random();
		ItemStack lightOrb = new ItemStack(Darkness.lightOrb);
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger(Reference.POWER, this.getPower());
			nbt.setInteger(Reference.INITAL_POWER, this.getPower());
			nbt.setInteger(Reference.ID, rand.nextInt(Integer.MAX_VALUE));
		compound.setTag("darkness", nbt);
		lightOrb.setTagCompound(compound);
		Darkness.darkLists.addLightOrb(lightOrb);
		return lightOrb;
	}
	
	public boolean isPowered(){
		return powered;
	}
	
	private void borderEffectRender(){
		int towerRadius=Reference.TOWER_RADIUS;
		IBlockState state = Darkness.lightBlock.getDefaultState();

		int y = Minecraft.getMinecraft().thePlayer.getPosition().getY()-2;

		createXLineSparkles(worldObj, pos.getX()-towerRadius, pos.getX()+towerRadius, y, pos.getZ()-towerRadius,  7);
		createXLineSparkles(worldObj, pos.getX()-towerRadius, pos.getX()+towerRadius, y, pos.getZ()+towerRadius,  7);
		createZLineSparkles(worldObj, pos.getX()+towerRadius, y, pos.getZ()-towerRadius, pos.getZ()+towerRadius,  7);
		createZLineSparkles(worldObj, pos.getX()-towerRadius, y, pos.getZ()-towerRadius, pos.getZ()+towerRadius,  7);
		
		y=y+2;
		createXLineSparkles(worldObj, pos.getX()-towerRadius, pos.getX()+towerRadius, y, pos.getZ()-towerRadius,  7);
		createXLineSparkles(worldObj, pos.getX()-towerRadius, pos.getX()+towerRadius, y, pos.getZ()+towerRadius,  7);
		createZLineSparkles(worldObj, pos.getX()+towerRadius, y, pos.getZ()-towerRadius, pos.getZ()+towerRadius,  7);
		createZLineSparkles(worldObj, pos.getX()-towerRadius, y, pos.getZ()-towerRadius, pos.getZ()+towerRadius,  7);
	}
	
	private void createXLineSparkles(World w, int xstart, int xend, int y, int z, int density){
		Random rand = new Random();
		for(int x=xstart; x<xend; x=x+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(rand.nextFloat()<=density){
				w.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 1.0D, 0.0D);
			}
		}
	}
	
	private void createZLineSparkles(World w, int x, int y, int zstart, int zend, int density){
		Random rand = new Random();
		for(int z=zstart; z<zend; z=z+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(rand.nextFloat()<=density){
					w.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 1.0D, 0.0D);
			}
		}
	}
	
	private void createXLine(World w, int xstart, int xend, int y, int z, IBlockState state, int density){
		Random rand = new Random();
		for(int x=xstart; x<xend; x=x+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(worldObj.getChunkFromBlockCoords(pos).getBlock(pos) instanceof BlockAir
					&& rand.nextFloat()<=density){
				w.getChunkFromBlockCoords(pos).setBlockState(pos, state);
			}
		}
	}
	
	private void createZLine(World w, int x, int y, int zstart, int zend, IBlockState state, int density){
		Random rand = new Random();
		for(int z=zstart; z<zend; z=z+rand.nextInt(density)){
			BlockPos pos = new BlockPos(x,y,z);
			if(worldObj.getChunkFromBlockCoords(pos).getBlock(pos) instanceof BlockAir
					&& rand.nextFloat()<=density){
					w.getChunkFromBlockCoords(pos).setBlockState(pos, state);
			}
		}
	}
	private void borderEffectOff(){
		BlockPos minPos = new BlockPos(this.getPos().getX()-Reference.TOWER_RADIUS, 0, this.getPos().getZ()-Reference.TOWER_RADIUS);
		BlockPos maxPos = new BlockPos(getPos().getX()+Reference.TOWER_RADIUS, 256, this.getPos().getZ()+Reference.TOWER_RADIUS);
		worldObj.markBlockRangeForRenderUpdate(minPos, maxPos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TowerTileEntity){
			TowerTileEntity t = (TowerTileEntity) obj;
			if(t.getPos()==this.getPos()){
				return true;
			}
		}
		return false;
	}
	
}
