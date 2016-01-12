package org.mahu.proto.lifecycle;

import org.mahu.proto.lifecycle.impl.RequestProxy;

/**
 * Through this interface a list of RequestProxy is maintained so that it can be
 * cancelled when services restart.
 */
public interface IRequestProxyList {

    void add(RequestProxy requestProxy);

    void remove(RequestProxy requestProxy);

}
