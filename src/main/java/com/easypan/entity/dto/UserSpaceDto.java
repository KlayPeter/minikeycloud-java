package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-20 12:16:06
 */
// 序列化忽略未知属性
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
// 报错日志：org.springframework.data.redis.serializer.SerializationException: Could not read JSON: Cannot construct instance of `com.easypan.entity.dto.UserSpaceDto` (no Creators,
// like
// default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
//  at [Source: (byte[])"{"@class":"com.easypan.entity.dto.UserSpaceDto","useSpace":0,"totalSpace":5242880}"; line: 1, column: 49]; nested exception is com.fasterxml.jackson
//  .databind.exc.InvalidDefinitionException: Cannot construct instance of `com.easypan.entity.dto.UserSpaceDto` (no Creators, like default constructor, exist): cannot
//  deserialize from Object value (no delegate- or property-based Creator)
//  at [Source: (byte[])"{"@class":"com.easypan.entity.dto.UserSpaceDto","useSpace":0,"totalSpace":5242880}"; line: 1, column: 49]
// 防止反序列化时报错
@NoArgsConstructor
// 防止@Builder报错
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserSpaceDto implements Serializable
{
    private Long useSpace;
    private Long totalSpace;

    public static UserSpaceDtoBuilder builder() {
        return new UserSpaceDtoBuilder();
    }

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public static class UserSpaceDtoBuilder {
        private Long useSpace;
        private Long totalSpace;

        public UserSpaceDtoBuilder useSpace(Long useSpace) {
            this.useSpace = useSpace;
            return this;
        }

        public UserSpaceDtoBuilder totalSpace(Long totalSpace) {
            this.totalSpace = totalSpace;
            return this;
        }

        public UserSpaceDto build() {
            UserSpaceDto userSpaceDto = new UserSpaceDto();
            userSpaceDto.useSpace = this.useSpace;
            userSpaceDto.totalSpace = this.totalSpace;
            return userSpaceDto;
        }
    }
}
