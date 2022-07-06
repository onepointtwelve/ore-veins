/*
 * Part of the Ore Veins Mod by alcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

package com.alcatrazescapee.oreveins.cmd;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import com.alcatrazescapee.oreveins.api.IVein;
import com.alcatrazescapee.oreveins.api.IVeinType;
import com.alcatrazescapee.oreveins.vein.VeinRegistry;
import com.alcatrazescapee.oreveins.world.WorldGenVeins;

@ParametersAreNonnullByDefault
public class CommandFindVeins extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "findveins";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/findveins [all|<vein name>] <radius> -> Finds all instances of a specific vein, or all veins within a certian chunk radius";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2) throw new WrongUsageException("2 arguments required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        sender.sendMessage(new TextComponentString("Veins Found: "));

        final int radius = parseInt(args[1], 1, 1000);
        final List<IVein<?>> veins = WorldGenVeins.getNearbyVeins(sender.getCommandSenderEntity().chunkCoordX, sender.getCommandSenderEntity().chunkCoordZ, sender.getEntityWorld().getSeed(), radius);
        if (!args[0].equals("all"))
        {
            final IVeinType<?> type = VeinRegistry.getVein(args[0]);
            if (type == null)
            {
                throw new WrongUsageException("Vein supplied does not match 'all' or any valid vein names. Use /veininfo to see valid vein names");
            }
            // Search for veins matching type
            veins.removeIf(x -> x.getType() != type);
        }
        veins.forEach(x -> sender.sendMessage(new TextComponentString("> Vein: " + x.toString())));
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, VeinRegistry.getNames());
        }
        return Collections.emptyList();
    }
}
