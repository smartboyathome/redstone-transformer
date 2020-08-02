package net.glowstone.datapack.processor.generation.recipes;

import com.squareup.javapoet.CodeBlock;
import net.glowstone.datapack.loader.model.external.recipe.Item;
import net.glowstone.datapack.loader.model.external.recipe.ShapelessRecipe;
import net.glowstone.datapack.recipes.ShapelessRecipeProvider;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;

public class ShapelessRecipeGenerator extends AbstractCraftingRecipeGenerator<ShapelessRecipe, ShapelessRecipeProvider> {
    @Override
    public Class<ShapelessRecipe> getAssociatedClass() {
        return ShapelessRecipe.class;
    }

    @Override
    public Class<ShapelessRecipeProvider> getProviderClass() {
        return ShapelessRecipeProvider.class;
    }

    @Override
    public String getDefaultMethodName() {
        return "defaultShapelessRecipes";
    }

    @Override
    protected Material getResultingMaterial(ShapelessRecipe shapelessRecipe) {
        return Material.matchMaterial(shapelessRecipe.getResult().getItem());
    }

    @Override
    protected int getResultCount(ShapelessRecipe shapelessRecipe) {
        return shapelessRecipe.getResult().getCount();
    }

    @Override
    protected Optional<CodeBlock> extraBuilderCalls(String namespaceName, String itemName, ShapelessRecipe shapelessRecipe) {
        CodeBlock.Builder methodBlock = CodeBlock.builder();

        for (List<Item> ingredientsStack : shapelessRecipe.getIngredients()) {
            methodBlock.add(
                ".addIngredient($L)",
                Helpers.createRecipeChoice(namespaceName, ingredientsStack)
            );
        }

        return Optional.of(methodBlock.build());
    }
}