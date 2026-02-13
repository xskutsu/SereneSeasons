/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.config;

public enum SeasonsOption implements ISyncedOption
{
    DAY_DURATION("Day Duration"),
    SUB_SEASON_DURATION("Sub Season Duration"),
    STARTING_SUB_SEASON("Starting Sub Season"),
    PROGRESS_SEASON_WHILE_OFFLINE("Progress Season While Offline"),

    ENABLE_SEASONAL_HUMIDITY("Enable Seasonal Humidity"),

    EARLY_SPRING_HUMIDITY_OFFSET("Early"),
    MID_SPRING_HUMIDITY_OFFSET("Mid"),
    LATE_SPRING_HUMIDITY_OFFSET("Late"),

    EARLY_SUMMER_HUMIDITY_OFFSET("Early"),
    MID_SUMMER_HUMIDITY_OFFSET("Mid"),
    LATE_SUMMER_HUMIDITY_OFFSET("Late"),

    EARLY_AUTUMN_HUMIDITY_OFFSET("Early"),
    MID_AUTUMN_HUMIDITY_OFFSET("Mid"),
    LATE_AUTUMN_HUMIDITY_OFFSET("Late"),

    EARLY_WINTER_HUMIDITY_OFFSET("Early"),
    MID_WINTER_HUMIDITY_OFFSET("Mid"),
    LATE_WINTER_HUMIDITY_OFFSET("Late");
    
    private final String optionName;

    SeasonsOption(String name)
    {
        this.optionName = name;
    }

    @Override
    public String getOptionName()
    {
        return this.optionName;
    }
}
