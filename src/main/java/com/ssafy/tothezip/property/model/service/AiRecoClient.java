package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.AiRecoRankDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AiRecoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.server.base-url:http://127.0.0.1:8000}")
    private String aiBaseUrl;

    public AiRecoRankDto.RankResponse rankExplain(AiRecoRankDto.RankRequest req) {
        String url = aiBaseUrl + "/reco/rank-explain";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRecoRankDto.RankRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<AiRecoRankDto.RankResponse> res =
                restTemplate.exchange(url, HttpMethod.POST, entity, AiRecoRankDto.RankResponse.class);

        return res.getBody();
    }
}
