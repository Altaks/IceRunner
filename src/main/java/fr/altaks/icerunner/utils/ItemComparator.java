package fr.altaks.icerunner.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemComparator {

    /**
     * Provides a soft comparison between two items Compares Material and ItemMeta
     *
     * @param compared the item to compare against
     * @return whether the items are softly equal or not
     */
    public static boolean softCompare(ItemStack base, ItemStack compared) {
        if (base.getType() != compared.getType()) return false;
        if (base.hasItemMeta() != compared.hasItemMeta()) return false;

        // Checking ItemMetas
        ItemMeta firstMeta = base.getItemMeta();
        ItemMeta secondMeta = compared.getItemMeta();

        return Objects.equals(firstMeta, secondMeta);
    }

    /**
     * Provides a comparison between two items Compares Material, ItemMeta and display name
     *
     * @param base the item to compare
     * @return whether the items are equal or not
     */
    public static boolean compare(ItemStack base, ItemStack compared) {
        if (base.getType() != compared.getType()) return false;
        if (base.hasItemMeta() != compared.hasItemMeta()) return false;

        // Checking ItemMetas
        ItemMeta firstMeta = base.getItemMeta();
        ItemMeta secondMeta = compared.getItemMeta();

        if(firstMeta == null && secondMeta == null) return true;
        if(firstMeta != null ^ secondMeta != null) return false;
        if (!Objects.equals(firstMeta, secondMeta)) return false;
        if (firstMeta.hasDisplayName() != secondMeta.hasDisplayName()) return false;

        return firstMeta.getDisplayName().equals(secondMeta.getDisplayName());
    }
}
