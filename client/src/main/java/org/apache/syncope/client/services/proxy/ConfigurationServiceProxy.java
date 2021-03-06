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
package org.apache.syncope.client.services.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import org.apache.syncope.common.SyncopeConstants;
import org.apache.syncope.common.services.ConfigurationService;
import org.apache.syncope.common.to.ConfigurationTO;
import org.apache.syncope.common.to.MailTemplateTO;
import org.apache.syncope.common.to.ValidatorTO;
import org.apache.syncope.common.util.CollectionWrapper;
import org.springframework.web.client.RestTemplate;

public class ConfigurationServiceProxy extends SpringServiceProxy implements ConfigurationService {

    public ConfigurationServiceProxy(final String baseUrl, final RestTemplate restTemplate) {
        super(baseUrl, restTemplate);
    }

    @Override
    public Response create(final ConfigurationTO configurationTO) {
        ConfigurationTO created = getRestTemplate().postForObject(baseUrl + "configuration/create", configurationTO,
                ConfigurationTO.class);
        try {
            URI location = URI.create(baseUrl
                    + "configuration/read/"
                    + URLEncoder.encode(created.getKey(), SyncopeConstants.DEFAULT_ENCODING)
                    + ".json");
            return Response.created(location)
                    .header(SyncopeConstants.REST_HEADER_ID, created.getKey())
                    .build();
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public void delete(final String key) {
        getRestTemplate().getForObject(baseUrl + "configuration/delete/{key}.json", ConfigurationTO.class, key);
    }

    @Override
    public List<ConfigurationTO> list() {
        return Arrays.asList(getRestTemplate().getForObject(baseUrl + "configuration/list.json",
                ConfigurationTO[].class));
    }

    @Override
    public ConfigurationTO read(final String key) {
        return getRestTemplate().getForObject(baseUrl + "configuration/read/{key}.json", ConfigurationTO.class, key);
    }

    @Override
    public void update(final String key, final ConfigurationTO configurationTO) {
        getRestTemplate().postForObject(baseUrl + "configuration/update", configurationTO, ConfigurationTO.class);
    }

    @Override
    public Set<ValidatorTO> getValidators() {
        return CollectionWrapper.wrapValidators(
                handlePossiblyEmptyStringCollection(baseUrl + "configuration/validators.json"));
    }

    @Override
    public Set<MailTemplateTO> getMailTemplates() {
        return CollectionWrapper.wrapMailTemplates(
                handlePossiblyEmptyStringCollection(baseUrl + "configuration/mailTemplates.json"));
    }

    @Override
    public Response dbExport() {
        return handleStream(baseUrl + "configuration/dbexport");
    }
}
