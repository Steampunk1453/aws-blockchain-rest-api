package com.innocv.hyperledger.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
public class QueryController {

    @SneakyThrows
    @GetMapping("/donors")
    private void list() {
        log.debug("Query all donors");

        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

        File configFile = new File("/home/ec2-user/aws-blockchain-rest-api/src/main/resources/config-profile.yml");
        NetworkConfig networkConfig = NetworkConfig.fromYamlFile(configFile);
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);

        client.loadChannelFromConfig("mychannel", networkConfig);

        log.debug("Created client");
    }

}
