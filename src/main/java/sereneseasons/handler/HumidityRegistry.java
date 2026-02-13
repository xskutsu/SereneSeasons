package sereneseasons.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.config.SeasonsConfig;

public class HumidityRegistry
{
    // original rainfall values (Biome ID -> Rainfall)
    private static final Map<Integer, Float> baselineRainfall = new HashMap<Integer, Float>();
    private static boolean isSnapshotTaken = false;

    // capture the state of all biomes before any changes, needs to be called before post init
    public static void snapshotBaseline()
    {
        if (isSnapshotTaken) return;

        int count = 0;
        BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
        
        for (BiomeGenBase biome : biomeArray)
        {
            if (biome != null)
            {
                baselineRainfall.put(biome.biomeID, biome.rainfall);
                count++;
            }
        }
        
        isSnapshotTaken = true;
        sereneseasons.core.SereneSeasons.logger.info("HumidityRegistry: Snapshotted baseline rainfall for " + count + " biomes.");
    }

    // iterate through all registered biomes and apply the offset for the current seaason
    // new = clamp(og + offset, 0.0, 2.0)
    public static void updateBiomeHumidity(SubSeason subSeason)
    {
        if (!isSnapshotTaken)
        {
            snapshotBaseline();
        }

        float offset = SeasonsConfig.getHumidityOffset(subSeason);
        BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();

        for (BiomeGenBase biome : biomeArray)
        {
            if (biome != null && baselineRainfall.containsKey(biome.biomeID))
            {
                if (!sereneseasons.config.BiomeConfig.enablesSeasonalEffects(biome)) continue;

                float original = baselineRainfall.get(biome.biomeID);
                
                if (original <= 0.0F && offset > 0)
                {
                    biome.rainfall = 0.0F;
                }
                else
                {
                    biome.rainfall = MathHelper.clamp_float(original + offset, 0.0F, 2.0F);
                }
            }
        }
    }
    
    public static float getBaseline(int biomeId)
    {
        return baselineRainfall.containsKey(biomeId) ? baselineRainfall.get(biomeId) : -1.0F;
    }
}