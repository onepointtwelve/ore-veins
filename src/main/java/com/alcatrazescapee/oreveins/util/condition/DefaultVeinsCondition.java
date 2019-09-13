/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.util.condition;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import com.alcatrazescapee.oreveins.Config;

import static com.alcatrazescapee.oreveins.OreVeins.MOD_ID;

public class DefaultVeinsCondition implements ICondition
{
    private static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "default_veins");

    @Override
    public ResourceLocation getID()
    {
        return KEY;
    }

    @Override
    public boolean test()
    {
        return Config.SERVER.enableDefaultVeins.get();
    }

    public enum Serializer implements IConditionSerializer<DefaultVeinsCondition>
    {
        INSTANCE;

        @Override
        public void write(JsonObject json, DefaultVeinsCondition value) {}

        @Override
        public DefaultVeinsCondition read(JsonObject json)
        {
            return new DefaultVeinsCondition();
        }

        @Override
        public ResourceLocation getID()
        {
            return KEY;
        }
    }
}
