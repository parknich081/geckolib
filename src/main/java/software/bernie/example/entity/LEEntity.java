package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class LEEntity extends PathfinderMob implements IAnimated, IAnimationTickable {
	private final AnimationData data = new AnimationData();

	private <E extends IAnimated> PlayState predicate(AnimationController<E> controller, AnimationEvent<E> event) {
		controller.setAnimation(new AnimationBuilder().addAnimation("animation.geoLayerEntity.idle", true));
		return PlayState.CONTINUE;
	}

	public LEEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
	}

	@Override
	public AnimationData getAnimationData() {
		return data;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		super.registerGoals();
	}

	@Override
	public int tickTimer() {
		return tickCount;
	}
}
