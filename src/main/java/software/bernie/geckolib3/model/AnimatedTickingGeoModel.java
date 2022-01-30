package software.bernie.geckolib3.model;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public abstract class AnimatedTickingGeoModel<T extends IAnimated & IAnimationTickable> extends AnimatedGeoModel<T> {
	public AnimatedTickingGeoModel() {
	}

	@Override
	public void setLivingAnimations(T entity, AnimationData manager, AnimationEvent<T> customPredicate) {
		super.setLivingAnimations(entity, manager, customPredicate);

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			codeAnimations(entity, manager, customPredicate);
		}
	}

	public void codeAnimations(T entity, AnimationData data, AnimationEvent<T> customPredicate) {

	}
}
