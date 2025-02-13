package net.mrscauthd.beyond_earth.events.forgeevents;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.mrscauthd.beyond_earth.entities.LanderEntity;

public class StartRideLanderEvent extends Event {

    private final LanderEntity landerEntity;
    private final Player player;

    public StartRideLanderEvent(LanderEntity landerEntity, Player player) {
        this.landerEntity = landerEntity;
        this.player = player;
    }

    public LanderEntity getLanderEntity() {
        return landerEntity;
    }

    public Player getPlayer() {
        return player;
    }
}
