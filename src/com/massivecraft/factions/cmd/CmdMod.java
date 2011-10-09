package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdMod extends FCommand
{
	
	public CmdMod()
	{
		super();
		this.aliases.add("mod");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MOD.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() != myFaction)
		{
			sendMessageParsed("%s<b> is not a member in your faction.", you.getNameAndRelevant(fme));
			return;
		}
		
		if (you == fme)
		{
			sendMessageParsed("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Role.MODERATOR)
		{
			// Revoke
			you.setRole(Role.NORMAL);
			myFaction.sendMessageParsed("%s<i> is no longer moderator in your faction.", you.getNameAndRelevant(myFaction));
		}
		else
		{
			// Give
			you.setRole(Role.MODERATOR);
			myFaction.sendMessageParsed("%s<i> was promoted to moderator in your faction.", you.getNameAndRelevant(myFaction));
		}
	}
	
}
