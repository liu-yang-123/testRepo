package com.zcxd.sync.service;

import java.io.IOException;

/**
 * @author songanwei
 * @date 2021/4/15 17:53
 */
public interface SyncService {

    /**
     * 上传数据到总平台
     */
    void upload() throws IOException;

}
