package com.arzeyt.darkness;

public final class Reference {

	public static final int DARKNESS_CHECK_RATE = 40*2;//twice the ticks

	//tower
	public static final int TOWER_RADIUS=50;
	public static final int TOWER_DEPLETION_RATE=12000/100;
	public static final int TOWER_CHARGE_RATE=6000/100;
	public static final int TOWER_CHARGE_START_TIME=0;
	public static final int TOWER_CHARGE_END_TIME=12000;
	public static final int TOWER_DEPLETE_START_TIME=12001;
	public static final int TOWER_DEPLETE_END_TIME=24000;


	//orb
	public static final int HELD_ORB_RADIUS=5;
	public static final int ORB_DEPLETETION_RATE = 6000/100;//24,000 ticks per day / half a day to depletion / 100 percent
	public static final int ORB_DETONATION_RAIDUS=10;
	public static final int DETONATION_EFFECT_TICK_RATE = 2;
	public static final int DETONATION_LIFETIME = 40*20;
	
	//orb NBT
	public static final String DISSIPATION_PERCENT = "DissipationPercent";
	public static final String POWER = "power";
	public static final String TICKS_LIVED = "ticksLived";
	public static final String INITAL_POWER = "initalPower";
	public static final String ID = "id";

	
	//FX
	public static final int FX_VANISH = 1;
	public static final int FX_BLOCK = 2;
	public static final int FX_OUTWARDS_SPARKLE = 3;

	//client effect tick
	public static final int ATMOSPHERE_TICK = 10;

	//player effects
	public static final int EVASION_CHECK_RADIUS= 5;
	public static final int EVASION_RADIUS = 15;
	public static final int EVASION_CHECK_RATE = 20*5;

	//mobs
	public static final int MOB_SPAWN_RATE = 40*1;
	public static final int MOB_LIFETIME_SECONDS = 20;
	public static final int MOB_SPAWN_MAX = 15;

	//player nbt
	public static final String P_GHOST = "ghost";

	//mob nbt
	public static final String MOB_LIFETIME = "lifetime";

}
