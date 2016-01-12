package org.mahu.proto.lifecycle;

import java.util.List;

public interface IApiRegistry {

    void setPublicService(List<PublicServiceKey<?>> services);
    
    void removeAllPublicServices();

}
