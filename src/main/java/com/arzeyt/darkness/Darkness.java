package com.arzeyt.darkness;

import com.arzeyt.darkness.effectObject.EffectBlock;
import com.arzeyt.darkness.effectObject.EffectItem;
import com.arzeyt.darkness.effectObject.EffectMessageToClient;
import com.arzeyt.darkness.effectObject.EffectMessageToServer;
import com.arzeyt.darkness.effectObject.EffectTileEntity;
import com.arzeyt.darkness.effectObject.EffectMessageHandlerOnClient;
import com.arzeyt.darkness.effectObject.EffectMessageHandlerOnServer;
import com.arzeyt.darkness.lightOrb.DetonationMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.DetonationMessageToClient;
import com.arzeyt.darkness.lightOrb.LightOrb;
import com.arzeyt.darkness.lightOrb.LightOrbBlock;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageHandlerOnClient;
import com.arzeyt.darkness.lightOrb.OrbUpdateMessageToClient;
import com.arzeyt.darkness.towerObject.LightBlock;
import com.arzeyt.darkness.towerObject.TowerBlock;
import com.arzeyt.darkness.towerObject.TowerMessageHandlerOnClient;
import com.arzeyt.darkness.towerObject.TowerMessageToClient;
import com.arzeyt.darkness.towerObject.TowerTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


@Mod(modid = Darkness.MODID, version = Darkness.VERSION)
public class Darkness {
	public static final String MODID = "darkness";
    public static final String VERSION = "a1";
    
    //blocks
    public static Block effectBlock;
    public static Block towerBlock;
    public static Block lightOrbBlock;
    public static Block lightBlock;
    
    //items
    public static Item effectItem;
    public static Item lightOrb;
   
    
    //network 
    public static SimpleNetworkWrapper simpleNetworkWrapper;
    
    	//network variables
    	public static final byte ID_MESSAGE_CTOS = 10;
    	public static final byte EFFECTID_MESSAGE_STOC = 11;
    	public static final byte TOWER_MESSAGE_STOC = 12;
    	public static final byte DETONATION_MESSAGE_STOC = 13;
    	public static final byte ORB_UPDATE_MESSAGE_STOC=14;
    	public static final byte FX_MESSAGE_STOC=15;
        
    //other stuff
    public final static boolean debugMode=false;
    public static DarkLists darkLists;
    public static ClientLists clientLists;
    public static Reference reference;
    
    public static final DarknessTab darknessTab = new DarknessTab("tabDarkness");
    
    //render
    private static StatusBarRenderer statusBarRenderer;
    
    
    @EventHandler 
    public void preInit(FMLPreInitializationEvent e){
    	
    	if(debugMode==true){
    		//blocks
    		effectBlock = new EffectBlock();
    		//items
    		effectItem = new EffectItem();
    		//tile entities
    		GameRegistry.registerTileEntity(EffectTileEntity.class, "darknessEffectTile");
    	}
    	//blocks
    		towerBlock = new TowerBlock();
    		lightOrbBlock = new LightOrbBlock();
    		lightBlock = new LightBlock();
    	
    	//items
    		lightOrb = new LightOrb();
    		
    	//tile entities
    		GameRegistry.registerTileEntity(TowerTileEntity.class, "towerTileEntity");
    	
    	//network
	    	simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("DarknessChannel");
	    	simpleNetworkWrapper.registerMessage(EffectMessageHandlerOnServer.class, EffectMessageToServer.class, ID_MESSAGE_CTOS, Side.SERVER);
	    	
	    	if(e.getSide()==Side.CLIENT){
	    		simpleNetworkWrapper.registerMessage(EffectMessageHandlerOnClient.class, EffectMessageToClient.class, EFFECTID_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(TowerMessageHandlerOnClient.class, TowerMessageToClient.class, TOWER_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(DetonationMessageHandlerOnClient.class, DetonationMessageToClient.class, DETONATION_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(OrbUpdateMessageHandlerOnClient.class, OrbUpdateMessageToClient.class, ORB_UPDATE_MESSAGE_STOC, Side.CLIENT);
	    		simpleNetworkWrapper.registerMessage(FXMessageHandlerOnClient.class, FXMessageToClient.class, FX_MESSAGE_STOC, Side.CLIENT);
	    	}
	    	
    	//classes
	    	darkLists=new DarkLists();
		 	    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		//Events
		FMLCommonHandler.instance().bus().register(new DarkEventHandlerFML());
		FMLCommonHandler.instance().bus().register(new MobSpawner());
		MinecraftForge.EVENT_BUS.register(new DarkEventHandlerMinecraftForge());
    	
    	if(event.getSide() == Side.CLIENT){ //client side stuff. screw proxies.
	    	RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
	    
	    	if(debugMode==true){
		    	//blocks
		    	renderItem.getItemModelMesher().register(Item.getItemFromBlock(effectBlock), 0, new ModelResourceLocation(Darkness.MODID + ":" + ((EffectBlock) effectBlock).getName(), "inventory"));
		    	//items
		    	renderItem.getItemModelMesher().register(effectItem, 0, new ModelResourceLocation(Darkness.MODID + ":" + ((EffectItem)effectItem).getName(), "inventory"));
	    	}
	    	//blocks
		    	renderItem.getItemModelMesher().register(Item.getItemFromBlock(towerBlock), 0, new ModelResourceLocation(Darkness.MODID + ":" + ((TowerBlock) towerBlock).getName(), "inventory"));
		    	renderItem.getItemModelMesher().register(Item.getItemFromBlock(lightOrbBlock), 0, new ModelResourceLocation(Darkness.MODID + ":" + ((LightOrbBlock) lightOrbBlock).getName(), "inventory"));
		    	
	    	//items
		    	renderItem.getItemModelMesher().register(lightOrb, 0, new ModelResourceLocation(Darkness.MODID + ":" + ((LightOrb)lightOrb).getName(), "inventory"));
	
	    	//Events
		    	FMLCommonHandler.instance().bus().register(new ClientEffectTick());
				MinecraftForge.EVENT_BUS.register(new ClientEffectEventHandler());

	    	//classes
		    	this.clientLists=new ClientLists();
    	}
    	

    }
    
    @EventHandler
    public void init(FMLPostInitializationEvent event)
    {
    	//overlay
    	if(event.getSide()== Side.CLIENT){
    		statusBarRenderer = new StatusBarRenderer(Minecraft.getMinecraft());
    		MinecraftForge.EVENT_BUS.register(new OverlayEventHandler(statusBarRenderer));
    	}
    }
    
    @EventHandler
    public void serverStop(FMLServerStoppingEvent e){

		darkLists.clearTowerList();
		clientLists.clearTowerList();
    }
}


   
