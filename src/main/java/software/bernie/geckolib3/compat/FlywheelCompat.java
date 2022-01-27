package software.bernie.geckolib3.compat;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.util.FlwUtil;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.world.WorldEvent;
import software.bernie.geckolib3.compat.flywheel.GeoTileInstance;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class FlywheelCompat {

	private static boolean done = false;

	public static void init(final WorldEvent.Load event) {
		if (done) return;
		done = true;

		FlwUtil.getBlockEntityRenderers().forEach((type, renderer) -> {
			if (renderer instanceof GeoBlockRenderer<?> geo) {

				AnimatedGeoModel<?> modelProvider = geo.getGeoModelProvider();

				registerInstanceFactoryGenericsHack(type, modelProvider);
			}
		});
	}

	private static <T extends BlockEntity & IAnimated> void registerInstanceFactoryGenericsHack(BlockEntityType<?> type,
			AnimatedGeoModel<?> modelProvider) {
		InstancedRenderRegistry.configure(type).alwaysSkipRender()
				.factory((manager, te) -> (BlockEntityInstance) (new GeoTileInstance<>(manager, (T) te, (AnimatedGeoModel<T>) modelProvider)))
				.apply();
	}
}
