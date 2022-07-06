package com.alcatrazescapee.oreveins.vein;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.Logger;

import com.alcatrazescapee.oreveins.OreVeinsConfig;
import com.alcatrazescapee.oreveins.api.ICondition;
import com.alcatrazescapee.oreveins.api.IVein;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.util.IWeightedList;
import com.google.gson.annotations.SerializedName;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

@SuppressWarnings({"WeakerAccess"})
@ParametersAreNonnullByDefault
public class VeinTypeMultiple implements IVeinType<IVein<?>> {

    protected boolean disabled = false;

    protected int count = 1;
    protected int rarity = 10;

    @SerializedName("min_y")
    protected int minY = 16;

    @SerializedName("max_y")
    protected int maxY = 64;

    @SerializedName("use_relative_y")
    protected boolean useRelativeY = false;

    @SerializedName("vertical_size")
    protected int verticalSize = 8;

    @SerializedName("horizontal_size")
    protected int horizontalSize = 15;

    protected float density = 20;

    @SerializedName("dimensions_is_whitelist")
    protected boolean dimensionIsWhitelist = true;

    @SerializedName("biomes_is_whitelist")
    protected boolean biomesIsWhitelist = true;

    private List<String> biomes = null;
    private List<Integer> dimensions = null;
    private List<ICondition> conditions = null;
    private IWeightedList<Indicator> indicator = null;

    @SerializedName("veins")
    private List<IVeinType<?>> types;

    @Nonnull
    @Override
    public IBlockState getStateToGenerate(Random rand) {
        throw new IllegalStateException("This should never be called directly");
    }

