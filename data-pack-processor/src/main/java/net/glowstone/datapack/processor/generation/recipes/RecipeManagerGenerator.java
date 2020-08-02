package net.glowstone.datapack.processor.generation.recipes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import net.glowstone.datapack.AbstractRecipeManager;
import net.glowstone.datapack.AbstractTagManager;
import net.glowstone.datapack.loader.model.external.Data;
import net.glowstone.datapack.loader.model.external.recipe.BlastingRecipe;
import net.glowstone.datapack.loader.model.external.recipe.CampfireCookingRecipe;
import net.glowstone.datapack.loader.model.external.recipe.Recipe;
import net.glowstone.datapack.loader.model.external.recipe.SmeltingRecipe;
import net.glowstone.datapack.loader.model.external.recipe.SmokingRecipe;
import net.glowstone.datapack.loader.model.external.recipe.special.ArmorDyeRecipe;
import net.glowstone.datapack.loader.model.external.recipe.special.RepairItemRecipe;
import net.glowstone.datapack.processor.generation.CodeBlockStatementCollector;
import net.glowstone.datapack.processor.generation.DataPackItemSourceGenerator;
import net.glowstone.datapack.recipes.ArmorDyeRecipeProvider;
import net.glowstone.datapack.recipes.CookingRecipeProvider;
import net.glowstone.datapack.recipes.RecipeProvider;
import net.glowstone.datapack.recipes.RepairItemRecipeProvider;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeManagerGenerator implements DataPackItemSourceGenerator {
    private static final Map<Class<? extends Recipe>, RecipeGenerator<?, ?>> RECIPE_GENERATORS =
        Stream.<RecipeGenerator<?, ?>>of(
            new CookingRecipeGenerator<>(BlastingRecipe.class, CookingRecipeProvider.class, org.bukkit.inventory.BlastingRecipe.class),
            new CookingRecipeGenerator<>(CampfireCookingRecipe.class, CookingRecipeProvider.class, org.bukkit.inventory.CampfireRecipe.class),
            new CookingRecipeGenerator<>(SmeltingRecipe.class, CookingRecipeProvider.class, org.bukkit.inventory.FurnaceRecipe.class),
            new CookingRecipeGenerator<>(SmokingRecipe.class, CookingRecipeProvider.class, org.bukkit.inventory.SmokingRecipe.class),
            new ShapelessRecipeGenerator(),
            new ShapedRecipeGenerator(),
            new StonecuttingRecipeGenerator(),
            new SpecialRecipeGenerator<>(ArmorDyeRecipe.class, ArmorDyeRecipeProvider.class),
            new SpecialRecipeGenerator<>(RepairItemRecipe.class, RepairItemRecipeProvider.class)
        )
        .collect(
            Collectors.toMap(
                RecipeGenerator::getAssociatedClass,
                Function.identity()
            )
        );

    private final Map<String, List<MethodSpec>> recipeMethods = new HashMap<>();

    @Override
    public void addItems(String namespaceName,
                         Data data) {
        data.getRecipes().forEach((itemName, recipe) -> {
            RecipeGenerator<?, ?> generator = RECIPE_GENERATORS.get(recipe.getClass());

            if (generator != null) {
                recipeMethods
                    .computeIfAbsent(generator.getDefaultMethodName(), (key) -> new ArrayList<>())
                    .add(
                        generator.generateMethod(namespaceName, itemName, recipe)
                    );
            }
        });
    }

    @Override
    public void generateManager(Path generatedClassPath,
                                String generatedClassNamespace) {
        ParameterizedTypeName recipeListType = ParameterizedTypeName.get(
            ClassName.get(List.class),
            ParameterizedTypeName.get(
                ClassName.get(RecipeProvider.class),
                WildcardTypeName.subtypeOf(Object.class)
            )
        );
        List<MethodSpec> defaultMethods = recipeMethods.entrySet()
            .stream()
            .map((entry) -> {
                return MethodSpec.methodBuilder(entry.getKey())
                    .addModifiers(Modifier.PRIVATE)
                    .returns(recipeListType)
                    .addStatement(
                        "$T recipes = new $T<>()",
                        recipeListType,
                        ArrayList.class)
                    .addCode(
                        entry.getValue()
                            .stream()
                            .map((method) -> CodeBlock.of("recipes.add($N())", method))
                            .collect(CodeBlockStatementCollector.collect())
                    )
                    .addStatement("return recipes")
                    .build();
            })
            .collect(Collectors.toList());

        MethodSpec defaultRecipesMethod = MethodSpec.methodBuilder("defaultRecipes")
            .addModifiers(Modifier.PROTECTED)
            .returns(recipeListType)
            .addStatement(
                "$T recipes = new $T<>()",
                recipeListType,
                ArrayList.class
            )
            .addCode(
                defaultMethods.stream()
                    .map((method) -> CodeBlock.of("recipes.addAll($N())", method))
                    .collect(CodeBlockStatementCollector.collect())
            )
            .addStatement("return recipes")
            .build();

        MethodSpec constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(AbstractTagManager.class, "tagManager")
            .addStatement("super(tagManager)")
            .build();

        TypeSpec recipeManagerTypeSpec = TypeSpec.classBuilder("RecipeManager")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(AbstractRecipeManager.class)
            .addMethod(constructorMethod)
            .addMethod(defaultRecipesMethod)
            .addMethods(defaultMethods)
            .addMethods(recipeMethods.values().stream().flatMap(List::stream).collect(Collectors.toList()))
            .build();

        JavaFile recipeManagerJavaFile = JavaFile.builder(generatedClassNamespace, recipeManagerTypeSpec)
            .indent("    ")
            .build();

        try {
            recipeManagerJavaFile.writeTo(generatedClassPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
