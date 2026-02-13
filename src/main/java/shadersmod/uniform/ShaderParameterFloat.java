package shadersmod.uniform;

import net.minecraft.world.World;

public enum ShaderParameterFloat {
	DUMMY;

	@SuppressWarnings("null")
	public float eval() {
		World w = null;
		w.getBiomeGenForCoords(0, 0);
		return 0;
	}
}
