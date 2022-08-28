package com.cssbham.cssbuildcompetition.exception;

public class CompetitionInProgressException extends RuntimeException {

    public CompetitionInProgressException() {
        super("A competition is already in progress");
    }

}
