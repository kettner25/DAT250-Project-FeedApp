package no.hvl.group17.feedapp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class OptionTests {
    @Test
    void optionOk_returnsTrue() {
        var poll = Poll.builder().build();

        var option = Option.builder().id(1).caption("Pancakes").poll(poll).build();

        assertThat(option.Verify()).isTrue();
    }

    @Test
    void optionCaptionMissing_returnsFalse() {
        var option = Option.builder().id(1).build();

        assertThat(option.Verify()).isFalse();
    }

    @Test
    void optionOrderNegative_returnsFalse() {
        var option = Option.builder().id(1).caption("Pancakes").order(-1).build();

        assertThat(option.Verify()).isFalse();
    }

}
