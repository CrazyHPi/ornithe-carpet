# Carpet Mod Settings
## accurateBlockPlacement
Flexible/accurate block placement places blocks in a precise direction  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## antiCheatSpeed
Prevents players from rubberbanding when moving too fast  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `SURVIVAL`  
  
## asyncBeaconUpdate
Updating a beacon with redstone power sends an async NC and PP update each  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `POPULATION`  
  
## asyncPacketSyncing
Async world modification no longer mess up internal states of player chunk map  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `POPULATION`  
  
## blockEventRange
Range for block events to be synced to client  
* Type: `Integer`  
* Default value: `64`  
* Suggested options: `4`, `64`, `256`  
* Categories: `OPTIMIZATION`  
* Additional notes:  
  * You must choose a value from 1 to 1024  
  
## cactusCounter
Items thrown into a cactus will count items that are destroyed in them.  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`  
  
## cakeAlwaysEat
Clicking on a cake always eats a slice  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `TWEAK`  
  
## carefulBreak
Mining blocks while sneaking and subscribed to /log carefulBreak will place them in your inventory   
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## carpetCommandPermissionLevel
Carpet command permission level  
* Type: `String`  
* Default value: `ops`  
* Suggested options: `ops`, `0`, `2`, `4`  
* Categories: `CREATIVE`  
* Additional notes:  
  * This setting can only be set by admins with op level 4  
  
## chunkCommand
Enables /chunk command  
chunk info command  
* Type: `String`  
* Default value: `false`  
* Suggested options: `true`, `false`, `ops`, `0`, `2`, `4`  
* Categories: `COMMAND`, `CREATIVE`, `FEATURE`, `POPULATION`  
* Additional notes:  
  * This setting can only be set by admins with op level 4  
  
## commandLog
Enables /log command to monitor events via chat and overlays  
* Type: `String`  
* Default value: `true`  
* Suggested options: `true`, `false`, `ops`, `0`, `1`, `2`, `3`, `4`  
* Categories: `COMMAND`  
  
## commandPlayer
Enables /player command to control/spawn players  
* Type: `String`  
* Default value: `ops`  
* Suggested options: `ops`, `0`, `2`, `4`  
* Categories: `COMMAND`, `CREATIVE`, `FEATURE`  
* Additional notes:  
  * This setting can only be set by admins with op level 4  
  
## commandTick
Enables /tick command to control game clocks  
* Type: `String`  
* Default value: `ops`  
* Suggested options: `ops`, `0`, `2`, `4`  
* Categories: `CREATIVE`, `COMMAND`  
* Additional notes:  
  * This setting can only be set by admins with op level 4  
  
## creativeNoClip
Creative No Clip  
On servers it needs to be set on both   
client and server to function properly.  
Has no effect when set on the server only  
Can allow to phase through walls  
if only set on the carpet client side  
but requires some trapdoor magic to  
allow the player to enter blocks  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `CLIENT`  
  
## dispenserNoItemCost
Controls the circumstances in which dispensers don't consume items when firing  
* Type: `String`  
* Default value: `off`  
* Suggested options: `off`, `wool`, `all`  
* Categories: `CREATIVE`  
  
## dropperNoItemCost
Controls the circumstances in which droppers don't consume items when firing  
* Type: `String`  
* Default value: `off`  
* Suggested options: `off`, `wool`, `all`  
* Categories: `CREATIVE`  
  
## elytraCheckFix
Fixes the elytra check like in 1.15, players don't have to fall to deploy elytra  
Requires matching client fix, vanilla client still only deploys elytra while falling  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `BUGFIX`  
  
## explosionNoBlockDamage
Explosions won't destroy blocks  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `TNT`  
  
## fakePlayerStats
Allows fake players to gain stats.  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## fillLimit
Custom limit of changed blocks for /fill and /clone  
* Type: `Integer`  
* Default value: `32768`  
* Suggested options: `32768`, `114514`, `1919810`, `2147483647`  
* Categories: `CREATIVE`  
* Additional notes:  
  * Must be a positive number or 0  
  
## fillUpdates
fill/clone/setblock and structure blocks cause block updates  
When false, placed blocks skip onAdded/onRemoved and neighbor/observer updates  
/clone doesn't copy scheduled ticks  
/fill destroy and /clone move still send neighbor updates  
* Type: `Boolean`  
* Default value: `true`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`  
  
