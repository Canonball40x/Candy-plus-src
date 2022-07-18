package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class TargetHUD extends Hud {
    public static EntityPlayer target;
    public double health = 36.0;

    public Setting<Boolean> shadow = register(new Setting("Shadow" , true));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));

    public TargetHUD(){
        super("TargetHud" , 100 , 50);
    }

    @Override
    public void onRender() {
		try{
	    if(nullCheck()) return;

        float width = 200;
        float height = 70;
        this.width = width;
        this.height = height;

        target = PlayerUtil.getNearestPlayer(30.0);
        if(target == null) return;
        RenderUtil.drawRect(this.x.getValue() , this.y.getValue() , width , height , ColorUtil.toRGBA(40,40,40));
        RenderUtil.renderEntity(target , this.x.getValue() + 30 , this.y.getValue() + 63 , 30F);
        float health = target.getHealth() + target.getAbsorptionAmount();
        this.health += (health - this.health) * 0.4;
        double lineWidth = width * (this.health / 36.0);
        float thickness = 1;
        RenderUtil.drawGradientRect(this.x.getValue() , this.y.getValue() + height - thickness , (float) (this.x.getValue() + lineWidth), this.y.getValue() + height ,
                            ColorUtil.toRGBA(255 , 0 , 0) , ColorUtil.toRGBA(getHealthColor((int) health)));

        int white = ColorUtil.toRGBA(255 , 255 , 255);
        RenderUtil.drawString(target.getName() , this.x.getValue() + 60 , this.y.getValue() + 10 , white , shadow.getValue() , 1.0F);

        //armor
        ItemStack slot4 = getArmorInv(3);
        ItemStack slot3 = getArmorInv(2);
        ItemStack slot2 = getArmorInv(1);
        ItemStack slot1 = getArmorInv(0);
        //main hand
        ItemStack mainHand = target.getHeldItemMainhand();
        ItemStack offHand = target.getHeldItemOffhand();

        renderItem(slot4 , 60 , 20);
        renderItem(slot3 , 80 , 20);
        renderItem(slot2 , 100 , 20);
        renderItem(slot1 , 120 , 20);
        renderItem(mainHand , 140 , 20);
        renderItem(offHand , 160 , 20);

        //health and distance
        RenderUtil.drawString("Health : " + (int)health , this.x.getValue() + 60 , this.y.getValue() + 42 , white , shadow.getValue() , 1.0F);
        RenderUtil.drawString("Distance : " + (int)PlayerUtil.getDistance(target) , this.x.getValue() + 60 , this.y.getValue() + 57 , white , shadow.getValue() , 1.0F);
		}
		catch(Exception e){
		}
    }

    public void renderItem(ItemStack item , int x , int y){
        if(item == null) return;
        if(!item.isEmpty){
            //if(item.isItemStackDamageable()) renderDmg(item , this.x.getValue() + x , this.y.getValue() + y + 12);
            RenderUtil.renderItem(item , (int)(this.x.getValue() + x) , (int)(this.y.getValue() + y - 4));
        }
    }

    public void renderDmg(ItemStack item , float x, float y){
        float width = 10;
        RenderUtil.drawRect(x + 3 , y , width , 1 , ColorUtil.toRGBA(0 , 0 ,0));
        float dmg = getItemDmg(item) / 100.0F;
        RenderUtil.drawRect(x + 3 , y , (width * dmg) , 1 , getItemDmgColor(item));
    }

    public int getItemDmgColor(ItemStack is){
        float maxDmg = is.getMaxDamage();
        float dmg = is.getMaxDamage() - is.getItemDamage();

        double offset = 255 / (maxDmg / 2);
        int red = 0;
        int green = 0;
        if(dmg > maxDmg / 2){
            red = (int) ((maxDmg - dmg) * offset);
            green = 255;
        }
        else{
            red = 255;
            green = (int) (255 - ((maxDmg / 2 - dmg) * offset));
        }

        return ColorUtil.toRGBA(red, green, 0, 255);
    }

    public float getItemDmg(ItemStack is){
        return ((is.getMaxDamage() - is.getItemDamage()) / (float)is.getMaxDamage()) * 100.0F;
    }

    public ItemStack getArmorInv(int slot){
        InventoryPlayer inv = target.inventory;
        return inv.armorItemInSlot(slot);
    }

    private static Color getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }

        int red = 0;
        int green = 0;
        if(health > 18){
            red = (int) ((36 - health) * 14.1666666667);
            green = 255;
        }
        else{
            red = 255;
            green = (int) (255 - ((18 - health) * 14.1666666667));
        }

        return new Color(red, green, 0, 255);
    }
}
