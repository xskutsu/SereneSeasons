/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonASMHelper;

public class RandomUpdateHandler
{
    void turnIntoWater(World worldIn, int x, int y, int z)
    {
        if (worldIn.provider.isHellWorld)
        {
            worldIn.setBlockToAir(x, y, z);
        }
        else
        {
            worldIn.setBlock(x, y, z, Blocks.water);
            worldIn.notifyBlockOfNeighborChange(x, y, z, Blocks.water);
        }
    }

    // Randomly melt ice and snow when it isn't winter
    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.END && event.side == Side.SERVER)
        {

            Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.world).getSubSeason();
            Season season = subSeason.getSeason();

            if (season == Season.WINTER)
            {
                if (ModConfig.seasons.changeWeatherFrequency)
                {
                    if (event.world.getWorldInfo().isThundering())
                    {
                        event.world.getWorldInfo().setThundering(false);
                    }
                    if (!event.world.getWorldInfo().isRaining() && event.world.getWorldInfo().getRainTime() > 36000)
                    {
                        event.world.getWorldInfo().setRainTime(event.world.rand.nextInt(24000) + 12000);
                    }
                }
            }
            else
            {
                if (ModConfig.seasons.changeWeatherFrequency)
                {
                    if (season == Season.SPRING)
                    {
                        if (!event.world.getWorldInfo().isRaining() && event.world.getWorldInfo().getRainTime() > 96000)
                        {
                            event.world.getWorldInfo().setRainTime(event.world.rand.nextInt(84000) + 12000);
                        }
                    }
                    else if (season == Season.SUMMER)
                    {
                        if (!event.world.getWorldInfo().isThundering() && event.world.getWorldInfo().getThunderTime() > 36000)
                        {
                            event.world.getWorldInfo().setThunderTime(event.world.rand.nextInt(24000) + 12000);
                        }
                    }
                }

                if (ModConfig.seasons.generateSnowAndIce && SeasonsConfig.isDimensionWhitelisted(event.world.provider.dimensionId))
                {
                    if (ModConfig.seasons.meltRolls <= 0) return;

                    WorldServer world = (WorldServer) event.world;
                    List<Chunk> chunks = new ArrayList<Chunk>(world.theChunkProviderServer.loadedChunks);

                    for (Chunk chunk : chunks)
                    {
                        for (int i = 0; i < ModConfig.seasons.meltRolls; i++)
                        {
                            if (world.rand.nextFloat() < ModConfig.seasons.meltChance)
                            {
                                processMeltRoll(world, chunk);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processMeltRoll(WorldServer world, Chunk chunk)
    {
        int x = (chunk.xPosition << 4) + world.rand.nextInt(16);
        int z = (chunk.zPosition << 4) + world.rand.nextInt(16);
        
        int y = world.getPrecipitationHeight(x, z);
        if (y <= 0) return;

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (!BiomeConfig.enablesSeasonalEffects(biome)) return;

        Block topBlock = world.getBlock(x, y, z);
        if (topBlock == Blocks.snow_layer)
        {
            if (SeasonASMHelper.getFloatTemperature(world, biome, x, y, z) >= 0.15F)
            {
                world.setBlockToAir(x, y, z);
                return;
            }
        }

        Block belowBlock = world.getBlock(x, y - 1, z);
        if (belowBlock == Blocks.ice)
        {
            if (SeasonASMHelper.getFloatTemperature(world, biome, x, y - 1, z) >= 0.15F)
            {
                turnIntoWater(world, x, y - 1, z);
            }
        }
    }
}