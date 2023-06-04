package com.xuyue.octopus.controller.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateUrlMapRequest implements Serializable {

    private String requestId;

    private String longUrl;

    private String description;
}

