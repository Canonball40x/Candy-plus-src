package cc.candy.candymod.utils;

import net.minecraft.util.math.BlockPos;

import java.util.Locale;

public class StringUtil implements Util{
    public static String getPositionString(BlockPos pos)
    {
        return "X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ();
    }

    public static String getName(String full){
        String r = "";
        boolean a = false;
        for(char c : full.toCharArray()){
            if(!a)
                r += String.valueOf(c).toUpperCase();
            else
                r += String.valueOf(c).toLowerCase();
            a = true;
        }
        return r;
    }
}
