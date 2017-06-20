package org.mahu.proto.lifecycle;

import java.util.List;

/**
 * Registry of all available service.
 */
public interface IApiRegistry {

    void setPublicService(List<PublicServiceKey<?>> services);

    void removeAllPublicServices();
}
