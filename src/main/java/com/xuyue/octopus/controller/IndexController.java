package com.xuyue.octopus.controller;

import com.xuyue.octopus.controller.request.CreateUrlMapRequest;
import com.xuyue.octopus.controller.response.CreateUrlMapResponse;
import com.xuyue.octopus.controller.response.Response;
import com.xuyue.octopus.entity.UrlMap;
import com.xuyue.octopus.infra.common.UrlMapStatus;
import com.xuyue.octopus.service.UrlMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController {


    @Autowired
    UrlMapService urlMapService;

    @Value("${default.octopus.domain}")
    private String defaultDomain;

    @PostMapping("/generate")
    @ResponseBody
    public Response<CreateUrlMapResponse> generateShortURL(@RequestBody CreateUrlMapRequest request) {
        UrlMap urlMap = new UrlMap();
        urlMap.setUrlStatus(UrlMapStatus.AVAILABLE.getValue());
        urlMap.setLongUrl(request.getLongUrl());
        urlMap.setDescription(request.getDescription());
        String shortUrl = urlMapService.createUrlMap(defaultDomain, urlMap);
        return Response.succeed(new CreateUrlMapResponse(request.getRequestId(), shortUrl));
    }


}
