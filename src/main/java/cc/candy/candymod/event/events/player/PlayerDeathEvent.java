package cc.candy.candymod.event.events.player;

import cc.candy.candymod.event.CandyEvent;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerDeathEvent extends CandyEvent {
    public EntityPlayer player;
    public PlayerDeathEvent(EntityPlayer player)
    {
        this.player = player;
    }
}
