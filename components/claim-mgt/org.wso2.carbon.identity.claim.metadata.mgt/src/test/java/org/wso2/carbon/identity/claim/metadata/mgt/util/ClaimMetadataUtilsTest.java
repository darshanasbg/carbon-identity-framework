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
import org.testng.annotations.Test;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.ClaimDialectDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.ClaimPropertyDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.dto.LocalClaimDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.model.AttributeMapping;
import org.wso2.carbon.identity.claim.metadata.mgt.model.ClaimDialect;
import org.wso2.carbon.identity.claim.metadata.mgt.model.LocalClaim;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.util.UserCoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClaimMetadataUtilsTest {

    @Test
    public void testConvertClaimDialectToClaimDialectDTO() {

        String claimDialectURI = "testClaimDialectURI";
        ClaimDialect claimDialect = new ClaimDialect(claimDialectURI);

        ClaimDialectDTO claimDialectDTO = ClaimMetadataUtils.convertClaimDialectToClaimDialectDTO(claimDialect);

        Assert.assertEquals(claimDialectDTO.getClaimDialectURI(), claimDialectURI);
    }

    @Test
    public void testConvertClaimDialectsToClaimDialectDTOs() {

        String claimDialectURI = "testClaimDialectURI";
        int arraySize = 2;
        ClaimDialect[] claimDialects = new ClaimDialect[arraySize];

        for (int i = 0; i < claimDialects.length; i++) {
            claimDialects[i] = new ClaimDialect(claimDialectURI + i);
        }

        ClaimDialectDTO[] claimDialectDTOs = ClaimMetadataUtils.convertClaimDialectsToClaimDialectDTOs(claimDialects);

        Assert.assertEquals(claimDialectDTOs.length, arraySize);

        for (int i = 0; i < claimDialectDTOs.length; i++) {
            Assert.assertEquals(claimDialectDTOs[i].getClaimDialectURI(), claimDialectURI + i);
        }
    }

    @Test
    public void testConvertClaimDialectDTOToClaimDialect() {

        String claimDialectURI = "testClaimDialectURI";
        ClaimDialectDTO claimDialectDTO = new ClaimDialectDTO();
        claimDialectDTO.setClaimDialectURI(claimDialectURI);

        ClaimDialect claimDialect = ClaimMetadataUtils.convertClaimDialectDTOToClaimDialect(claimDialectDTO);

        Assert.assertEquals(claimDialect.getClaimDialectURI(), claimDialectURI);
    }

    @Test
    public void testConvertLocalClaimToLocalClaimDTO() {

        String localClaimURI = "testLocalClaimURI";
        LocalClaim localClaim = new LocalClaim(localClaimURI);

        LocalClaimDTO localClaimDTO = ClaimMetadataUtils.convertLocalClaimToLocalClaimDTO(localClaim);

        Assert.assertEquals(localClaimDTO.getLocalClaimURI(), localClaimURI);


        String localClaimURI2 = "testLocalClaimURI2";

        AttributeMapping attributeMapping1 = new AttributeMapping(UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME, "uid");
        AttributeMapping attributeMapping2 = new AttributeMapping("AD", "sAMAccountName");

        List<AttributeMapping> attributeMappingList = new ArrayList<>();
        attributeMappingList.add(attributeMapping1);
        attributeMappingList.add(attributeMapping2);

        Map<String, String> claimPropertiesMap = new HashMap<>();
        claimPropertiesMap.put(ClaimConstants.DISPLAY_NAME_PROPERTY, "username");
        claimPropertiesMap.put(ClaimConstants.READ_ONLY_PROPERTY, "true");

        LocalClaim localClaim2 = new LocalClaim(localClaimURI2, attributeMappingList, claimPropertiesMap);

        LocalClaimDTO localClaimDTO2 = ClaimMetadataUtils.convertLocalClaimToLocalClaimDTO(localClaim2);

        Assert.assertEquals(localClaimDTO2.getLocalClaimURI(), localClaimURI2);
        Assert.assertEquals(localClaimDTO2.getAttributeMappings().length, 2);
        Assert.assertEquals(localClaimDTO2.getClaimProperties().length, 2);

        for (int i = 0; i < localClaimDTO2.getAttributeMappings().length; i++) {
            Assert.assertEquals(localClaimDTO2.getAttributeMappings()[i].getUserStoreDomain(),
                    attributeMappingList.get(i).getUserStoreDomain());
            Assert.assertEquals(localClaimDTO2.getAttributeMappings()[i].getAttributeName(),
                    attributeMappingList.get(i).getAttributeName());
        }

        for (int i = 0; i < localClaimDTO2.getClaimProperties().length; i++) {
            ClaimPropertyDTO claimPropertyDTO = localClaimDTO2.getClaimProperties()[i];
            String propertyName = claimPropertyDTO.getPropertyName();
            String propertyValue = claimPropertyDTO.getPropertyValue();

            Assert.assertEquals(propertyValue, claimPropertiesMap.get(propertyName));
        }

    }
//
//    @Test
//    public void testConvertLocalClaimsToLocalClaimDTOs() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertLocalClaimDTOToLocalClaim() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertExternalClaimToExternalClaimDTO() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertExternalClaimsToExternalClaimDTOs() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertExternalClaimDTOToExternalClaim() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertLocalClaimToClaimMapping() throws Exception {
//
//    }
//
//    @Test
//    public void testConvertExternalClaimToClaimMapping() throws Exception {
//
//    }
//
//    @BeforeMethod
//    public void setUp() throws Exception {
//
//    }
//
//    @AfterMethod
//    public void tearDown() throws Exception {
//
//    }

}