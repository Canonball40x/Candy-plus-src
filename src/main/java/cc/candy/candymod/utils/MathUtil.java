package cc.candy.candymod.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Random;

public class MathUtil implements Util{
    public static Random rnd = new Random();

    public static int getRandom(int min , int max)
    {
        return rnd.nextInt(max - min + 1) + min;
    }

    public static ArrayList moveItemToFirst(ArrayList list , int index)
    {
        ArrayList newlist = new ArrayList();
        newlist.add(list.get(index));
        for (int i = 0; i < list.size(); i++)
        {
            //skip
            if(i == index) continue;

            newlist.add(list.get(index));
        }
        return new ArrayList(list);
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static float square(float v1)
    {
        return v1 * v1;
    }
}
