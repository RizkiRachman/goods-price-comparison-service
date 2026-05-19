package com.example.goodsprice.activity.application.port.out;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;

public interface ActivityLogEventOutPort {

  void publishLogged(ActivityLogDomain activity);
}
