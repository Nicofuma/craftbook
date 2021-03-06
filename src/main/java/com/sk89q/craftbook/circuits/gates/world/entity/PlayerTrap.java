package com.sk89q.craftbook.circuits.gates.world.entity;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractICFactory;
import com.sk89q.craftbook.circuits.ic.AbstractSelfTriggeredIC;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.circuits.ic.IC;
import com.sk89q.craftbook.circuits.ic.ICFactory;
import com.sk89q.craftbook.circuits.ic.RestrictedIC;
import com.sk89q.craftbook.util.PlayerType;
import com.sk89q.craftbook.util.RegexUtil;
import com.sk89q.craftbook.util.SearchArea;

public class PlayerTrap extends AbstractSelfTriggeredIC {

    public PlayerTrap (Server server, ChangedSign sign, ICFactory factory) {
        super(server, sign, factory);
    }

    @Override
    public String getTitle () {
        return "Player Trap";
    }

    @Override
    public String getSignTitle () {
        return "PLAYER TRAP";
    }

    SearchArea area;

    int damage;
    PlayerType type;
    String nameLine;

    @Override
    public void load() {

        area = SearchArea.createArea(BukkitUtil.toSign(getSign()).getBlock(), RegexUtil.AMPERSAND_PATTERN.split(getSign().getLine(2))[0]);

        try {
            damage = Integer.parseInt(RegexUtil.AMPERSAND_PATTERN.split(getSign().getLine(2))[1]);
        } catch (Exception ignored) {
            damage = 2;
        }

        if (getLine(3).contains(":"))
            type = PlayerType.getFromChar(getLine(3).trim().toCharArray()[0]);
        else
            type = PlayerType.ALL;

        nameLine = getLine(3).replace("g:", "").replace("p:", "").replace("n:", "").replace("t:", "").replace("a:", "").trim();
    }

    @Override
    public void trigger (ChipState chip) {
        if(chip.getInput(0))
            chip.setOutput(0, hurt());
    }

    public boolean hurt() {

        boolean hasHurt = false;

        for(Player p : area.getPlayersInArea()) {
            if(!type.doesPlayerPass(p, nameLine)) continue;
            p.damage(damage);
            hasHurt = true;
        }

        return hasHurt;
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new PlayerTrap(getServer(), sign, this);
        }

        @Override
        public String getShortDescription() {

            return "Damages nearby players that fit criteria.";
        }

        @Override
        public String[] getLineHelp() {

            return new String[] {"SearchArea&damage", "Player Criteria"};
        }
    }
}