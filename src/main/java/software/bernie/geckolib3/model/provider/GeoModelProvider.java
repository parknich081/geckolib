package software.bernie.geckolib3.model.provider;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public interface GeoModelProvider<T> {
	GeoModel getModel(T object);
}
