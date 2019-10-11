package com.innocv.hyperledger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innocv.hyperledger.config.UserContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
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
                .mspId("m-75QMFNVAAZFRDGD54TJW3BMHKM")
                .build();

        Properties properties = new Properties();
        properties.put("pemBytes", Files.readAllBytes(Paths.get("/home/ec2-user/aws-blockchain-rest-api/src/main/resources/managed-blockchain.pem")));
        HFCAClient hfcaClient = HFCAClient.createNewInstance("m-75QMFNVAAZFRDGD54TJW3BMHKM",
                "https://ca.m-75qmfnvaazfrdgd54tjw3bmhkm.n-rvten2q5fbcsno27kghwnmxqvq.managedblockchain.us-east-1.amazonaws.com:30002",
                properties);

        hfcaClient.setCryptoSuite(cryptoSuite);
        Enrollment enrollment = hfcaClient.enroll("tftestadminuser", "iNn0cvSolutions");
        userContext.setEnrollment(enrollment);
        client.setUserContext(userContext);

        client.loadChannelFromConfig("mychannel", networkConfig);

        log.debug("Created client");

        Channel channel = client.getChannel("mychannel");
        channel.initialize();

        TransactionProposalRequest request = client.newTransactionProposalRequest();
        String chainCodeName = "ngo";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chainCodeName).build();

        request.setChaincodeID(chaincodeID);
        request.setFcn("queryAllDonors");

        request.setProposalWaitTime(3000);
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);

        log.debug("Found {} proposals", responses.size());

        ObjectMapper objectMapper = new ObjectMapper();
        for (ProposalResponse response : responses) {
            log.debug("Writing proposal from response");
            log.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        }
    }

}
