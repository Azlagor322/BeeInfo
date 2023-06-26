package com.azlagor.beeinfo;

import com.azlagor.beeinfo.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BeeInfo extends JavaPlugin implements Listener {
    public static Config config;

    public static class Config {
        public String lang = "EN_en";
        public Map<String, String> langData;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        config = new Config();
        Utils.loadLang();
        System.out.println(Utils.lang("test.test"));
    }

    @Override
    public void onDisable() {

    }

    Material[] flowers = {Material.POPPY, Material.ROSE_BUSH,
            Material.LILY_OF_THE_VALLEY, Material.BLUE_ORCHID,
            Material.DANDELION, Material.PEONY, Material.LILAC,
            Material.LARGE_FERN, Material.BAMBOO, Material.CACTUS,
            Material.PINK_TULIP, Material.WITHER_ROSE, Material.CORNFLOWER,
            Material.OXEYE_DAISY, Material.ORANGE_TULIP, Material.RED_TULIP,
            Material.WHITE_TULIP, Material.ALLIUM, Material.SUNFLOWER};
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GLASS_BOTTLE) {
            if (event.getClickedBlock().getType() == Material.BEEHIVE || event.getClickedBlock().getType() == Material.BEE_NEST) {
                if(event.getHand() == EquipmentSlot.OFF_HAND) return;
                org.bukkit.block.data.type.Beehive beehiveState = (org.bukkit.block.data.type.Beehive) event.getClickedBlock().getState().getBlockData();
                if(event.getClickedBlock().getState() instanceof Beehive beehive)
                {
                    ItemStack iteminHand = event.getPlayer().getInventory().getItemInMainHand();
                    Location flower = null;
                    boolean isFlower = false;
                    if(beehive.getFlower() != null)
                    {
                        isFlower = Arrays.asList(flowers).contains(beehive.getFlower().getBlock().getType());
                    }
                    if(beehive.getFlower() != null && isFlower) flower = beehive.getFlower();
                    if(isFlower && iteminHand.getType() == beehive.getFlower().getBlock().getType())
                    {
                        flower = beehive.getFlower();
                        if(beehiveState.getHoneyLevel() < 5)
                        {
                            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                            if(new Random().nextInt(10) > 5)
                            {
                                if(beehiveState.getHoneyLevel() > 3) beehiveState.setHoneyLevel(beehiveState.getHoneyLevel() + 1);
                                else beehiveState.setHoneyLevel(beehiveState.getHoneyLevel() + 2);
                                beehive.setBlockData(beehiveState);
                                beehive.update();

                                event.getPlayer().playSound( event.getPlayer().getLocation(), Sound.BLOCK_HONEY_BLOCK_PLACE,1,1);
                                beehive.getWorld().spawnParticle(Particle.HEART, beehive.getLocation().add(0.5,0,0.5), 20, 0.5, 0.5,0.5);
                            }
                            else
                            {
                                event.getPlayer().playSound( event.getPlayer().getLocation(),Sound.ENTITY_BEE_HURT,1,1);
                                beehive.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, beehive.getLocation().add(0.5,0,0.5), 20, 0.5, 0.5,0.5);
                            }
                            event.setCancelled(true);
                        }

                    }
                    else
                    {
                        Player player = event.getPlayer();
                        CustomInventoryHolder ci = new CustomInventoryHolder(beehiveState.getHoneyLevel(), flower, beehive.getEntityCount());
                        player.playSound(player.getLocation(),Sound.BLOCK_HONEY_BLOCK_FALL,1,1);
                        player.openInventory(ci.getInventory());
                        event.setCancelled(true);
                    }
                }


            }

        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(Utils.lang("gui.bees")))
        {
            event.setCancelled(true);
        }
    }

    public static class CustomInventoryHolder implements InventoryHolder {
        private int honeyLevel;
        private final Location flower;
        private int beesCount;
        public CustomInventoryHolder(int honeyLevel, Location flower, int beesCount) {
            this.honeyLevel = honeyLevel;
            this.flower = flower;
            this.beesCount = beesCount;
        }

        @Override
        public Inventory getInventory() {
            Inventory inv = Bukkit.createInventory(this, 27, Utils.lang("gui.bees"));
            ItemStack hasHoney = new ItemStack(Material.HONEY_BOTTLE);
            ItemMeta meta = hasHoney.getItemMeta();
            meta.setDisplayName(Utils.lang("gui.honey"));
            hasHoney.setItemMeta(meta);
            ItemStack noHoney = new ItemStack(Material.GLASS_BOTTLE);
            meta = noHoney.getItemMeta();
            meta.setDisplayName(Utils.lang("gui.noHoney"));
            noHoney.setItemMeta(meta);
            for (int i = 2; i < 7; i++) {
                if(honeyLevel > 0)
                {
                    honeyLevel--;
                    inv.setItem(i, hasHoney);
                }
                else
                {
                    inv.setItem(i, noHoney);
                }
            }
            ItemStack bee = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) bee.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MineCap_"));
            skullMeta.setDisplayName(Utils.lang("gui.beeRelax"));
            bee.setItemMeta(skullMeta);
            ItemStack notBee = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta nbMeta = notBee.getItemMeta();
            nbMeta.setDisplayName(Utils.lang("gui.empty"));
            notBee.setItemMeta(nbMeta);
            for (int i = 10; i < 17; i+=3) {
                if(beesCount > 0)
                {
                    beesCount--;
                    inv.setItem(i, bee);
                }
                else
                {
                    inv.setItem(i, notBee);
                }
            }
            if(flower != null)
            {
                ItemStack lovFlover = new ItemStack(flower.getBlock().getType());
                ItemMeta fMeta = lovFlover.getItemMeta();
                fMeta.setDisplayName(Utils.lang("gui.flower"));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(Utils.lang("gui.click") + " " + Utils.lang("gui.rcm") + " " + Utils.lang("gui.todo") );
                lore.add(Utils.lang("gui.toSpeed"));
                fMeta.setLore(new ArrayList<>(){});
                fMeta.setLore(lore);
                lovFlover.setItemMeta(fMeta);
                inv.setItem(22,lovFlover);
            }
            else
            {
                ItemStack lovFlover = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta fMeta = lovFlover.getItemMeta();
                fMeta.setDisplayName(Utils.lang("gui.noFlower"));
                lovFlover.setItemMeta(fMeta);
                inv.setItem(22,lovFlover);
            }
            return inv;
        }
    }
}
