package software.bernie.example.block.tile;

import net.minecraft.block.entity.BlockEntity;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HabitatTileEntity extends BlockEntity implements IAnimatable {
	private final AnimationFactory factory = new AnimationFactory(this);

	public HabitatTileEntity() {
		super(TileRegistry.HABITAT_TILE);
	}

	private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gecko_habitat.idle", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<HabitatTileEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
}