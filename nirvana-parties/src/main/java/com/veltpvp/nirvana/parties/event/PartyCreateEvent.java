package com.veltpvp.nirvana.parties.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PartyCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private Player leader;

    public PartyCreateEvent(Player leader) {
        this.leader = leader;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
