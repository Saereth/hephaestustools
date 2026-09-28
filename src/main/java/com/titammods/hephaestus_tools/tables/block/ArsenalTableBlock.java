package com.titammods.hephaestus_tools.tables.block;

import com.mojang.serialization.MapCodec;
import com.titammods.hephaestus_tools.registry.ModSounds;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ArsenalTableBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<TablePart> PART = EnumProperty.create("part", TablePart.class);
    private static final MapCodec<ArsenalTableBlock> CODEC = simpleCodec(ArsenalTableBlock::new);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public ArsenalTableBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(PART, TablePart.MAIN));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) { return SHAPE; }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) { return true; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING, PART);
    }

    private static Direction secondDir(Direction facing) { return facing.getCounterClockWise(); }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockPos second = ctx.getClickedPos().relative(secondDir(facing));
        Level level = ctx.getLevel();
        if (level.getBlockState(second).canBeReplaced(ctx) && level.getWorldBorder().isWithinBounds(second))
            return this.defaultBlockState().setValue(FACING, facing).setValue(PART, TablePart.MAIN);
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING);
            BlockPos second = pos.relative(secondDir(facing));
            level.setBlock(second, state.setValue(PART, TablePart.SECOND), 3);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    private static BlockPos mainPos(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return state.getValue(PART) == TablePart.MAIN ? pos : pos.relative(secondDir(facing).getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == TablePart.MAIN ? new ArsenalTableBlockEntity(pos, state) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockPos mp = mainPos(state, pos);
            if (level.getBlockEntity(mp) instanceof ArsenalTableBlockEntity arsenal) {
                final BlockPos openPos = mp;
                player.openMenu(arsenal, buf -> buf.writeBlockPos(openPos));
                level.playSound(null, pos, ModSounds.ARSENAL_TABLE_OPEN.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        TablePart part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        BlockPos otherPos = part == TablePart.MAIN
                ? pos.relative(secondDir(facing))
                : pos.relative(secondDir(facing).getOpposite());
        BlockState other = level.getBlockState(otherPos);
        if (other.is(this) && other.getValue(PART) != part) {
            level.removeBlock(otherPos, false);
        }
        if (part == TablePart.MAIN && level.getBlockEntity(pos) instanceof ArsenalTableBlockEntity a) {
            a.dropContents(level, pos);
        }
    }
}