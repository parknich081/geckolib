package software.bernie.example.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.item.GeoArmorItem;

public class PotatoArmorItem extends GeoArmorItem {
	public PotatoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder.tab(GeckoLibMod.geckolibItemGroup));
	}
}
