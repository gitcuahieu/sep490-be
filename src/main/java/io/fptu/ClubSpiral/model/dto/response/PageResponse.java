package io.fptu.ClubSpiral.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse {
    private Integer totalPage;
    private Integer totalItems;
    private Integer currentPage;
    private Integer pageSize;
}