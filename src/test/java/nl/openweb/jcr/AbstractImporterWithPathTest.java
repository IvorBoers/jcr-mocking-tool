/*
 * Copyright 2017 Open Web IT B.V. (https://www.openweb.nl/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.openweb.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * @author Ebrahim Aharpour
 * @since 9/9/2017
 */
public abstract class AbstractImporterWithPathTest extends AbstractImporterXmlTest {

    private InMemoryJcrRepository inMemoryJcrRepository;

    @Override
    protected String getImportPath() {
        return "some/path/";
    }

    @Override
    protected Importer createImporter() throws IOException, RepositoryException, URISyntaxException {
        inMemoryJcrRepository = new InMemoryJcrRepository();
        return new Importer.Builder(() -> {
            Session session = inMemoryJcrRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            return session.getRootNode();
        })
                .addMixins(true)
                .addUuid(true)
                .addUnknownTypes(true)
                .saveSession(true)
                .build();
    }

    @Test
    public void intermediateNodeTest() throws RepositoryException {
        Node someNode = rootNode.getNode("some");
        assertEquals(expectedIntermediateNodeType(), someNode.getPrimaryNodeType().getName());
        assertEquals("jmt:folder", someNode.getNode("path").getPrimaryNodeType().getName());
        assertEquals("cafebabe-cafe-babe-cafe-babecafebab1", someNode.getNode("path").getIdentifier());

    }

    protected String expectedIntermediateNodeType() {
        return "nt:unstructured";
    }

    @Override
    protected void shutdown() throws IOException {
        inMemoryJcrRepository.shutdown();
    }
}
