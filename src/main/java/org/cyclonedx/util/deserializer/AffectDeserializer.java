/*
 * This file is part of CycloneDX Core (Java).
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) OWASP Foundation. All Rights Reserved.
 */
package org.cyclonedx.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyclonedx.model.vulnerability.Vulnerability;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AffectDeserializer
    extends JsonDeserializer<Vulnerability.Affect>
{
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Vulnerability.Affect deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);

    Vulnerability.Affect affect = new Vulnerability.Affect();

    JsonNode refNode = node.get("ref");
    if (refNode != null) {
      affect.setRef(refNode.asText());
    }

    JsonNode versionsNode = node.get("versions");
    if (versionsNode != null) {
      if (versionsNode.isArray()) {
        List<Vulnerability.Version> versions = mapper.convertValue(node.get("versions"), new TypeReference<List<Vulnerability.Version>>() {});
        affect.setVersions(versions);
      } else if (versionsNode.has("version")) {
        JsonNode versionNode = versionsNode.get("version");
        if (versionNode.isArray()) {
          List<Vulnerability.Version> versions = mapper.convertValue(versionNode, new TypeReference<List<Vulnerability.Version>>() {});
          affect.setVersions(versions);
        }
        else {
          affect.addVersion(mapper.convertValue(versionNode, Vulnerability.Version.class));
        }
      }
    }

    return affect;
  }
}
