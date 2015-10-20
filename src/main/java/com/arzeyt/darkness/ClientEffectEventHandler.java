package com.arzeyt.darkness;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import scala.collection.parallel.ParIterableLike;

import static net.minecraftforge.client.event.EntityViewRenderEvent.*;
import static org.lwjgl.opengl.GL11.*;

public class ClientEffectEventHandler {

    float minDark = 0.01F;

    @SubscribeEvent
    public void renderDarkFogDensity(FogDensity e){

       if(Darkness.darkLists.isPlayerInTowerRadius(Minecraft.getMinecraft().thePlayer)==false){
           e.density =0.005F * (Darkness.darkLists.getDistanceToNearestTower(Minecraft.getMinecraft().thePlayer)-Reference.TOWER_RADIUS);
           e.density = (float) (0.005F * (Darkness.darkLists.getDistanceToNearestTowerDouble(Minecraft.getMinecraft().theWorld.provider.getDimensionId(), Minecraft.getMinecraft().thePlayer.getPosition())-Reference.TOWER_RADIUS));

           if(e.density> 0.25F){
               e.density=0.25F;
           }else if(e.density<0.01F){
               e.density=0.01F;
           }
           e.setCanceled(true);
       }else {
           e.density = 0.01F;
           e.setCanceled(true);
       }
    }

    @SubscribeEvent
    public void renderDarkFogColor(FogColors e){
           e.blue=0.0F;
           e.red=0.0F;
           e.green=0.0F;
    }


    public void renderPlayer(RenderLivingEvent.Post e){
        if(e.entity instanceof EntityPlayer==false){return;}
        EntityPlayer player = (EntityPlayer) e.entity;
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        double x = player.lastTickPosX - rm.viewerPosX;
        double y = player.lastTickPosY - rm.viewerPosY;
        double z = player.lastTickPosZ - rm.viewerPosZ;
        glPushMatrix();
        glTranslatef((float)x, (float)y + player.height+1.25f, (float)z); //Translates to the target player
        glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F); //Faces towards the the viewport
        //Draw here

    }

    public void renderPlayerHand(RenderHandEvent e){
        glPushMatrix();
        glBegin(GL_MATRIX_MODE);
        glVertex2d(0,0);
        glVertex2d(10,10);
        glColor3f(1.0F,1.0F,1.0F);
        glPopMatrix();
    }

}
