package de.steuerungc.mrtp;

import com.wimbli.WorldBorder.BorderData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 02.04.2016.
 * This class checks all important and needed config values for MRTP v0.1
 */
public class ConfigValidator {

    Config c = null;
    Main m ;
    ArrayList<String> ret;

    public ArrayList<String> validateConfig(Config c, Main m) {
        ret = new ArrayList<>();
        this.c = c;
        this.m = m;
        this.checkConfigured();
        this.checkWorlds();
        this.checkMessages();
        this.checkCooldown();
        this.checkVault();
        this.checkAnticheat();
        this.checkInterrupt();

        if(ret.size() <= 0) {
            return null;
        } else {
            return ret;
        }
    }

    /**
     * Checking Node "configured".
     * Wrong if:
     *      - not readable
     *      - not a boolean value
     *      - not true
     */
    private void checkConfigured() {
        String s = null;
        try {
            s = c.getConfig().getString("configured");
            if (s == null || s.equals("")) {
                ret.add("- Can't load configured: The value is not set!");
            } else {
                if (!Boolean.parseBoolean(s)) {
                    ret.add("- Please configure the Plugin and set configured to true!");
                }
            }
        } catch (Exception ex) {
            ret.add("- Can't load configured: The value " + s + " is not a boolean-value!");
        }
    }

    /**
     * Checking Node "enabled_worlds" and all further per-world-settings
     * The list "enabled_worlds" has to contain one world.
     * Every world in this list needs an own entry for per world setting.
     * A world needs the mode:
     *      - rect with dist-x and dist-y
     *      - round with a radius
     *      - auto with working default WorldBorder
     */
    private void checkWorlds() {

        List<String> li;
        try {
            li = c.getConfig().getStringList("enabled_worlds");
            if (li.size() <= 0) {
                ret.add("- No world is enabled?");
                return;
            }
        } catch (Exception ex) {
            ret.add("- Can't load enabled-worlds: Not set?");
            return;
        }

        for (String s : li) {
            String mode;
            try {
                mode = c.get(s).getString("mode");
                if (mode == null || mode.equals("")) {
                    ret.add("- No mode set for world " + s + ". Is the complete setting missing?");
                    continue;
                }
            } catch (Exception ex) {
                ret.add("- No mode set for world " + s + ". Is the complete setting missing?");
                continue;
            }
            switch (mode.toLowerCase()) {
                case "rect":
                    String x = c.get(s).getString("dist-x");
                    String z = c.get(s).getString("dist-z");
                    int x1 = 0;
                    int z1 = 0;
                    if (x == null || x.equals("")) {
                        ret.add("- Value missing for world " + s + " in mode 'rect': dist-x is not set or missing");
                    } else {
                        try {
                            x1 = Integer.parseInt(x);
                        } catch (Exception ex) {
                            ret.add("- Can't load " + s + ".dist-x: " + x + " is not an Integer value");
                        }
                    }
                    if (z == null || z.equals("")) {
                        ret.add("- Value missing for world " + s + " in mode 'rect': dist-z is not set or missing");
                    } else {
                        try {
                            z1 = Integer.parseInt(z);
                        } catch (Exception ex) {
                            ret.add("- Can't load " + s + ".dist-x: " + x + " is not an Integer value");
                        }
                    }
                    if (x1 > 0 && z1 > 0) {
                        try {
                            int mindis =  Integer.parseInt(c.get(s).getString("minimal-dist"));
                            if (mindis > x1 || mindis > z1) {
                                ret.add("- Can't load mode for world" + s + ": minimal-dist is bigger that the maximal size!");
                            }
                        } catch (Exception ex) {}
                    }
                    break;
                case "round":
                    String r = c.get(s).getString("radius");
                    int r1 = 0;
                    if (r == null || r.equals("")) {
                        ret.add("- Value missing for world " + s + " in mode 'round': radius is not set or missing");
                    } else {
                        try {
                            r1 = Integer.parseInt(r);
                        } catch (Exception ex) {
                            ret.add("- Can't load " + s + ".radius: " + r + " is not an Integer value");
                        }
                    }
                    if (r1 > 0) {
                        try {
                            int mindis =  Integer.parseInt(c.get(s).getString("minimal-dist"));
                            if (mindis > r1) {
                                ret.add("- Can't load mode for world" + s + ": minimal-dist is bigger that the maximal size!");
                            }
                        } catch (Exception ex) {}
                    }
                    break;
                case "auto":
                    try {
                        if (m.getServer().getWorld(s).getWorldBorder().getSize() > 2.0E5) {
                            m.warn("NOTICE: The Worldborder for world " + s + " in mode auto is very big.");
                            m.warn("NOTICE: The Border size is " + (int) (m.getServer().getWorld(s).getWorldBorder().getSize()) + "! This may cause lags.");
                        }
                        try {
                            int mindis = Integer.parseInt(c.get(s).getString("minimal-dist"));
                            double r2 = m.getServer().getWorld(s).getWorldBorder().getSize();
                            if (mindis > r2) {
                                ret.add("- Can't load mode for world" + s + ": minimal-dist is bigger that the maximal size!");
                            }
                        } catch (Exception ex) {}
                    } catch (Exception ex) {}

                    break;
                case "plugin":
                    if(m.getServer().getPluginManager().getPlugin("WorldBorder") == null) {
                        ret.add("- Invalid mode for world " + s + ": Plugin WorldBorder is not installed!");
                    } else {
                        BorderData bd = com.wimbli.WorldBorder.Config.Border(s);
                        if (bd == null) {
                            ret.add("- The WorldBorder in Plugin WorldBorder in World " + s + " is not set.");
                        } else {
                            try {
                                int mindis = Integer.parseInt(c.get(s).getString("minimal-dist"));
                                if (mindis > bd.getRadiusX() || mindis > bd.getRadiusZ()) {
                                    ret.add("- Can't load mode for world" + s + ": minimal-dist is bigger that the maximal size!");
                                }
                            } catch (Exception ex) {}
                        }
                    }
                    break;
                default:
                    ret.add("- Wronng mode " + mode + " for world " + s);
                    break;
            }
        }
    }

