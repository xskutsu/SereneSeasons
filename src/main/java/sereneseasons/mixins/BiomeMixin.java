package sereneseasons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.asm.IBiomeMixin;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonASMHelper;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColourUtil;
import sereneseasons.handler.HumidityRegistry; 

@Mixin(BiomeGenBase.class)
public abstract class BiomeMixin implements IBiomeMixin
{
    @Shadow
    public boolean enableRain;

    @Shadow
    public abstract boolean func_150559_j();

    @Shadow
    protected static NoiseGeneratorPerlin temperatureNoise;

    @Shadow
    public float temperature;

    @Shadow
    public int biomeID;

    @Shadow
    @SideOnly(Side.CLIENT)
    public abstract float getFloatRainfall();

    @Shadow(remap = false)
    public abstract int getModdedBiomeGrassColor(int original);

    public boolean canSpawnLightningBoltOld()
    {
        // Overridden methods provided by WeatherTransformer
        return this.func_150559_j() ? false : this.enableRain;
    }

    public boolean getEnableSnowOld()
    {
        // Overridden methods provided by WeatherTransformer
        return this.func_150559_j();
    }

    public float getFloatTemperatureOld(int x, int y, int z)
    {
        // Overridden methods provided by WeatherTransformer
        if (y > 64)
        {
            float f = (float) temperatureNoise.func_151601_a((double) x * 1.0D / 8.0D, (double) z * 1.0D / 8.0D) * 4.0F;
            return this.temperature - (f + (float) y - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.temperature;
        }
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public boolean canSpawnLightningBolt()
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return canSpawnLightningBoltOld();
        }
        return SeasonASMHelper.shouldAddRainParticles(world, (BiomeGenBase) (Object) this);
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public boolean getEnableSnow()
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return getEnableSnowOld();
        }
        return SeasonASMHelper.shouldRenderRainSnow(world, (BiomeGenBase) (Object) this);
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public float getFloatTemperature(int x, int y, int z)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return getFloatTemperatureOld(x, y, z);
        }
        return SeasonASMHelper.getFloatTemperature(Minecraft.getMinecraft().theWorld, (BiomeGenBase) (Object) this, x, y, z);
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
        double d0 = (double) MathHelper.clamp_float(this.getFloatTemperature(p_150558_1_, p_150558_2_, p_150558_3_), 0.0F, 1.0F);
        
        float originalRainfall = HumidityRegistry.getBaseline(this.biomeID);
        if (originalRainfall == -1.0F) 
        {
             originalRainfall = this.getFloatRainfall();
        }

        double d1 = (double) MathHelper.clamp_float(originalRainfall, 0.0F, 1.0F);

        int old = getModdedBiomeGrassColor(ColorizerGrass.getGrassColor(d0, d1));

        BiomeGenBase biome = (BiomeGenBase)(Object)this;
        if (BiomeConfig.enablesSeasonalEffects(biome))
        {
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
            return SeasonColourUtil.applySeasonalGrassColouring(colorProvider, biome, old);
        }
        return old;
    }
}