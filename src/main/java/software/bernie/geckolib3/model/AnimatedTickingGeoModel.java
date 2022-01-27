package software.bernie.geckolib3.model;

import java.util.Collections;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.util.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class AnimatedTickingGeoModel<T extends IAnimated & IAnimationTickable> extends AnimatedGeoModel<T> {
	public AnimatedTickingGeoModel() {
	}

	@Override
	public void setLivingAnimations(T entity, AnimationData manager, @Nullable AnimationEvent<T> customPredicate) {
		super.setLivingAnimations(entity, manager, customPredicate);

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			codeAnimations(entity, manager, customPredicate);
		}
	}

	public void codeAnimations(T entity, AnimationData data, AnimationEvent<?> customPredicate) {

	}
}
