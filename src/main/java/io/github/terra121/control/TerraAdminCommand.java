package io.github.terra121.control;

import io.github.terra121.TerraConstants;
import io.github.terra121.control.fragments.FragmentManager;
import io.github.terra121.control.fragments.admin.AdminOverpassFragment;
import io.github.terra121.util.TranslateUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class TerraAdminCommand extends FragmentManager {

    public TerraAdminCommand() {
        setTitle(TranslateUtil.translate("terra121.commands.terraadmin.title"));
        setCommandBase("terraadmin");
        registerCommandFragment(new AdminOverpassFragment());
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("ta");
        return aliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "terraadmin";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "terra121.commands.terraadmin.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(!hasAdminPermission("terra121.admin", sender)) {
            sender.sendMessage(TerraConstants.TextConstants.noPermission);
            return;
        }
        executeFragment(server, sender, args);
    }
}
