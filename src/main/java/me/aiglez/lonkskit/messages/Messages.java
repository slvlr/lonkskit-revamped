package me.aiglez.lonkskit.messages;

import co.aikar.commands.Locales;
import java.util.HashMap;

public enum Messages {

    PLAYER_SAFESTATUS_UPDATED("player.safe-status.updated", "&aYou can now play !"),
    PLAYER_METRICS_KILLSTREAK("player.metrics.killstreak", "&e{0} &fgot a killstreak of &a{1}&f."),
    PLAYER_METRICS_RUINED_KILLSTREAK("player.metrics.killstreak", "&c{0} &fended &e{1}&f's killstreak of &a{2}&f."),

    PLAYER_SELECT_MAX_LEVEL("player.select.max.level","&cYou have already upgraded to the max level for {0}."),

    SELECTOR_ERROR("selector.error", "&cAn error occurred while trying to select the kit, please try again."),
    SELECTOR_SELECTED("selector.selected", "&eYou have selected the kit &b{0}&e."),
    SELECTOR_NO_ACCESS("selector.no-access", "&cYou don't have access to this kit."),

    SELECTOR_RENT_FAILED("selector.rent.failed", "&cTransaction failed"),
    SELECTOR_RENT_RENTED("selector.rent.rented", "&aYou have successfully rented the kit &b{0}&a for &e{1} &apoint(s) with &e{2} &aavailable uses."),

    COMMAND_LEAVE_SUCCESSFULLY("command.leave.successfully", "&aYou have been successfully teleported to the main world."),
    COMMAND_LEAVE_TELEPORT_ISSUE("command.leave.teleport-issue", "&cAn error occurred while trying to teleport you to the main world. Try later."),
    COMMAND_LEAVE_ERROR("command.leave.error", "&cYou're already in the main world."),
    COMMAND_LEAVE_MUSTBEATSPAWN("command.leave.must-be-at-spawn", "&cYou must be in the spawn area to execute this command."),

    COMMAND_JOIN_SUCCESSFULLY("command.join.successfully", "&aWelcome into the Kit PVP world."),
    COMMAND_JOIN_TELEPORT_ISSUE("command.join.teleport-issue", "&cAn error occurred while trying to teleport you to the Kit PvP world. Try later."),
    COMMAND_JOIN_ERROR("command.join.error", "&cYou're already in the Kit PvP world."),
    COMMAND_JOIN_UNSAFE("command.join.unsafe", "&cIt seems like you have items in your inventory, put them in your enderchest to be able to play."),

    COMMAND_POINTS_SHOW("command.points.show", "&eYou have {0} point(s)."),
    COMMAND_POINTS_SHOW_OTHER("command.points.show-other", "&e{0} has {1} point(s)."),
    COMMAND_POINTS_PAY_FAILED("command.points.pay.invalid", "&cTransaction failed"),
    COMMAND_POINTS_PAY_NOT_ENOUGH("command.points.pay.not-enough", "&cYou don't have enough points!"),
    COMMAND_POINTS_PAY_SENT("command.points.pay.sent", "&fYou have sent &a{0} &fpoint(s) to &b{1}&f."),
    COMMAND_POINTS_PAY_RECEIVED("command.points.pay.sent", "&aYou have received {0} &fpoint(s) from &b{1}&a."),


    COMMAND_ADMIN_STATS_POINTS_SET("command.admin.stats.points.set", "&eYou have set &a{0}&e's points to &6{1}&e."),
    COMMAND_ADMIN_STATS_POINTS_GIVE("command.admin.stats.points.give", "&eYou have added &a{0} &epoints to &6{1}&e."),
    COMMAND_ADMIN_STATS_POINTS_TAKE("command.admin.stats.points.take", "&eYou have taken &c{0} &epoints from &c{1}&e."),
    COMMAND_ADMIN_STATS_POINTS_RESET("command.admin.stats.points.reset", "&eYou have reset &a{0}&e's epoints."),


    COMMAND_ADMIN_STATS_KILLS_SET("command.admin.stats.kills.set", "&eYou have set &a{0}&e's kills to &6{1}&e."),
    COMMAND_ADMIN_STATS_KILLS_GIVE("command.admin.stats.kills.give", "&eYou have added &a{0} &ekills to &6{1}&e."),
    COMMAND_ADMIN_STATS_KILLS_TAKE("command.admin.stats.kills.take", "&eYou have taken &c{0} &ekills from &c{1}&e."),
    COMMAND_ADMIN_STATS_KILLS_RESET("command.admin.stats.kills.reset", "&eYou have reset &a{0}&e's kills count."),

    COMMAND_ADMIN_STATS_DEATHS_SET("command.admin.stats.deaths.set", "&eYou have set &a{0}&e's deaths to &6{1}&e."),
    COMMAND_ADMIN_STATS_DEATHS_GIVE("command.admin.stats.deaths.give", "&eYou have added &a{0} &edeaths to &6{1}&e."),
    COMMAND_ADMIN_STATS_DEATHS_TAKE("command.admin.stats.deaths.take", "&eYou have taken &c{0} &edeaths from &c{1}&e."),
    COMMAND_ADMIN_STATS_DEATHS_RESET("command.admin.stats.deaths.reset", "&eYou have reset &a{0}&e's deaths."),


    COMMAND_ENGINE_MUST_BE_INT("command.engine.must_be_a_number", "&cArgument must be a number and valid."),
    COMMAND_ENGINE_PERMISSION_DENIED("command.engine.permission_denied",  "&cYou do not have access to this command.");



    private final String path, fallback;
    Messages(final String path, final String fallback) {
        this.path = path;
        this.fallback = fallback;
    }

    public String getValue() {
        return this.fallback;
    }

    public static void setACFMessages(final Locales locales) {
        final HashMap<String, String> acfMessages = new HashMap<>();
        for (final Messages message : values()) {
            if(message.path.startsWith("command.engine.")) {
                acfMessages.put(message.path.replaceFirst("command.engine.", ""), message.fallback);
            }
        }
        locales.addMessageStrings(Locales.ENGLISH, acfMessages);
    }



}
