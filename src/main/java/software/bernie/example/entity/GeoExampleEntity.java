package software.bernie.example.entity;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

public class GeoExampleEntity extends PathfinderMob {
	
	public GeoExampleEntity(EntityType<? extends GeoExampleEntity> type, Level worldIn) {
		super(type, worldIn);
		this.noCulling = true;
	}

	public boolean isTwoHandedAnimationRunning() {
		return this.isSpellCasting() || this.isSpinToWinActive() || this.isWieldingTwoHandedWeapon();
	}

	public boolean isSpinToWinActive() {
		return false;
	}

	public boolean isSpellCasting() {
		return false;
	}

	public boolean isWieldingTwoHandedWeapon() {
		return
		// Bow and crossbows
		(this.getMainHandItem().getItem() instanceof ProjectileWeaponItem
				|| this.getOffhandItem().getItem() instanceof ProjectileWeaponItem
				|| this.getMainHandItem().getUseAnimation() == UseAnim.BOW
				|| this.getOffhandItem().getUseAnimation() == UseAnim.BOW
				|| this.getMainHandItem().getUseAnimation() == UseAnim.CROSSBOW
				|| this.getOffhandItem().getUseAnimation() == UseAnim.CROSSBOW)
				|| (this.getMainHandItem().getUseAnimation() == UseAnim.SPEAR
						|| this.getOffhandItem().getUseAnimation() == UseAnim.SPEAR);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		super.registerGoals();
	}

	public InteractionHand getLeftHand() {
		return this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	public InteractionHand getRightHand() {
		return !this.isLeftHanded() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	@Override
	protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack item = pPlayer.getItemInHand(pHand);
		if (item != null && !item.isEmpty() && !this.level.isClientSide) {
			if (item.getItem() instanceof ArmorItem) {
				ArmorItem ai = (ArmorItem) item.getItem();
				this.setItemSlot(ai.getSlot(), item);
			} else if (item.getItem().getEquipmentSlot(item) != null) {
				this.setItemSlot(item.getItem().getEquipmentSlot(item), item);
			} else if (item.getItem() instanceof BlockItem
					&& ((BlockItem) item.getItem()).getBlock() instanceof AbstractSkullBlock) {
				this.setItemSlot(EquipmentSlot.HEAD, item);
			} else {
				this.setItemInHand(pHand, item);
			}
			pPlayer.sendMessage(
					new TextComponent("Equipped item: " + item.getItem().getRegistryName().toString() + "!"),
					this.getUUID());
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(pPlayer, pHand);
	}
}
