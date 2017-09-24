package com.veltpvp.nirvana.parties.command;

import org.bukkit.ChatColor;
import us.ikari.phoenix.command.Command;
import us.ikari.phoenix.command.CommandArgs;

public class PartyCommand extends BasePartyCommand {

    @Command(name = "party", aliases = {"p", "t", "team"})
    public void onCommand(CommandArgs command) {
        command.getPlayer().sendMessage(ChatColor.RED + "/" + command.getLabel() + " create/disband/invite/kick/leave");
    }

}
