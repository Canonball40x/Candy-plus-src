package cc.candy.candymod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class CandyEvent extends Event {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel(){
        cancelled = true;
    }
}
