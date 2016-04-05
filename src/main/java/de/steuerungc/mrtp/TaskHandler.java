package de.steuerungc.mrtp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Martin on 03.04.2016.
 */
public class TaskHandler {

    private Main m;
    private WorldChecker wc;

    public TaskHandler (Main m, WorldChecker wc) {
        this.m = m;
        this.wc = wc;
        if (m.sendConfig().getBoolean("cooldown.enabled")) {
            new Cooldown(m.sendConfig().getInteger("cooldown.time") + 1);
        } else {
            new Cooldown(0);
        }

    }

    protected void performTP (Player p, String target) {
        if (!wc.isValid(target)) {
            p.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.not_enabled"));
            return;
        }

        UUID player = p.getUniqueId();
        Long cooldown = Cooldown.test(player);

        if(cooldown < 1000) {
            p.sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.cooldown").replace("%time%", "" + cooldown / 1000));
            return;
        }

        Run r = new Run(m, player, target);
        int taskid = Bukkit.getScheduler().runTask(m, r).getTaskId();
        r.set(taskid);
    }
}
