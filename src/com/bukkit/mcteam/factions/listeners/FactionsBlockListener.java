package com.bukkit.mcteam.factions.listeners;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;


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

	//special cases, check for destruction of: torch, redstone torch (on & off), repeater (on & off), redstonewire, sapling, crops, sugar cane
	private static Set<Integer> specialBlocks = new HashSet<Integer>(Arrays.asList(
		 new Integer[] {50, 75, 76, 93, 94, 55, 6, 59, 83}
	));
	
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		// debug
		//event.getPlayer().sendMessage("Block damaged: " + event.getBlock().getTypeId() + " (" + event.getBlock().getType().toString() + ")");

		if (event.isCancelled()) {
			return; // Alright. lets listen to that.
		}

		boolean badBlock = event.getDamageLevel() == BlockDamageLevel.STOPPED || specialBlocks.contains(new Integer(event.getBlock().getTypeId()));
		if (badBlock && ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy")) {
			event.setCancelled(true);
		}
	}
	
	public boolean playerCanBuildDestroyBlock(Player player, Block block, String action) {
		Faction otherFaction = Board.getFactionAt(new FLocation(block));
		
		if (otherFaction == null || otherFaction.getId() == 0) {
			return true; // This is no faction territory. You may build or break stuff here.
		}
		
		FPlayer me = FPlayer.get(player);
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
		
		if (otherFaction != null && otherFaction.getId() != 0 && myFaction != otherFaction) {
			me.sendMessage(Conf.colorSystem+"You can't use "+TextUtil.getMaterialName(material)+" in the territory of "+otherFaction.getTag(myFaction));
			//otherFaction.sendMessage(me.getNameAndRelevant(otherFaction)+Conf.colorSystem+" tried to use "+TextUtil.getMaterialName(material)+" in your territory");
			return false;
		}
		return true;
	}
}
