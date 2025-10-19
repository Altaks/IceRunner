/* (C) 2025 */
package fr.altzec.icerunner.utils;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A factory for creating {@link ItemStack} instances, empowered by the {@link ItemStack}
 * class This class is used to provide a more fluent API for building {@link ItemStack} instances
 *
 * @since 1.0.0
 * @author altaks
 */
public class ItemFactory {

    private final ItemStack customItemStack;
    private final ItemMeta customItemMeta;

    /**
     * Allows to create an {@link ItemFactory} from any class extending the {@link ItemStack}
     * class
     *
     * @param itemStack the item stack to create the builder from
     */
    public ItemFactory(ItemStack itemStack) {
        this.customItemStack = itemStack;
        this.customItemMeta = this.customItemStack.getItemMeta();
    }

    /**
     * Follows the ItemStack constructor
     *
     * @param material the material of the item stack
     */
    public ItemFactory(Material material) {
        this(new ItemStack(material));
    }

    /**
     * Follows the ItemStack constructor
     *
     * @param material the material of the item stack
     * @param amount the amount of items in the stack
     */
    public ItemFactory(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    /**
     * Follows the ItemStack constructor
     *
     * @param material the material of the item stack
     * @param amount the amount of items in the stack
     * @param damage the damage dealt on the item
     * @param data the data of the item (wool and dyes variants etc.)
     * @deprecated from Spigot's API
     */
    @Deprecated
    public ItemFactory(Material material, int amount, short damage, byte data) {
        this(new ItemStack(material, amount, damage, data));
    }

    /**
     * Builds the final version of the {@link ItemStack} Provides a clone of the ItemStack used
     * to build the Item, allows to chain ItemStack variants without modifying the other copies
     *
     * @return the built {@link ItemStack}
     */
    public ItemStack build() {
        ItemStack baseItem = customItemStack.clone();
        baseItem.setItemMeta(customItemMeta);
        return baseItem;
    }

    /**
     * Modifies the amount of items in the stack
     *
     * @param amountToSet the amount of items to set, must be between 1 and the max stack size
     * @return the {@link ItemFactory} instance
     * @throws InvalidParameterException if the amount is invalid / out of the bounds of the max
     *     material stack size
     */
    public ItemFactory setAmount(int amountToSet) throws InvalidParameterException {
        if (amountToSet <= 0
                || this.customItemStack.getType().getMaxStackSize() < amountToSet) {
            throw new InvalidParameterException(
                    "Amount must be between 1 and "
                            + this.customItemStack.getType().getMaxStackSize());
        }
        customItemStack.setAmount(amountToSet);
        return this;
    }

    /**
     * Changes the material of the item
     *
     * @param material the material to set the item to
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory setMaterial(Material material) {
        this.customItemStack.setType(material);
        return this;
    }

    /**
     * Changes the display name within the {@link ItemMeta} of the built item
     *
     * @param displayName the display name to set
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory setDisplayName(String displayName) {
        this.customItemMeta.setDisplayName(displayName);
        return this;
    }

    /**
     * Hides the item name by setting it to an empty string within the {@link ItemMeta} of the
     * built item
     *
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory removeItemName() {
        this.customItemMeta.setDisplayName(ChatColor.RESET + " ");
        return this;
    }

    /**
     * Replaces the lore lines within the {@link ItemMeta} of the built item
     *
     * @param loreLines the lines to set
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory setLore(List<String> loreLines) {
        this.customItemMeta.setLore(loreLines);
        return this;
    }

    /**
     * Replaces the lore lines within the {@link ItemMeta} of the built item
     *
     * @param loreLines the lines to set
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory setLore(String... loreLines) {
        this.customItemMeta.setLore(Arrays.asList(loreLines));
        return this;
    }

    /**
     * Appends new lines of lore to the existing ones within the {@link ItemMeta} of the built
     * item
     *
     * @param newLore the lines to add
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory appendLoreLines(String... newLore) {
        List<String> existingLoreList = this.customItemMeta.getLore();
        Objects.requireNonNull(existingLoreList, "No lore has been found on item").addAll(Arrays.asList(newLore));
        this.customItemMeta.setLore(existingLoreList);
        return this;
    }

    /**
     * Appends new lines of lore to the existing ones within the {@link ItemMeta} of the built
     * item
     *
     * @param newLore the lines to add
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory appendLoreLines(List<String> newLore) {
        List<String> existingLoreList = this.customItemMeta.getLore();
        Objects.requireNonNull(existingLoreList, "No lore has been found on item").addAll(newLore);
        this.customItemMeta.setLore(existingLoreList);
        return this;
    }

    /**
     * Empties the lore lines within the {@link ItemMeta} of the built item
     *
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory clearLore() {
        this.customItemMeta.setLore(Collections.emptyList());
        return this;
    }

    /**
     * Remove lines of lore from the existing ones within the {@link ItemMeta} of the built item
     *
     * @param linesToRemove the indexes of the lines to remove
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory removeLoreLines(int... linesToRemove) {
        List<String> actualLore = this.customItemMeta.getLore();
        for (int indexesToRemove : linesToRemove) Objects.requireNonNull(actualLore, "No lore has been found on item").remove(indexesToRemove);
        this.customItemMeta.setLore(actualLore);
        return this;
    }

    /**
     * Removes lines of lore which matches the provided strings from the existing ones within
     * the {@link ItemMeta} of the built item
     *
     * @param loreLines the lines to remove
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory removeLoreLines(String... loreLines) {
        List<String> actualLore = this.customItemMeta.getLore();
        List<String> filteredLore =
                Objects.requireNonNull(actualLore, "No lore has been found on item").stream()
                        .filter(
                                loreLine ->
                                        Arrays.stream(loreLines)
                                                .noneMatch(elem -> elem.equals(loreLine)))
                        .collect(Collectors.toList());

        this.customItemMeta.setLore(filteredLore);

        return this;
    }

    /**
     * Updates the lore line at the provided line index
     *
     * @param lineIndex The index of the line to update
     * @param newValue The new value
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory modifyLoreLine(int lineIndex, String newValue) {
        List<String> actualLore = this.customItemMeta.getLore();
        Objects.requireNonNull(actualLore, "No lore has been found on item").set(lineIndex, newValue);
        this.customItemMeta.setLore(actualLore);
        return this;
    }

    /**
     * Inserts a new line of lore at the provided line index
     *
     * @param lineIndex The index at which the line should be inserted
     * @param newValue The new line to insert
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory insertLoreLine(int lineIndex, String newValue) {
        List<String> actualLore = this.customItemMeta.getLore();
        Objects.requireNonNull(actualLore, "No lore has been found on item").add(lineIndex, newValue);
        this.customItemMeta.setLore(actualLore);
        return this;
    }

    public List<String> getLore() {
        return this.customItemMeta.getLore();
    }

    /**
     * Adds the provided item flags to the built item
     *
     * @param flags the {@link ItemFlag}s to add
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory addItemFlags(ItemFlag... flags) {
        this.customItemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * Removes the provided item flags from the built item
     *
     * @param flags the {@link ItemFlag}s to remove
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory removeItemFlags(ItemFlag... flags) {
        this.customItemMeta.removeItemFlags(flags);
        return this;
    }

    /**
     * Deletes all item flags from the built item
     *
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory clearItemFlags() {
        this.customItemMeta.removeItemFlags(
                this.customItemMeta.getItemFlags().toArray(new ItemFlag[0]));
        return this;
    }

    /**
     * Adds a safe enchant to the built item. If the enchantment is already present on the item,
     * its level is increased by the provided amount.
     *
     * @param enchantment The enchantment to add
     * @param level The level to increase the enchantment by
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory addSafeEnchantment(Enchantment enchantment, int level) {
        this.customItemStack.addEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds an unsafe enchant to the built item
     *
     * @param enchantment The enchantment to add
     * @param level The level to set the enchantment to
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.customItemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Removes the specified enchantment from the built item.
     *
     * @param enchantment The enchantment to remove
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory removeEnchant(Enchantment enchantment) {
        this.customItemStack.removeEnchantment(enchantment);
        return this;
    }

    /**
     * Adds a fake enchant to the built item. This means that the enchantment won't actually do
     * anything (unless used on durability affected items), but it will be displayed in the
     * item's lore.
     *
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory addFakeEnchant() {
        this.customItemStack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        return this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    /**
     * Sets the enchantments of the built item
     *
     * @param enchants The enchantments to set
     * @return the {@link ItemFactory} instance
     */
    public ItemFactory setEnchants(Map<Enchantment, Integer> enchants) {
        for (Enchantment enchant : this.customItemStack.getEnchantments().keySet()) {
            this.customItemStack.removeEnchantment(enchant);
        }
        for (Map.Entry<Enchantment, Integer> enchantEntry : enchants.entrySet()) {
            this.customItemStack.addUnsafeEnchantment(
                    enchantEntry.getKey(), enchantEntry.getValue());
        }
        return this;
    }

    public ItemFactory setUnbreakable(boolean unbreakable) {
        this.customItemMeta.setUnbreakable(unbreakable);
        return this;
    }
}