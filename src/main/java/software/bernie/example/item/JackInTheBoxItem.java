package software.bernie.example.item;

import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.example.ExampleModelTypes;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements ISyncable {
	public static final String CONTROLLER_NAME = "popupController";
	private static final int ANIM_OPEN = 0;

	public JackInTheBoxItem(Properties properties) {
		super(properties.tab(GeckoLibMod.geckolibItemGroup));
		GeckoLibNetwork.registerSyncable(this);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final BlockEntityWithoutLevelRenderer renderer = new GeoItemRenderer<>(ExampleModelTypes.JACK_IN_THE_BOX);

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			// Gets the item that the player is holding, should be a JackInTheBoxItem
			final ItemStack stack = player.getItemInHand(hand);
			final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) world);
			// Tell all nearby clients to trigger this JackInTheBoxItem
			final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
			GeckoLibNetwork.syncAnimation(target, this, id, ANIM_OPEN);
		}
		return super.use(world, player, hand);
	}

	@Override
	public void onAnimationSync(int id, int state) {
//		if (state == ANIM_OPEN) {
//			// Always use GeckoLibUtil to get AnimationControllers when you don't have
//			// access to an AnimationEvent
//			final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);
//
//			if (controller.getAnimationState() == AnimationState.Stopped) {
//				final LocalPlayer player = Minecraft.getInstance().player;
//				if (player != null) {
//					player.displayClientMessage(new TextComponent("Opening the jack in the box!"), true);
//				}
//				// Set the animation to open the JackInTheBoxItem which will start playing music
//				// and
//				// eventually do the actual animation. Also sets it to not loop
//				controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", false));
//			}
//		}
	}
}
