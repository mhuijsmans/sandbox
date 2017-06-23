package org.mahu.proto.webguice.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mahu.proto.webguice.restconfig.ApplicationInitializeDestroy;

import com.google.inject.Injector;

public class RestServiceTest {

    @Test
    public void test() {
        Injector injector = ApplicationInitializeDestroy.createApplicationInjector();
        RestService restService = new RestService(injector);
        Response response = restService.postMsg("hi");
        assertEquals(200, response.getStatus());
    }

}
