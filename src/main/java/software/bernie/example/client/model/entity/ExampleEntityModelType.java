package software.bernie.example.client.model.entity;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import software.bernie.example.entity.ExampleEntityAnimator;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.GeoModelType;

public class ExampleEntityModelType extends GeoModelType<GeoExampleEntity> {

	public static final String ANIM_NAME_PREFIX = "animation.biped.";
	public static final String ANIM_NAME_IDLE = ANIM_NAME_PREFIX + "idle";
	public static final String ANIM_NAME_SITTING = ANIM_NAME_PREFIX + "sit";
	public static final String ANIM_NAME_SNEAKING = ANIM_NAME_PREFIX + "body.sneak";
	public static final String ANIM_NAME_BLOCKING_LEFT = ANIM_NAME_PREFIX + "arms.left.block";
	public static final String ANIM_NAME_BLOCKING_RIGHT = ANIM_NAME_PREFIX + "arms.right.block";
	public static final String ANIM_NAME_SPELLCASTING = ANIM_NAME_PREFIX + "arms.cast-spell";
	public static final String ANIM_NAME_SPEAR_POSE_LEFT = ANIM_NAME_PREFIX + "arms.left.spear";
	public static final String ANIM_NAME_SPEAR_POSE_RIGHT = ANIM_NAME_PREFIX + "arms.right.spear";
	public static final String ANIM_NAME_FIREARM_POSE_LEFT = ANIM_NAME_PREFIX + "arms.left.firearm";
	public static final String ANIM_NAME_FIREARM_POSE_RIGHT = ANIM_NAME_PREFIX + "arms.right.firearm";
	public static final String ANIM_NAME_GREATSWORD_POSE = ANIM_NAME_PREFIX + "arms.greatsword";
	public static final String ANIM_NAME_GREATSWORD_SWING = ANIM_NAME_PREFIX + "arms.attack-greatsword";
	public static final String ANIM_NAME_SPEAR_SWING = ANIM_NAME_PREFIX + "arms.attack-spear";

