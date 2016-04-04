package de.steuerungc.mrtp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Martin on 03.04.2016.
 */
public class ActionHandler implements Listener {

    private Main m;
    private TaskHandler th;

    public ActionHandler(Main m, TaskHandler th) {
        this.m = m;
        this.th = th;
    }


    @EventHandler
    protected void onLogout(PlayerQuitEvent pqe) {
        Run.remove(pqe.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent sce) {
        if (!sce.getLines()[0].equalsIgnoreCase("[randomtp]")) {
            return;
        }
        if (!sce.getPlayer().hasPermission("mrtp.util.create")) {
            return;
        }

        sce.setLine(0, ChatColor.BLUE + "[RandomTP]");
        sce.getPlayer().sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.sign"));
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (pie.getHand() !=  org.bukkit.inventory.EquipmentSlot.HAND)) {
            return;
        }

        if ((!pie.getClickedBlock().getType().equals(Material.SIGN_POST)) && (!pie.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
            return;
        }

        Sign sign;
        try {
            sign = (Sign) pie.getClickedBlock().getState();
        } catch (Exception ex) {
            return;
        }

        if (!sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[randomtp]")) {
            return;
        }

        if (!pie.getPlayer().hasPermission("mrtp.teleport.sign")) {
            pie.getPlayer().sendMessage(m.sendConfig().getMessage("messages.prefix") + "§r " + m.sendConfig().getMessage("messages.permission"));
            return;
        }

        if (m.wc.isInConfig(sign.getLine(1))) {
            th.performTP(pie.getPlayer(), sign.getLine(1));
            return;
        } else {
            th.performTP(pie.getPlayer(), sign.getWorld().getName());
            return;
        }
    }
}
