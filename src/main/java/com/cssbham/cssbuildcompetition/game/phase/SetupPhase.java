package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.game.team.TeamManager;

public final class SetupPhase extends Phase {

    private final TeamManager teamManager;

    public SetupPhase(TeamManager teamManager) {
        super("setup");

        this.teamManager = teamManager;
    }

    @Override
    public void end() {
        super.logInfo("Disallowing team leaving and pruning teams");
        teamManager.setLeavingAllowed(false);
        teamManager.pruneTeams();
    }

    @Override
    public boolean tick() {
        return false;
    }

}
