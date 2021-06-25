package com.jazzkuh.mtwapens.function;

import com.cryptomorin.xseries.XMaterial;
import com.jazzkuh.mtwapens.Main;
import com.jazzkuh.mtwapens.commands.AmmoCMD;
import com.jazzkuh.mtwapens.utils.ItemBuilder;
import com.jazzkuh.mtwapens.utils.Utils;
import com.jazzkuh.mtwapens.utils.menu.GUIHolder;
import com.jazzkuh.mtwapens.utils.messages.DefaultMessages;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class AmmoMenu extends GUIHolder {
    public AmmoMenu(Plugin plugin, int page) {
        int pageSize = 9 * 5;

        ArrayList<String> weapons = new ArrayList<>(plugin.getConfig().getConfigurationSection("ammo.").getKeys(false));
        this.inventory = Bukkit.createInventory(this, 6 * 9, Main.getMessages().get(DefaultMessages.MENU_AMMO_TITLE));

        for (int i = 0; i < Math.min(weapons.size() - page * pageSize, pageSize); i++) {
            int index = i + page * pageSize;
            String type = weapons.get(index);

            ItemStack weapon = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("ammo." + type + ".material")).get().parseMaterial())
                    .setName(Utils.color("&a" + type))
                    .setNBT(plugin.getConfig().getString("ammo." + type + ".nbt"), plugin.getConfig().getString("ammo." + type + ".nbtvalue"))
                    .setNBT("menuItem", "true")
                    .toItemStack();

            this.inventory.setItem(i, weapon);
        }

        this.inventory.setItem(pageSize + 4, new ItemBuilder(Material.BOW)
                .setName(Main.getMessages().get(DefaultMessages.MENU_SWITCHER)
                        .replace("<Menu>", Main.getMessages().get(DefaultMessages.MENU_WEAPON_TITLE)))
                .setNBT("switcher", "true")
                .toItemStack());

        if (page > 0) {
            this.inventory.setItem(pageSize + 3, new ItemBuilder((XMaterial.OAK_SIGN.parseMaterial()))
                    .setName(Main.getMessages().get(DefaultMessages.MENU_WEAPON_BUTTON_PAGE)
                            .replace("<Page>", String.valueOf(page)))
                    .toItemStack());
        }

        if (weapons.size() - page * pageSize > pageSize) {
            this.inventory.setItem(pageSize + 5, new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
                    .setName(Main.getMessages().get(DefaultMessages.MENU_WEAPON_BUTTON_PAGE)
                            .replace("<Page>", String.valueOf((page + 2))))
                    .toItemStack());
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        ItemStack item = event.getCurrentItem();

        if (NBTEditor.contains(item, "menuItem") && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String type = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            AmmoCMD.getAmmo(player, type);
        }

        if (NBTEditor.contains(item, "switcher")) {
            new WeaponMenu(Main.getInstance(), 0).open(player);
        } else if (item.getType() == XMaterial.OAK_SIGN.parseMaterial()) {
            int newPage = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).replaceAll("([a-zA-Z]|\\s|§\\d)+", "")) - 1;
            new AmmoMenu(Main.getInstance(), newPage).open(player);
        }
    }
}