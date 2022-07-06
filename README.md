![Ore Veins Banner Image](https://github.com/alcatrazEscapee/ore-veins/blob/1.12/img/banner.png?raw=true)

This is a Minecraft mod to add realistically shaped veins of ore to your world. Useful for custom maps, mod packs, or just a different survival experience. Everything is fully configurable via json, meaning you can have ore veins of whatever type of shape or size you want.

For example images of various types and configurations of veins see the curseforge images [here](https://minecraft.curseforge.com/projects/realistic-ore-veins/images).

## Configuration:

Ore Veins will look for all files under config/oreveins/. When you first add ore veins, it will create a default file with some example configuration. Feel free to use or modify this. It is also found here on github at [src/main/resources/assets/ore_veins.json](https://github.com/alcatrazEscapee/ore-veins/blob/1.12/src/main/resources/assets/ore_veins.json).

There's also an example vanilla-like config which can be found [here](https://github.com/alcatrazEscapee/ore-veins/blob/1.12/examples/example_vanilla_like_config.json)

Each json file in config/oreveins/ should consist of a set of objects, each one being a different type of vein. These represent a single ore type or configuration that will be generated in the world. Each entry must contain the following values:

* `type` is the registry name of the [Vein Type](#veins) that this entry will spawn. Based on what vein this is, there might be other required or optional values as well.
* `stone` is a [Block Entry](#block-entries). This represents the block states that the ore can spawn in.
* `ore` is a [Block Entry](#block-entries), with optional weights. This represents the possible states that the vein will spawn. This **does** support weighted entries.

Each entry can also contain any or all of the following values. If they don't exist, they will assume a default value. These apply to all vein types:

* `count` (Default: 1) Generate at most N veins per chunk. Rarity is applied to each attempt to generate a vein.
* `rarity` (Default: 10) 1 / N chunks will spawn this ore vein.
* `min_y` (Default: 16) Minimum y value for veins to generate at.
* `max_y` (Default: 64) Maximum y value for veins to generate at.
* `use_relative_y` (Default: false) If true, the y-values will be interpreted as an offset from the surface at each position. (i.e. min_y = -20, max_y = -5 will generate ore veins from 20 to 5 blocks under the surface)
* `density` (Default: 50) Density of the ore vein. Higher values are more dense. (FYI: This number is not a percentage. For 100% density use values >1000)
* `vertical_size` (Default: 15) Vertical radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `horizontal_size` (Default: 8) Horizontal radius. This is not an absolute number in blocks, but is close to. Experimentation is required.
* `biomes` (Default: all) Whitelist of biome names or biome tags for a biome to spawn in. Must be a list of strings. For info on possible tags see the Forge [Biome Dictionary](https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/common/BiomeDictionary.java).
* `biomes_is_whitelist` (Default: true) When false, the biome list becomes a blacklist
* `dimensions` (Default: 0) Whitelist of dimension ids that the ore can spawn in. Must be a list of integers.
* `dimensions_is_whitelist` (Default: true) When false, the dimension list becomes a blacklist
* `indicator` (Default: empty) This is an [Indicator](#indicators) which will spawn on the surface underneath where the vein is found. This can also be a weighted list of multiple indicators, in which case only one will be chosen for each vertical column within range of a vein.
* `conditions` (Default: none) This is a list of json objects which specifies conditions on each individual block within the ore vein. See [Conditions](#conditions).

### Veins

Veins represent different types of shapes or structures that can be spawned. Each entry must define a vein type.

*Spheres*: (`"type": "sphere"`)
This represents a single sphere (or spheroid, if vertical and horizontal size values are different). This vein type has no additional parameters. This vein type has an optional parameter:
* `uniform` (Default: `false`) This is a boolean which determines if the density of the sphere will be uniformly distributed, or if it will be denser towards the center.

*Clusters* (`"type": "cluster"`)
This vein represents a scattered group of spheroids.  This vein type has an optional parameter:
* `clusters` (Default: 3) This represents the average number of other clusters that will spawn as part of this vein.

*Vertical Pipe* (`"type": "pipe"`)
This vein represents a single vertical column / cylinder. This vein type has no additional parameters.

*Cone* (`"type": "cone"`)
This vein represents a vertical cone. The pointy end of the cone can point upwards or downwards. This vein type has two optional parameters:
* `inverted` (Default: false) If true, the cone will have a pointy end facing down. If false, the pointy end will face up
* `shape` (Default: 0.5) This value determines how pointy the cone will be. It should be between 0.0 and 1.0. Higher values mean less pointy (more cylindrical). Smaller values are more pointy

*Curve* (`"type": "curve"`)
This vein represents a curve (created with a cubic Bezier curve.) It has two optional parameters:
* `radius` (Default: 5) This is the approximate radius of the curve in blocks.
* `angle` (Default: 45) This is the maximum angle for vertical vein rotation, in a range from 0 to 90. Zero be completely horizontal, and 90 will have the full range of vertical directions to curve in.


### Indicators

Indicators are configurable objects that will spawn on the surface when a vein is detected underneath them. They are specified as a JSON object. They can also be specified as a JSON array to specify a weighted list of indicators.

An indicator must contain the following entries:

* `blocks` is a [Block Entry](#block-entries). This represents the possible states that the indicator will spawn. This supports weighted entries.

Indicators can also contain the following optional entries

* `rarity` (Default: 10) 1 / N blocks will generate an indicator, provided there is a valid ore block directly underneath.
* `max_depth` (Default: 32) This is the maximum depth for an ore block to generate which would attempt to spawn an indicator.
* `ignore_vegetation` (Default: true) If the vein should ignore vegetation when trying to spawn indicators. (i.e. should the indicators spawn underneath trees, leaves or huge mushrooms?)
* `ignore_liquids` (Default: false) If the vein should ignore liquids when trying to spawn indicators. (i.e. should the indicator spawn inside lakes or the ocean?)
* `blocks_under` (Default: accepts all blocks) This is a [Block Entry](#block-entries). The list of blocks that this indicator is allowed to spawn on.
* `weight` (Default: 1). If specified in a list, this is the weight assigned to each entry. 

An example indicator that spawns roses when ore blocks are less than twenty blocks under the surface would be added to the ore entry as such:

```json
{
  "example_entry": {
    "type": "cluster",
    "stone": "minecraft:stone",
    "ore": "minecraft:iron_ore",
    "indicator": {
      "blocks": "minecraft:red_flower",
      "max_depth": 20
    }
  }
}
```

### Block Entries

A Block Entry can be any of the following:

1. A single string representing a block's registry name: `"ore": "minecraft:iron_ore"`
2. A single string representing a block via a corresponding ore dictionary name, prefixed with "ore": `"ore": "ore:oreCoal"`.
2. A single object representing a block with metadata: `"ore": { "block": "minecraft:wool", "meta": 3 }`
3. A single object representing a block via an corresponding ore dictionary name: `"stone": { "ore": "oreIron" }`.
3. A list of objects (as above). Note that these can be weighed (when used in `ore`) but are not necessary. If weight is not found for a particular object, it will default to 1.
```json
{
  "ore": [
   {
      "block": "minecraft:wool",
      "weight": 4,
      "meta": 3
    },
    {
      "block": "minecraft:coal_ore",
      "weight": 39
    },
    {
      "ore": "oreIron"
    }
  ]
}
```

### Conditions

A condition is a check for each ore block in a vein. The vein will still generate, but ore each block will only appear if each condition is passed.

Conditions must be passed in as a list of json objects. Each object must have a `type` entry which specifies the name of the condition. The following types are available

##### Touching
Specification: `"type": "touching"
`
This condition specifies that an ore block must be adjacent to another block.

Required Parameters
 - `block` is a [Block Entry](#block-entries) (Not a list! A single block entry!) which specifies a block that the vein must touch

Optional Parameters
 - `min` (Default: 1) This specifies the minimum amount of additional blocks that this ore block must touch.
 - `max` (Default: Infinity) This specifies the maximum amount of additional blocks that this ore block must touch.
 
 Example: Any ore block must be adjacent to at least two air blocks.
 ```json
 {
  "conditions": [
    {
      "type": "touching",
      "block": "minecraft:air",
      "min": 2
    }
  ]
 }
 ```
