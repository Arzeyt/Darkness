package com.arzeyt.darkness;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import static com.arzeyt.darkness.Darkness.darkLists;
import static com.arzeyt.darkness.MobSpawnerData.darkMobSpawn;
import static com.arzeyt.darkness.MobSpawnerData.mobs;
import static com.arzeyt.darkness.Reference.*;
import static net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;


public class MobSpawner {



    int counter = 0;
    Random rand = new Random();


    @SubscribeEvent
    public void darkMobSpawn(ServerTickEvent e){
        counter++;
        if(counter% MOB_SPAWN_RATE==0
                && darkMobSpawn>=0){
            System.out.println("dark mob spawn: "+darkMobSpawn);
            ArrayList list = (ArrayList) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
            Iterator iterator = list.iterator();
            while(iterator.hasNext()) {
                EntityPlayer player = (EntityPlayer) iterator.next();
                if(Darkness.darkLists.isGhost(player)==false){
                    WorldServer world = MinecraftServer.getServer().worldServerForDimension(player.dimension);
                    BlockPos ppos = player.getPosition();
                    if (ppos == null) return;

                    if (darkLists.isPlayerInTowerRadius(player) == false) {
                        int i = rand.nextInt(100);
                        if (i <= 100) {
                            spawnZombie(player, world);
                        }
                        if (i <= 30) {
                            spawnSkeleton(player, world);
                        }
                        if(i<=10){
                            spawnSpider(player, world);
                        }
                    }
                }

            }
        }
        //every second deplete liftime
        if(counter%40==0){
            HashSet<EntityLiving> toRemove = new HashSet<EntityLiving>();
            for(EntityLiving mob : mobs){
                int lifetime = mob.getEntityData().getCompoundTag("darkness").getInteger(MOB_LIFETIME);
                if(lifetime<=0 ){
                    mob.captureDrops=true;
                    mob.setDead();
                    toRemove.add(mob);
                    darkMobSpawn++;
                    Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(FX_VANISH, mob.getPosition().getX(), mob.getPosition().getY(), mob.getPosition().getZ()));
                }else {
                    lifetime=lifetime-1;
                    setLifetime(mob, lifetime);
                }
            }
            for(EntityLiving mob : toRemove){
                mobs.remove(mob);
            }
        }
        //regen mob spawn
       if(counter%(MOB_SPAWN_RATE*10)==0
               && darkMobSpawn< MOB_SPAWN_MAX){
            darkMobSpawn++;
        }

        //prevent overdrive
        if(darkMobSpawn>20){
            darkMobSpawn--;
        }
    }

    public void spawnZombie(EntityPlayer player, World world){
        //could do a counter check. seems as if server seconds are 40 ticks long... each check after would be an additional second
        BlockPos ppos = player.getPosition();
        EntityZombie zombie = new EntityZombie(world);
        BlockPos zloc = EffectHelper.getRandomGroundPos(world, ppos, 20);
        if(zloc==null || darkLists.inDarkness(world, zloc)==false)return;
        zombie.setPosition(zloc.getX(), zloc.getY(), zloc.getZ());
        world.spawnEntityInWorld(zombie);
        zombie.setAttackTarget(player);
        zombie.setCurrentItemOrArmor(4, new ItemStack(Item.getByNameOrId("leather_helmet")));
        if(rand.nextFloat()<0.2F){
            zombie.setCurrentItemOrArmor(0, new ItemStack(Item.getByNameOrId("iron_sword")));
        }
        setLifetime(zombie, MOB_LIFETIME_SECONDS);
        mobs.add(zombie);

        Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(FX_VANISH, zloc.getX(), zloc.getY(), zloc.getZ()));
        world.playSoundAtEntity(zombie, "darkness:teleWhoosh", 1.0F, 1.0F);
        darkMobSpawn--;
    }

    public void spawnSkeleton(EntityPlayer player, World world){
        //could do a counter check. seems as if server seconds are 40 ticks long... each check after would be an additional second
        BlockPos ppos = player.getPosition();
        EntitySkeleton skeleton = new EntitySkeleton(world);
        BlockPos sloc = EffectHelper.getRandomGroundPos(world, ppos, 20);
        if(sloc==null || darkLists.inDarkness(world, sloc)==false)return;

        skeleton.setPosition(sloc.getX(), sloc.getY(), sloc.getZ());
        world.spawnEntityInWorld(skeleton);
        skeleton.setAttackTarget(player);
        skeleton.setCurrentItemOrArmor(4, new ItemStack(Item.getByNameOrId("leather_helmet")));
        if(rand.nextFloat()<0.5F){
            skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
        }
        setLifetime(skeleton, MOB_LIFETIME_SECONDS);
        mobs.add(skeleton);


        Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(FX_VANISH, sloc.getX(), sloc.getY(), sloc.getZ()));
        world.playSoundAtEntity(skeleton, "darkness:teleWhoosh", 1.0F, 1.0F);
        darkMobSpawn--;
    }

    public void spawnSpider(EntityPlayer player, World world){
        //could do a counter check. seems as if server seconds are 40 ticks long... each check after would be an additional second
        BlockPos ppos = player.getPosition();
        EntitySpider spider = new EntitySpider(world);
        BlockPos sloc = EffectHelper.getRandomGroundPos(world, ppos, 20);
        if(sloc==null || darkLists.inDarkness(world, sloc)==false)return;

        spider.setPosition(sloc.getX(), sloc.getY(), sloc.getZ());
        world.spawnEntityInWorld(spider);
        spider.setAttackTarget(player);
        setLifetime(spider, MOB_LIFETIME_SECONDS);
        mobs.add(spider);


        Darkness.simpleNetworkWrapper.sendToAll(new FXMessageToClient(FX_VANISH, sloc.getX(), sloc.getY(), sloc.getZ()));
        world.playSoundAtEntity(spider, "darkness:teleWhoosh", 1.0F, 1.0F);
        darkMobSpawn--;
    }

    public void setLifetime(EntityLiving e, int lifetime){
        System.out.println("set mob lifetime to: "+lifetime+" mobs in list: "+ mobs.size());
        if(e.getEntityData().hasKey("darkness")==false){
            e.getEntityData().setTag("darkness", new NBTTagCompound());
            System.out.println("entity data: "+e.getEntityData().toString());
        }
        NBTTagCompound nbt = e.getEntityData().getCompoundTag("darkness");
        nbt.setInteger(MOB_LIFETIME, lifetime);
    }
}
