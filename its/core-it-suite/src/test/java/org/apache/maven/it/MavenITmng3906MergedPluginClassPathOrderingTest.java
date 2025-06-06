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
package org.apache.maven.it;

import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-3906">MNG-3906</a>.
 *
 * @author Benjamin Bentmann
 *
 */
public class MavenITmng3906MergedPluginClassPathOrderingTest extends AbstractMavenIntegrationTestCase {

    public MavenITmng3906MergedPluginClassPathOrderingTest() {
        super("(2.0.10,2.1.0-M1),(2.1.0-M1,)");
    }

    /**
     * Test that project-level plugin dependencies are properly merged during inheritance.
     *
     * @throws Exception in case of failure
     */
    @Test
    public void testitMNG3906() throws Exception {
        File testDir = extractResources("/mng-3906");

        Verifier verifier = newVerifier(new File(testDir, "sub").getAbsolutePath());
        verifier.setAutoclean(false);
        verifier.deleteDirectory("target");
        verifier.deleteArtifacts("org.apache.maven.its.mng3906");
        verifier.filterFile("settings-template.xml", "settings.xml");
        verifier.addCliArgument("--settings");
        verifier.addCliArgument("settings.xml");
        verifier.addCliArgument("validate");
        verifier.execute();
        verifier.verifyErrorFreeLog();

        Properties pclProps = verifier.loadProperties("target/pcl.properties");

        String className = "org.apache.maven.its.mng3906.SomeClass";
        String resName = className.replace('.', '/') + ".class";

        assertEquals("5", pclProps.getProperty(resName + ".count"));

        assertTrue(
                pclProps.getProperty(resName + ".0").endsWith("/c-0.1.jar!/" + resName),
                pclProps.getProperty(resName + ".0"));
        assertTrue(
                pclProps.getProperty(resName + ".1").endsWith("/a-0.2.jar!/" + resName),
                pclProps.getProperty(resName + ".1"));
        assertTrue(
                pclProps.getProperty(resName + ".2").endsWith("/b-0.1.jar!/" + resName),
                pclProps.getProperty(resName + ".2"));
        assertTrue(
                pclProps.getProperty(resName + ".3").endsWith("/e-0.1.jar!/" + resName),
                pclProps.getProperty(resName + ".3"));
        assertTrue(
                pclProps.getProperty(resName + ".4").endsWith("/d-0.1.jar!/" + resName),
                pclProps.getProperty(resName + ".4"));
    }
}
