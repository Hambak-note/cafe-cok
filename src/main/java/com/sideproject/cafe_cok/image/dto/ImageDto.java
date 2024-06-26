package com.sideproject.cafe_cok.image.dto;

import com.sideproject.cafe_cok.image.domain.Image;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ImageDto {

    private final Long id;
    private final String origin;
    private final String thumbnail;

    public static ImageDto from(final Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .origin(image.getOrigin())
                .thumbnail(image.getThumbnail())
                .build();
    }

    public static List<ImageDto> fromList(final List<Image> images) {
        return images.stream()
                .map(image -> ImageDto.from(image))
                .collect(Collectors.toList());
    }

}
