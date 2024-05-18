package studio.magemonkey.fabled.dynamic.target;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.LinkedList;
import java.util.List;

public class PlayerTarget extends TargetComponent {
    private static final String TYPE = "type";

    @Override
    public String getKey() {
        return "player";
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {
        String teamType = settings.getString(TYPE, "ally").toLowerCase();
        List<LivingEntity> result = new LinkedList<>();
        Team casterTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(caster.getName());
        for (Player player : caster.getWorld().getPlayers()) {
            Team playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (switch (teamType) {
                case "ally" -> playerTeam != null && playerTeam.equals(casterTeam);
                case "enemy" -> playerTeam == null || !playerTeam.equals(casterTeam);
                case "all" -> true;
                default -> false;
            }) result.add(player);
        }
        return result;
    }
}