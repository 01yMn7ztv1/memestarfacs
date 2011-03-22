package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandOpen extends FBaseCommand {
	
	public FCommandOpen() {
		aliases = new ArrayList<String>();
		aliases.add("open");
		aliases.add("close");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Switch if invitation is required to join";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		myFaction.setOpen( ! me.getFaction().getOpen());
		
		String open = myFaction.getOpen() ? "open" : "closed";
		
		// Inform
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed the faction to "+open);
		for (Faction faction : Faction.getAll()) {
			if (faction == me.getFaction()) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+myFaction.getTag(faction)+Conf.colorSystem+" is now "+open);
		}
	}
	
}
