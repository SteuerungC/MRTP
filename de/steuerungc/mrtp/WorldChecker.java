package de.steuerungc.mrtp;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Martin on 03.04.2016.
 */
public class WorldChecker {

    private HashMap<String, Boolean> worlds;
    private boolean allState = true;
    private String prefix;

    public WorldChecker (List<String> worldlist, String prefix) {
        worlds = new HashMap<>();
        this.prefix = prefix;
        for (String s : worldlist) {
            worlds.put(s, true);
        }
    }

    public List<String> getWorlds() {
        List<String> ret = new ArrayList<>();
        for (String s: worlds.keySet()) {
            ret.add(s);
        }
        return ret;
    }

    public boolean isValid (String world) {
        if (worlds.containsKey(world) && worlds.get(world) == true) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInConfig (String world) {
        return worlds.containsKey(world);
    }

    public void toggleAll (CommandSender se, String world) {
        if (world == null) {
            if (allState) {
                Set<String> ms = worlds.keySet();
                HashMap<String, Boolean> tmp = new HashMap<>();
                for (String s : ms) {
                   tmp.put(s, false);
                }
                worlds = tmp;
                se.sendMessage(prefix + " §rRandomTP is now §4disabled §reverywhere.");
                allState = false;
            } else {
                Set<String> ms = worlds.keySet();
                HashMap<String, Boolean> tmp = new HashMap<>();
                for (String s : ms) {
                    tmp.put(s, true);
                }
                worlds = tmp;
                se.sendMessage(prefix + " §rRandomTP is now §2allowed §reverywhere.");
                allState = true;
            }
        } else {
            if (worlds.containsKey(world)) {
                if(worlds.get(world)) {
                    worlds.remove(world);
                    worlds.put(world, false);
                    se.sendMessage(prefix + " §rRandomTP is now §4disabled §rin World§7 " + world + "§r.");
                } else {
                    worlds.remove(world);
                    worlds.put(world, true);
                    se.sendMessage(prefix + " §rRandomTP is now §2enabled §rin World§7 " + world + "§r.");
                }
            } else {
                se.sendMessage(prefix + " §cWorld §4" + world + " §cis not configured.");
            }
        }
    }

    public void list (CommandSender se) {
        if (!worlds.containsValue(false)) {
            se.sendMessage(prefix + " §rAll wolrds are §2enabled§r.");
        } else if (!worlds.containsValue(true)) {
            se.sendMessage(prefix + " §rAll wolrds are §4disabled§r.");
        } else {
            String off = "";
            String on = "";
            boolean doff = true;
            boolean don = true;
            Set<String> ms = worlds.keySet();
            for (String s : ms) {
                if (worlds.get(s)) {
                    if (don) {
                       on += s;
                        don = false;
                    } else {
                        on += ", " + s;
                    }
                } else {
                    if (doff) {
                        off += s;
                        doff = false;
                    } else {
                        off += ", " + s;
                    }
                }
            }
            se.sendMessage(prefix + " §rList of all worlds and current states:");
            se.sendMessage("§4Disabled: §r" + off);
            se.sendMessage("§2Enabled: §r" + on);
        }
    }

}


