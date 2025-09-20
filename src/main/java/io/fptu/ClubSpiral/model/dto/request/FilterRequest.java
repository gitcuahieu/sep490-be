package io.fptu.ClubSpiral.model.dto.request;

import io.fptu.ClubSpiral.common.constant.SortDirection;
import lombok.Getter;

@Getter
public class FilterRequest extends PageRequest{
    private String type;

    private String SortBy;

    private SortDirection sortDirection;
}
