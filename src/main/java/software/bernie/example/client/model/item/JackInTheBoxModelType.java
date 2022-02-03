package software.bernie.example.client.model.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.Animator;
import software.bernie.geckolib3.model.GeoModelType;

public class JackInTheBoxModelType extends GeoModelType<ItemStack> {
	@Override
	public ResourceLocation getModelResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/jack.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/jack.png");
	}

	@Override
	public ResourceLocation getAnimationResource(ItemStack animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/jackinthebox.animation.json");
	}

	@Override
	protected Animator<ItemStack> createAnimator(ItemStack stack) {
		Animator<ItemStack> animator = new Animator<>(stack, this);
		AnimationController<ItemStack> controller = new AnimationController<>(stack, 20, this::predicate);

		// Registering a sound listener just makes it so when any sound keyframe is hit
		// the method will be called.
		// To register a particle listener or custom event listener you do the exact
		// same thing, just with registerParticleListener and
		// registerCustomInstructionListener, respectively.
		controller.registerSoundListener(this::soundListener);
		animator.addAnimationController(controller);
		return animator;
	}

	private AnimationBuilder predicate(AnimationController<ItemStack> controller, AnimationEvent<ItemStack> event) {
		// Not setting an animation here as that's handled below
		// TODO: item animation refactor
		return null;
	}

	private void soundListener(SoundKeyframeEvent<ItemStack> event) {
		// The animation for the JackInTheBoxItem has a sound keyframe at time 0:00.
		// As soon as that keyframe gets hit this method fires and it starts playing the
		// sound to the current player.
		// The music is synced with the animation so the box opens as soon as the music
		// plays the box opening sound
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.playSound(SoundRegistry.JACK_MUSIC.get(), 1, 1);
		}
	}
}
