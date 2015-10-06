package com.arzeyt.darkness;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


@Mod(modid = Darkness.MODID, version = Darkness.VERSION)
public class Darkness {
	public static final String MODID = "darkness";
    public static final String VERSION = "a1";
    
    //blocks
    public static Block effectBlock;
    
    //items
    public static Item effectItem;
        
    //other stuff
    public final boolean debugMode=true;
    
    public static final DarknessTab darknessTab = new DarknessTab("tabDarkness");
    
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
    
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//register renders
    	if(event.getSide() == Side.CLIENT)
    	{
	    	RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
	    
	    	if(debugMode==true){
		    	//blocks
		    	renderItem.getItemModelMesher().register(Item.getItemFromBlock(effectBlock), 0, new ModelResourceLocation(Darkness.MODID + ":" + ((EffectBlock) effectBlock).getName(), "inventory"));
		    	//items
		    	renderItem.getItemModelMesher().register(effectItem, 0, new ModelResourceLocation(Darkness.MODID + ":" + ((EffectItem)effectItem).getName(), "inventory"));
	    	}
    	}
    }
    
    @EventHandler
    public void init(FMLPostInitializationEvent event)
    {
		
    	
    }
}


   
