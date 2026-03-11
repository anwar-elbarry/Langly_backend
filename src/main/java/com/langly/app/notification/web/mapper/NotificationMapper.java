package com.langly.app.notification.web.mapper;

import com.langly.app.notification.entity.Notification;
import com.langly.app.notification.web.dto.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "type",   expression = "java(notification.getType() != null ? notification.getType().name() : null)")
    @Mapping(target = "status", expression = "java(notification.getStatus() != null ? notification.getStatus().name() : null)")
    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
