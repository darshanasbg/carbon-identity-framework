/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.identity.claim.metadata.mgt.util;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.*;
import org.wso2.carbon.identity.claim.metadata.mgt.model.AttributeMapping;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ClaimDialect;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ExternalClaim;
import org.wso2.carbon.identity.claim.metadata.mgt.model.LocalClaim;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.claim.ClaimMapping;

import java.util.*;


public class ClaimMetadataUtilsTest {

    ClaimDialect claimDialect;
    ClaimDialect[] claimDialects;

    ClaimDialectDTO claimDialectDTO;

    LocalClaim localClaim1;
    LocalClaim localClaim2;
    LocalClaim[] localClaims;

    LocalClaimDTO localClaimDTO1;
    LocalClaimDTO localClaimDTO2;
    LocalClaimDTO[] localClaimDTOs;

    ExternalClaim externalClaim;
    ExternalClaim[] externalClaims;

    ExternalClaimDTO externalClaimDTO;


    @BeforeMethod
    public void setUp() throws Exception {
        setUpClaimDialects();
        setUpLocalClaims();
        setUpExternalClaims();
    }

    private void setUpClaimDialects() {
        String claimDialectURI = "testClaimDialectURI";
        claimDialect = new ClaimDialect(claimDialectURI);

        int arraySize = 2;
        claimDialects = new ClaimDialect[arraySize];

        for (int i = 0; i < claimDialects.length; i++) {
            claimDialects[i] = new ClaimDialect(claimDialectURI + i);
        }

        claimDialectDTO = new ClaimDialectDTO();
        claimDialectDTO.setClaimDialectURI(claimDialectURI);
    }

    private void setUpLocalClaims() {

        String localClaimURI1 = "testLocalClaimURI1";
        String localClaimURI2 = "testLocalClaimURI2";

        {
            localClaim1 = new LocalClaim(localClaimURI1);

            AttributeMapping attributeMapping1 = new AttributeMapping(UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME,
                    "uid");
            AttributeMapping attributeMapping2 = new AttributeMapping("AD", "sAMAccountName");

            List<AttributeMapping> attributeMappingList = new ArrayList<>();
            attributeMappingList.add(attributeMapping1);
            attributeMappingList.add(attributeMapping2);

            Map<String, String> claimPropertiesMap = new HashMap<>();
            claimPropertiesMap.put(ClaimConstants.DISPLAY_NAME_PROPERTY, "username");
            claimPropertiesMap.put(ClaimConstants.READ_ONLY_PROPERTY, "true");

            localClaim2 = new LocalClaim(localClaimURI2, attributeMappingList, claimPropertiesMap);

            localClaims = new LocalClaim[]{localClaim1, localClaim2};
        }

        {
            localClaimDTO1 = new LocalClaimDTO();
            localClaimDTO1.setLocalClaimURI(localClaimURI1);

            AttributeMappingDTO attributeMappingDTO1 = new AttributeMappingDTO();
            attributeMappingDTO1.setUserStoreDomain(UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME);
            attributeMappingDTO1.setAttributeName("uid");

            AttributeMappingDTO attributeMappingDTO2 = new AttributeMappingDTO();
            attributeMappingDTO2.setUserStoreDomain("AD");
            attributeMappingDTO2.setAttributeName("sAMAccountName");

            AttributeMappingDTO[] attributeMappingDTOs = new AttributeMappingDTO[]{attributeMappingDTO1,
                    attributeMappingDTO2};

            ClaimPropertyDTO claimPropertyDTO1 = new ClaimPropertyDTO();
            claimPropertyDTO1.setPropertyName(ClaimConstants.DISPLAY_NAME_PROPERTY);
            claimPropertyDTO1.setPropertyValue("username");

            ClaimPropertyDTO claimPropertyDTO2 = new ClaimPropertyDTO();
            claimPropertyDTO2.setPropertyName(ClaimConstants.READ_ONLY_PROPERTY);
            claimPropertyDTO2.setPropertyValue("true");

            ClaimPropertyDTO[] claimPropertyDTOs = new ClaimPropertyDTO[]{claimPropertyDTO1, claimPropertyDTO2};

            localClaimDTO2 = new LocalClaimDTO();
            localClaimDTO2.setLocalClaimURI(localClaimURI2);
            localClaimDTO2.setAttributeMappings(attributeMappingDTOs);
            localClaimDTO2.setClaimProperties(claimPropertyDTOs);

            localClaimDTOs = new LocalClaimDTO[]{localClaimDTO1, localClaimDTO2};
        }
    }