## fixAsyncChunkMapCrash
Chunk map no longer throws a possible CME with an async line running - infamous 8001gt crash  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `POPULATION`, `TWEAK`  
  
## flattenedNoteBlocks
Note blocks have exact 1.13 behavior  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## flippinCactus
Players can flip and rotate blocks when holding cactus  
Placing block while holding cactus offhand will flip block rotation  
Doesn't cause block updates when rotated/flipped  
Applies to pistons, observers, droppers, repeaters, stairs, glazed terracotta etc...  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `SURVIVAL`, `FEATURE`  
  
## hopperCounters
hoppers pointing to wool will count items passing through them  
works only in SP, and will slow down players  
Enables /counter command, and actions while placing red and green carpets on wool blocks  
Use /counter <color?> reset to reset the counter, and /counter <color?> to query  
In survival, place green carpet on same color wool to query, red to reset the counters  
Counters are global and shared between players, 16 channels available  
Items counted are destroyed, count up to one stack per tick per hopper  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `COMMAND`, `CREATIVE`, `FEATURE`  
  
## hopperNoItemCost
Controls the circumstances in which hoppers don't consume items when transferring out  
* Type: `String`  
* Default value: `off`  
* Suggested options: `off`, `wool`, `all`  
* Categories: `CREATIVE`  
  
## hudUpdateInterval
HUD update interval  
* Type: `Integer`  
* Default value: `20`  
* Suggested options: `1`, `5`, `20`, `100`  
* Categories: `FEATURE`  
  
## instantFall
Instant fall, the global flag turned on by suppression any part of a population  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `POPULATION`  
  
## instantTileTicks
Instant tile ticks, the dimensional flag turned on by suppressing a populating liquid pocket  
* Type: `String`  
* Default value: `none`  
* Suggested options: `none`, `overworld_false`, `overworld_true`, `nether_false`, `nether_true`, `end_false`, `end_true`  
* Categories: `CREATIVE`, `POPULATION`  
  
## language
Sets the language for Carpet  
* Type: `String`  
* Default value: `en_us`  
* Suggested options: `en_us`, `zh_cn`  
* Categories: `FEATURE`  
  
## maxEntityCollisions
Limit for entity collisions per entity per tick, 0 = no limit  
* Type: `Integer`  
* Default value: `0`  
* Suggested options: `0`, `20`, `100`  
* Categories: `OPTIMIZATION`  
* Additional notes:  
  * Must be a positive number or 0  
  
## miningGhostBlocksFix
Fix client-side ghost blocks when instant mining, like in 1.13  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `BUGFIX`  
  
## missingTools
Pickaxe mines Piston and Glass faster, hoe mines Sponge, Hay Bale, Leaves, Nether Wart blocks faster and takes damage.  
Pickaxes are effective on pistons in 1.16  
Hoes are effective in 1.16  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `SURVIVAL`, `FEATURE`  
  
## movableBlockEntities
Block entities can be pushed or pulled by pistons  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## observerInitialPulse
Whether observers pulse once when placed by a player  
* Type: `Boolean`  
* Default value: `true`  
* Allowed options: `true`, `false`  
* Categories: `SURVIVAL`, `TWEAK`  
  
