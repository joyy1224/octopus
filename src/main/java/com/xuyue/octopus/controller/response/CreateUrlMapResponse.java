package com.xuyue.octopus.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

//生成具有所需参数的构造函数 带final
@RequiredArgsConstructor
@Getter
public class CreateUrlMapResponse implements Serializable {

    private final String requestId;

    private final String shortUrl;
}