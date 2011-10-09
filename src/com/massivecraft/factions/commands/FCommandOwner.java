package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;


public class FCommandOwner extends FCommand {
	
	public FCommandOwner() {
		aliases.add("owner");

		optionalParameters.add("player name");

		helpDescription = "set ownership of claimed land";
	}
	
	@Override
	public void perform() {
		boolean hasBypass = P.hasPermAdminBypass(fme);

		if ( ! hasBypass && ! assertHasFaction()) {
			return;
		}

		if( isLocked() ) {
			sendLockMessage();
			return;
		}

		if ( ! Conf.ownedAreasEnabled) {
			fme.sendMessage("Sorry, but owned areas are disabled on this server.");
			return;
		}

		Faction myFaction = fme.getFaction();

		if (!hasBypass && Conf.ownedAreasLimitPerFaction > 0 && myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction) {
			fme.sendMessage("Sorry, but you have reached the server's limit of "+Conf.ownedAreasLimitPerFaction+" owned areas per faction.");
			return;
		}

		if (!hasBypass && !assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.ADMIN)) {
			return;
		}

		FLocation flocation = new FLocation(fme);

		if (Board.getIdAt(flocation) != myFaction.getId()) {
			if (!hasBypass) {
				fme.sendMessage("This land is not claimed by your faction, so you can't set ownership of it.");
				return;
			}

			myFaction = Board.getFactionAt(flocation);
			if (!myFaction.isNormal()) {
				fme.sendMessage("This land is not claimed by a faction. Ownership is not possible.");
				return;
			}
		}

		FPlayer target;

		if (parameters.size() > 0) {
			target = findFPlayer(parameters.get(0), false);
		} else {
			target = fme;
		}
		if (target == null) {
			return;
		}

		String playerName = target.getName();

		if (target.getFaction().getId() != myFaction.getId()) {
			fme.sendMessage(playerName + " is not a member of this faction.");
			return;
		}

		// if no player name was passed, and this claim does already have owners set, clear them
		if (parameters.isEmpty() && myFaction.doesLocationHaveOwnersSet(flocation)) {
			myFaction.clearClaimOwnership(flocation);
			fme.sendMessage("You have cleared ownership for this claimed area.");
			return;
		}

		if (myFaction.isPlayerInOwnerList(playerName, flocation)) {
			myFaction.removePlayerAsOwner(playerName, flocation);
			fme.sendMessage("You have removed ownership of this claimed land from "+playerName+".");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostOwner)) {
			return;
		}

		myFaction.setPlayerAsOwner(playerName, flocation);
		fme.sendMessage("You have added "+playerName+" to the owner list for this claimed land.");
	}
}
