package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private static final int MAX_ROUNDS = 50;
    private Random random = new Random(1L);

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {
        RaidResult result = new RaidResult();
        // TODO: Validate inputs (null checks, alive checks, required skills).
        // TODO: Implement round-based simulation:
        // 1) Team A casts on Team B
        if (teamA == null || teamB == null || teamASkill == null || teamBSkill == null) {
            result.setWinner("ERROR: null argument");
            result.addLine("Raid aborted — one or more arguments are null.");
            return result;
        }
        // 2) Team B casts on Team A (if still alive)
        if (!teamA.isAlive() || !teamB.isAlive()) {
            result.setWinner("ERROR: team already defeated before raid");
            result.addLine("Raid aborted — a team entered the battle already defeated.");
            return result;
        }
        // 3) Track rounds and log each step
        int round = 0;
        while (teamA.isAlive() && teamB.isAlive() && round < MAX_ROUNDS) {
            round++;
            result.addLine("-Round " + round + " -");
            if (teamA.isAlive()) {
                boolean crit = random.nextInt(100) < 10;
                result.addLine(teamA.getName() + " casts " + teamASkill.getSkillName() + (crit ? " [CRITICAL]" : "") + " on " + teamB.getName());
                int hpBefore = teamB.getHealth();
                teamASkill.cast(teamB);
                if (crit) {
                    int bonus = teamASkill.getBasePower() / 2;
                    teamB.takeDamage(bonus);
                }
                result.addLine("  " + teamB.getName() + " HP: " + hpBefore + " -> " + teamB.getHealth());
            }
            if (teamB.isAlive()) {
                boolean crit = random.nextInt(100) < 10;
                result.addLine(teamB.getName() + " casts " + teamBSkill.getSkillName() + (crit ? " [CRITICAL]" : "") + " on " + teamA.getName());
                int hpBefore = teamA.getHealth();
                teamBSkill.cast(teamA);
                if (crit) {
                    int bonus = teamBSkill.getBasePower() / 2;
                    teamA.takeDamage(bonus);
                }
                result.addLine("  "  + teamA.getName() + " HP: " + hpBefore + " -> " + teamA.getHealth());
            }

        }
        // 4) Stop when one team is defeated (or max rounds reached)
        //
        // Optional extension:
        // Use random for critical strikes or other deterministic events.
        // Example: boolean critA = random.nextInt(100) < 10;
        result.setRounds(round);
        if (!teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner("Draw");
        } else if (!teamB.isAlive()) {
            result.setWinner(teamA.getName());
        } else if (!teamA.isAlive()) {
            result.setWinner(teamB.getName());
        } else {
            result.setWinner("Draw (max rounds reached)");
        }
        return result;
    }
}
