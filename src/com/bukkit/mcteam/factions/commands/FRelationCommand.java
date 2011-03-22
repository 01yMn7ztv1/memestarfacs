package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;

public class FRelationCommand extends FBaseCommand {
	
	public FRelationCommand() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("faction tag");
		helpDescription = "Set relation wish to another faction";
		permissions = "";
		
		senderMustBePlayer = true;
	}
	
	public void relation(Relation whishedRelation, String otherFactionName) {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		Faction otherFaction = findFaction(otherFactionName, false);
		if (otherFaction == null) {
			return;
		}
		
		if (otherFaction.getId() == 0) {
			sendMessage("Nope! You can't :) The default faction is not a real faction.");
			return;
		}
		
		if (otherFaction == myFaction) {
			sendMessage("Nope! You can't declare a relation to yourself :)");
			return;
		}
		
		myFaction.setRelationWish(otherFaction, whishedRelation);
		Relation currentRelation = myFaction.getRelation(otherFaction);
		ChatColor currentRelationColor = currentRelation.getColor();
		if (whishedRelation == currentRelation) {
			otherFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+myFaction.getTag());
			myFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+otherFaction.getTag());
		} else {
			otherFaction.sendMessage(currentRelationColor+myFaction.getTag()+Conf.colorSystem+ " wishes to be your "+whishedRelation.getColor()+whishedRelation.toString());
			otherFaction.sendMessage(Conf.colorSystem+"Type "+Conf.colorCommand+Factions.instance.getBaseCommand()+" "+whishedRelation+" "+myFaction.getTag()+Conf.colorSystem+" to accept.");
			myFaction.sendMessage(currentRelationColor+otherFaction.getTag()+Conf.colorSystem+ " were informed that you wish to be "+whishedRelation.getColor()+whishedRelation);
		}
	}
}