    private void setUpExternalClaims() {

        String claimDialectURI = "testClaimDialectURI";
        String externalClaimURI = "testExternalClaimURI";
        String mappedLocalClaimURI = "testLocalClaimURI2";

        externalClaim = new ExternalClaim(claimDialectURI, externalClaimURI, mappedLocalClaimURI);


        int arraySize = 2;
        externalClaims = new ExternalClaim[arraySize];

        for (int i = 0; i < externalClaims.length; i++) {
            externalClaims[i] = new ExternalClaim(claimDialectURI + i, externalClaimURI + i, mappedLocalClaimURI + i);
        }

        externalClaimDTO = new ExternalClaimDTO();
        externalClaimDTO.setExternalClaimDialectURI(claimDialectURI);
        externalClaimDTO.setExternalClaimURI(externalClaimURI);
        externalClaimDTO.setMappedLocalClaimURI(mappedLocalClaimURI);
    }

    @Test
    public void testConvertClaimDialectToClaimDialectDTO() {

        ClaimDialectDTO claimDialectDTO = ClaimMetadataUtils.convertClaimDialectToClaimDialectDTO(claimDialect);
        Assert.assertEquals(claimDialectDTO.getClaimDialectURI(), claimDialect.getClaimDialectURI());
    }

    @Test
    public void testConvertClaimDialectsToClaimDialectDTOs() {

        ClaimDialectDTO[] claimDialectDTOs = ClaimMetadataUtils.convertClaimDialectsToClaimDialectDTOs(claimDialects);
        Assert.assertEquals(claimDialectDTOs.length, claimDialects.length);

        for (int i = 0; i < claimDialects.length; i++) {
            Assert.assertEquals(claimDialectDTOs[i].getClaimDialectURI(), claimDialects[i].getClaimDialectURI());
        }
    }

    @Test
    public void testConvertClaimDialectDTOToClaimDialect() {

        ClaimDialect claimDialect = ClaimMetadataUtils.convertClaimDialectDTOToClaimDialect(claimDialectDTO);
        Assert.assertEquals(claimDialect.getClaimDialectURI(), claimDialectDTO.getClaimDialectURI());
    }

    @Test
    public void testConvertLocalClaimToLocalClaimDTO() {

        LocalClaimDTO localClaimDTO1 = ClaimMetadataUtils.convertLocalClaimToLocalClaimDTO(localClaim1);
        Assert.assertEquals(localClaimDTO1.getLocalClaimURI(), localClaim1.getClaimURI());

        LocalClaimDTO localClaimDTO2 = ClaimMetadataUtils.convertLocalClaimToLocalClaimDTO(localClaim2);

        Assert.assertEquals(localClaimDTO2.getLocalClaimURI(), localClaim2.getClaimURI());
        Assert.assertEquals(localClaimDTO2.getAttributeMappings().length, localClaim2.getMappedAttributes().size());
        Assert.assertEquals(localClaimDTO2.getClaimProperties().length, localClaim2.getClaimProperties().size());

        // TODO:
        for (int i = 0; i < localClaimDTO2.getAttributeMappings().length; i++) {
            Assert.assertEquals(localClaimDTO2.getAttributeMappings()[i].getUserStoreDomain(),
                    localClaim2.getMappedAttributes().get(i).getUserStoreDomain());
            Assert.assertEquals(localClaimDTO2.getAttributeMappings()[i].getAttributeName(),
                    localClaim2.getMappedAttributes().get(i).getAttributeName());
        }

        // TODO:
        for (int i = 0; i < localClaimDTO2.getClaimProperties().length; i++) {
            ClaimPropertyDTO claimPropertyDTO = localClaimDTO2.getClaimProperties()[i];
            String propertyName = claimPropertyDTO.getPropertyName();
            String propertyValue = claimPropertyDTO.getPropertyValue();

            Assert.assertEquals(propertyValue, localClaim2.getClaimProperties().get(propertyName));
        }
    }