    /**
     * Checking all message nodes
     * Wrong if:
     *      - Node not exists
     *      - Message not exists
     */
    private void checkMessages() {
        String[] array = {
                "prefix",
                "success",
                "not_enabled",
                "poor_player",
                "took_money",
                "cooldown",
                "permission",
                "anticheat",
                "sign"};

        for(String s : array) {
            String s1;
            try {
                s1 = c.get("messages").getString(s);
                if (s1 == null || s1.equals("")) {
                    ret.add("- Can't load messages." + s + ": The value is not set!");
                }
            } catch (Exception ex) {
                ret.add("- messages." + s + " does not exists!");
            }
        }

    }

    /**
     * Checking node cooldown
     * .enabled is wrong if:
     *      - not exists
     *      - not a boolean value
     *  .time will only be checked if .enabled is true. Wrong if:
     *      - not exists
     *      - not an integer value
     */
    private void checkCooldown() {
        String s;
        Boolean on = false;
        try {
            s = c.get("cooldown").getString("enabled");
            if (s == null || s.equals("")) {
                ret.add("- Can't load cooldown.enabled: The value is not set!");
            } else {
                try {
                    on = Boolean.parseBoolean(s);
                } catch (Exception ex) {
                    ret.add("- Can't load cooldpwn.enabled: " + s + " is not a Boolean value!");
                }
            }
        } catch (Exception ex) {
            ret.add("- cooldown.enabled does not exists!");
        }

        if(on) {
            try {
                s = c.get("cooldown").getString("time");
                if (s == null || s.equals("")) {
                    ret.add("- Can't load cooldown.time: The value is not set!");
                } else {
                     try {
                         Integer.parseInt(s);
                     } catch (Exception ex) {
                          ret.add("- Can't load cooldpwn.time: " + s + " is not an Integer value!");
                     }
                 }
            } catch (Exception ex) {
                ret.add("- cooldown.time does not exists!");
            }
        }
    }

    /**
     * Checking nodes vault.
     * .enabled is Wrong when:
     *      - not set
     *      - not a boolean value
     * If .enabled is true, the system will check if vault is installed and is checking .price
     * .price is wrong if:
     *      - not set
     *      - not a double value
     */
    private void checkVault() {
        String s;
        Boolean on = false;
        try {
            s = c.get("vault").getString("enabled");
            if (s == null || s.equals("")) {
                ret.add("- Can't load vault.enabled: The value is not set!");
            } else {
                try {
                    on = Boolean.parseBoolean(s);
                } catch (Exception ex) {
                    ret.add("- Can't load vault.enabled: " + s + " is not a Boolean value!");
                }
            }
        } catch (Exception ex) {
            ret.add("- vault.enabled does not exists!");
        }

        if(on) {
            if (m.getServer().getPluginManager().getPlugin("Vault") == null) {
                ret.add("- Vault is enabled in your config but not installed!");
            }
            try {
                s = c.get("vault").getString("price");
                if (s == null || s.equals("")) {
                    ret.add("- Can't load vault.price: The value is not set!");
                } else {
                    try {
                        Double.parseDouble(s);
                    } catch (Exception ex) {
                        ret.add("- Can't load vault.price: " + s + " is not a Double value!");
                    }
                }
            } catch (Exception ex) {
                ret.add("- vault.price does not exists!");
            }
        }
    }

    /**
     * Checking Node anticheat. Wrong if:
     *      - not set
     *      - not a boolean value
     */
    private void checkAnticheat() {
        String s = null;
        try {
            s = c.getConfig().getString("anticheat");
            if (s == null || s.equals("")) {
                ret.add("- Can't load anticheat: The value is not set!");
            } else {
                Boolean.parseBoolean(s);
            }
        } catch (Exception ex) {
            ret.add("- Can't load anticheat: The value " + s + " is not a boolean-value!");
        }
    }

    /**
     * Checking Node anticheat. Wrong if:
     *      - not set
     *      - not an inetger value
     */
    private void checkInterrupt() {
        String s = null;
        try {
            s = c.getConfig().getString("interrupt");
            if (s == null || s.equals("")) {
                ret.add("- Can't load imterrupt: The value is not set!");
            } else {
                Integer.parseInt(s);
            }
        } catch (Exception ex) {
            ret.add("- Can't load interrupt: The value " + s + " is not an integer-value!");
        }
    }
}
