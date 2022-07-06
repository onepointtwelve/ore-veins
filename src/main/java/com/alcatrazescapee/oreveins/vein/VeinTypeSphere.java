/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.vein;


import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.oreveins.api.DefaultVein;
import com.alcatrazescapee.oreveins.api.DefaultVeinType;

@SuppressWarnings({"unused"})
@ParametersAreNonnullByDefault
public class VeinTypeSphere extends DefaultVeinType {
    
    boolean uniform = false;

    @Override
    public float getChanceToGenerate(DefaultVein vein, BlockPos pos) {
        float dx = (vein.getPos().getX() - pos.getX()) * (vein.getPos().getX() - pos.getX());
        float dy = (vein.getPos().getY() - pos.getY()) * (vein.getPos().getY() - pos.getY());
        float dz = (vein.getPos().getZ() - pos.getZ()) * (vein.getPos().getZ() - pos.getZ());

        float radius = ((dx + dz) / (horizontalSize * horizontalSize * vein.getSize()) + dy / (verticalSize * verticalSize * vein.getSize()));
        if (uniform && radius < 1.0f) {
            radius = 0.0f;
        }

        return 0.005f * density * (1.0f - radius);
    }
}
