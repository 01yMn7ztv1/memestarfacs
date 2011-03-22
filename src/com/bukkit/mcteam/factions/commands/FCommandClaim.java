package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandClaim extends FBaseCommand {
	
	public FCommandClaim() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Claim the land where you are standing";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		FLocation flocation = new FLocation(me);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if (myFaction == otherFaction) {
			sendMessage("You already own this land.");
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		
		if (myFaction.getLandRounded() >= myFaction.getPowerRounded()) {
			sendMessage("You can't claim more land! You need more power!");
			return;
		}
		
		if (otherFaction.getRelation(me) == Relation.ALLY) {
			sendMessage("You can't claim the land of your allies.");
			return;
		}
		
		if (otherFaction.getId() != 0) {
			if ( ! otherFaction.hasLandInflation()) { // TODO more messages WARN current faction most importantly
				sendMessage(me.getRelationColor(otherFaction)+otherFaction.getTag()+Conf.colorSystem+" owns this land and is strong enough to keep it.");
				return;
			}
			
			if ( ! Board.isBorderLocation(flocation)) {
				sendMessage("You must start claiming land at the border of the territory.");
				return;
			}
		}
		
		if (otherFaction.getId() == 0) {
			myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some new land :D");
		} else {
			// ASDF claimed some of your land 450 blocks NNW of you.
			// ASDf claimed some land from FACTION NAME
			otherFaction.sendMessage(me.getNameAndRelevant(otherFaction)+Conf.colorSystem+" stole some of your land :O");
			myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some land from "+otherFaction.getTag(myFaction));
		}
		
		Board.setFactionAt(myFaction, flocation);
	}
	
}
