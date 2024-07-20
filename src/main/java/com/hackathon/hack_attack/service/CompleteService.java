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
    @Autowired
    private RestTemplate restTemplate;

    public Object fetch() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHAiOiJIYWNrIEF0dGFjayIsIm9yZyI6Ind3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiaXNzIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5uYXR3ZXN0LmNvbSIsInRva2VuX3R5cGUiOiJBQ0NFU1NfVE9LRU4iLCJleHRlcm5hbF9jbGllbnRfaWQiOiJLODRYMDg3aXZPci11TUNyQXlDV1pXZFg1RjdBaVhmdXdIX1NQbVJ5QWVNPSIsImNsaWVudF9pZCI6IjgyMzU3MTUzLWE1YWQtNDExNS04Y2RmLWU4NGNiNzRmZTliMSIsIm1heF9hZ2UiOjg2NDAwLCJhdWQiOiI4MjM1NzE1My1hNWFkLTQxMTUtOGNkZi1lODRjYjc0ZmU5YjEiLCJ1c2VyX2lkIjoiMTIzNDU2Nzg5MDEyQHd3dy5ib2EtaGFjay1hdHRhY2suY29tIiwiZ3JhbnRfaWQiOiI1ODBlZjk0Zi03Yjg3LTRjNTAtOGEwNi0yYWFmYzkzNzkwMjEiLCJzY29wZSI6ImFjY291bnRzIG9wZW5pZCIsImNvbnNlbnRfcmVmZXJlbmNlIjoiYWI2OWMyYzctNTQ2ZC00MTcwLTk0NWQtNTA2ZmUzOTUwMDI4IiwiZXhwIjoxNzIxNDU1MjQ4LCJpYXQiOjE3MjE0NTQ2NDgsImp0aSI6IjZmNDQ3NTljLTVhZGQtNDVhMi1hZmNiLTgzOGI2MDhkODM5MyIsInRlbmFudCI6Ik5hdFdlc3QifQ.sxz-C5btuZ2Z7tjlB1Pb0IPXyCtFYrGJHfxYVHGxgOozeDmaIOq2_3_Z9iTDmHsIpjkRzSfTEQkeV0-RZazyoQ7dPB6TmYLSE81GLvJUiQNA-XYeHzNo51KQY7hX03U8qEZqWc_cnz1xd_FCvc2KwdIdN_mmujzbq9qWEtRAlZomy_FifWVvwESW_-LIg9nIQkd_0bEGGXNcC3biHHbjJgSp3cE5YCMo9ItKVcBJsS8YHXbT7rNg8drEEw48_miTBOnaz_QY2bk5FK3m3REVjsP1lUJBdYhrIxWRQjbNE1CwffDCyyjlwFAvK3r3tbJduptc9knLcbn6IClWJ0dJnw";
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> response = restTemplate.exchange("https://ob.sandbox.natwest.com/open-banking/v3.1/aisp/accounts", HttpMethod.GET, entity, Object.class);
        System.out.println(response);
        return response.getBody();
    }
}
