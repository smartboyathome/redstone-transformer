package net.glowstone.datapack.recipes.inputs;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ShapedRecipeInput extends CraftingRecipeInput {
    public static Optional<ShapedRecipeInput> create(Inventory inventory) {
        return create(ShapedRecipeInput::new, inventory);
    }

    public static Optional<ShapedRecipeInput> create(InventoryType inventoryType, ItemStack[] itemStacks) {
        return create(ShapedRecipeInput::new, inventoryType, itemStacks);
    }

    public ShapedRecipeInput(CraftingInventory inventory) {
        super(inventory);
    }

    public ShapedRecipeInput(ItemStack[] input) {
        super(input);
    }
}