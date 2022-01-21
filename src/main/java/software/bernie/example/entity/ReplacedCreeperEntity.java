package software.bernie.example.entity;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimatableSingleton;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ReplacedCreeperEntity implements IAnimatableSingleton<Creeper> {
	private final AnimationFactory<Creeper> factory = new AnimationFactory<>(this::registerControllers);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void registerControllers(Creeper creeper, AnimationData data) {
		data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
	}

	private <P extends IAnimated> PlayState predicate(AnimationEvent<P> event) {
		if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("creeper_walk", true));
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("creeper_idle", true));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationData getAnimationData(Creeper key) {
		return factory.getOrCreateAnimationData(key);
	}
}
