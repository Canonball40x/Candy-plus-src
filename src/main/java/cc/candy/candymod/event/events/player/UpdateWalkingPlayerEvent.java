package cc.candy.candymod.event.events.player;

import cc.candy.candymod.event.CandyEvent;

public class UpdateWalkingPlayerEvent extends CandyEvent {
    public int stage;
    public UpdateWalkingPlayerEvent(int stage)
    {
        this.stage = stage;
    }

}
