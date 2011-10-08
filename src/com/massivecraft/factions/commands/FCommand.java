package com.massivecraft.factions.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.MCommand;


public abstract class FCommand extends MCommand<P>
{
	//TODO: Legacy to handle
	//public boolean senderIsConsole;	
	//private static boolean lock = false;
	
	
	public FPlayer fme;
	public boolean senderMustBeMember;
	public boolean senderMustBeModerator;
	public boolean senderMustBeAdmin;
	
	public FCommand()
	{
		super(P.p);
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain)
	{
		if (sender instanceof Player)
		{
			this.fme = FPlayers.i.get((Player)sender);
		}
		else
		{
			this.fme = null;
		}
		super.execute(sender, args, commandChain);
	}
	
	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot)
	{
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if ( ! superValid) return false;
		
		if ( ! (this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin)) return true;
		
		if ( ! (sender instanceof Player)) return false;
		
		FPlayer fplayer = FPlayers.i.get((Player)sender);
		
		if ( ! fplayer.hasFaction())
		{
			sender.sendMessage(p.txt.parse("<b>You are not member of any faction."));
			return false;
		}
		
		if (this.senderMustBeModerator && ! fplayer.getRole().isAtLeast(Role.MODERATOR))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction moderators can %s.", this.helpShort));
			return false;
		}
		
		if (this.senderMustBeAdmin && ! fplayer.getRole().isAtLeast(Role.ADMIN))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction admins can %s.", this.helpShort));
			return false;
		}
			
		return true;
	}
	
	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	
	// ARG AS FPLAYER
	public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.get(name); 
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def)
	{
		return this.argAsFPlayer(idx, def, true);
	}
	public FPlayer argAsFPlayer(int idx)
	{
		return this.argAsFPlayer(idx, null);
	}
	
	// ARG AS BEST FPLAYER MATCH
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.find(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def)
	{
		return this.argAsBestFPlayerMatch(idx, def, true);
	}
	public FPlayer argAsBestFPlayerMatch(int idx)
	{
		return this.argAsBestFPlayerMatch(idx, null);
	}
	
	// ARG AS FACTION
	public Faction argAsFaction(int idx, Faction def, boolean msg)
	{
		Faction ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			// First we search faction names
			Faction faction = Factions.i.findByTag(name);
			if (faction != null)
			{
				ret = faction;
			}

			// Next we search player names
			FPlayer fplayer = FPlayers.i.find(name);
			if (fplayer != null)
			{
				ret = fplayer.getFaction();
			}
			
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The faction or player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public Faction argAsFaction(int idx, Faction def)
	{
		return this.argAsFaction(idx, def, true);
	}
	public Faction argAsFaction(int idx)
	{
		return this.argAsFaction(idx, null);
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(p.txt.parse("%s <b>is not in the same faction as you.",you.getNameAndRelevant(i)));
			return false;
		}
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) )
		{
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN))
		{
			i.sendMessage(p.txt.parse("<b>Only the faction admin can do that."));
		}
		else if (i.getRole().equals(Role.MODERATOR))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(p.txt.parse("<b>Moderators can't control each other..."));
			}
		}
		else
		{
			i.sendMessage(p.txt.parse("<b>You must be a faction moderator to do that."));
		}
		
		return false;
	}
	
	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost)
	{
		if ( ! Econ.enabled() || this.me == null || cost == 0.0 || Conf.adminBypassPlayers.contains(me.getName()))
		{
			return true;
		}

		String desc = this.helpShort.toLowerCase();

		Faction faction = fme.getFaction();
		
		// pay up
		if (cost > 0.0)
		{
			String costString = Econ.moneyString(cost);
			if(Conf.bankFactionPaysCosts && fme.hasFaction() )
			{
				if(!faction.removeMoney(cost))
				{
					sendMessage("It costs "+costString+" to "+desc+", which your faction can't currently afford.");
					return false;
				}
				else
				{
					sendMessage(faction.getTag()+" has paid "+costString+" to "+desc+".");
				}
					
			}
			else
			{
				if (!Econ.deductMoney(me.getName(), cost))
				{
					sendMessage("It costs "+costString+" to "+desc+", which you can't currently afford.");
					return false;
				}
				sendMessage("You have paid "+costString+" to "+desc+".");
			}
		}
		// wait... we pay you to use this command?
		else
		{
			String costString = Econ.moneyString(-cost);
			
			if(Conf.bankFactionPaysCosts && fme.hasFaction() )
			{
				faction.addMoney(-cost);
				sendMessage(faction.getTag()+" has been paid "+costString+" to "+desc+".");
			}
			else
			{
				Econ.addMoney(me.getName(), -cost);
			}
			
			
			sendMessage("You have been paid "+costString+" to "+desc+".");
		}
		return true;
	}
	
	
	// TODO: Move these messages to the locked command??
	// TODO: I lost the check for this code somewhere as well :/
	public void setIsLocked(boolean isLocked)
	{
		if( isLocked )
		{
			sendMessage("Factions is now locked");
		}
		else
		{
			sendMessage("Factions in now unlocked");
		}
		
		lock = isLocked;
	}
	
	public boolean isLocked()
	{
		return lock;
	}
	
	public void sendLockMessage()
	{
		me.sendMessage("Factions is locked. Please try again later");
	}
}
