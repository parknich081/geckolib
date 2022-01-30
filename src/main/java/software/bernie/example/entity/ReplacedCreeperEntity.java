package software.bernie.example.entity;

import net.minecraft.world.entity.monster.Creeper;
import software.bernie.geckolib3.core.IAnimatableSingleton;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ReplacedCreeperEntity implements IAnimatableSingleton<Creeper> {
	private final AnimationFactory<Creeper> factory = new AnimationFactory<>(this::createAnimationData);

	public AnimationData createAnimationData(Creeper creeper) {
		AnimationData data = new AnimationData();
		data.addAnimationController(new AnimationController<>(creeper, "controller", 20, this::predicate));
		return data;
	}

	private AnimationBuilder predicate(AnimationController<Creeper> controller, AnimationEvent<Creeper> event) {
		if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
			return new AnimationBuilder().addAnimation("creeper_walk", true);
		} else {
			return new AnimationBuilder().addAnimation("creeper_idle", true);
		}
	}

	@Override
	public AnimationData getAnimationData(Creeper key) {
		return factory.getOrCreateAnimationData(key);
	}
}
