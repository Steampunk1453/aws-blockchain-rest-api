package com.innocv.hyperledger.controllers;

import com.innocv.hyperledger.config.UserContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Properties;

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

        UserContext userContext = UserContext.builder()
                .name("tftestadminuser")
                .affiliation("Org1")
                .mspId("Org1")
                .build();

        HFCAClient hfcaClient = HFCAClient.createNewInstance("ca-org1",
                "https://ca.m-75qmfnvaazfrdgd54tjw3bmhkm.n-rvten2q5fbcsno27kghwnmxqvq.managedblockchain.us-east-1.amazonaws.com:30002",
                new Properties());

        hfcaClient.setCryptoSuite(cryptoSuite);
        Enrollment enrollment = hfcaClient.enroll("tftestadminuser", "iNn0cvSolutions");
        userContext.setEnrollment(enrollment);
        client.setUserContext(userContext);

        client.loadChannelFromConfig("mychannel", networkConfig);

        log.debug("Created client");
    }

}
