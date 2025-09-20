package io.fptu.ClubSpiral.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class PageRequest {
    @Min(1)
    private int page;

    @Positive
    private int size;
}
