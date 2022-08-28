package com.cssbham.cssbuildcompetition.exception;

public class PlayerNotInTeamException extends RuntimeException {

    public PlayerNotInTeamException() {
        super("Player is not in team.");
    }

}
