package com.myhandjava.momentours.couple.command.application.controller;

import com.myhandjava.momentours.client.UserClient;
import com.myhandjava.momentours.common.ResponseMessage;
import com.myhandjava.momentours.couple.command.application.dto.CoupleDTO;
import com.myhandjava.momentours.couple.command.application.service.CoupleServiceImpl;
import com.myhandjava.momentours.couple.command.domain.vo.CoupleRegistVO;
import com.myhandjava.momentours.couple.command.domain.vo.CoupleUpdateVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/couple")
@Slf4j
public class CoupleController {

    private final ModelMapper modelMapper;
    private final CoupleServiceImpl coupleService;
    private final UserClient userClient;

    @Autowired
    public CoupleController(ModelMapper modelMapper, CoupleServiceImpl coupleService, UserClient userClient) {
        this.modelMapper = modelMapper;
        this.coupleService = coupleService;
        this.userClient = userClient;
    }

    @PostMapping("")
    public ResponseEntity<ResponseMessage> registNewCouple(@RequestAttribute("claims") Claims claims) {
        if(claims.isEmpty()) return ResponseEntity.noContent().build();
        int userNo1 = (Integer)claims.get("userNo");
        ResponseEntity<ResponseMessage> response = userClient.findPartnerByUserNo(userNo1);
        int userNo2 = (Integer) response.getBody().getResult().get("partnerNo");
        int coupleNo = coupleService.registCouple(userNo1, userNo2);
        Map<String, Object> result = new HashMap<>();
        result.put("coupleNo", coupleNo);
        return ResponseEntity.ok(new ResponseMessage(200, "커플 객체 생성 성공", result));
    }

    @PostMapping("/profile")
    public ResponseEntity<ResponseMessage> fillCoupleInfo(@RequestAttribute("claims") Claims claims,
                                            @RequestBody CoupleRegistVO coupleInfo) {
        int userNo1 = (Integer)claims.get("userNo");
        ResponseEntity<ResponseMessage> response = userClient.findPartnerByUserNo(userNo1);
        Map<String, Object> map = response.getBody().getResult();
        int userNo2 = (Integer)map.get("partnerNo");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CoupleDTO coupleDTO = modelMapper.map(coupleInfo, CoupleDTO.class);
        coupleService.inputCoupleInfo(userNo1, userNo2, coupleDTO);
        Map<String, Object> result = new HashMap<>();
        map.put("updatedCouple", coupleDTO);
        return ResponseEntity.ok(new ResponseMessage(200, "커플이 입력한 정보 출력", result));
    }


    @PutMapping("/profile")
    public ResponseEntity<ResponseMessage> updateCoupleInfo(@RequestBody CoupleUpdateVO updateInfo,
                                                            @RequestAttribute("claims") Claims claims) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        int updateCoupleNo = (Integer)claims.get("coupleNo");
        CoupleDTO updatedCouple = modelMapper.map(updateInfo, CoupleDTO.class);
        coupleService.updateCouple(updateCoupleNo, updatedCouple);
        Map<String, Object> map = new HashMap<>();
        map.put("updatedCouple", updatedCouple);
        return ResponseEntity.ok(new ResponseMessage(200, "커플 정보가 수정되었습니다.", map));
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseMessage> deleteCoupleInfo(@RequestAttribute("claims") Claims claims) {
        int coupleNo = (Integer)claims.get("coupleNo");
        coupleService.deleteCouple(coupleNo);
        return ResponseEntity.noContent().build();
    }
}
