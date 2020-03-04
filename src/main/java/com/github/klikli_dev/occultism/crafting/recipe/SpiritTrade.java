/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.klikli_dev.occultism.crafting.recipe;

import com.github.klikli_dev.occultism.registry.OccultismRecipes;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.klikli_dev.occultism.util.StaticUtil.modLoc;

public class SpiritTrade extends ShapelessRecipe {
    //region Initialization
    public SpiritTrade(ResourceLocation id, ItemStack result, NonNullList<Ingredient> input) {
        super(id, null, result, input);
    }
    //endregion Initialization

    //region Overrides
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return OccultismRecipes.SPIRIT_TRADE.get();
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inventory, @Nonnull World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventoryCrafting) {
        //as we don't have an inventory this is ignored.
        return null;
    }
    //endregion Overrides

    //region Methods

    /**
     * Consumes the given input and returns all unused items. isValid needs to be called first!
     *
     * @param input the input to consume.
     * @return the remaining items.
     */
    public List<ItemStack> consume(List<ItemStack> input) {
        //deep copy, otherwise stack.shrink will eat original input.
        List<ItemStack> result = input.stream().map(ItemStack::copy).collect(Collectors.toList());
        for (Ingredient ingredient : this.getIngredients()) {
            for (Iterator<ItemStack> it = result.iterator(); it.hasNext(); ) {
                ItemStack stack = it.next();
                if (ingredient.test(stack)) {
                    stack.shrink(1);
                    if (stack.isEmpty())
                        it.remove();
                }
            }
        }
        return result;
    }

    public boolean isValid(ItemStack... input) {
        return this.isValid(Arrays.asList(input));
    }

    public boolean isValid(List<ItemStack> input) {
        //deep copy, otherwise stack.shrink will eat original input.
        List<ItemStack> cached = input.stream().map(ItemStack::copy).collect(Collectors.toList());
        for (Ingredient ingredient : this.getIngredients()) {
            boolean matched = false;
            for (Iterator<ItemStack> it = cached.iterator(); it.hasNext(); ) {
                ItemStack stack = it.next();
                if (ingredient.test(stack)) {
                    matched = true;
                    stack.shrink(1);
                    if (stack.isEmpty())
                        it.remove();
                }
            }
            if (!matched)
                return false;
        }
        return true;
    }
    //endregion Methods

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SpiritTrade> {
        //region Fields
        private static final ResourceLocation NAME = modLoc("spirit_trade");
        private static final ShapelessRecipe.Serializer serializer = new ShapelessRecipe.Serializer();
        //endregion Fields

        //region Overrides
        public SpiritTrade read(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = serializer.read(recipeId, json);
            return new SpiritTrade(recipe.getId(), recipe.getRecipeOutput(), recipe.getIngredients());
        }


        public SpiritTrade read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapelessRecipe recipe = serializer.read(recipeId, buffer);
            return new SpiritTrade(recipe.getId(), recipe.getRecipeOutput(), recipe.getIngredients());
        }

        public void write(PacketBuffer buffer, SpiritTrade recipe) {
            serializer.write(buffer, recipe);
        }
        //endregion Overrides
    }
}
