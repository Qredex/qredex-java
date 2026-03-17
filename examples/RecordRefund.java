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
import com.qredex.sdk.model.request.RecordRefundRequest;
import com.qredex.sdk.model.response.OrderAttributionResponse;

public final class RecordRefund {

    public static void main(String[] args) {
        Qredex qredex = Qredex.bootstrap();

        OrderAttributionResponse result = qredex.refunds().recordRefund(
            RecordRefundRequest.builder()
                .storeId(System.getenv("QREDEX_STORE_ID"))
                .externalOrderId("order-100045")
                .externalRefundId("refund-100045-1")
                .refundTotal(25.00)
                .build());

        System.out.println(result);
    }
}
