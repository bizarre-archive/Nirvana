package com.veltpvp.nirvana.parties.command;

import com.veltpvp.nirvana.parties.NirvanaParties;
import us.ikari.phoenix.command.CommandArgs;

public abstract class BasePartyCommand {

    protected static NirvanaParties main = NirvanaParties.getInstance();

    public BasePartyCommand() {
        main.getFramework().registerCommands(this);
    }

    abstract public void onCommand(CommandArgs command);

}
