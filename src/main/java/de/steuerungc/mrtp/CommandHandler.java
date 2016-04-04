package de.steuerungc.mrtp;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 03.04.2016.
 */
public class CommandHandler implements TabExecutor {

    private Main m;
    private TaskHandler th;

    public CommandHandler(Main m, TaskHandler th) {
        this.m = m;
        this.th = th;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            this. proceedTeleport(commandSender);
            return true;
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    this.listWorlds(commandSender);
                    break;
                case "toggle":
                    this.toggleWorld(commandSender, null);
                    break;
                case "reload":
                    this.reload(commandSender);
                    break;
                default:
                    this. proceedTeleport(commandSender);
                    break;
            }
            return true;
        } else if (args.length == 2 && args[0].toLowerCase().equals("toggle")) {
            this.toggleWorld(commandSender, args[1]);
            return true;
        } else {
            this. proceedTeleport(commandSender);
            return true;
        }
    }

    private void proceedTeleport(CommandSender cs) {
        if (cs instanceof Player) {
            this.proceedTeleport((Player)cs);

        } else {
            cs.sendMessage("§4Sorry. Only players can teleport.");
        }
    }

    public void proceedTeleport(Player p) {
        if (m.isWorking()) {
            if (p.hasPermission("mrtp.teleport.command")) {
                if (m.sendConfig().getBoolean("anticheat") && !p.hasPermission("mrtp.util.cheat")) {
                    if (p.getLocation().getBlock().getType().equals(Material.STATIONARY_LAVA) ||
                            p.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER) ||
                            (p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)  &&
                                    !p.isFlying()) ||
                            p.isSleeping() ||
                            p.getFireTicks() > 0)
                    {
                        p.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.anticheat"));
                        return;
                    } else {
                        th.performTP(p, p.getWorld().getName());
                    }
                } else {
                    th.performTP(p, p.getWorld().getName());
                }
            } else {
                p.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.permission"));
            }
        } else {
            p.sendMessage("§4There is an internal error in the plugins configuration. Please refer to log for further information");
        }
    }

    public void toggleWorld(CommandSender s, String world) {
        if (m.isWorking()) {
            if (s.hasPermission("mrtp.util.toggle")) {
                m.wc.toggleAll(s, world);
            } else {
                s.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.permission"));
            }
        } else {
            s.sendMessage("§4There is an internal error in the plugins configuration. Please refer to log for further information");
        }
    }

    public void listWorlds(CommandSender s) {
        if (m.isWorking()) {
            if (s.hasPermission("mrtp.util.list")) {
                m.wc.list(s);
            } else {
                s.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.permission"));
            }
        } else {
            s.sendMessage("§4There is an internal error in the plugins configuration. Please refer to log for further information");
        }
    }

    public void reload(CommandSender s) {
        if (s.hasPermission("mrtp.util.reload")) {
            String savedpre = m.sendConfig().getMessage("messages.prefix");
            s.sendMessage(savedpre + " §rStarting to reload the plugin...");
            m.performReload(s, savedpre);
        } else {
            s.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.permission"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> ret = new ArrayList<String>() {
        };
        if(args.length == 1) {
            if(commandSender.hasPermission("mrtp.util.toggle")) {
                ret.add("toggle");
            }
            if(commandSender.hasPermission("mrtp.util.list")) {
                ret.add("list");
            }
            if(commandSender.hasPermission("mrtp.util.reload")) {
                ret.add("reload");
            }
            return ret;
        } else if (args.length == 2){
            if (args[0].equals("toggle") && commandSender.hasPermission("mrtp.util.toggle")) {
                ret.addAll(m.wc.getWorlds());
            }
            return ret;
        }
        return null;
    }
}
