package com.massivecraft.factions.commands;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class FCommandCreate extends FCommand
{
	public FCommandCreate()
	{
		super();
		this.aliases.add("create");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.CREATE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		String tag = this.argAsString(0);
		
		if (fme.hasFaction())
		{
			sendMessage("You must leave your current faction first.");
			return;
		}
		
		if (Factions.i.isTagTaken(tag))
		{
			sendMessage("That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = Factions.validateTag(tag);
		if (tagValidationErrors.size() > 0)
		{
			sendMessage(tagValidationErrors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostCreate))
		{
			return;
		}

		Faction faction = Factions.i.create();
		faction.setTag(tag);
		fme.setRole(Role.ADMIN);
		fme.setFaction(faction);

		for (FPlayer follower : FPlayers.i.getOnline())
		{
			follower.sendMessageParsed("%s<i> created a new faction %s", fme.getNameAndRelevant(follower), faction.getTag(follower));
		}
		
		sendMessage("You should now: " + new FCommandDescription().getUseageTemplate());
	}
	
}
