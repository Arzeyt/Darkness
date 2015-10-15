package com.arzeyt.darkness;

public final class Reference {

	public static final int TOWER_RADIUS=25;
	
	public static final int HELD_ORB_RADIUS=5;

	public static final int ORB_DEPLETETION_RATE = 6000/100;//24,000 ticks per day / half a day to depletion / 100 percent
	
	public static final int DARKNESS_CHECK_RATE = 40*2;//twice the ticks
	
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


}
