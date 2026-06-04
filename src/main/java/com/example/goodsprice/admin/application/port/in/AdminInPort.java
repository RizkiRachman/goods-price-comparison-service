package com.example.goodsprice.admin.application.port.in;

import com.example.goodsprice.api.model.AdminJobTriggerResponse;

@FunctionalInterface
public interface AdminInPort {

  AdminJobTriggerResponse triggerJob(String jobName);
}
