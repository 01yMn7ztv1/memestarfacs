package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;

public class FCommandNoBoom extends FCommand {

	public FCommandNoBoom() {
		aliases.add("noboom");
		
		helpDescription = "Peaceful factions only: toggle explosions";
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return P.hasPermPeacefulExplosionToggle(sender);
	}

	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}

		if( isLocked() ) {
			sendLockMessage();
			return;
		}

		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}

		Faction myFaction = fme.getFaction();

		if (!myFaction.isPeaceful()) {
			fme.sendMessage("This command is only usable by factions which are specially designated as peaceful.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostNoBoom)) {
			return;
		}

		myFaction.setPeacefulExplosions();

		String enabled = myFaction.noExplosionsInTerritory() ? "disabled" : "enabled";

		// Inform
		myFaction.sendMessage(fme.getNameAndRelevant(myFaction)+Conf.colorSystem+" has "+enabled+" explosions in your faction's territory.");
	}

}
