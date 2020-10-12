package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.external.itembased.GhostAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Casper extends FunctionalAbility {
    public Casper(ConfigurationNode configuration) {
        super("casper", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .handler(e->{
                    Player player = e.getPlayer();
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    if (!player.isSneaking()){
                        player.sendMessage("Sneaking");
                        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 423, true, false, true));
                            clearArmor(player);
                            teleport(player,10,0);

                        }

                    }
                    if (player.isSneaking()){
                        player.sendMessage("DISABLE SNEAKING");
                            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                                setArmor(player,armor,item);
                            }

                    }

                });

    }
    private void teleport(Player player,int x,int z){
        if (player.getLocation().add(x,0,z).getBlock().isEmpty() || player.getLocation().add(10,0,0).getBlock().getType() != Material.AIR
                && player.getLocation().add(x,0,z).getBlock().getType() != Material.CAVE_AIR ){
            player.teleport(player.getLocation().add(x, 0, z));
        }else{
            x -= 5;
            z += 5;
            teleport(player,x,z);
        }

    }
    private void setArmor(Player player,ItemStack[] armor,ItemStack itemInMainHand){
        player.getInventory().setHelmet(armor[3]);
        player.getInventory().setChestplate(armor[2]);
        player.getInventory().setLeggings(armor[1]);
        player.getInventory().setBoots(armor[0]);
        player.getInventory().setItemInMainHand(itemInMainHand);
    }
    private void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItemInMainHand(null);
    }
}
/*
*/
/**/