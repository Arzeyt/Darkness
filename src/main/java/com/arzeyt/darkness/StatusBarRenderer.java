package com.arzeyt.darkness;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StatusBarRenderer extends Gui{

	private final static ResourceLocation overlayBar = new ResourceLocation(Darkness.MODID, "/textures/gui/darkness_hud_overlay.png");
	
	private final static int BAR_WIDTH = 81, 
			BAR_HEIGHT=9,
			BAR_SPACING_ABOVE_EXP_BAR=3;
	
	private Minecraft minecraft;
	
	public StatusBarRenderer(Minecraft mc){
		this.minecraft=mc;
	}
	
	public void renderStatusBar(int screenWidth, int screenHeight){
		World world = minecraft.theWorld;
		EntityPlayer player = minecraft.thePlayer;
		
		FontRenderer fontRenderer = minecraft.fontRendererObj;
		
		//DecimalFormat decimalFormat= new DecimalFormat("#,###");
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			minecraft.renderEngine.bindTexture(overlayBar);
			
			final int vanillaEXPLeftX = screenWidth/2-91;
			final int vanillaEXPTopY = screenHeight-32+3;
			
			GL11.glTranslatef(vanillaEXPLeftX, vanillaEXPTopY-BAR_SPACING_ABOVE_EXP_BAR-BAR_HEIGHT, 0);
			
			drawTexturedModalRect(0, 0, 0, 0, BAR_WIDTH, BAR_HEIGHT);
			
		    drawTexturedModalRect(0, 0, 0, BAR_HEIGHT, (int)(BAR_WIDTH*(player.getTotalArmorValue()/20f)), BAR_HEIGHT);
	
		    GL11.glPushMatrix();
		    
			    GL11.glTranslatef(1, 1, 0);
		
			    float maxHp = player.getMaxHealth();
			    float absorptionAmount = player.getAbsorptionAmount();
			    float effectiveHp = player.getHealth() + absorptionAmount;
			        
			    GL11.glPushMatrix();
				    
				    GL11.glScalef((BAR_WIDTH - 2)*Math.min(1, effectiveHp/maxHp), 1, 1);
				    
				    final int WITHER_EFFECT_ID = 20;
				    final int POISON_EFFECT_ID = 19;
				    final int REGEN_EFFECT_ID = 10;
				    final int NORMAL_TEXTURE_U = BAR_WIDTH;     // red texels  - see mbe40_hud_overlay.png
				    final int REGEN_TEXTURE_U = BAR_WIDTH + 1;  //  green texels
				    final int POISON_TEXTURE_U = BAR_WIDTH + 2;  // black texels
				    final int WITHER_TEXTURE_U = BAR_WIDTH + 3;  // brown texels

				    if (player.isPotionActive(WITHER_EFFECT_ID)) {
				      drawTexturedModalRect(0, 0, WITHER_TEXTURE_U, 0, 1, BAR_HEIGHT - 2);
				    }
				    else if (player.isPotionActive(POISON_EFFECT_ID)) {
				      drawTexturedModalRect(0, 0, POISON_TEXTURE_U, 0, 1, BAR_HEIGHT - 2);
				    }
				    else if (player.isPotionActive(REGEN_EFFECT_ID)) {
				      drawTexturedModalRect(0, 0, REGEN_TEXTURE_U, 0, 1, BAR_HEIGHT - 2);
				    }
				    else {
				      drawTexturedModalRect(0, 0, NORMAL_TEXTURE_U, 0, 1, BAR_HEIGHT - 2);
				    }

				    GL11.glPopMatrix();
				        
				        /* Move to the right end of the bar, minus a few pixels. */
			    GL11.glTranslatef(BAR_WIDTH - 3, 1, 0);
				        
				        /* The default minecraft font is too big, so I scale it down a bit. */
			    GL11.glPushMatrix();
				    GL11.glScalef(0.5f, 0.5f, 1);
				          
				          /* This generates the string that I want to draw. */
				    //String s = decimalFormat.format(effectiveHp) + "/" + decimalFormat.format(maxHp);
				    String s = effectiveHp + "/" + maxHp;
				          
				          /* If the player has the absorption effect, draw the string in gold color, otherwise
				           * draw the string in white color. For each case, I call drawString twice, once to
				           * draw the shadow, and once for the actual string.
				           */
				    if (absorptionAmount > 0) {
				            
				            /* Draw the shadow string */
				      fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) + 1, 2, 0x5A2B00);
				            
				            /* Draw the actual string */
				      fontRenderer.drawString(s, -fontRenderer.getStringWidth(s), 1, 0xFFD200);
				    }
				    else {
				      fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) + 1, 2, 0x4D0000);
				      fontRenderer.drawString(s, -fontRenderer.getStringWidth(s), 1, 0xFFFFFF);
				    }
				    GL11.glPopMatrix();

			    GL11.glPopMatrix();

		    GL11.glPopMatrix();
	    GL11.glPopAttrib();
		  }
		

	    
	}

