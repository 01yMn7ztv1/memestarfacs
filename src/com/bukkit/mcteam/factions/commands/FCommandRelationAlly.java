package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.struct.Relation;

public class FCommandRelationAlly extends FRelationCommand {
	
	public FCommandRelationAlly() {
		aliases = new ArrayList<String>();
		aliases.add("ally");
	}
	
	public void perform() {
		relation(Relation.ALLY, parameters.get(0));
	}
	
}