	@Override
	public ResourceLocation getAnimationResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/extendedrendererentity.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/extendedrendererentity.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/extendedrendererentity.png");
	}

	@Override
	protected Animator<GeoExampleEntity> createAnimator(GeoExampleEntity geoExampleEntity) {
		return new ExampleEntityAnimator(geoExampleEntity, createModelFor(geoExampleEntity), this).createChannel()
				.setPredicate(this::predicate).setPredicate(this::predicateTwoHandedSwing)
				.setPredicate(this::predicateTwoHandedPose).setPredicate(this::predicateRightArmPose)
				.setPredicate(this::predicateRightArmSwing).setPredicate(this::predicateLeftArmSwing)
				.setPredicate(this::predicateLeftArmPose).build();
	}

	private AnimationBuilder predicateRightArmSwing(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		return this.predicateHandSwing(event.getAnimatable().getRightHand(), false, controller, event);
	}

	private AnimationBuilder predicateLeftArmSwing(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		return this.predicateHandSwing(event.getAnimatable().getLeftHand(), true, controller, event);
	}

	protected AnimationBuilder predicateHandSwing(InteractionHand hand, boolean leftHand,
			AnimationChannel<GeoExampleEntity> controller, AnimationEvent<GeoExampleEntity> event) {
		if (event.getAnimatable().swinging && !event.getAnimatable().isTwoHandedAnimationRunning()) {
			ItemStack handItemStack = event.getAnimatable().getItemInHand(hand);
			if (!handItemStack.isEmpty()) {
				if (handItemStack.getItem().getUseAnimation(handItemStack) == UseAnim.EAT
						|| handItemStack.getItem().getUseAnimation(handItemStack) == UseAnim.DRINK) {
					// Eating/Drinking animation
				} else {
					// Normal swinging
				}
			}
		}
		return new AnimationBuilder();
	}

	private AnimationBuilder predicate(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		return new AnimationBuilder().addAnimation("animation.bat.fly", true);
	}

	private AnimationBuilder predicateRightArmPose(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		return this.predicateHandPose(event.getAnimatable().getRightHand(), false, controller, event);
	}

	private AnimationBuilder predicateLeftArmPose(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		return this.predicateHandPose(event.getAnimatable().getLeftHand(), true, controller, event);
	}

	protected AnimationBuilder predicateHandPose(InteractionHand hand, boolean leftHand,
			AnimationChannel<GeoExampleEntity> controller, AnimationEvent<GeoExampleEntity> event) {
		ItemStack handItemStack = event.getAnimatable().getItemInHand(hand);
		if (!handItemStack.isEmpty() && !event.getAnimatable().isTwoHandedAnimationRunning()) {
			Item handItem = handItemStack.getItem();
			if (event.getAnimatable().isBlocking()
					&& (handItem instanceof ShieldItem || handItem.getUseAnimation(handItemStack) == UseAnim.BLOCK)) {
				return new AnimationBuilder()
						.addAnimation(leftHand ? ANIM_NAME_BLOCKING_LEFT : ANIM_NAME_BLOCKING_RIGHT, true);
			} else {
				// If the item is a small gun play the correct animation
			}
		}
		return new AnimationBuilder();
	}

	private AnimationBuilder predicateTwoHandedPose(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		if (event.getAnimatable().isTwoHandedAnimationRunning()) {
			if (event.getAnimatable().isSpellCasting()) {
				return new AnimationBuilder().addAnimation(ANIM_NAME_SPELLCASTING, true);
			} else {
				// First: Check for firearm, spear and greatsword in either hand
				// Main hand has priority
				Optional<AnimationBuilder> resultState = performTwoHandedLogicPerHand(
						event.getAnimatable().getMainHandItem(), event.getAnimatable().isLeftHanded(), controller,
						event);
				if (!resultState.isPresent()) {
					resultState = performTwoHandedLogicPerHand(event.getAnimatable().getOffhandItem(),
							!event.getAnimatable().isLeftHanded(), controller, event);
				}
				if (resultState.isPresent()) {
					return resultState.get();
				}
			}
		}
		return new AnimationBuilder();
	}

	private Optional<AnimationBuilder> performTwoHandedLogicPerHand(ItemStack itemStack, boolean leftHanded,
			AnimationChannel<GeoExampleEntity> controller, AnimationEvent<GeoExampleEntity> event) {
		if (itemStack.isEmpty()) {
			return Optional.empty();
		}
		Item item = itemStack.getItem();
		// If item instanceof ItemGreatsword => Greatsword animation
		// If item instanceof Spear => spear animation
		// If item instanceof Firearm/Bow/Crossbow => firearm animation
		if (item.getUseAnimation(itemStack) == UseAnim.BOW || item.getUseAnimation(itemStack) == UseAnim.CROSSBOW) {
			// Firearm
			return Optional.of(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_FIREARM_POSE_LEFT : ANIM_NAME_FIREARM_POSE_RIGHT, true));
		} else if (item.getUseAnimation(itemStack) == UseAnim.SPEAR) {
			// Yes this is for tridents but we can use it anyway
			// Spear
			return Optional.of(new AnimationBuilder()
					.addAnimation(leftHanded ? ANIM_NAME_SPEAR_POSE_LEFT : ANIM_NAME_SPEAR_POSE_RIGHT, true));
		}
		// If item is greatsword => greatsword animation
		return Optional.of(new AnimationBuilder());
	}

	private AnimationBuilder predicateTwoHandedSwing(AnimationChannel<GeoExampleEntity> controller,
			AnimationEvent<GeoExampleEntity> event) {
		if (event.getAnimatable().isTwoHandedAnimationRunning() && event.getAnimatable().swinging) {
			// Check for greatsword & spear and play their animations
			if (event.getAnimatable().getMainHandItem().getItem()
					.getUseAnimation(event.getAnimatable().getMainHandItem()) == UseAnim.SPEAR
					|| event.getAnimatable().getOffhandItem().getItem()
							.getUseAnimation(event.getAnimatable().getOffhandItem()) == UseAnim.SPEAR) {
				// Spear use animation
				return new AnimationBuilder().addAnimation(ANIM_NAME_SPEAR_SWING, false);
			}
		}
		return new AnimationBuilder();
	}

}
