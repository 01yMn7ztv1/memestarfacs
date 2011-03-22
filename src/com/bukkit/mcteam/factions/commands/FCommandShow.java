package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;
import java.util.Collection;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandShow extends FBaseCommand {
	
	public FCommandShow() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		optionalParameters.add("faction tag");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Show faction information";
	}
	
	public void perform() {
		Faction faction;
		if (parameters.size() > 0) {
			faction = findFaction(parameters.get(0), true);
		} else {
			faction = me.getFaction();
		}
		
		Collection<FPlayer> admins = faction.getFPlayersWhereRole(Role.ADMIN);
		Collection<FPlayer> mods = faction.getFPlayersWhereRole(Role.MODERATOR);
		Collection<FPlayer> normals = faction.getFPlayersWhereRole(Role.NORMAL);
		
		sendMessage(TextUtil.titleize(faction.getTag(me)));
		sendMessage(Conf.colorChrome+"Description: "+Conf.colorSystem+faction.getDescription());
		if (faction.id == 0) {
			return;
		}
		
		if(faction.getOpen()) {
			sendMessage(Conf.colorChrome+"Joining: "+Conf.colorSystem+"no invitation is needed");
		} else {
			sendMessage(Conf.colorChrome+"Joining: "+Conf.colorSystem+"invitation is required");
		}
		sendMessage(Conf.colorChrome+"Land / Power / Maxpower: "+Conf.colorSystem+ faction.getLandRounded()+" / "+faction.getPowerRounded()+" / "+faction.getPowerMaxRounded());
	
		String listpart;
		
		// List relation
		String allyList = Conf.colorChrome+"Allies: ";
		String enemyList = Conf.colorChrome+"Enemies: ";
		for (Faction otherFaction : Faction.getAll()) {
			if (otherFaction == faction) {
				continue;
			}
			listpart = otherFaction.getTag(me)+Conf.colorSystem+", ";
			if (otherFaction.getRelation(faction) == Relation.ALLY) {
				allyList += listpart;
			} else if (otherFaction.getRelation(faction) == Relation.ENEMY) {
				enemyList += listpart;
			}
		}
		if (allyList.endsWith(", ")) {
			allyList = allyList.substring(0, allyList.length()-2);
		}
		if (enemyList.endsWith(", ")) {
			enemyList = enemyList.substring(0, enemyList.length()-2);
		}
		
		sendMessage(allyList);
		sendMessage(enemyList);
		
		// List the members...
		String onlineList = Conf.colorChrome+"Members online: ";
		String offlineList = Conf.colorChrome+"Members offline: ";
		for (FPlayer follower : admins) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		for (FPlayer follower : mods) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		for (FPlayer follower : normals) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		
		if (onlineList.endsWith(", ")) {
			onlineList = onlineList.substring(0, onlineList.length()-2);
		}
		if (offlineList.endsWith(", ")) {
			offlineList = offlineList.substring(0, offlineList.length()-2);
		}
		
		sendMessage(onlineList);
		sendMessage(offlineList);
	}
	
}
