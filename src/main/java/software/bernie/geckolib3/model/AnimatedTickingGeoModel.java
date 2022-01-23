package software.bernie.geckolib3.model;

import java.util.Collections;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class AnimatedTickingGeoModel<T extends IAnimated & IAnimationTickable> extends AnimatedGeoModel<T> {
	public AnimatedTickingGeoModel() {
	}

	public boolean isInitialized() {
		return this.getAnimationProcessor().isNotEmpty();
	}

	@Override
	public void setLivingAnimations(T entity, AnimationData manager, @Nullable AnimationEvent<T> customPredicate) {
		// Each animation has it's own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		if (manager.startTick == null) {
			manager.startTick = (double) (entity.tickTimer() + Minecraft.getInstance().getFrameTime());
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			manager.tick = (entity.tickTimer() + Minecraft.getInstance().getFrameTime());
			double gameTick = manager.tick;
			double deltaTicks = gameTick - lastGameTickTime;
			seekTime += deltaTicks;
			lastGameTickTime = gameTick;
		}

		AnimationEvent<T> predicate;
		if (customPredicate == null) {
			predicate = new AnimationEvent<T>(entity, 0, 0, 0, false, Collections.emptyList());
		} else {
			predicate = customPredicate;
		}

		manager.setModelRendererList(getModel(entity).getBones());

		predicate.animationTick = seekTime;
		getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), seekTime);
		if (this.getAnimationProcessor().isNotEmpty()) {
			getAnimationProcessor().tickAnimation(manager, seekTime, predicate,
					GeckoLibCache.getInstance().parser, shouldCrashOnMissing);
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			codeAnimations(entity, manager, customPredicate);
		}
	}

	public void codeAnimations(T entity, AnimationData data, AnimationEvent<?> customPredicate) {

	}
}
