package software.bernie.geckolib3.compat;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.util.FlwUtil;

import net.minecraftforge.event.world.WorldEvent;
import software.bernie.geckolib3.compat.flywheel.GeoTileInstance;
import software.bernie.geckolib3.model.GeoModelType;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class FlywheelCompat {

	private static boolean done = false;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void init(final WorldEvent.Load event) {
		if (done) return;
		done = true;

		FlwUtil.getBlockEntityRenderers().forEach((type, renderer) -> {
			if (renderer instanceof GeoBlockRenderer<?> geo) {

				GeoModelType<?> modelType = geo.getModelType();

				InstancedRenderRegistry.configure(type).alwaysSkipRender()
						.factory((manager, te) -> new GeoTileInstance(manager, te, modelType))
						.apply();
			}
		});
	}

}
