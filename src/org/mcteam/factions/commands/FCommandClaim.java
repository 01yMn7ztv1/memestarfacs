package org.mcteam.factions.commands;

public class FCommandClaim extends FBaseCommand {
	
	public FCommandClaim() {
		aliases.add("claim");
		
		helpDescription = "Claim the land where you are standing";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		me.attemptClaim(true);
	}
	
}
