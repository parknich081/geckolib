package software.bernie.example.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.GeoModelType;

public class PotatoArmorModelType extends GeoModelType<ItemStack> {
	@Override
	public ResourceLocation getModelResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/potato_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/potato_armor.png");
	}

	@Override
	public ResourceLocation getAnimationResource(ItemStack animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/potato_armor.animation.json");
	}

	@Override
	protected Animator<ItemStack> createAnimator(ItemStack stack) {
		return new Animator<>(stack, createModelFor(stack), this)
				.createChannel()
				.setTransitionLengthTicks(20)
				.setPredicate(this::predicate)
				.build();
	}

	// Predicate runs every frame
	private AnimationBuilder predicate(AnimationChannel<ItemStack> controller, AnimationEvent<ItemStack> event) {
		// TODO: item animation refactor
		return null;
		//		// This is all the extradata this event carries. The livingentity is the entity
		//		// that's wearing the armor. The itemstack and equipmentslottype are self
		//		// explanatory.
		//		List<EquipmentSlot> slotData = event.getExtraDataOfType(EquipmentSlot.class);
		//		List<ItemStack> stackData = event.getExtraDataOfType(ItemStack.class);
		//		LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);
		//
		//		// Always loop the animation but later on in this method we'll decide whether or
		//		// not to actually play it
		//		controller.setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", true));
		//
		//		// If the living entity is an armorstand just play the animation nonstop
		//		if (livingEntity instanceof ArmorStand) {
		//			return PlayState.CONTINUE;
		//		}
		//
		//		// The entity is a player, so we want to only play if the player is wearing the
		//		// full set of armor
		//		else if (livingEntity instanceof Player) {
		//			Player player = (Player) livingEntity;
		//
		//			// Get all the equipment, aka the armor, currently held item, and offhand item
		//			List<Item> equipmentList = new ArrayList<>();
		//			player.getAllSlots().forEach((x) -> equipmentList.add(x.getItem()));
		//
		//			// elements 2 to 6 are the armor so we take the sublist. Armorlist now only
		//			// contains the 4 armor slots
		//			List<Item> armorList = equipmentList.subList(2, 6);
		//
		//			// Make sure the player is wearing all the armor. If they are, continue playing
		//			// the animation, otherwise stop
		//			boolean isWearingAll = armorList.containsAll(Arrays.asList(ItemRegistry.POTATO_BOOTS.get(), ItemRegistry.POTATO_LEGGINGS.get(), ItemRegistry.POTATO_CHEST.get(), ItemRegistry.POTATO_HEAD.get()));
		//			return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
		//		}
		//		return PlayState.STOP;
	}
}
