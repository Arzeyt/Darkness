package com.arzeyt.darkness;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class DarknessTab extends CreativeTabs{

	public DarknessTab(String label) {
		super(label);
		this.setBackgroundImageName("darkness.png");
	}

	@Override
	public Item getTabIconItem() {
		return Darkness.effectItem;
	}

}
