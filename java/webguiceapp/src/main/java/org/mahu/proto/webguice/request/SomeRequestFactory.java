package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.stm.IRequest;

// ALternative way to bind a request class to IRequest 
public interface SomeRequestFactory {
    IRequest create(PostRequestData rostRequestData);
}
