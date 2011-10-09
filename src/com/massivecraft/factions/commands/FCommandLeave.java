package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Permission;

public class FCommandLeave extends FCommand {
	
	public FCommandLeave()
	{
		super();
		this.aliases.add("leave");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_LEAVE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		fme.leave(true);
	}
	
}
