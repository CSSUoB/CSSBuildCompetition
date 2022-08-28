package com.cssbham.cssbuildcompetition.exception;

public class CompetitionNotRunningException extends RuntimeException {

    public CompetitionNotRunningException() {
        super("Competition is not running");
    }

}
