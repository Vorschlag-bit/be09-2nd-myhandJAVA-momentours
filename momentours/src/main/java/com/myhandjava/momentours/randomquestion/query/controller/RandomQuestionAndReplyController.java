package com.myhandjava.momentours.randomquestion.query.controller;

import com.myhandjava.momentours.client.UserClient;
import com.myhandjava.momentours.common.ResponseMessage;
import com.myhandjava.momentours.couple.command.domain.vo.RequestSignCoupleVO;
import com.myhandjava.momentours.randomquestion.query.dto.RandomQuestionAndReplyDTO;
import com.myhandjava.momentours.randomquestion.query.dto.RandomQuestionDTO;

import com.myhandjava.momentours.randomquestion.query.service.RandomQuestionAndReplyServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController(value = "randomQuestionQueryController")
@RequestMapping("/randomquestion")
public class RandomQuestionAndReplyController {

    private final RandomQuestionAndReplyServiceImpl randomQuestionService;
    private final UserClient userClient;

    @Autowired
    public RandomQuestionAndReplyController(RandomQuestionAndReplyServiceImpl randomQuestionService,
                                            UserClient userClient) {
        this.randomQuestionService = randomQuestionService;
        this.userClient = userClient;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseMessage> findAllRandomQuestion(@RequestAttribute("claims") Claims claims) {
        int coupleNo = (Integer) claims.get("coupleNo");
        List<RandomQuestionDTO> result =
                randomQuestionService.findAllRandomQuestionByCoupleNo(coupleNo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", result);
        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "조회 성공.", responseMap);

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage> findAllRandomQuestionAndReply(@RequestAttribute("claims") Claims claims) {

        int userNo = (Integer) claims.get("userNo");
        int coupleNo = (Integer) claims.get("coupleNo");
        ResponseEntity<ResponseMessage> response = userClient.findPartnerByUserNo(userNo);
        Map<String, Object> responseMap = response.getBody().getResult();
        int partnerNo = (Integer) responseMap.get("partnerNo");

        List<RandomQuestionAndReplyDTO> result =
                randomQuestionService.findAllRandomQuestionAndRepliesByUserNoAndCoupleNo(coupleNo, userNo, partnerNo);

        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "조회 성공.", map);

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
