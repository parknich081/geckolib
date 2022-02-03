package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.ExampleModelTypes;
import software.bernie.example.client.model.entity.LEModelType;
import software.bernie.example.client.renderer.entity.layer.GeoExampleLayer;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<LEEntity> {

	public LERenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, ExampleModelTypes.LE);
		this.addLayer(new GeoExampleLayer<>(this));
		this.shadowRadius = 0.25f;
	}
}
