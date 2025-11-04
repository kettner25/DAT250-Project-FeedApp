package no.hvl.group17.feedapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.hvl.group17.feedapp.domain.Option;

@Data
@AllArgsConstructor
public class OptionCount {
    private Option option;

    private int count;
}
