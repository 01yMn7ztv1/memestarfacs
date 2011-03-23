package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FBaseCommand {
	public List<String> aliases;
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	
	public String helpNameAndParams;
	public String helpDescription;
	
	public CommandSender sender;
	public boolean senderMustBePlayer;
	public Player player;
	public FPlayer me;
	
	public List<String> parameters;
	
	
	public FBaseCommand() {
		aliases = new ArrayList<String>();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		senderMustBePlayer = true;
		
		helpNameAndParams = "fail!";
		helpDescription = "no description";
	}
	
	public List<String> getAliases() {
		return aliases;
	}
		
	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;
		
		if ( ! validateCall()) {
			return;
		}
		
		if (this.senderMustBePlayer) {
			this.player = (Player)sender;
			this.me = FPlayer.get(this.player);
		}
		
		perform();
	}
	
	public void perform() {
		
	}
	
	public void sendMessage(String message) {
		sender.sendMessage(Conf.colorSystem+message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}
	
	public boolean validateCall() {
		if ( this.senderMustBePlayer && ! (sender instanceof Player)) {
			sendMessage("This command can only be used by ingame players.");
			return false;
		}
		
		if( ! hasPermission(sender)) {
			sendMessage("You lack the permissions to "+this.helpDescription.toLowerCase()+".");
			return false;
		}
		
		if (parameters.size() < requiredParameters.size()) {
			sendMessage("Usage: "+this.getUseageTemplate(true));
			return false;
		}
		
		return true;
	}
	
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermParticipate(sender);
	}
	
	/*public boolean testPermission(CommandSender sender) {
		// There are two cases where we default to op:
		// 1. Permissions is not installed
		// 2. The sender is not a player
		if ( Factions.Permissions == null || (! (sender instanceof Player))) {
			if (this.opOnly && sender.isOp()) {
				return true;
			}
			return false;
		}
		
		// No permissions are needed to use this command.
		if (this.permissions.length() == 0) {
			return true;
		}
		
		Player player = (Player)sender;
		return Factions.Permissions.has(player, this.permissions);		
	}*/
	
	// -------------------------------------------- //
	// Help and usage description
	// -------------------------------------------- //
	public String getUseageTemplate(boolean withColor, boolean withDescription) {
		String ret = "";
		
		if (withColor) {
			ret += Conf.colorCommand;
		}
		
		ret += Factions.instance.getBaseCommand()+ " " +TextUtil.implode(this.getAliases(), ",")+" ";
		
		List<String> parts = new ArrayList<String>();
		
		for (String requiredParameter : this.requiredParameters) {
			parts.add("["+requiredParameter+"]");
		}
		
		for (String optionalParameter : this.optionalParameters) {
			parts.add("*["+optionalParameter+"]");
		}
		
		if (withColor) {
			ret += Conf.colorParameter;
		}
		
		ret += TextUtil.implode(parts, " ");
		
		if (withDescription) {
			ret += "  "+Conf.colorSystem + this.helpDescription;
		}
		return ret;
	}
	
	public String getUseageTemplate(boolean withColor) {
		return getUseageTemplate(withColor, false);
	}
	
	public String getUseageTemplate() {
		return getUseageTemplate(true);
	}
	
	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //
	
	public boolean assertHasFaction() {
		if ( ! me.hasFaction()) {
			sendMessage("You are not member of any faction.");
			return false;
		}
		return true;
	}
	
	public boolean assertMinRole(Role role) {
		if (me.getRole().value < role.value) {
			sendMessage("You must be "+role+" to "+this.helpDescription+".");
			return false;
		}
		
		return true;
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public FPlayer findFPlayer(String playerName, boolean defaultToMe) {
		FPlayer fp = FPlayer.find(playerName);
		
		if (fp == null) {
			if (defaultToMe) {
				return me;
			}
			sendMessage("The player \""+playerName+"\" could not be found");
		}
		
		return fp;
	}
	
	public FPlayer findFPlayer(String playerName) {
		return findFPlayer(playerName, false);
	}
	
	
	public Faction findFaction(String factionName, boolean defaultToMine) {
		// First we search player names
		FPlayer fp = FPlayer.find(factionName);
		if (fp != null) {
			return fp.getFaction();
		}
		
		// Secondly we search faction names
		Faction faction = Faction.findByTag(factionName);
		if (faction != null) {
			return faction;
		}
		
		if (defaultToMine) {
			return me.getFaction();
		}
		
		me.sendMessage(Conf.colorSystem+"No faction or player \""+factionName+"\" was found");
		return null;
	}
	
	public Faction findFaction(String factionName) {
		return findFaction(factionName, false);
	}
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you) {
		if ( ! i.getFaction().equals(you.getFaction())) {
			i.sendMessage(you.getNameAndRelevant(i)+Conf.colorSystem+" is not in the same faction as you.");
			return false;
		}
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) ) {
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN)) {
			i.sendMessage(Conf.colorSystem+"Only the faction admin can do that.");
		} else if (i.getRole().equals(Role.MODERATOR)) {
			i.sendMessage(Conf.colorSystem+"Moderators can't control each other...");
		} else {
			i.sendMessage(Conf.colorSystem+"You must be a faction moderator to do that.");
		}
		
		return false;
	}
	
	public boolean parseBool(String str) {
		List<String> aliasTrue = new ArrayList<String>();
		aliasTrue.add("true");
		aliasTrue.add("yes");
		aliasTrue.add("y");
		aliasTrue.add("ok");
		aliasTrue.add("on");
		aliasTrue.add("+");
		
		return aliasTrue.contains(str.toLowerCase());
	}
}