## oreUpdateSuppressor
Emerald ore receiving power throws an exception on update  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`  
  
## persistentParrots
Parrots don't get of your shoulders until you receive proper damage  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `SURVIVAL`, `TWEAK`  
  
## playersSleepingPercentage
Percentage of online players required to be sleeping to skip night  
* Type: `Integer`  
* Default value: `100`  
* Suggested options: `100`, `0`, `20`  
* Categories: `FEATURE`  
* Additional notes:  
  * You must choose a value from 0 to 100  
  
## portalCreativeDelay
Amount of delay ticks to use a nether portal in creative  
* Type: `Integer`  
* Default value: `1`  
* Suggested options: `1`, `40`, `80`, `72000`  
* Categories: `CREATIVE`  
* Additional notes:  
  * You must choose a value from 1 to 72000  
  
## portalSurvivalDelay
Amount of delay ticks to use a nether portal in survival  
* Type: `Integer`  
* Default value: `80`  
* Suggested options: `1`, `40`, `80`, `72000`  
* Categories: `SURVIVAL`  
* Additional notes:  
  * You must choose a value from 1 to 72000  
  
## pushLimit
Customizable piston push limit  
* Type: `Integer`  
* Default value: `12`  
* Suggested options: `10`, `12`, `14`, `100`  
* Categories: `CREATIVE`  
* Additional notes:  
  * You must choose a value from 1 to 1024  
  
## quasiConnectivity
Rule controlling quasi-connectivity  
* Type: `Boolean`  
* Default value: `true`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## railPowerLimit
Customizable powered rail power range  
* Type: `Integer`  
* Default value: `9`  
* Suggested options: `9`, `15`, `30`  
* Categories: `CREATIVE`  
* Additional notes:  
  * You must choose a value from 1 to 1024  
  
## redstonePower
Redstone power, the global flag turned off by suppressing a population caused by an RS dust power check  
* Type: `Boolean`  
* Default value: `true`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `POPULATION`  
  
## relaxedBlockPlacement
Allow placing of pumpkins and fence gates mid-air  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `SURVIVAL`, `TWEAK`  
  
## reloadFakePlayers
Reloads fake players on server startup that were loaded before server shutdown.  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## removeFakePlayerSkins
Removes skins from fake players  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `SURVIVAL`  
  
## smoothClientAnimations
smooth client animations with low tps settings  
works only in SP, and will slow down players  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `SURVIVAL`, `CLIENT`  
  
## superSecretSetting
Gbhs sgnf sadsgras fhskdpri!!!  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `EXPERIMENTAL`  
  
## tileTickLimit
Customizable maximum # of tile ticks ran per tick  
* Type: `Integer`  
* Default value: `65536`  
* Suggested options: `1000`, `65536`, `2147483647`  
* Categories: `CREATIVE`  
* Additional notes:  
  * Must be a positive number or 0  
  
## tntDoNotUpdate
TNT doesn't update when placed against a power source  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`, `TNT`  
  
## tntFixedRandomAngle
Fix random angle of TNT for debugging; unit in radians, <0 = vanilla behavior  
* Type: `Double`  
* Default value: `-1.0`  
* Suggested options: `-1.0`  
* Categories: `CREATIVE`, `TNT`  
  
## tntFixedRandomRange
Fix random range of TNT to 0.7+0.6*setting, <0 = vanilla behavior  
* Type: `Float`  
* Default value: `-1.0`  
* Suggested options: `-1.0`  
* Categories: `CREATIVE`, `TNT`  
* Additional notes:  
  * Must be between 0 and 1  
  
## tntFuseLength
Changes default tnt fuse.  
* Type: `Integer`  
* Default value: `80`  
* Suggested options: `70`, `80`, `100`  
* Categories: `CREATIVE`, `TNT`  
* Additional notes:  
  * Must be a positive number or 0  
  
## tpAcrossDimensions
/tp will teleport the players across dimensions  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `CREATIVE`  
  
## viewDistance
Changes the view distance of the server.  
Set to 0 to not override the value in server settings  
* Type: `Integer`  
* Default value: `0`  
* Suggested options: `0`, `10`, `12`, `16`, `32`, `64`  
* Categories: `CREATIVE`  
* Additional notes:  
  * You must choose a value from 0 (use server settings) to 32  
  
## xpNoCooldown
Players absorb XP instantly, without delay  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `FEATURE`  
  
## yeetComparatorUpdates
Prevents all comparator updates  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## yeetInitialUpdates
Prevents all onAdded() calls  
Redstone wire doesn't update neighbors, pistons don't check for power, sand doesn't fall  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## yeetNeighborUpdates
Prevents all neighbor updates  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## yeetObserverUpdates
Prevents all observer updates  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## yeetRemovalUpdates
Prevents all onRemoved() calls  
Containers don't drop items, redstone doesn't depower neighbors, piston heads don't break their base  
Block entities are left behind and can crash the server (CCE)  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `YEET`  
  
## yeetTntInitialMotion
TNT no longer receives initial momentum  
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `TNT`, `YEET`  
  
