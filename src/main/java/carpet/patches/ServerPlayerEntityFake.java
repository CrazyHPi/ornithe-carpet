package carpet.patches;

import carpet.CarpetSettings;
import carpet.fakes.ServerPlayerEntityF;
import carpet.fakes.ServerWorldF;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.PacketFlow;
import net.minecraft.network.packet.s2c.play.EntityHeadAnglesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTeleportS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerInfoS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Formatting;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class ServerPlayerEntityFake extends ServerPlayerEntity {
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;

    private double setX;
    private double setY;
    private double setZ;
    private float setYaw;
    private float setPitch;

    public static ServerPlayerEntityFake createFake(String username, MinecraftServer server, double x, double y, double z, double yaw, double pitch, int dimension, int gamemode) {
        ServerWorld worldIn = server.getWorld(dimension);
        ServerPlayerInteractionManager interactionManagerIn = new ServerPlayerInteractionManager(worldIn);
        GameProfile gameprofile = server.getGameProfileCache().get(username);
        if (gameprofile == null) {
            UUID uuid = PlayerEntity.getUuid(new GameProfile(null, username));
            gameprofile = new GameProfile(uuid, username);
        } else {
            gameprofile = fixSkin(gameprofile);
        }
        ServerPlayerEntityFake instance = new ServerPlayerEntityFake(server, worldIn, gameprofile, interactionManagerIn);
        instance.setSetPosition(x, y, z, (float) yaw, (float) pitch);
        server.getPlayerManager().onLogin(new ConnectionFake(PacketFlow.CLIENTBOUND), instance);
        if (instance.dimensionId != dimension) {
            //player was logged in in a different dimension
            ServerWorld old_world = server.getWorld(instance.dimensionId);
            instance.dimensionId = dimension;
            old_world.removeEntity(instance);
            instance.removed = false;
            worldIn.addEntity(instance);
            instance.setWorld(worldIn);
            server.getPlayerManager().onChangedDimension(instance, old_world);
            instance.networkHandler.teleport(x, y, z, (float) yaw, (float) pitch);
            instance.interactionManager.setWorld(worldIn);
        }
        instance.setHealth(20.0F);
        instance.removed = false;
        instance.stepHeight = 0.6F;
        interactionManagerIn.setGameMode(GameMode.byId(gamemode));
        server.getPlayerManager().sendPacket(new EntityHeadAnglesS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), instance.dimensionId);
        server.getPlayerManager().sendPacket(new EntityTeleportS2CPacket(instance), instance.dimensionId);
        server.getPlayerManager().move(instance);
        instance.dataTracker.set(MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
        createAndAddFakePlayerToTeamBot(instance);
        return instance;
    }

    public static ServerPlayerEntityFake createShadow(MinecraftServer server, ServerPlayerEntity player) {
        //todo cameraMode
//        if(CarpetSettings.cameraModeRestoreLocation && player.getGamemodeCamera()) {
//            GameMode GameMode = server.getGameMode();
//            player.moveToStoredCameraData();
//            player.setGameMode(GameMode);
//            player.removePotionEffect(Potion.getPotionFromResourceLocation("night_vision"));
//        }
        player.getServer().getPlayerManager().remove(player);
        player.networkHandler.sendDisconnect(new TranslatableText("multiplayer.disconnect.duplicate_login"));
        ServerWorld worldIn = server.getWorld(player.dimensionId);
        ServerPlayerInteractionManager interactionManagerIn = new ServerPlayerInteractionManager(worldIn);
        GameProfile gameprofile = player.getGameProfile();
        gameprofile = fixSkin(gameprofile);
        ServerPlayerEntityFake playerShadow = new ServerPlayerEntityFake(server, worldIn, gameprofile, interactionManagerIn);
        playerShadow.setSetPosition(player.x, player.y, player.z, player.yaw, player.pitch);
        server.getPlayerManager().onLogin(new ConnectionFake(PacketFlow.CLIENTBOUND), playerShadow);

        playerShadow.setHealth(player.getHealth());
        playerShadow.networkHandler.teleport(player.x, player.y, player.z, player.yaw, player.pitch);
        interactionManagerIn.setGameMode(player.interactionManager.getGameMode());
        ((ServerPlayerEntityF) playerShadow).getActionPack().copyFrom(((ServerPlayerEntityF) player).getActionPack());
        playerShadow.stepHeight = 0.6F;

        server.getPlayerManager().sendPacket(new EntityHeadAnglesS2CPacket(playerShadow, (byte) (player.headYaw * 256 / 360)), playerShadow.dimensionId);
        server.getPlayerManager().sendPacket(new PlayerInfoS2CPacket(PlayerInfoS2CPacket.Action.ADD_PLAYER, playerShadow));
        server.getPlayerManager().move(playerShadow);
        createAndAddFakePlayerToTeamBot(playerShadow);
        return playerShadow;
    }

    public static ServerPlayerEntityFake create(String info, MinecraftServer server) {
        String[] infos = info.split("/");
        String username = infos[0];
        ServerWorld worldIn = server.getWorld(0);
        ServerPlayerInteractionManager interactionManagerIn = new ServerPlayerInteractionManager(worldIn);
        GameProfile gameprofile = server.getGameProfileCache().get(username);
        if (gameprofile == null) {
            UUID uuid = PlayerEntity.getUuid(new GameProfile(null, username));
            gameprofile = new GameProfile(uuid, username);
        } else {
            gameprofile = fixSkin(gameprofile);
        }
        ServerPlayerEntityFake instance = new ServerPlayerEntityFake(server, worldIn, gameprofile, interactionManagerIn);
        server.getPlayerManager().load(instance);
        instance.setSetPosition(instance.x, instance.y, instance.z, instance.yaw, instance.pitch);
        ((ServerWorldF) worldIn).setLoginMinecartFix(true);
        server.getPlayerManager().onLogin(new ConnectionFake(PacketFlow.CLIENTBOUND), instance);
        ((ServerWorldF) worldIn).setLoginMinecartFix(false);
        if (instance.dimensionId != 0) //player was logged in in a different dimension
        {
            worldIn = server.getWorld(instance.dimensionId);
            instance.setWorld(worldIn);
            server.getPlayerManager().onChangedDimension(instance, worldIn);
            instance.interactionManager.setWorld(worldIn);
        }
        instance.removed = false;
        instance.stepHeight = 0.6F;
        server.getPlayerManager().sendPacket(new EntityHeadAnglesS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), instance.dimensionId);
        server.getPlayerManager().sendPacket(new EntityTeleportS2CPacket(instance), instance.dimensionId);
        server.getPlayerManager().move(instance);
        instance.dataTracker.set(MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
        createAndAddFakePlayerToTeamBot(instance);
        if (infos.length > 1) {
            ((ServerPlayerEntityF) instance).getActionPack().fromString(infos[1]);
        }
        return instance;
    }

    private ServerPlayerEntityFake(MinecraftServer server, ServerWorld worldIn, GameProfile profile, ServerPlayerInteractionManager interactionManagerIn) {
        super(server, worldIn, profile, interactionManagerIn);
    }

    private static GameProfile fixSkin(GameProfile gameProfile) {
        if (!CarpetSettings.removeFakePlayerSkins && !gameProfile.getProperties().containsKey("texture"))
            return SkullBlockEntity.updateProfile(gameProfile);
        else
            return gameProfile;
    }

    @Override
    public void discard() {
        logout();
    }

    @Override
    public void tick() {
        super.tick();
        this.tickPlayer();
        this.playerMoved();
    }

    @Override
    public void onKilled(DamageSource source) {
        super.onKilled(source);
        logout();
    }

    private void logout() {
        this.stopRiding();
        server.getPlayerManager().remove(this);
        removePlayerFromTeams(this);
    }

    public void despawn() {
        server.getPlayerManager().remove(this);
        removePlayerFromTeams(this);
    }

    private void playerMoved() {
        if (x != lastReportedPosX || y != lastReportedPosY || z != lastReportedPosZ) {
            server.getPlayerManager().move(this);
            lastReportedPosX = x;
            lastReportedPosY = y;
            lastReportedPosZ = z;
        }
    }

    public void setSetPosition(double x, double y, double z, float yaw, float pitch) {
        this.setX = x;
        this.setY = y;
        this.setZ = z;
        this.setYaw = yaw;
        this.setPitch = pitch;
    }

    public void resetToSetPosition() {
        this.refreshPositionAndAngles(setX, setY, setZ, setYaw, setPitch);
    }

    private static void createAndAddFakePlayerToTeamBot(ServerPlayerEntityFake player) {
        Scoreboard scoreboard = player.server.getWorld(0).getScoreboard();
        if (!scoreboard.getTeamNames().contains("Bots")) {
            scoreboard.addTeam("Bots");
            Team team = scoreboard.getTeam("Bots");
            Formatting formatting = Formatting.byName("dark_green");
            team.setColor(formatting);
            team.setPrefix(formatting.toString());
            team.setSuffix(Formatting.RESET.toString());
        }
        scoreboard.addMemberToTeam(player.getName(), "Bots");
    }

    public static void removePlayerFromTeams(ServerPlayerEntityFake player) {
        Scoreboard scoreboard = player.server.getWorld(0).getScoreboard();
        scoreboard.removeMemberFromTeam(player.getName());
    }

    public static String getInfo(ServerPlayerEntity p) {
        return p.getName() + "/" + ((ServerPlayerEntityF) p).getActionPack();
    }
}
