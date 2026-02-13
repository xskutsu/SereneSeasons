package serenetweaks.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;

public class TimeStampsWorldSavedData extends WorldSavedData{
	
	private static Map<String, Integer> timeStampMap = new HashMap<>();
	
	public static void setChunkTimeStamp(Chunk chunk, int timeStamp) {
		if (timeStampMap.isEmpty()) {
			get(chunk.worldObj);
		}
		String key = chunk.xPosition + "_" + chunk.zPosition;
		timeStampMap.put(key, timeStamp);
	}
	
	public static int getChunkTimeStamp(Chunk chunk) {
		if (timeStampMap.isEmpty()) {
			get(chunk.worldObj);
		}
		String key = chunk.xPosition + "_" + chunk.zPosition;
		if (!timeStampMap.containsKey(key)) {
			return 0;
		}
		return timeStampMap.get(key);
	}
	
	private static final String DATA_NAME = "SereneTweaks_TimeStampData";
	
	public TimeStampsWorldSavedData() {
		super(DATA_NAME);
	}
	
	public TimeStampsWorldSavedData(String dataName) {
		super(dataName);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		@SuppressWarnings("unchecked")
		Set<String> keys = nbt.func_150296_c();
		for (String key : keys) {
			int value = nbt.getInteger(key);
			timeStampMap.put(key, value);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		Set<String> keys = timeStampMap.keySet();
		for (String key : keys) {
			int value = timeStampMap.get(key);
			nbt.setInteger(key, value);
		}
	}
	
	public static TimeStampsWorldSavedData get(World world) {
		  MapStorage storage = world.perWorldStorage;
		  TimeStampsWorldSavedData instance = (TimeStampsWorldSavedData) storage.loadData(TimeStampsWorldSavedData.class, DATA_NAME);

		  if (instance == null) {
		    instance = new TimeStampsWorldSavedData();
		    storage.setData(DATA_NAME, instance);
		  }
		  return instance;
		}

}
