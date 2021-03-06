/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.syncope.common.types.CipherAlgorithm;
import org.apache.syncope.core.util.PasswordEncoder;
import org.junit.Test;

/**
 * Testclass to test all encryption algorithms.
 */
public class PasswordEncoderTest {

    private final String password = "password";
    
    /**
     * Verify all algorithms
     */
    @Test
    public void testEncoder()
            throws Exception {

        for (CipherAlgorithm cipherAlgorithm : CipherAlgorithm.values()) {
            final String encPassword = PasswordEncoder.encode(password, cipherAlgorithm);

            assertNotNull(encPassword);
            assertTrue(PasswordEncoder.verify(password, cipherAlgorithm, encPassword));
            assertFalse(PasswordEncoder.verify("pass", cipherAlgorithm, encPassword));

            // check that same password encoded with BCRYPT or Salted versions results in different digest
            if (cipherAlgorithm.equals(CipherAlgorithm.BCRYPT) || cipherAlgorithm.getAlgorithm().startsWith("S-")) {
                final String encSamePassword = PasswordEncoder.encode(password, cipherAlgorithm);
                assertNotNull(encSamePassword);
                assertFalse(encSamePassword.equals(encPassword));
                assertTrue(PasswordEncoder.verify(password, cipherAlgorithm, encSamePassword));
            }
        }

    }
}
