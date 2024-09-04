package com.myhandjava.momentours.randomquestion.command.application.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RandomReplyDTO {
    private String randomReplyContent;
    private int randomReplyUserNo;
    private int randomQuestionNo;
    private int randomReplyIsDeleted;
    private int randomCoupleNo;
}
