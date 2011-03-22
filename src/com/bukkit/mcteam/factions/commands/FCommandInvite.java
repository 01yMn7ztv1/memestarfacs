package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandInvite extends FBaseCommand {
	
	public FCommandInvite() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("player name");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Invite a player";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if (you.getFaction() == myFaction) {
			sendMessage(you.getName()+" is already a member of "+myFaction.getTag());
			sendMessage("You might want to "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasKick.get(0)+Conf.colorParameter+" "+you.getName());
			return;
		}
		
		myFaction.invites.add(you.playerName);
		Faction.save();
		
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" invited you to "+myFaction.getTag(you));
		myFaction.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" invited "+you.getNameAndRelevant(me)+Conf.colorSystem+" to your faction.");
	}
	
}
