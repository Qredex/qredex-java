/*
 *    ▄▄▄▄
 *  ▄█▀▀███▄▄              █▄
 *  ██    ██ ▄             ██
 *  ██    ██ ████▄▄█▀█▄ ▄████ ▄█▀█▄▀██ ██▀
 *  ██  ▄ ██ ██   ██▄█▀ ██ ██ ██▄█▀  ███
 *   ▀█████▄▄█▀  ▄▀█▄▄▄▄█▀███▄▀█▄▄▄▄██ ██▄
 *        ▀█
 *
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  Licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
 *  You may not use this file except in compliance with that License.
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific language governing permissions
 *  and limitations under the License.
 *
 *  If you need additional information or have any questions, please email: copyright@qredex.com
 */
package examples;

import com.qredex.sdk.Qredex;
import com.qredex.sdk.model.request.IssueInfluenceIntentTokenRequest;
import com.qredex.sdk.model.response.InfluenceIntentResponse;

public final class IssueIit {

    public static void main(String[] args) {
        Qredex qredex = Qredex.bootstrap();

        InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
            IssueInfluenceIntentTokenRequest.builder()
                .linkId(System.getenv("QREDEX_LINK_ID"))
                .landingPath("/products/spring-launch")
                .referrer("https://instagram.com/alice")
                .build());

        System.out.println(iit);
    }
}
