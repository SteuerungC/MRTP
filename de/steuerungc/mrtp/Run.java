package de.steuerungc.mrtp;

import de.steuerungc.mrtp.world.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Martin on 03.04.2016.
 */
public class Run implements Runnable {

    private static HashMap<UUID, Integer> tasks = new HashMap();
    private UUID player;
    private String world, prefix, sucess, poor_player, took_money;
    private Mode mode;
    private int stop, savespawn;
    private double cost = 0.0D;

    public Run (Main m, UUID player, String world) {
        this.player = player;

        if(tasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(tasks.get(player));
        }

        List<String> biomes = m.sendConfig().getStringList("disabled.biomes");
        List<String> blocks = m.sendConfig().getStringList("disabled.blocks");
        this.prefix = m.sendConfig().getMessage("messages.prefix");
        this.sucess = m.sendConfig().getMessage("messages.success");
        this.poor_player = m.sendConfig().getMessage("messages.poor_player");
        this.took_money = m.sendConfig().getMessage("messages.took_money");
        this.world = world;
        stop = m.sendConfig().getInteger("interrupt");

        if (m.sendConfig().getBoolean("vault.enabled")) {
            cost = m.sendConfig().getDouble("vault.price");
        }

        int minimal;
        try {
            minimal = m.sendConfig().getInteger(world + ".minimal-dist");
        } catch (Exception ex) {
            minimal = -1;
        }

        try {
           savespawn = m.sendConfig().getInteger(world + ".save-spawn");
        } catch (Exception ex) {
            savespawn = 0;
        }
        int downfall;
        try {
            downfall = m.sendConfig().getInteger(world + ".falldown");
        } catch (Exception ex){
            downfall = -1;
        }

        switch (m.sendConfig().getString(world + ".mode")) {
            case "auto":
                mode = new AutoMode(biomes, blocks, world, downfall, minimal);
                break;
            case "round":
                try {
                    mode = new RoundMode(biomes,
                            blocks,
                            world,
                            m.sendConfig().getInteger(world + ".radius"),
                            m.sendConfig().getInteger(world + ".spawn-x"),
                            m.sendConfig().getInteger(world + ".spawn-z"),
                            downfall,
                            minimal);
                } catch (Exception ex) {
                    mode = new RoundMode(biomes,
                            blocks,
                            world,
                            m.sendConfig().getInteger(world + ".radius"),
                            downfall,
                            minimal);
                }
                break;
            case "rect":
                try {
                    mode = new RectMode(biomes,
                            blocks,
                            world,
                            m.sendConfig().getInteger(world + ".dist-x"),
                            m.sendConfig().getInteger(world + ".dist-z"),
                            m.sendConfig().getInteger(world + ".spawn-x"),
                            m.sendConfig().getInteger(world + ".spawn-z"),
                            downfall,
                            minimal);
                } catch (Exception ex) {
                    mode = new RectMode(biomes,
                            blocks,
                            world,
                            m.sendConfig().getInteger(world + ".dist-x"),
                            m.sendConfig().getInteger(world + ".dist-z"),
                            downfall,
                            minimal);
                }
                break;
            case "plugin":
                mode = new WBMode(biomes, blocks, world, downfall, minimal);
                break;
            default:
                Bukkit.getServer().getPlayer(player).sendMessage("§Sorry. Unable to complete generation of the teleport task.");
                break;
        }

    }

    public void run() {
        int count = 0;
        Player p = Bukkit.getServer().getPlayer(player);
        Location l = null;

        while (l == null && count < stop) {
            l = mode.generateLocation();
            count++;
        }

        if(l == null) {
            p.sendMessage("§4Can't find save location in " + stop +  " attempts.");
            return;
        }

        if (cost != 0.0D && !p.hasPermission("mrtp.teleport.free")) {
            Economy eco = null;
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                eco = rsp.getProvider();
            }

            EconomyResponse.ResponseType type = eco.bankWithdraw(p.getName(), cost).type;
            if (type.equals(EconomyResponse.ResponseType.SUCCESS)) {
                p.sendMessage(prefix + " §r" + took_money.replace("%cost%", "" + cost));
            } else {
                p.sendMessage(prefix + " §r" + poor_player);
                remove(player);
                tasks.remove(player);
                return;
            }
        }

        if (!p.hasPermission("mrtp.util.nocooldown")) {
            Cooldown.start(p.getUniqueId());
        }

        if(savespawn > 0) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, savespawn*20, 100));
        }

        l.getChunk().load(true);
        p.teleport(l);
        p.sendMessage(prefix + "§r " + sucess);
        remove(player);
        tasks.remove(player);
    }

    public static void remove(UUID id) {
        if (tasks.containsKey(id)) {
            Bukkit.getScheduler().cancelTask(tasks.get(id));
            tasks.remove(id);
        }
    }

    public void set(int taskid) {
        tasks.put(player, taskid);
    }
}
