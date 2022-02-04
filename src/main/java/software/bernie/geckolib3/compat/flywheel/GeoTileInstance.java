package software.bernie.geckolib3.compat.flywheel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.jozufozu.flywheel.util.transform.MatrixTransformStack;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.geo.render.AnimatingBone;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.model.GeoModelType;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class GeoTileInstance<T extends BlockEntity> extends BlockEntityInstance<T> implements DynamicInstance {

	private static final Function<ResourceLocation, RenderType> states = Util.memoize(RenderType::entityCutout);

	private final List<GeoInstanceTree> topLevelBones = new ArrayList<>();
	private final MatrixTransformStack stack;
	private final Animator<T> animator;

	public GeoTileInstance(MaterialManager materialManager, T tile, GeoModelType<T> modelProvider) {
		super(materialManager, tile);
		stack = new MatrixTransformStack();

		AnimatingModel model = modelProvider.getOrCreateBoneTree(tile);
		RenderType state = states.apply(modelProvider.getTextureResource(tile));

		stack.translate(getInstancePosition()).translate(0.5, 0.01, 0.5).rotateToFace(getFacing());

		for (AnimatingBone bone : model.getTopLevelBones()) {
			topLevelBones.add(new GeoInstanceTree(materialManager, state, bone));
		}

		animator = modelProvider.getOrCreateAnimator(tile);
	}

	@Override
	public void beginFrame() {

		animator.tickAnimation(new AnimationEvent<>(blockEntity), GeckoLibCache.getInstance().getParser(), AnimationTickHolder.getRenderTime());

		for (GeoInstanceTree bone : topLevelBones) {
			bone.recursiveCheckNeedsUpdate();
			bone.transform(stack.unwrap());
		}
	}

	@Override
	public void updateLight() {
		BlockPos pos = getWorldPosition();
		int block = world.getBrightness(LightLayer.BLOCK, pos);
		int sky = world.getBrightness(LightLayer.SKY, pos);

		for (GeoInstanceTree bone : topLevelBones) {
			bone.updateLight(block, sky);
		}
	}

	@Override
	public void remove() {
		topLevelBones.forEach(GeoInstanceTree::delete);
	}

	private Direction getFacing() {
		BlockState blockState = blockEntity.getBlockState();
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		} else if (blockState.hasProperty(BlockStateProperties.FACING)) {
			return blockState.getValue(BlockStateProperties.FACING);
		} else {
			return Direction.NORTH;
		}
	}
}
