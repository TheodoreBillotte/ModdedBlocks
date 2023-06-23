# Modded Blocks Project

## Description
A complete lib to easily add persistent data in any kind of blocks and possibility to add custom block with custom models and properties.

## Features
- [X] Add persistent data in any kind of blocks
- [X] When place a block, it will automatically add the persistent data
- [X] When break a block, it will automatically remove the persistent data
- [X] Custom block models (159 textures available)
- [X] Custom block specific properties

## How to use
### Add persistent data in any kind of blocks
```java
// Add persistent data in a block
BlockPersistentData data = new BlockPersistentData(block);
data.set("key", "value");
```

### Get persistent data in any kind of blocks
```java
// Get persistent data in a block
BlockPersistentData data = new BlockPersistentData(block);

// Get the value of the key as Object
data.get("key");

// Get the value of the key as String
data.getString("key");
// Or you can define type
data.get("key", String.class);
```

### Remove persistent data in any kind of blocks
```java
// Remove persistent data in a block
BlockPersistentData data = new BlockPersistentData(block);

// Remove the value of the key
data.remove("key");
// Or you can remove all persistent data
data.clear();
```

### Use events of blocks with persistent data
#### List of events
- PersistentDataBlockDestroyedEvent
- PersistentDataBlockFeltEvent
- PersistentDataBlockMovedEvent
- PersistentDataBlockPlaceEvent
- PersistentDataBlockStartFallingEvent

#### Example
```java
public class MyEvents implements Listener {

    @EventHandler
    public void onBlockPlace(PersistentDataBlockPlaceEvent event) {
        // Do something
    }
}
```

### Custom block models
#### Example config for custom block
```yaml
# Id of the block must be unique and between 0 and 159
id:
  # Name of the block in inventory
  name: block name
    # Lore of the block in inventory
  lore:
    - 'line 1 for lore'
    - 'line 2 for lore'
  tools:
    # Tool type to break the block
    # See below for the list of tool types
    type: PICKAXE
    # Time player need to break the block
    hardness: 2.0
    # Level of the tool to break the block
    # See below for the list of harvest levels
    harvest-level: 1
    # Resistance of the block to explosion (not working for now)
    blast-resistance: 100.0
  # Loots of the block
  loots:
    # List of items
    items:
      # Format - MATERIAL: amount%chance
      # You can find the list of materials here:
      # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
      DIAMOND: 1%50
      GOLD_INGOT: 1%100
    # XP dropped when break the block
    # Format - min~max (min and max are included)
    xp: 1~10
  properties:
    # If the block is unbreakable
    breakable: false
    # If the block is pushable by piston (not working for now)
    pushable: true
    # If the block is affected by gravity (not working for now)
    gravity: true
    # If the block can explode when ignite like TNT (not working for now)
    explosive: true
    # If the block is flammable (not working for now)
    flammable: true
    # If the block loots is affected by fortune
    fortunate: true
    # If the block loot himself when break with silk touch
    silk-touchable: true
    # If the block loot with wrong tool
    loot-need-tool: true
  # Persistent data added when place the block
  data:
    field-name1: data
    field-name2: 1
    field-name3: 1.0
  # Effects player when block is break
  effects:
    # Block material for particle effect (works only with block material)
    # You can find the list of materials here:
    # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
    break-particle: STONE
    # Sound effect when break the block
    break-sound: BLOCK_STONE_BREAK
  # Generate the block in the world
  generation:
    # List of blocks where the block can be generated
    # You can find the list of materials here:
    # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
    replace:
      - STONE
    # Spawn chance of the block at each chunk (0.0 to 1.0)
    chunk-chance: 0.7
    # Depth where the block can be generated (-64 to 320)
    depth: 0~32
    # Vein size of generated block
    # Format - min~max (min and max are included)
    vein-size: 1~10
    # Vein count of generated block in a single chunk
    # Format - min~max (min and max are included)
    vein-count: 1~10
    # If the block can be generated in slime chunk only
    slime-chunk-only: true
    # Generate the block in the world only if the biome is in the list (add ! before the biome to exclude it)
    # You can find the list of biomes here:
    # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html
    biomes:
      - PLAINS
      - DESERT
    # Generate the block in the world only if the world is in the list (add ! before the world to exclude it)
    # World names are case sensitive and must be the same as the name in the server.properties file
    # Default world names are: world, world_nether, world_the_end
    worlds:
      - world
      - !world_nether
    # Generate the block in the world only if the block is bordering with a block in the list (add ! before the block to exclude it)
    bordering:
      - AIR
```

#### List of tool types
- AXE
- HOE
- PICKAXE
- SHOVEL
- SHEARS
- OTHER

#### List of harvest levels
- 0: WOOD / GOLD
- 1: STONE
- 2: IRON
- 3: DIAMOND
- 4: NETHERITE
