package com.arzeyt.darkness;

import com.arzeyt.darkness.lightOrb.LightOrb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import scala.collection.parallel.ParIterableLike;

import java.util.Random;

import static net.minecraftforge.client.event.EntityViewRenderEvent.*;
import static org.lwjgl.opengl.GL11.*;

public class ClientEffectEventHandler {

    float minDark = 0.01F;
    int counter = 0;

    @SubscribeEvent
    public void renderDarkFogDensity(FogDensity e){
        if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode){
            e.density=0.01F;
            e.setCanceled(true);

        }else if(Darkness.darkLists.isPlayerInTowerRadius(Minecraft.getMinecraft().thePlayer)==false){
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


    @SubscribeEvent
    public void renderPlayerInvisible(RenderHandEvent e){
        if(Minecraft.getMinecraft().thePlayer.isInvisible()==true) {
            e.setCanceled(true);
        }
    }

    public void renderPlayer(RenderPlayerEvent e) {
        counter++;
        if (e.entityPlayer.getHeldItem() != null) {
            if (e.entityPlayer.getHeldItem().getItem() instanceof LightOrb) {
                if (Darkness.clientLists.getDetonations().isEmpty() == false
                        && counter % (Reference.DETONATION_EFFECT_TICK_RATE * 5) == 0) {
                    BlockPos pos = e.entityPlayer.getPosition();
                    double i = pos.getX();
                    double j = pos.getY();
                    double k = pos.getZ();
                    Random rand = new Random();
                    int r = Reference.HELD_ORB_RADIUS;
                    int density = 20;

                    for (double x = -r; x < r; x++) {
                        for (double y = -r; y < r; y++) {
                            for (double z = -r; z < r; z++) {
                                double dist = MathHelper.sqrt_double((x * x + y * y + z * z)); //Calculates the distance
                                if ((dist >= r - 1 && dist <= r + 1) && rand.nextInt(100) < density) {
                                    Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, i + x + rand.nextDouble(), j + y + rand.nextDouble(), k + z + rand.nextDouble(), 0.0, 0.0, 0.0);
                                }
                            }
                        }
                    }

                }
            }
        }
    }


    //light orb stuff
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
        glBegin(GL_POINTS);
        glVertex3i(0,0,0);
        glVertex3i(0,1,0);
        glVertex3i(1,1,1);
        glEnd();
        glPopMatrix();

    }

    //light orb stuff
    public void renderPlayerHand(RenderHandEvent e){
        glPushMatrix();
        glBegin(GL_MATRIX_MODE);
        glVertex2d(0,0);
        glVertex2d(10,10);
        glColor3f(1.0F,1.0F,1.0F);
        glPopMatrix();
    }

}
