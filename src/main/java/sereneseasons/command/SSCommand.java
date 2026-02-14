package sereneseasons.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import sereneseasons.handler.HumidityRegistry;

public class SSCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "sereneseasons";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Lists.newArrayList("ss");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.sereneseasons.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    String[] getSeasons()
    {
        return new String[]
        { "early_spring", "mid_spring", "late_spring", "early_summer", "mid_summer", "late_summer", "early_autumn", "mid_autumn", "late_autumn", "early_winter", "mid_winter", "late_winter" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1) 
        {
            throw new WrongUsageException("commands.sereneseasons.usage");
        }
        
        switch (args[0].toLowerCase())
        {
            case "setseason":
                if (args.length < 2)
                {
                    String seasons = String.join(", ", getSeasons());
                    sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.available_seasons", seasons));
                    break;
                }

                setSeason(sender, args);
                break;

            case "gethumidity":
                getHumidity(sender);
                break;
        }
    }

    private void getHumidity(ICommandSender sender)
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));

        float original = HumidityRegistry.getBaseline(biome.biomeID);
        float current = biome.rainfall;

        sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.gethumidity.biome", biome.biomeName, biome.biomeID));
        sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.gethumidity.original", (original == -1 ? "Unknown" : String.format("%.2f", original))));
        sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.gethumidity.current", String.format("%.2f", current)));
    } 

    private void setSeason(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        Season.SubSeason newSeason = null;

        for (Season.SubSeason season : Season.SubSeason.VALUES)
        {
            if (season.toString().toLowerCase().equals(args[1].toLowerCase()))
            {
                newSeason = season;
                break;
            }
        }

        if (newSeason != null)
        {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(player.worldObj);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * newSeason.ordinal();
            seasonData.markDirty();
            sereneseasons.handler.HumidityRegistry.updateBiomeHumidity(newSeason);
            SeasonHandler.sendSeasonUpdate(player.worldObj);
            sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.setseason.success", args[1]));
        }
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.setseason.fail", args[1]));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                return getListOfStringsMatchingLastWord(args, "setseason", "gethumidity");
            case 2:
                switch (args[0])
                {
                    case "setseason":
                        return getListOfStringsMatchingLastWord(args, getSeasons());
                }
        }

        return null;
    }
}
