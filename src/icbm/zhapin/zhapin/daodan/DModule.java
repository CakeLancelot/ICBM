package icbm.zhapin.zhapin.daodan;

import icbm.core.di.MICBM;
import icbm.zhapin.muoxing.daodan.MMYaSuo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class DModule extends DaoDanTeBie
{
	public DModule(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{

	}

	@Override
	public MICBM getMuoXing()
	{
		return MMYaSuo.INSTANCE;
	}
}
