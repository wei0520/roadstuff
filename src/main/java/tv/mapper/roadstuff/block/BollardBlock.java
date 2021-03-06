package tv.mapper.roadstuff.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class BollardBlock extends RotatableBlock implements IWaterLoggable
{
    private static final VoxelShape EUR_BOLLARD_N = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 10.0D, 16.0D, 9.0D);
    private static final VoxelShape EUR_BOLLARD_E = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 10.0D);
    private static final VoxelShape EUR_BOLLARD_S = Block.makeCuboidShape(6.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape EUR_BOLLARD_W = Block.makeCuboidShape(7.0D, 0.0D, 6.0D, 9.0D, 16.0D, 9.0D);

    private static final VoxelShape EUR_SMALL_BOLLARD_N = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 8.0D);
    private static final VoxelShape EUR_SMALL_BOLLARD_E = Block.makeCuboidShape(8.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D);
    private static final VoxelShape EUR_SMALL_BOLLARD_S = Block.makeCuboidShape(7.0D, 0.0D, 8.0D, 9.0D, 8.0D, 9.0D);
    private static final VoxelShape EUR_SMALL_BOLLARD_W = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 8.0D, 8.0D, 9.0D);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private boolean small = false;

    public BollardBlock(Properties properties, boolean small)
    {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(DIRECTION, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
        this.small = small;
    }

    public BollardBlock(Properties properties, ToolType toolType, boolean small)
    {
        super(properties, toolType);
        this.setDefaultState(this.getDefaultState().with(DIRECTION, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
        this.small = small;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if(small)
            switch((Direction)state.get(DIRECTION))
            {
                case NORTH:
                    return EUR_SMALL_BOLLARD_N;
                case EAST:
                    return EUR_SMALL_BOLLARD_E;
                case SOUTH:
                    return EUR_SMALL_BOLLARD_S;
                case WEST:
                    return EUR_SMALL_BOLLARD_W;
                default:
                    return EUR_SMALL_BOLLARD_N;
            }
        else
            switch((Direction)state.get(DIRECTION))
            {
                case NORTH:
                    return EUR_BOLLARD_N;
                case EAST:
                    return EUR_BOLLARD_E;
                case SOUTH:
                    return EUR_BOLLARD_S;
                case WEST:
                    return EUR_BOLLARD_W;
                default:
                    return EUR_BOLLARD_N;
            }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return hasEnoughSolidSide(worldIn, pos.down(), Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockpos = context.getPos();
        IFluidState ifluidstate = context.getWorld().getFluidState(blockpos);

        return this.getDefaultState().with(DIRECTION, context.getPlacementHorizontalFacing()).with(WATERLOGGED, Boolean.valueOf(Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER)));
    }

    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        if(facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos))
            return Blocks.AIR.getDefaultState();

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(DIRECTION).add(WATERLOGGED);
    }
}