    @Nonnull
    @Override
    public Collection<IBlockState> getOreStates() {
        return this.types.stream().map(IVeinType::getOreStates).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Indicator getIndicator(Random random) {
        throw new IllegalStateException("This should never be called directly");
    }

    @Override
    public boolean canGenerateAt(World world, BlockPos pos) {
        if (this.disabled) {
            return false;
        }

        if (this.conditions != null) {
            for (ICondition condition : this.conditions) {
                if (!condition.test(world, pos)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean inRange(IVein<?> vein, int xOffset, int zOffset) {
        if (this.disabled) {
            return false;
        }

        return xOffset * xOffset + zOffset * zOffset < horizontalSize * horizontalSize * vein.getSize();
    }

    @Override
    public int getChunkRadius() {
        return this.types.stream().mapToInt(IVeinType::getChunkRadius).max().orElse(0);
    }

    @Override
    public boolean matchesDimension(int id) {
        if (this.dimensions == null) {
            return id == 0;
        }

        for (int i : this.dimensions) {
            if (id == i) {
                return this.dimensionIsWhitelist;
            }
        }

        return !this.dimensionIsWhitelist;
    }

    @Override
    public boolean matchesBiome(Biome biome) {
        if (this.biomes == null) {
            return true;
        }

        for (String s : this.biomes) {
            String biomeName = biome.getRegistryName().getPath();

            if (biomeName.equals(s)) {
                return this.biomesIsWhitelist;
            }

            for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
                if (s.equalsIgnoreCase(type.getName())) {
                    return this.biomesIsWhitelist;
                }
            }
        }

        return !this.biomesIsWhitelist;
    }

    @Override
    public int getMinY() {
        return this.minY;
    }

    @Override
    public int getMaxY() {
        return this.maxY;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getRarity() {
        return this.rarity;
    }

    @Override
    public boolean useRelativeY() {
        return this.useRelativeY;
    }

    @Override
    public String toString() {
        return String.format("[%s: Types: %s]", VeinRegistry.getName(this), this.types);
    }

    protected final BlockPos defaultStartPos(int chunkX, int chunkZ, Random rand) {
        int spawnRange = maxY - minY, minRange = minY;

        if (OreVeinsConfig.AVOID_VEIN_CUTOFFS) {
            if (verticalSize * 2 < spawnRange) {
                spawnRange -= verticalSize * 2;
                minRange += verticalSize;
            }
            else {
                minRange = minY + (maxY - minY) / 2;
                spawnRange = 1;
            }
        }

        return new BlockPos(
            chunkX * 16 + rand.nextInt(16),
            minRange + rand.nextInt(spawnRange),
            chunkZ * 16 + rand.nextInt(16)
        );
    }


    @Override
    public float getChanceToGenerate(IVein<?> vein, BlockPos pos) {
        throw new IllegalStateException("This should never be called directly");
    }

    @Override
    public void createVeins(List<IVein<?>> veins, int chunkX, int chunkZ, Random random) {
        BlockPos centerPos = this.defaultStartPos(chunkX, chunkZ, random);

        List<IVein<?>> innerVeins = new ArrayList<>();
        innerVeins.add(new VeinMultiple(this, centerPos, random));

        for (IVeinType<?> type : this.types) {
            type.createVeins(innerVeins, chunkX, chunkZ, random);
        }

        innerVeins.forEach(vein -> {
            vein.setPos(centerPos);
            veins.add(vein);
        });
        
    }

    @Override
    public boolean isValid(Logger logger, String veinName) {
        if (this.disabled) {
            logger.warn("Vein {} is explicitly disabled", veinName);
            return false;
        }

        boolean valid =
            (indicator == null || (!indicator.isEmpty() && indicator.values().stream().map(Indicator::isValid).reduce((x, y) -> x && y).orElse(false))) &&
            maxY > minY && (minY >= 0 || useRelativeY) &&
            count > 0 &&
            rarity > 0 &&
            verticalSize > 0 && horizontalSize > 0 && density > 0
        ;

        valid = valid && (this.types != null) && (this.types.size() >= 2);

        if (this.types == null || this.types.size() < 2) {
            logger.error("Vein {} should have atleast 2 or more subveins defined", veinName);
        }

        if (indicator != null && !indicator.isEmpty() && !indicator.values().stream().map(Indicator::isValid).reduce((x, y) -> x && y).orElse(false)) {
            logger.error("Vein {} has invalid indicators", veinName);
        }

        if (maxY <= minY) {
            logger.error("Vein {} has maxY <= minY ({} <= {})", veinName, maxY, minY);
        }

        if (minY < 0 && !useRelativeY) {
            logger.error("Vein {} has minY ({}) < 0 and useRelativeY = false", veinName, minY);
        }

        if (count <= 0) {
            logger.error("Vein {} has nonpositive count {}", veinName, count);
        }

        if (rarity <= 0) {
            logger.error("Vein {} has nonpositive rarity {}", veinName, rarity);
        }

        if (verticalSize <= 0) {
            logger.error("Vein {} has nonpositive verticalSize {}", veinName, verticalSize);
        }

        if (horizontalSize <= 0) {
            logger.error("Vein {} has nonpositive horizontalSize {}", veinName, horizontalSize);
        }

        if (density <= 0) {
            logger.error("Vein {} has nonpositive density {}", veinName, density);
        }

        return valid;
    }

    public static class VeinMultiple implements IVein<VeinTypeMultiple> {

        protected final VeinTypeMultiple type;
        protected BlockPos pos;
        protected float size;

        public VeinMultiple(VeinTypeMultiple type, BlockPos pos, float size) {
            this.pos = pos;
            this.type = type;
            this.size = size;
        }

        public VeinMultiple(VeinTypeMultiple type, BlockPos pos, Random random) {
            this(type, pos, 0.7f + random.nextFloat() * 0.3f);
        }

        @Nonnull
        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Nonnull
        @Override
        public void setPos(@Nonnull BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public VeinTypeMultiple getType() {
            return type;
        }

        @Override
        public float getSize() {
            return size;
        }

        @Override
        public String toString() {
            return String.format("Vein: %s, Pos: %s", VeinRegistry.getName(type), pos);
        }

        @Override
        public boolean canGenerateAt(World world, BlockPos pos) {
            return getType().canGenerateAt(world, pos);
        }

        @Override
        public boolean inRange(int x, int z) {
            // Never in range, so this vein should never generate in the world.
            // It only serves as a marker for commands like /findveins
            return false;
        }

        @Override
        public double getChanceToGenerate(BlockPos pos) {
            // Same reasoning as above
            return 0.0d;
        }
        
    }

}