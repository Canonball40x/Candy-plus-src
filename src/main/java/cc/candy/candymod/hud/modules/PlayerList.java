package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayerList extends Hud {
    public Setting<Integer> maxPlayers = register(new Setting("MaxPlayers" , 5 , 10 , 3));
    public Setting<Boolean> health = register(new Setting("Health" , true));
    public Setting<Boolean> distance = register(new Setting("Distance" , true));
    public Setting<Boolean> shadow = register(new Setting("Shadow" , false));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));

    public PlayerList(){
        super("PlayerList" , 50 , 50);
    }

    @Override
    public void onRender(){
        try {
            List<EntityPlayer> players = getPlayerList();
            float width = 0.0F;
            float height = 0.0F;
            for(EntityPlayer player : players){
                if(player.entityId == mc.player.entityId) continue;
                int health = PlayerUtil.getHealth(player);
                double distance = PlayerUtil.getDistance(player);
                String str = player.getName();
                if(this.health.getValue()) str += " " + getHealthColor(health) + health;
                if(this.distance.getValue()) str += " " + getDistanceColor(distance) + ((int)distance);
                if(RenderUtil.getStringWidth(str , 1.0F) > width) width = RenderUtil.getStringWidth(str , 1.0F);
                if(width < RenderUtil.getStringWidth(str , 1.0F)) width = RenderUtil.getStringWidth(str , 1.0F);

                RenderUtil.drawString(str , x.getValue() , y.getValue() + height , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , 1.0F);
                height += RenderUtil.getStringHeight(1.0F) + 4;
            }

            this.width = width;
            this.height = height - RenderUtil.getStringHeight(1.0F) + 5;
        }
        catch (Exception ignored){

        }
    }

    public ChatFormatting getDistanceColor(double distance){
        if(distance > 20){
            return ChatFormatting.GREEN;
        }
        if(distance > 6){
            return ChatFormatting.YELLOW;
        }

        return ChatFormatting.RED;
    }

    public ChatFormatting getHealthColor(int health){
        if(health > 23){
            return ChatFormatting.GREEN;
        }
        if(health > 7){
            return ChatFormatting.YELLOW;
        }

        return ChatFormatting.RED;
    }

    public List<EntityPlayer> getPlayerList(){
        List<EntityPlayer> players = new ArrayList<>();

        List<EntityPlayer> _players = new ArrayList<>(mc.world.playerEntities);
        _players.sort(new Comparator<EntityPlayer>() {
            @Override
            public int compare(EntityPlayer o1, EntityPlayer o2) {
                if (PlayerUtil.getDistance(o1) == PlayerUtil.getDistance(o2)) return 0;
                return PlayerUtil.getDistance(o1) < PlayerUtil.getDistance(o2) ? -1 : 1;
            }
        });

        Iterator<EntityPlayer> iterator = _players.iterator();
        int count = 0;
        while(iterator.hasNext()){
            players.add(iterator.next());
            if(count >= maxPlayers.getValue()) break;
            count++;
        }
        return players;
    }
}
