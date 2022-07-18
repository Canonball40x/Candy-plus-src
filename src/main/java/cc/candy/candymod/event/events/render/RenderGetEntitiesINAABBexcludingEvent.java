package cc.candy.candymod.event.events.render;

import cc.candy.candymod.event.CandyEvent;
import com.google.common.base.Predicate;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

public class RenderGetEntitiesINAABBexcludingEvent extends CandyEvent {
    public RenderGetEntitiesINAABBexcludingEvent(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate){

    }
}
