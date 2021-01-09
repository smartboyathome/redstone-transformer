package net.glowstone.datapack.recipes.providers;

import com.google.common.collect.ImmutableList;
import net.glowstone.datapack.recipes.StaticResultRecipe;
import net.glowstone.datapack.recipes.inputs.MapCloningRecipeInput;
import net.glowstone.datapack.recipes.inputs.MapExtendingRecipeInput;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.glowstone.datapack.utils.ItemStackUtils.itemStackIsEmpty;

public class MapCloningRecipeProvider extends DynamicRecipeProvider<MapCloningRecipeInput> {
    public MapCloningRecipeProvider(String namespace, String key) {
        super(MapCloningRecipeInput.class, new NamespacedKey(namespace, key));
    }

    @Override
    public Optional<Recipe> getRecipeFor(MapCloningRecipeInput input) {
        ItemStack original = null;
        int copies = 0;

        for (ItemStack itemStack : input.getInput()) {
            if (itemStackIsEmpty(itemStack)) {
                continue;
            }
            if (itemStack.getType() == Material.FILLED_MAP) {
                if (original == null) {
                    original = itemStack;
                    continue;
                }
                return Optional.empty(); // Can't have more than one cloneable map
            }
            if (itemStack.getType() == Material.MAP) {
                copies += 1;
                continue;
            }

            return Optional.empty(); // Unmatched item
        }

        if (original == null || copies == 0) {
            return Optional.empty(); // Either a filled map or an empty map was missing
        }

        ItemStack ret = original.clone();
        ret.setAmount(copies);

        return Optional.of(new StaticResultRecipe(getKey(), ret));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }
}