    @Test
    public void testConvertLocalClaimsToLocalClaimDTOs() {

        LocalClaimDTO[] localClaimDTOs = ClaimMetadataUtils.convertLocalClaimsToLocalClaimDTOs(localClaims);
        Assert.assertEquals(localClaimDTOs.length, localClaims.length);

        // TODO:
        for (int i = 0; i < localClaimDTOs.length; i++) {

            LocalClaim localClaim = localClaims[i];
            LocalClaimDTO localClaimDTO = localClaimDTOs[i];

            Assert.assertEquals(localClaimDTO.getLocalClaimURI(), localClaim.getClaimURI());
            Assert.assertEquals(localClaimDTO.getAttributeMappings().length, localClaim.getMappedAttributes().size());
            Assert.assertEquals(localClaimDTO.getClaimProperties().length, localClaim.getClaimProperties().size());

            // TODO:
            for (int j = 0; j < localClaimDTO.getAttributeMappings().length; j++) {
                Assert.assertEquals(localClaimDTO.getAttributeMappings()[j].getUserStoreDomain(),
                        localClaim.getMappedAttributes().get(j).getUserStoreDomain());
                Assert.assertEquals(localClaimDTO.getAttributeMappings()[j].getAttributeName(),
                        localClaim.getMappedAttributes().get(j).getAttributeName());
            }

            // TODO:
            for (int j = 0; j < localClaimDTO.getClaimProperties().length; j++) {
                ClaimPropertyDTO claimPropertyDTO = localClaimDTO.getClaimProperties()[j];
                String propertyName = claimPropertyDTO.getPropertyName();
                String propertyValue = claimPropertyDTO.getPropertyValue();

                Assert.assertEquals(propertyValue, localClaim.getClaimProperties().get(propertyName));
            }

        }
    }

    @Test
    public void testConvertLocalClaimDTOToLocalClaim() throws Exception {

        LocalClaim localClaim1 = ClaimMetadataUtils.convertLocalClaimDTOToLocalClaim(localClaimDTO1);
        Assert.assertEquals(localClaim1.getClaimURI(), localClaimDTO1.getLocalClaimURI());

        LocalClaim localClaim2 = ClaimMetadataUtils.convertLocalClaimDTOToLocalClaim(localClaimDTO2);

        Assert.assertEquals(localClaim2.getClaimURI(), localClaimDTO2.getLocalClaimURI());
        Assert.assertEquals(localClaim2.getMappedAttributes().size(), localClaimDTO2.getAttributeMappings().length);
        Assert.assertEquals(localClaim2.getClaimProperties().size(), localClaimDTO2.getClaimProperties().length);

        // TODO: Do not expect the order, loop through localClaimDTO2.getAttributeMappings()
        for (int i = 0; i < localClaimDTO2.getAttributeMappings().length; i++) {
            Assert.assertEquals(localClaim2.getMappedAttributes().get(i).getUserStoreDomain(),
                    localClaimDTO2.getAttributeMappings()[i].getUserStoreDomain());
            Assert.assertEquals(localClaim2.getMappedAttributes().get(i).getAttributeName(),
                    localClaimDTO2.getAttributeMappings()[i].getAttributeName());
        }

        for (int i = 0; i < localClaimDTO2.getClaimProperties().length; i++) {
            ClaimPropertyDTO claimPropertyDTO = localClaimDTO2.getClaimProperties()[i];
            String propertyName = claimPropertyDTO.getPropertyName();
            String propertyValue = claimPropertyDTO.getPropertyValue();

            Assert.assertEquals(propertyValue, localClaim2.getClaimProperties().get(propertyName));
        }
    }

    @Test
    public void testConvertExternalClaimToExternalClaimDTO() throws Exception {

        ExternalClaimDTO externalClaimDTO = ClaimMetadataUtils.convertExternalClaimToExternalClaimDTO(externalClaim);

        Assert.assertEquals(externalClaimDTO.getExternalClaimDialectURI(), externalClaim.getClaimDialectURI());
        Assert.assertEquals(externalClaimDTO.getExternalClaimURI(), externalClaim.getClaimURI());
        Assert.assertEquals(externalClaimDTO.getMappedLocalClaimURI(), externalClaim.getMappedLocalClaim());

        // TODO: Pass null reference
//        ExternalClaimDTO externalClaimDTO = ClaimMetadataUtils.convertExternalClaimToExternalClaimDTO(null);
    }

