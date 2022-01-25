package software.bernie.example.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class BotariumTileEntity extends BlockEntity implements IAnimated {
	private final AnimationData factory = new AnimationData();

	private <E extends BlockEntity & IAnimated> PlayState predicate(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;
		event.getController().setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", true));
		return PlayState.CONTINUE;
	}

	public BotariumTileEntity(BlockPos pos, BlockState state) {
		super(TileRegistry.BOTARIUM_TILE.get(), pos, state);
		factory.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationData getAnimationData() {
		return factory;
	}
}
