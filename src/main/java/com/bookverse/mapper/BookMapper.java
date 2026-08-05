package com.bookverse.mapper;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.entity.Book;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper chuyển đổi giữa Book Entity và các DTO.
 * Sử dụng Spring component model để inject như một Bean.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    /**
     * Chuyển từ BookRequestDto sang Book Entity.
     * Bỏ qua các trường tự sinh (id, coverPath, timestamps).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coverPath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookRequestDto dto);

    /**
     * Chuyển từ Book Entity sang BookResponseDto.
     * Trường coverUrls sẽ được set thủ công ở tầng Service.
     */
    @Mapping(target = "coverUrls", ignore = true)
    BookResponseDto toResponseDto(Book book);

    /**
     * Chuyển danh sách Book Entity sang danh sách BookResponseDto.
     */
    List<BookResponseDto> toResponseDtoList(List<Book> books);

    /**
     * Cập nhật Entity từ DTO, bỏ qua các trường null.
     * Dùng khi PUT /api/books/{id} - chỉ cập nhật các trường được gửi lên.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coverPath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BookRequestDto dto, @MappingTarget Book book);
}
