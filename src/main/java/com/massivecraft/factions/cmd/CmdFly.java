package com.massivecraft.factions.cmd;

import com.darkblade12.particleeffect.ParticleEffect;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CmdFly extends FCommand {


    public static HashMap<String,Boolean> flyMap = new HashMap<String,Boolean>();
    public static int id = -1;
    public static int flyid = -1;
    public CmdFly() {
        super();
        this.aliases.add("fly");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.FLY.node;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        // Disabled by default.
        if (!P.p.getConfig().getBoolean("enable-faction-flight", false)) {
            fme.msg(TL.COMMAND_FLY_DISABLED);
            return;
        }

        FLocation myfloc = new FLocation(me.getLocation());
        Faction toFac = Board.getInstance().getFactionAt(myfloc);
        if (Board.getInstance().getFactionAt(myfloc) != fme.getFaction()){
            if (!me.hasPermission("factions.fly.wilderness") && toFac.isWilderness()) {
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.safezone") && toFac.isSafeZone()) {
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.warzone") && toFac.isWarZone()) {
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.enemy") && toFac.getRelationTo(myFaction)== Relation.ENEMY){
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.ally") && toFac.getRelationTo(myFaction)== Relation.ALLY){
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.truce") && toFac.getRelationTo(myFaction)== Relation.TRUCE){
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            if (!me.hasPermission("factions.fly.neutral") && toFac.getRelationTo(myFaction) == Relation.NEUTRAL) {
                fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                return;
            }
            }


        List<Entity> entities = me.getNearbyEntities(16,256,16);
        for (int i = 0; i <= entities.size() -1;i++)
        {
            if (entities.get(i) instanceof Player)
            {
                Player eplayer = (Player) entities.get(i);
                FPlayer efplayer = FPlayers.getInstance().getByPlayer(eplayer);
                if (efplayer.getRelationTo(fme) == Relation.ENEMY)
                {
                   fme.msg(TL.COMMAND_FLY_CHECK_ENEMY);
                    return;
                }
            }
        }



        if (args.size() == 0) {
            if (!fme.canFlyAtLocation() && !fme.isFlying()) {
                if (!me.hasPermission("factions.fly.wilderness") && toFac.isWilderness()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.safezone") && toFac.isSafeZone()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.warzone") && toFac.isWarZone()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.enemy") && toFac.getRelationTo(myFaction) == Relation.ENEMY) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.ally") && toFac.getRelationTo(myFaction) == Relation.ALLY) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.truce") && toFac.getRelationTo(myFaction) == Relation.TRUCE) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                } else if (!me.hasPermission("factions.fly.neutral") && toFac.getRelationTo(myFaction) == Relation.NEUTRAL) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                }

            }

            toggleFlight(!fme.isFlying(),me);
        } else if (args.size() == 1) {
            if (!fme.canFlyAtLocation() && argAsBool(0)) {
                if (!me.hasPermission("factions.fly.wilderness") && toFac.isWilderness()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.safezone") && toFac.isSafeZone()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.warzone") && toFac.isWarZone()) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.enemy") && toFac.getRelationTo(myFaction) == Relation.ENEMY) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.ally") && toFac.getRelationTo(myFaction) == Relation.ALLY) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                    return;
                } else if (!me.hasPermission("factions.fly.truce") && toFac.getRelationTo(myFaction) == Relation.TRUCE) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                } else if (!me.hasPermission("factions.fly.neutral") && toFac.getRelationTo(myFaction) == Relation.NEUTRAL) {
                    fme.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(myfloc).getTag(fme));
                }

            }


            toggleFlight(argAsBool(0),me);
        }
    }

    public boolean isInFlightChecker(Player player){
        if (flyMap.containsKey(player.getName())){
            return true;
        } else {
            return false;
        }
    }


    public static void startParticles() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(P.p, new Runnable() {
            @Override
            public void run() {
                for(String name : flyMap.keySet()){
                    Player player = Bukkit.getPlayer(name);
                    if (player == null){
                        continue;
                    }
                    if (!player.isFlying()){
                        continue;
                    }


                    ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, player.getLocation().add(0, -0.35, 0), 16);

                }
                if (flyMap.keySet().size() == 0){
                    Bukkit.getScheduler().cancelTask(id);
                    id = -1;
                }
            }
        }, 10L, 10L);
    }

    public static void startFlyCheck() {
        flyid = Bukkit.getScheduler().scheduleSyncRepeatingTask(P.p, new Runnable() {
            @Override
            public void run() {
                Iterator itr = flyMap.keySet().iterator();
                while (itr.hasNext()) {
                    Object nameObj = itr.next();
                    String name = nameObj + "";
                    Player player = Bukkit.getPlayer(name);
                    if (player == null) { continue;}
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

                    if (fPlayer == null) { continue; }
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR){
                        continue;
                    }
                    Faction myFaction = fPlayer.getFaction();
                    if (myFaction.isWilderness()){
                        fPlayer.setFlying(false);
                        flyMap.remove(name);
                        continue;
                    }
                    if(fPlayer.checkIfNearbyEnemies()){
                        continue;
                    }
                    FLocation myFloc = new FLocation(player.getLocation());
                    Faction toFac = Board.getInstance().getFactionAt(myFloc);
                    if (Board.getInstance().getFactionAt(myFloc) != myFaction) {
                        if (!checkBypassPerms(fPlayer,player,toFac)){
                            fPlayer.setFlying(false);
                            itr.remove();
                            continue;
                        }

                    }


                    checkTaskState();
                }
            }
        },20L,20L);
    }


    private static boolean checkBypassPerms(FPlayer fplayer, Player player, Faction toFac) {
        if (player.hasPermission("factions.fly.wilderness") && toFac.isWilderness()){
           return true;
        }
        if (player.hasPermission("factions.fly.warzone") && toFac.isWarZone()){
          return true;
        }
        if (player.hasPermission("factions.fly.safezone") && toFac.isSafeZone()){
            return true;
        }
        if (player.hasPermission("factions.fly.enemy") && toFac.getRelationTo(fplayer.getFaction()) == Relation.ENEMY) {
            return true;
        }
        if (player.hasPermission("factions.fly.ally") && toFac.getRelationTo(fplayer.getFaction()) == Relation.ALLY) {
            return true;
        }
        if (player.hasPermission("factions.fly.truce") && toFac.getRelationTo(fplayer.getFaction()) == Relation.TRUCE) {
            return true;
        }
        if (player.hasPermission("factions.fly.neutral") && toFac.getRelationTo(fplayer.getFaction()) == Relation.NEUTRAL) {
            return true;
        }
        return false;
    }

    public static void checkTaskState() {
        if (flyMap.keySet().size() == 0) {
            Bukkit.getScheduler().cancelTask(flyid);
            flyid = -1;
        }
    }


    private void toggleFlight(final boolean toggle, final Player player) {
        if (!toggle) {
            fme.setFlying(false);
            flyMap.remove(player.getName());
            return;
        }
        
        this.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", new Runnable() {
            @Override
            public void run() {
                fme.setFlying(true);
                flyMap.put(player.getName(),true);
                if (id == -1){
                    if (P.p.getConfig().getBoolean("ffly.Particles.Enabled")){
                        startParticles();
                    }
                }
                if (flyid == -1){
                    startFlyCheck();
                }
            }
        }, this.p.getConfig().getLong("warmups.f-fly", 0));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
