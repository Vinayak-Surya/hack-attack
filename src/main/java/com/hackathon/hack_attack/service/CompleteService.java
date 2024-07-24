package com.hackathon.hack_attack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompleteService {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI3M2IzZmZiYi04NGQyLTQ0NzItYmI3ZS1kYWZhZjg3ZTRjNzQiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNzFiYjRlNGEtODg1YS00YmY5LWFkOWEtY2Y0YzAzYWQzYjRhIiwiZXhwIjoxNzIxNzkyMzk2LCJpYXQiOjE3MjE3OTE3OTYsImp0aSI6IjVmMTFkNDNkLTU3ZDktNGQzOS04NWUxLTU5ZmQxOGNkZDY1MCIsInRlbmFudCI6Ik5hdFdlc3QifQ.Inc5of5Z6FFc4Ej0ghlGqxpQ3qDdqqVEpdDqkj-Pic32zUDlQBsOmoVGpc4WUh10UkT3ROTCmhPVA6lujQEeLdTuyMReCQSjpHTVqrw0kpNOe5u2lAmCvncfgT_PdHS-rhverrbqHFCga493hT6Szoahf3_qqFAHR2bpKadMU0Coq08v2hZXAxo-3oLZ17Wd-LL5jzaH2x-8IvE9uZ0dpu724vPAHegAhYGpvBWu1OXuMQ940HFu8yzV0iq_rVC7FirfACgw5_DwjkDL_hOKD9GP5erc6vlfEsuBqLfkptj0doAMPJ6Or6NNFvN-fhaF8v8VSnwfSJX6aFOMO5dB8w";
    @Autowired
    private RestTemplate restTemplate;

    // -------------------------------- ACCOUNT APIS --------------------------------
    public Object fetchAccounts() {
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountBalances(String accountId) {
        //
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/balances", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountTransactions(String accountId) {
        HttpHeaders httpHeaders = new HttpHeaders();
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/transactions", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    // -------------------------------------- CARD APIS -------------------------------------
    public Object fetchAccountCards(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/cards", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountCardDetails(String accountId, String cardId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/cards/" + cardId, HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }

    public Object fetchAccountOffers(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/offers", HttpMethod.GET, entity, Object.class);
            System.out.println(response);
            return response.getBody();
        } catch (Exception e) {
            return "Offers couldn't be fetched. Offer information only available for Credit Card accounts";
        }

    }

    public Object fetchAccountStatements(String accountId) {
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiIzNzBjYTgxMS1lYjRiLTQwNmEtOTEyNC05MDkyMDU2MmI2MTYiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiNjM4ZGI5ZjktMDgxYy00NGRiLWI5ZjYtZWNlYjJmNmNhYTA1IiwiZXhwIjoxNzIxNzg3NTY3LCJpYXQiOjE3MjE3ODY5NjcsImp0aSI6IjFiNTE3MGNiLWY5MTctNDAzNC05OTY4LTU5ZGMxYjAwNTVhZiIsInRlbmFudCI6Ik5hdFdlc3QifQ.GLC11mj1A_FiP9KVzNkGN4fTprO4xA0czb8iBJEzuSouEE2Ak7JFiidFuIKrPlXvnenpCgAeoc22gjkw4Lk3NlEEMXeE8NT_N24VtgFA7aijygfzBpgMD1hR4jMxMfdgU8q5vZOTBjd_DCpRMnxaziUsMp06cletT2uSsFEMTIYvXGUXs77tkv5azUEE7ZWh1Wsya_2UYSlkWfYHpVgremU2wxzLZa8uSU_S7zCHa9QTwaps4GzqK4v43uRjuhHhVzIUe_riawK6Xf6Xvi7yFWXWKcFL2SbuR47cHIjYnfABglpa7UIsU5v_l2mk3DN_tVY0Wc4ZJNyuNCQARfGAbg";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts/" + accountId + "/statements", HttpMethod.GET, entity, Object.class);
            System.out.println(response);
            return response.getBody();
        } catch (Exception e) {
            return "Statement couldn't be fetched. Statement information only available for Credit Card accounts";
        }
    }
}
