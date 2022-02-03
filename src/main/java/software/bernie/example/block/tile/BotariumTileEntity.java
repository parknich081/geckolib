package software.bernie.example.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.registry.TileRegistry;

public class BotariumTileEntity extends BlockEntity {

	public BotariumTileEntity(BlockPos pos, BlockState state) {
		super(TileRegistry.BOTARIUM_TILE.get(), pos, state);
	}

}
