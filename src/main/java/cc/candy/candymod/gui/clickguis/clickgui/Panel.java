package cc.candy.candymod.gui.clickguis.clickgui;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.clickgui.componets.ModuleButton;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Panel implements Util {
    public String name;
    public int x,y;
    public int tmpy = 0;
    public int width, height;
    public boolean opening;
    private boolean isMoving;
    private int diff_x , diff_y;

    public static int Cy = 0;

    public List<ModuleButton> buttons;

    public Panel(Module.Categories category , int x , int y , boolean isOpening)
    {
        name = category.name();
        this.x = x;
        this.y = y;
        width = 95;
        height = 17;
        opening = isOpening;
        isMoving = false;
        diff_x = 0;
        diff_y = 0;

        //search module
        buttons = new ArrayList<>();
        List<Module> modules = new ArrayList<>(CandyMod.m_module.getModulesWithCategories(category));
        Collections.sort(modules, new Comparator<Module>() {
            @Override
            public int compare(Module c1 , Module c2) {
                return c1.name.compareToIgnoreCase(c2.name);
            }
        });
        int buttonY = y;
        for(Module m : modules)
        {
            if(m.hide) continue;
            buttons.add(new ModuleButton(m , x, width, 15));
        }
    }

    public void onRender(int mouseX , int mouseY , float partialTicks)
    {
        //panel
        RenderUtil.drawRect(x, y, width, height, ColorUtil.toARGB(50, 50, 50, 255));
        //draw panel string
        float width = RenderUtil.getStringWidth(name , 1.0F);
        float centeredX = x + (this.width - width) / 2;
        float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
        RenderUtil.drawString(name , centeredX  ,centeredY , ColorUtil.toARGB(255 , 255 , 255 , 255) , false ,1.0F);

        //draw moduleButton
        //reset
        Cy = y + 2;
        if(opening) {
            Cy = y + 2;

            buttons.forEach(b -> {
                Cy += 15;
                b.onRender(Cy);
            });
        }

        int outlineColor = ColorUtil.toRGBA(210 , 70 , 80 , 255);
        float y1 = (Cy -2) + this.height;
        //left
        RenderUtil.drawLine(x , y , x , y1, 2 , outlineColor);
        //right
        RenderUtil.drawLine(x + this.width , y , x + this.width , y1, 2 , outlineColor);
        //top
        RenderUtil.drawLine(x , y , x + this.width , y , 2 , outlineColor);
        //bottom
        RenderUtil.drawLine(x , y1, x + this.width , y1, 2 , outlineColor);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        //send event
        buttons.forEach(b -> {
            b.mouseClicked(mouseX, mouseY, mouseButton);
        });

        //start move panel
        //check mouse pos
        if(x < mouseX && x + width > mouseX && mouseButton == MouseUtil.MOUSE_LEFT && !CandyGUI.isHovering)
        {
            if(y < mouseY && y + height > mouseY) {
                isMoving = true;
                CandyGUI.isHovering = true;
                //set diff
                diff_x = x - mouseX;
                diff_y = y - mouseY;
            }
        }

        if(x < mouseX && x + width > mouseX && mouseButton == MouseUtil.MOUSE_RIGHT)
        {
            if(y < mouseY && y + height > mouseY)
                opening = !opening;
        }

    }


    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        //send event
        buttons.forEach(b -> b.mouseReleased(mouseX , mouseY , state));

        isMoving = false;
        CandyGUI.isHovering = false;
    }

    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {
        //send event
        buttons.forEach(b -> b.mouseClickMove(mouseX , mouseY , clickedMouseButton));

        //move
        if(isMoving)
        {
            x = mouseX + diff_x;
            y = mouseY + diff_y;

            //change panel x
            buttons.forEach(b -> b.changeX(x));
        }
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        buttons.forEach(b -> b.onKeyTyped(typedChar , keyCode));
    }
}
