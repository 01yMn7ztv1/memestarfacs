package com.bukkit.mcteam.factions.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FactionsBlockListener extends BlockListener {
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if ( ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "build")) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		boolean blockDestroyed = event.getDamageLevel() == BlockDamageLevel.STOPPED || Conf.instaDestroyMaterials.contains(event.getBlock().getType());
		
		if (blockDestroyed && ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy")) {
			event.setCancelled(true);
		}
	}
	
	public boolean playerCanBuildDestroyBlock(Player player, Block block, String action) {
		Faction otherFaction = Board.getFactionAt(new FLocation(block));
		
		if (otherFaction.isNone()) {
			return true;
		}
		
		FPlayer me = FPlayer.get(player);
		
		if (otherFaction.isSafeZone()) {
			if (Factions.hasPermManageSafeZone(player)) {
				return true;
			}
			me.sendMessage("You can't "+action+" in a safe zone.");
			return false;
		}
		
		Faction myFaction = me.getFaction();
		
		// Cancel if we are not in our own territory
		if (myFaction != otherFaction) {
			me.sendMessage("You can't "+action+" in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onBlockInteract(BlockInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if ( ! (event.getEntity() instanceof Player)) {
			// So far mobs does not interact with the environment :P
			return;
		}
	
		Block block = event.getBlock();
		Player player = (Player) event.getEntity();

		if ( ! canPlayerUseRightclickBlock(player, block)) {
			event.setCancelled(true);
		}
	}
	
	public boolean canPlayerUseRightclickBlock(Player player, Block block) {
		Material material = block.getType();

		// We only care about some material types.
		if ( ! Conf.territoryProtectedMaterials.contains(material)) {
			return true;
		}
		
		FPlayer me = FPlayer.get(player);
		Faction myFaction = me.getFaction();
		Faction otherFaction = Board.getFactionAt(new FLocation(block));
		
		// In safe zones you may use any block...
		if (otherFaction.isNormal() && myFaction != otherFaction) {
			me.sendMessage("You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}
		
		// You may use doors in both safeZone and wilderness
		return true;
	}
}
