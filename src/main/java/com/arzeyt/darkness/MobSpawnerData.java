package com.arzeyt.darkness;

import net.minecraft.entity.monster.EntityMob;

import java.util.HashSet;

import static com.arzeyt.darkness.Reference.MOB_SPAWN_MAX;

/**
 * Created by Default on 10/21/2015.
 */
public class MobSpawnerData {


    public static HashSet<EntityMob> mobs = new HashSet<EntityMob>();

    public static int darkMobSpawn= MOB_SPAWN_MAX;
}