    @Test
    public void testConvertExternalClaimsToExternalClaimDTOs() throws Exception {

        ExternalClaimDTO[] externalClaimDTOs = ClaimMetadataUtils.
                convertExternalClaimsToExternalClaimDTOs(externalClaims);

        Assert.assertEquals(externalClaimDTOs.length, externalClaims.length);

        for (int i = 0; i < externalClaims.length; i++) {
            Assert.assertEquals(externalClaimDTOs[i].getExternalClaimDialectURI(),
                    externalClaims[i].getClaimDialectURI());
            Assert.assertEquals(externalClaimDTOs[i].getExternalClaimURI(), externalClaims[i].getClaimURI());
            Assert.assertEquals(externalClaimDTOs[i].getMappedLocalClaimURI(), externalClaims[i].getMappedLocalClaim());
        }
    }

    @Test
    public void testConvertExternalClaimDTOToExternalClaim() throws Exception {

        ExternalClaim externalClaim = ClaimMetadataUtils.convertExternalClaimDTOToExternalClaim(externalClaimDTO);

        Assert.assertEquals(externalClaim.getClaimDialectURI(), externalClaimDTO.getExternalClaimDialectURI());
        Assert.assertEquals(externalClaim.getClaimURI(), externalClaimDTO.getExternalClaimURI());
        Assert.assertEquals(externalClaim.getMappedLocalClaim(), externalClaimDTO.getMappedLocalClaimURI());
    }

    @Test
    public void testConvertLocalClaimToClaimMapping() throws Exception {

        ClaimMapping claimMapping = ClaimMetadataUtils.convertLocalClaimToClaimMapping(localClaim2, -1234);

        Assert.assertEquals(claimMapping.getClaim().getDialectURI(), localClaim2.getClaimDialectURI());
        Assert.assertEquals(claimMapping.getClaim().getClaimUri(), localClaim2.getClaimURI());

        Assert.assertEquals(claimMapping.getClaim().getDisplayTag(),
                localClaim2.getClaimProperties().get(ClaimConstants.DISPLAY_NAME_PROPERTY));
        Assert.assertEquals(claimMapping.getClaim().isReadOnly(),
                Boolean.parseBoolean(localClaim2.getClaimProperties().get(ClaimConstants.READ_ONLY_PROPERTY)));

        for (AttributeMapping attributeMapping : localClaim2.getMappedAttributes()) {
            Assert.assertEquals(claimMapping.getMappedAttribute(attributeMapping.getUserStoreDomain()),
                    attributeMapping.getAttributeName());
        }

    }

    @Test
    public void testConvertExternalClaimToClaimMapping() throws Exception {

        List<LocalClaim> localClaimList = Arrays.asList(localClaims);
        ClaimMapping claimMapping = ClaimMetadataUtils.convertExternalClaimToClaimMapping(externalClaim, localClaimList, -1234);

        Assert.assertEquals(claimMapping.getClaim().getDialectURI(), externalClaim.getClaimDialectURI());
        Assert.assertEquals(claimMapping.getClaim().getClaimUri(), externalClaim.getClaimURI());

        Assert.assertEquals(claimMapping.getClaim().getDisplayTag(),
                localClaim2.getClaimProperties().get(ClaimConstants.DISPLAY_NAME_PROPERTY));
        Assert.assertEquals(claimMapping.getClaim().isReadOnly(),
                Boolean.parseBoolean(localClaim2.getClaimProperties().get(ClaimConstants.READ_ONLY_PROPERTY)));

        for (AttributeMapping attributeMapping : localClaim2.getMappedAttributes()) {
            Assert.assertEquals(claimMapping.getMappedAttribute(attributeMapping.getUserStoreDomain()),
                    attributeMapping.getAttributeName());
        }


        ClaimMapping claimMapping2 = ClaimMetadataUtils.convertExternalClaimToClaimMapping(externalClaim, null, -1234);

        Assert.assertEquals(claimMapping2.getClaim().getDialectURI(), externalClaim.getClaimDialectURI());
        Assert.assertEquals(claimMapping2.getClaim().getClaimUri(), externalClaim.getClaimURI());

    }
//
//    @AfterMethod
//    public void tearDown() throws Exception {
//
//    }

}