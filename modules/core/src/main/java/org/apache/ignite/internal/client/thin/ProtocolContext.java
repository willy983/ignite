/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.client.thin;

import java.util.EnumSet;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.client.ClientFeatureNotSupportedByServerException;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_SE_281_THIN_CLIENT_COMPATIBLE;

/**
 * Protocol Context.
 */
public class ProtocolContext {
    /** Protocol version. */
    private final ProtocolVersion ver;

    /** Features. */
    private final EnumSet<ProtocolBitmaskFeature> features;

    /** */
    private final boolean ise281Compatible;

    /**
     * @param ver Protocol version.
     * @param features Supported features.
     */
    public ProtocolContext(ProtocolVersion ver, EnumSet<ProtocolBitmaskFeature> features) {
        this(ver, features, false);
    }

    /**
     * @param ver Protocol version.
     * @param features Supported features.
     * @param ise281Compatible Ignite SE 4.281 compatibility flag.
     */
    public ProtocolContext(ProtocolVersion ver, EnumSet<ProtocolBitmaskFeature> features, boolean ise281Compatible) {
        this.ver = ver;
        this.features = features != null ? features : EnumSet.noneOf(ProtocolBitmaskFeature.class);
        this.ise281Compatible = ise281Compatible;
    }

    /**
     * @return {@code true} if bitmask protocol feature supported.
     */
    public boolean isFeatureSupported(ProtocolBitmaskFeature feature) {
        return features.contains(feature);
    }

    /**
     * Check that feature is supported by the server.
     *
     * @param feature Feature.
     * @throws ClientFeatureNotSupportedByServerException If feature is not supported by the server.
     */
    public void checkFeatureSupported(ProtocolBitmaskFeature feature) throws ClientFeatureNotSupportedByServerException {
        if (!isFeatureSupported(feature))
            throw new ClientFeatureNotSupportedByServerException(feature);
    }

    /**
     * @return {@code true} if protocol version feature supported.
     */
    public boolean isFeatureSupported(ProtocolVersionFeature feature) {
        return isFeatureSupported(ver, feature);
    }

    /**
     * @return Supported features.
     */
    public EnumSet<ProtocolBitmaskFeature> features() {
        return features;
    }

    /**
     * @return Protocol version.
     */
    public ProtocolVersion version() {
        return ver;
    }

    /**
     * Check if the feature was supported in the protocol version.
     * @param ver Protocol version.
     * @param feature Feature which support should be checked.
     * @return {@code true} if the feature was supported in the protocol version.
     */
    public static boolean isFeatureSupported(ProtocolVersion ver, ProtocolVersionFeature feature) {
        return ver.compareTo(feature.verIntroduced()) >= 0;
    }

    /** */
    public boolean isIse281Compatible() {
        return ise281Compatible;
    }

    /**
     * @param ver Protocol version.
     */
    public static boolean isIse281Compatible(ProtocolVersion ver) {
        return IgniteSystemProperties.getBoolean(IGNITE_SE_281_THIN_CLIENT_COMPATIBLE, false) &&
            ProtocolVersion.V1_7_0.equals(ver);
    }
}
